package it.polito.mad.lab5g10.seekscape.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log

import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.google.gson.Gson
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.DENIED
import it.polito.mad.lab5g10.seekscape.models.JOINED
import it.polito.mad.lab5g10.seekscape.models.NotificationItem
import it.polito.mad.lab5g10.seekscape.models.OWNED
import it.polito.mad.lab5g10.seekscape.models.PAST
import it.polito.mad.lab5g10.seekscape.models.PENDING
import it.polito.mad.lab5g10.seekscape.models.ProfilePic
import it.polito.mad.lab5g10.seekscape.models.Request
import it.polito.mad.lab5g10.seekscape.models.TO_REVIEW
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.models.User
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.util.UUID


object CommonModel {

    suspend fun getTravelById(id: String, isTravelLite: Boolean=false): Travel? {
        val userId = AppState.myProfile.value.userId
        val docSnapshot = Collections.travels.document(id).get().await()
        if(docSnapshot.exists()) {
            val firestoreModel = docSnapshot.toObject(TravelFirestoreModel::class.java)
            var travel = firestoreModel?.toAppModel(isTravelLite=isTravelLite)

            if (travel == null)
                return null

            if(userId==travel.creator.userId){
                travel.statusForUser = OWNED

            } else if(!isTravelLite){
                if (travel.travelCompanions?.any { comp -> comp.user.userId == userId } == true) {
                    if (travel.status == PAST){
                        if (travel.travelReviews!=null && travel.travelReviews?.any { r -> r.author.userId == userId } == true) {
                            travel.statusForUser = PAST
                        } else {
                            travel.statusForUser = TO_REVIEW
                        }
                    } else {
                        travel.statusForUser = JOINED
                    }
                } else {
                    val requests = getMyRequestForTrip(travel.travelId, userId)
                    if (!requests.isEmpty()){
                        val req: RequestFirestoreModel = requests[0]
                        if(req.refused){
                            travel.statusForUser = DENIED
                        } else if(!req.accepted){
                            travel.statusForUser = PENDING
                        }
                    }
                }
            }

            Log.d("getTravelById", "got travel with id $id")
            return travel
        }
        return null
    }

    suspend fun getPastTravels(userId: String): List<Travel> {
        val theTravelModel = TheTravelModel()
        val now = LocalDate.now()

        val ownedTravels = theTravelModel.getOwnedTravels(userId).filter { now.isAfter(it.endDate) }
        val pastTravels = theTravelModel.getPastTravels(null, userId);

        val travels: List<Travel> = (ownedTravels+pastTravels).sortedByDescending { it.endDate ?: LocalDate.MIN }

        Log.d("OwnedTravelsDebug", "Fetched ${travels.size} owned travels")
        return travels
    }


    suspend fun getMyRequestForTrip(travelId: String, userId:String): List<RequestFirestoreModel> {
        var query: Query = Collections.requests
        query = query.whereEqualTo("authorId", userId)
        query = query.whereEqualTo("tripId", travelId)
        val requestSnapshot = query.get().await()
        val requests = requestSnapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(RequestFirestoreModel::class.java)
            } catch (e: Exception) {
                Log.e("getMyRequestForTrip", "Error converting travel: ${doc.id}", e)
                null
            }
        }
        Log.d("getMyRequestForTrip", "Fetched ${requests.size} owned requests")
        return requests
    }

    suspend fun addRequestToJoin(request: Request): Result<Void?>{
        return try {
            val batch = Collections.requests.firestore.batch()

            val docRef = Collections.requests.document()
            val requestFirestore = request.toFirestoreModel()
            requestFirestore.id = docRef.id
            batch.set(docRef, requestFirestore)
            batch.commit().await()

            println("Successfully inserted the document in the 'requests' collection.")
            Result.success(null)
        } catch (e: Exception) {
            println("Error inserting document in 'requests' collection: $e")
            Result.failure(e)
        }
    }


    suspend fun getUser(userId: String): User? {
        val docSnapshot = Collections.users.document(userId).get().await()
        if(docSnapshot.exists()){

            val userFirebase = docSnapshot.toObject(UserFirestoreModel::class.java)

            val user: User? =
                    if (AppState.myProfile.value.userId==userId)
                        userFirebase?.toAppModel(isMyProfile = true)
                    else
                        userFirebase?.toAppModel()

            if(user==null)
                return null;

            user.trips = getPastTravels(userId)
            return user
        }
        return null
    }

    suspend fun getLiteUser(userId: String): User? {
        return try {
            val snapshot = Collections.users.document(userId)
                .get().await()
            if (snapshot.exists()) {
                val user: UserFirestoreModel?
                try {
                    user = snapshot.toObject(UserFirestoreModel::class.java)
                } catch (e: Exception) {
                    Log.e("getLiteUser", "Error during snapshot.toObject for user ID $userId: ${e.message}", e)
                    e.printStackTrace()
                    return null
                }

                if(user==null){
                    unknown_User
                } else {
                    val liteUser = User(
                        user.userId,
                        user.authUID,
                        user.nickname,
                        user.name,
                        user.surname,
                        0, "","","","","",
                        imageToAppModel(user.profilePic, isProfilePic = true) as ProfilePic?,
                        "",listOf<String>(),null,
                        user.reviews?.map { it.toAppModel() },
                        null,null,null,0
                        )
                    liteUser
                }
            } else {
                unknown_User
            }
        } catch (e: Exception) {
            Log.e("getLiteUser", "Error fetching user", e)
            unknown_User
        }
    }

    fun isFirebaseOrGoogleImageUrl(url: String?): Boolean{
        if(url==null)
            return false;
        val isGoogle = url.contains("googleusercontent.com")
        val isFirebase = url.startsWith("https://firebasestorage.googleapis.com/")
        return isGoogle || isFirebase
    }

    suspend fun deleteImagesFromFirebase(bucket: String): Result<Void?> {
        //bucket can be 'profile', 'travel/2'
        return try {
            val storageRef = Firebase.storage.reference.child("pics/$bucket")
            deleteRecursively(storageRef)
            Log.d("deleteImagesFromFirebase", "All images in 'pics/$bucket' deleted.")
            Result.success(null)
        } catch (e: Exception) {
            Log.e("deleteImagesFromFirebase", "Failed to delete images: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun deleteRecursively(ref: StorageReference) {
        val result = ref.listAll().await()
        for (fileRef in result.items) {
            fileRef.delete().await()
            Log.d("deleteRecursively", "Deleted file: ${fileRef.path}")
        }
        for (prefix in result.prefixes) {
            deleteRecursively(prefix)
        }
    }

    suspend fun uploadImageToFirebase(uri: Uri, bucket: String = "common"): String {
        //bucket can be 'profile', 'travel/2'
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("pics/$bucket/${UUID.randomUUID()}.jpg")

        imageRef.putFile(uri).await()
        val downloadUri = imageRef.downloadUrl.await()

        Log.d("uploadImageToFirebase", "Image uploaded: $downloadUri")
        return downloadUri.toString()
    }


    suspend fun uploadDrawableToFirebase(
        context: Context,
        drawableResId: Int,
        bucket: String = "common"
    ): String? {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("pics/$bucket/${UUID.randomUUID()}.jpg")
        try {
            val bitmap = BitmapFactory.decodeResource(context.resources, drawableResId)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            imageRef.putBytes(data).await()
            val downloadUri = imageRef.downloadUrl.await()

            Log.d("uploadDrawableToFirebase", "Image uploaded: $downloadUri")
            return downloadUri.toString()
        } catch (e: Exception) {
            Log.e("uploadDrawableToFirebase", "Upload failed: ${e.message}", e)
            return null
        }
    }

    /*
    ALL notifications IDS:

    deleted with firebase functions:
        `manage_apply_${doc.authorId}_${doc.tripId}`
        `last_minute_join_${doc.travelId}`

    deleted from app on action done:
        `request_accepted_${doc.authorId}_${doc.tripId}`,
        `request_denied_${doc.authorId}_${doc.tripId}`,
        `my_travel_review_${after.travelId}`,
        `my_profile_review_${authorDoc.userId}`,
    */

    suspend fun removeFcmToken(userId: String, token: String): Result<Void?> {
        return try {
            val docRef = Collections.users.document(userId)
            val snapshot = docRef.get().await()

            val currentTokens = snapshot.get("fcmTokens") as? List<String> ?: emptyList()
            val updatedTokens = currentTokens.filter {
                it != token
            }

            docRef.update("fcmTokens", updatedTokens).await()
            Log.d("removeFcmToken", "fcmTokens removed successfully.")
            Result.success(null)
        } catch (e: Exception) {
            Log.e("removeFcmToken", "Failed to remove Notification", e)
            Result.failure(e)
        }
    }

    suspend fun removeNotificationById(userId: String, notificationId: String): Result<Void?> {
        return try {
            val docRef = Collections.users.document(userId)
            val snapshot = docRef.get().await()

            val currentNotificationsRaw = snapshot.get("notifications") as? List<Map<String, Any>> ?: emptyList()

            val currentNotifications = currentNotificationsRaw.mapNotNull { map ->
                try {
                    val json = Gson().toJson(map)
                    Gson().fromJson(json, NotificationItem::class.java)
                } catch (e: Exception) {
                    Log.e("removeNotificationById", "Error converting notifications", e)
                    null
                }
            }

            val updatedNotifications = currentNotifications.filter {
                it.id != notificationId
            }
            docRef.update("notifications", updatedNotifications).await()

            AppState.removeNotification(notificationId)


            Log.d("removeNotificationById", "Notification removed successfully.")
            Result.success(null)
        } catch (e: Exception) {
            Log.e("removeNotificationById", "Failed to remove Notification", e)
            Result.failure(e)
        }
    }




// ------------ SUPPORT ACTIVITY -----------

    private var user_reset_DATA: HashMap<String, String> = HashMap()
    private var travel_reset_DATA: HashMap<String, String> = HashMap()

    suspend fun resetDB(context: Context): Result<Void?> {
        val defaultTravels = listOf(
            travel1, travel2, travel3, travel4, travel5,
            travel6, travel7, travel8, travel9, travel10,
            travel11, travel12, travel13, travel14, travel15,
            travel16, travel17, travel18
        )
        val defaultUsers = listOf(
            user_ob, user_sl, user_dw, user_me, user_eh, user_ec
        )
        val defaultRequests = listOf(
            t1_req1, t1_req2,
            t2_req1, t2_req2, t2_req3, t2_req4,
            t3_req1, t3_req2,
            t5_req1, t5_req2,
            t6_req1, t6_req2, t6_req3,
            t7_req1, t7_req2,
            t8_req1, t8_req2, t8_req3,
            t9_req1, t9_req2,
            t10_req1, t10_req2,
            t11_req1, t11_req2,
            t12_req1, t12_req2,
            t13_req1, t13_req2, t13_req3,
            t14_req1, t14_req2,
            t15_req1, t15_req2, t15_req3,
            t17_req1,
        )
        user_reset_DATA.clear()
        travel_reset_DATA.clear()

        val delUsersResult = deleteAllUsers()
        if (delUsersResult.isFailure) return delUsersResult

        val delTravelsResult = deleteAllTravels()
        if (delTravelsResult.isFailure) return delTravelsResult

        val deleteRequestsResult = deleteAllRequests()
        if (deleteRequestsResult.isFailure) return deleteRequestsResult

        val delPicsResult = deleteImagesFromFirebase("")
        if (delPicsResult.isFailure) return delPicsResult

        val addUsersResult = insertDefaultUsers(defaultUsers, context)
        if (addUsersResult.isFailure) return addUsersResult

        val addTravelsResult = insertDefaultTravels(defaultTravels, context)
        if (addTravelsResult.isFailure) return addTravelsResult

        val insertRequestsResult = insertRequests(defaultRequests)
        if (insertRequestsResult.isFailure) return insertRequestsResult

        var new_data: HashMap<String, String> = HashMap()
        for (entry in user_reset_DATA) {
            val user = defaultUsers.find { it.userId==entry.key}
            if(user==null) continue;
            new_data.put(user.email!!, entry.value)
        }
        for (entry in travel_reset_DATA) {
            val travel = defaultTravels.find { it.travelId==entry.key}
            if(travel==null) continue;
            new_data.put(travel.title!!, entry.value)
        }

        Log.d("UPDATED_DATA_AFTER_RESET",new_data.toString())

        return Result.success(null)
    }


    private suspend fun deleteAllUsers(): Result<Void?> {
        return try {
            val querySnapshot = Collections.users.get().await()
            val batch = Collections.users.firestore.batch()

            for (document in querySnapshot.documents) {
                batch.delete(document.reference)
            }

            batch.commit().await()
            println("Successfully deleted all documents in the 'users' collection.")
            Result.success(null)
        } catch (e: Exception) {
            println("Error deleting documents in 'users' collection: $e")
            Result.failure(e)
        }
    }

    private suspend fun deleteAllTravels(): Result<Void?> {
        return try {
            val querySnapshot = Collections.travels.get().await()
            val batch = Collections.travels.firestore.batch()

            for (document in querySnapshot.documents) {
                batch.delete(document.reference)
            }

            batch.commit().await()
            println("Successfully deleted all documents in the 'travels' collection.")
            Result.success(null)
        } catch (e: Exception) {
            println("Error deleting documents in 'travels' collection: $e")
            Result.failure(e)
        }
    }

    private suspend fun deleteAllRequests(): Result<Void?> {
        return try {
            val snapshot = Collections.requests.get().await()
            val batch = Collections.requests.firestore.batch()

            for (document in snapshot.documents) {
                batch.delete(document.reference)
            }

            batch.commit().await()

            println("Successfully deleted all documents in the 'requests' collection.")
            Result.success(null)
        } catch (e: Exception) {
            println("Error deleting documents in 'requests' collection: $e")
            Result.failure(e)
        }
    }


    private suspend fun insertDefaultUsers(defaultUsers: List<User>, context: Context): Result<Void?> {
        return try {
            val batch = Collections.users.firestore.batch()

            for (user in defaultUsers) {
                val docRef = Collections.users.document()
                val userFirestore = user.toFirestoreModel()
                userFirestore.userId = docRef.id
                user_reset_DATA.put(user.userId, userFirestore.userId)
                userFirestore.birthDay=generateRandomDateOfBirth(user.age)
                userFirestore.reviews?.forEach { review ->
                    review.authorId = user_reset_DATA.get(review.authorId).toString()
                }
                if (user.profilePic!=null && (user.profilePic is ProfilePic.Resource)){
                    val url = uploadDrawableToFirebase(
                        context,
                        (user.profilePic as ProfilePic.Resource).resId,
                        "profile"
                    )
                    userFirestore.profilePic = url
                }

                batch.set(docRef, userFirestore)
            }

            batch.commit().await()

            println("Successfully inserted all documents in the 'users' collection.")
            Result.success(null)
        } catch (e: Exception) {
            println("Error inserting documents in 'users' collection: $e")
            Result.failure(e)
        }
    }

    private suspend fun insertDefaultTravels(defaultTravels: List<Travel>, context: Context): Result<Void?> {
        return try {
            val batch = Collections.travels.firestore.batch()

            for (travel in defaultTravels) {
                val docRef = Collections.travels.document()
                val travelFirestore = travel.toFirestoreModel()
                travelFirestore.travelId = docRef.id
                travel_reset_DATA.put(travel.travelId, travelFirestore.travelId)
                travelFirestore.creatorId = user_reset_DATA.get(travel.creator.userId).toString()
                travelFirestore.travelCompanions?.forEach { comp ->
                    comp.userId = user_reset_DATA.get(comp.userId).toString()
                }
                travelFirestore.travelReviews?.forEach { travelReview ->
                    travelReview.authorId = user_reset_DATA.get(travelReview.authorId).toString()
                }

                travelFirestore.travelChat = travel.travelChat?.map {
                    it.toFirestoreModel()
                }
                travelFirestore.travelChat?.forEach { msg ->
                    if(msg.authorId!="system") {
                        msg.authorId = user_reset_DATA.get(msg.authorId).toString()
                    }
                }

                Log.d("InsertTravel", "Processing travel ID: ${travelFirestore.travelId}")
                // Upload travelImages
                val uploadedImageUrls = coroutineScope {
                    travel.travelImages?.mapNotNull { i ->
                        if (i is TravelImage.Resource) {
                            Log.d("InsertTravel", "Uploading travel image for travel ID: ${travelFirestore.travelId}")
                            async {
                                uploadDrawableToFirebase(context, i.resId, "travels/t_${travelFirestore.travelId}/travelImages")
                            }
                        } else null
                    }?.mapNotNull { it.await() } ?: emptyList()
                }
                Log.d("InsertTravel", "Uploaded ${uploadedImageUrls.size} travel images for travel ID: ${travelFirestore.travelId}")
                travelFirestore.travelImages = uploadedImageUrls

                // Upload reviewImages
                travel.travelReviews?.forEachIndexed { index, r ->
                    val travelReviewFirestore = travelFirestore.travelReviews?.get(index)
                    val uploadedReviewUrls = coroutineScope {
                        r.reviewImages?.mapNotNull { image ->
                            if (image is TravelImage.Resource) {
                                Log.d("Inserting Review IMAGE", "Travel id: "+travelFirestore.travelId)
                                async {
                                    uploadDrawableToFirebase(
                                        context,
                                        image.resId,
                                        "travels/t_${travelFirestore.travelId}/reviewImages/r_${travelReviewFirestore!!.date}"
                                    )
                                }
                            } else null
                        }?.mapNotNull { it.await() } ?: emptyList()
                    }

                    travelReviewFirestore?.reviewImages = uploadedReviewUrls
                }

                batch.set(docRef, travelFirestore)
            }

            batch.commit().await()

            println("Successfully inserted all documents in the 'travels' collection.")
            Result.success(null)
        } catch (e: Exception) {
            println("Error inserting documents in 'travels' collection: $e")
            Result.failure(e)
        }
    }

    private suspend fun insertRequests(requests: List<Request>): Result<Void?> {
        return try {
            val batch = Collections.requests.firestore.batch()

            for (request in requests) {
                val docRef = Collections.requests.document()
                val requestFirestore = request.toFirestoreModel()
                requestFirestore.id = docRef.id
                requestFirestore.authorId = user_reset_DATA.get(requestFirestore.authorId).toString()
                requestFirestore.tripId = travel_reset_DATA.get(requestFirestore.tripId).toString()
                batch.set(docRef, requestFirestore)
            }

            batch.commit().await()

            println("Successfully inserted all documents in the 'requests' collection.")
            Result.success(null)
        } catch (e: Exception) {
            println("Error inserting documents in 'requests' collection: $e")
            Result.failure(e)
        }
    }

}


