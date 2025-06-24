package it.polito.mad.lab5g10.seekscape.firebase

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.google.gson.Gson
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel.uploadImageToFirebase
import it.polito.mad.lab5g10.seekscape.models.AVAILABLE
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.ChatMessage
import it.polito.mad.lab5g10.seekscape.models.DENIED
import it.polito.mad.lab5g10.seekscape.models.FULL
import it.polito.mad.lab5g10.seekscape.models.JOINED
import it.polito.mad.lab5g10.seekscape.models.MAX_COMPANIONS
import it.polito.mad.lab5g10.seekscape.models.MAX_PRICE
import it.polito.mad.lab5g10.seekscape.models.MIN_COMPANIONS
import it.polito.mad.lab5g10.seekscape.models.MIN_PRICE
import it.polito.mad.lab5g10.seekscape.models.OWNED
import it.polito.mad.lab5g10.seekscape.models.PAST
import it.polito.mad.lab5g10.seekscape.models.PENDING
import it.polito.mad.lab5g10.seekscape.models.Request
import it.polito.mad.lab5g10.seekscape.models.Search
import it.polito.mad.lab5g10.seekscape.models.TO_REVIEW
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelCompanion
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.models.User
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime

class TheTravelModel() {
    suspend fun addTravel(updatedTravel: Travel) {
        val docRef = Collections.travels.document()
        val newTravelId = docRef.id

        val travelFirestore = updatedTravel.toFirestoreModel()

        try {
            val uploadedImageUrls = supervisorScope {
                updatedTravel.travelImages?.mapNotNull { i ->
                    if (i is TravelImage.Url) {
                        async {
                            val uploadedUrl = uploadImageToFirebase(i.value.toUri(), "travels/t$newTravelId/travelImages")
                            uploadedUrl
                        }
                    } else {
                        null
                    }
                }?.mapNotNull { it.await() } ?: emptyList()
            }

            travelFirestore.travelImages = uploadedImageUrls
            travelFirestore.travelId = newTravelId

            Collections.travels.document(newTravelId).set(travelFirestore).await()

        } catch (e: Exception) {
            Log.e("addTravel", "Failed to add/update travel $newTravelId", e)
            throw e // rethrow if you want to handle it further up the call stack
        }
    }


    suspend fun updateTravel(travel: Travel): Result<Void?> {
        return try {
            Log.d("updateTravel", "Starting update for travel ID: ${travel.travelId}")

            val travelRef = Collections.travels.document(travel.travelId)
            val travelFirebase = travel.toFirestoreModel()
            val travelId = travelFirebase.travelId

            Log.d("updateTravel", "Converted travel to Firestore model: $travelFirebase")

            val snapshot = travelRef.get().await()
            val existingData = snapshot.toObject(TravelFirestoreModel::class.java)
            val existingImageUrls = existingData?.travelImages ?: emptyList()

            Log.d("updateTravel", "Existing image URLs: $existingImageUrls")
            val currentImageUrls = travel.travelImages
                ?.filterIsInstance<TravelImage.Url>()
                ?.map { it.value }
                ?: emptyList()

            // Find REMOVED images
            val removedImageUrls = existingImageUrls.filterNot { it in currentImageUrls }
            Log.d("updateTravel", "Removed images: $removedImageUrls")

            removedImageUrls.forEach { url ->
                val storageRef = Firebase.storage.getReferenceFromUrl(url)
                storageRef.delete()
                    .addOnSuccessListener {
                        Log.d("updateTravel", "Deleted image: $url")
                    }
                    .addOnFailureListener {
                        Log.e("updateTravel", "Failed to delete image: $url", it)
                    }
            }
            // Upload new travel images
            val newUploadedUrls = coroutineScope {
                travel.travelImages?.mapNotNull { image ->
                    if (image is TravelImage.Url && !image.value.startsWith("https://")) {
                        Log.d("updateTravel", "Uploading new image: ${image.value}")
                        async {
                            uploadImageToFirebase(image.value.toUri(), "travels/t$travelId/travelImages")
                        }
                    } else {
                        if (image is TravelImage.Url) {
                            Log.d("updateTravel", "Already uploaded image: ${image.value}")
                        } else {
                            Log.d("updateTravel", "Skipping non-URL image: $image")
                        }
                        null
                    }
                }?.map { it.await() } ?: emptyList()
            }

            Log.d("updateTravel", "Newly uploaded image URLs: $newUploadedUrls")

            // Combine and set updated images
            val keptImages = currentImageUrls.filter { it.startsWith("https://") }
            val allImageUrls = (keptImages + newUploadedUrls).distinct()

            travelFirebase.travelImages = allImageUrls

            travelRef.set(travelFirebase).await()

            Log.d("updateTravel", "Successfully updated travel with ID: ${travel.travelId}")
            Result.success(null)

        } catch (e: Exception) {
            Log.e("updateTravel", "Error updating travel with ID ${travel.travelId}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteTravel(travelId: String): Result<Void?> {
        return try {
            val travelRef = Collections.travels.document(travelId)
            val snapshot = travelRef.get().await()
            val existingData = snapshot.toObject(TravelFirestoreModel::class.java)
            val message = "Sorry but I deleted the travel"

            if(existingData != null){
                val oldTravelStatus = existingData.status
                travelRef.update("status", "deleted")

                if(oldTravelStatus != "past"){                  //if old status was past we don't need ot update all the requests related to that trip
                    //Updating all the requests to cancelled
                    val requestSnapshot = Collections.requests.whereEqualTo("tripId", existingData.travelId).get().await()
                    val requests = requestSnapshot.documents.mapNotNull { doc ->
                        try {
                            doc.reference.update("accepted", false)
                            doc.reference.update("refused", true)
                            doc.reference.update("responseMessage", message)
                        } catch (e: Exception) {
                            Log.e("deleteTravel", "Error updating request: ${doc.id}", e)
                            null
                        }
                    }
                }
            }
            travelRef.update("status", "deleted")

            println("Travel deleted successfully and related requests canceled.")
            Result.success(null)
        } catch (e: Exception) {
            println("Error deleting travel or updating requests: $e")
            Result.failure(e)
        }
    }


    suspend fun updateParticipantAndStatus(travelIndex: Int, newCompanions: TravelCompanion){
        //TODO
    }


    suspend fun getSearchTravels(search: Search, lastStartDateFirebaseFound:String?): List<Travel> {
        val today = LocalDate.now()

        var query: Query = Collections.travels

        if(search.available) {
            query = query.whereEqualTo("status", AVAILABLE)

            var queryRequest: Query = Collections.requests
            queryRequest = queryRequest.whereEqualTo("authorId", AppState.myProfile.value.userId)
            queryRequest = queryRequest.limit(10)
            val requestSnapshot = queryRequest.get().await()
            val requests = requestSnapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(RequestFirestoreModel::class.java)
                } catch (e: Exception) {
                    Log.e("getRequestsByStatus", "Error converting travel: ${doc.id}", e)
                    null
                }
            }
            val tripIds = requests.map { it.tripId }.distinct()

            if (tripIds.isNotEmpty()) {
                query = query.whereNotIn("travelId", tripIds)
            }

        } else {
            query = query.whereNotEqualTo("creatorId", AppState.myProfile.value.userId)
            query = query.whereIn("status", listOf(FULL, PAST, AVAILABLE)) // all except DELETED
        }

        if (search.startDate != null && !search.startDate.isBefore(today)) {
            val startDate = search.startDate.format(firebaseFormatter)
            query = query.whereGreaterThanOrEqualTo("startDate", startDate)
        } else {
            val startDate = today.format(firebaseFormatter)
            query = query.whereGreaterThanOrEqualTo("startDate", startDate)
        }
        if (search.endDate != null) {
            val endDate = search.endDate.format(firebaseFormatter)
            query = query.whereLessThanOrEqualTo("endDate", endDate)
        }

        /*
        if (search.minPrice != MIN_PRICE) {
            query = query.whereGreaterThanOrEqualTo("minPrice", search.minPrice)
        }
        if (search.maxPrice != MAX_PRICE) {
            query = query.whereLessThanOrEqualTo("maxPrice", search.maxPrice)
        }

        if (search.minCompanions != MIN_COMPANIONS) {
            query = query.whereGreaterThanOrEqualTo("minCompanions", search.minCompanions)
        }
        if (search.maxCompanions != MAX_COMPANIONS) {
            query = query.whereLessThanOrEqualTo("maxCompanions", search.maxCompanions)
        }

        if (search.travelTypes.isNotEmpty()) {
            query = query.whereArrayContainsAny("travelTypes", search.travelTypes)
        }
        */
        query = query.orderBy("startDate", Query.Direction.ASCENDING)
        if(lastStartDateFirebaseFound!=null){
            query = query.startAfter(lastStartDateFirebaseFound)
        }

        if(search.text==""){
            query = query.limit(5)
        } else {
            query = query.limit(10)
        }

        val travelsSnapshot = query.get().await()
        val allTravels = travelsSnapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(TravelFirestoreModel::class.java)?.toAppModel(isTravelLite = true)
            } catch (e: Exception) {
                Log.e("getSearchTravels", "Error converting travel: ${doc.id}", e)
                null
            }
        }

        return allTravels
    }





    suspend fun getOwnedTravels(userId:String?=null): List<Travel> {
        val creatorId = userId ?: AppState.myProfile.value.userId

        var queryFuture: Query = Collections.travels
        queryFuture = queryFuture.whereEqualTo("creatorId", creatorId)
        queryFuture = queryFuture.whereIn("status", listOf<String>(AVAILABLE, FULL))

        queryFuture = queryFuture.orderBy("startDate", Query.Direction.ASCENDING)
        val travelsSnapshotFut = queryFuture.get().await()
        val travelsFuture = travelsSnapshotFut.documents.mapNotNull { doc ->
            try {
                doc.toObject(TravelFirestoreModel::class.java)?.toAppModel(isTravelLite = true, statusForUser=OWNED)
            } catch (e: Exception) {
                Log.e("getOwnedTravels", "Failed to convert doc ${doc.id} with data: ${doc.data}", e)
                null
            }
        }

        var queryPast: Query = Collections.travels
        queryPast = queryPast.whereEqualTo("creatorId", creatorId)
        queryPast = queryPast.whereIn("status", listOf<String>(PAST))

        queryPast = queryPast.orderBy("startDate", Query.Direction.DESCENDING)
        val travelsSnapshotPast = queryPast.get().await()
        val travelsPast = travelsSnapshotPast.documents.mapNotNull { doc ->
            try {
                doc.toObject(TravelFirestoreModel::class.java)?.toAppModel(isTravelLite = true, statusForUser=OWNED)
            } catch (e: Exception) {
                Log.e("getOwnedTravels", "Failed to convert doc ${doc.id} with data: ${doc.data}", e)
                null
            }
        }

        val travels = travelsFuture+ travelsPast
        Log.d("OwnedTravelsDebug", "Fetched ${travels.size} owned travels")
        return travels
    }



    suspend fun getRequestsToMyTrips(): List<Request> {
        var queryTravels: Query = Collections.travels
        queryTravels = queryTravels.whereEqualTo("creatorId", AppState.myProfile.value.userId)
        queryTravels = queryTravels.whereIn("status", listOf<String>(AVAILABLE, FULL))

        queryTravels = queryTravels.orderBy("startDate", Query.Direction.ASCENDING)
        val travelsSnapshot = queryTravels.get().await()
        val travels = travelsSnapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(TravelFirestoreModel::class.java)
            } catch (e: Exception) {
                Log.e("getRequestsToMyTrips", "Error converting travel: ${doc.id}", e)
                null
            }
        }
        val tripIds = travels.map { it.travelId }.distinct()

        var query: Query = Collections.requests
        if (tripIds.isNotEmpty()) {
            query = query.whereIn("tripId", tripIds)
            query = query.whereEqualTo("refused", false)
            query = query.whereEqualTo("accepted", false)
            query = query.orderBy("lastUpdate", Query.Direction.DESCENDING)

        } else {
            return emptyList()
        }
        val requestSnapshot = query.get().await()
        val requests = requestSnapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(RequestFirestoreModel::class.java)?.toAppModel()
            } catch (e: Exception) {
                Log.e("getRequestsToMyTrips", "Error converting travel: ${doc.id}", e)
                null
            }
        }
        Log.d("getRequestsToMyTrips", "Fetched ${requests.size} requests to my trips")
        return requests
    }



    suspend fun getRequestsByStatus(isAcceped: Boolean, isRefused: Boolean, userId:String?=null): List<RequestFirestoreModel> {
        var query: Query = Collections.requests

        val authorId = userId ?: AppState.myProfile.value.userId
        query = query.whereEqualTo("authorId", authorId)
        query = query.whereEqualTo("accepted", isAcceped)
        query = query.whereEqualTo("refused", isRefused)

        val requestSnapshot = query.get().await()
        val requests = requestSnapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(RequestFirestoreModel::class.java)
            } catch (e: Exception) {
                Log.e("getRequestsByStatus", "Error converting travel: ${doc.id}", e)
                null
            }
        }
        Log.d("getRequestsByStatus", "Fetched ${requests.size} owned requests")
        return requests
    }


    suspend fun getPendingTravels(lastStartDateFirebaseFound: String?): List<Travel> {
        val user = AppState.myProfile.value
        Log.d("getPendingTravels", "User ID: ${user.userId}")
        var requestsPending = getRequestsByStatus(false, false)
        if(requestsPending.isEmpty()){
            return emptyList()
        }
        val tripIds = requestsPending.map { it.tripId }.distinct()

        var query: Query = Collections.travels
        query = query.whereIn("travelId", tripIds)
        query = query.orderBy("startDate", Query.Direction.ASCENDING)

        val endDate = LocalDate.now().format(firebaseFormatter)
        query = query.whereGreaterThan("endDate", endDate)

        if(lastStartDateFirebaseFound!=null){
            query = query.startAfter(lastStartDateFirebaseFound)
        }
        query = query.limit(5)
        val travelsSnapshot = query.get().await()

        val travels = travelsSnapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(TravelFirestoreModel::class.java)?.toAppModel(isTravelLite = true, statusForUser = PENDING)
            } catch (e: Exception) {
                Log.e("getPendingTravels", "Error converting travel: ${doc.id}", e)
                null
            }
        }

        Log.d("getPendingTravels", "Fetched ${travels.size} pending travels")
        return travels
    }


    suspend fun getDeniedTravels(lastStartDateFirebaseFound: String?): List<Travel> {
        val user = AppState.myProfile.value
        Log.d("getDeniedTravels", "User ID: ${user.userId}")
        var getDeniedTravels = getRequestsByStatus(false, true)
        if(getDeniedTravels.isEmpty()){
            return emptyList()
        }
        val tripIds = getDeniedTravels.map { it.tripId }.distinct()

        var query: Query = Collections.travels
        query = query.whereIn("travelId", tripIds)
        query = query.orderBy("startDate", Query.Direction.ASCENDING)

        val endDate = LocalDate.now().format(firebaseFormatter)
        query = query.whereGreaterThan("endDate", endDate)

        if(lastStartDateFirebaseFound!=null){
            query = query.startAfter(lastStartDateFirebaseFound)
        }
        query = query.limit(5)
        val travelsSnapshot = query.get().await()

        val travels = travelsSnapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(TravelFirestoreModel::class.java)?.toAppModel(isTravelLite = true, statusForUser = DENIED)
            } catch (e: Exception) {
                Log.e("getDeniedTravels", "Error converting travel: ${doc.id}", e)
                null
            }
        }

        Log.d("getDeniedTravels", "Fetched ${travels.size} pending travels")
        return travels
    }


    suspend fun getJoinedTravels(lastStartDateFirebaseFound:String?): List<Travel> {
        val today = LocalDate.now()
        val user = AppState.myProfile.value
        Log.d("getJoinedTravels", "User ID: ${user.userId}")
        var requestsPending = getRequestsByStatus(true, false)
        if(requestsPending.isEmpty()){
            return emptyList()
        }
        val tripIds = requestsPending.map { it.tripId }.distinct()

        var query: Query = Collections.travels
        query = query.whereIn("travelId", tripIds)

        val todayStr = today.format(firebaseFormatter)
        query = query.whereGreaterThanOrEqualTo("endDate", todayStr)

        query = query.orderBy("startDate", Query.Direction.ASCENDING)
        if(lastStartDateFirebaseFound!=null){
            query = query.startAfter(lastStartDateFirebaseFound)
        }
        query = query.limit(5)
        val travelsSnapshot = query.get().await()

        val travels = travelsSnapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(TravelFirestoreModel::class.java)?.toAppModel(isTravelLite = true, statusForUser = JOINED)
            } catch (e: Exception) {
                Log.e("getJoinedTravels", "Error converting travel: ${doc.id}", e)
                null
            }
        }

        Log.d("getJoinedTravels", "Returning ${travels.size} joined travels")
        return travels
    }


    suspend fun getPastTravels(lastStartDateFirebaseFound:String?, userId: String?=null): List<Travel> {
        val today = LocalDate.now()

        val userCompId = userId ?: AppState.myProfile.value.userId
        Log.d("getPastTravels", "User ID: $userCompId")
        var requestsPending = getRequestsByStatus(true, false, userCompId)
        if(requestsPending.isEmpty()){
            return emptyList()
        }
        val tripIds = requestsPending.map { it.tripId }.distinct()

        var query: Query = Collections.travels
        query = query.whereIn("travelId", tripIds)

        val todayStr = today.format(firebaseFormatter)
        query = query.whereLessThan("endDate", todayStr)

        query = query.orderBy("startDate", Query.Direction.DESCENDING)
        if(lastStartDateFirebaseFound!=null){
            query = query.startAfter(lastStartDateFirebaseFound)
        }
        query = query.limit(5)
        val travelsSnapshot = query.get().await()

        val travels = travelsSnapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(TravelFirestoreModel::class.java)?.toAppModel(isTravelLite = true, statusForUser = PAST)
            } catch (e: Exception) {
                Log.e("getPastTravels", "Error converting travel: ${doc.id}", e)
                null
            }
        }

        Log.d("getPastTravels", "Returning ${travels.size} past travels")
        return travels
    }

    suspend fun getToReviewTravels(lastStartDateFirebaseFound:String?): List<Travel> {
        val today = LocalDate.now()
        val user = AppState.myProfile.value
        Log.d("getToReviewTravels", "User ID: ${user.userId}")
        var requestsPending = getRequestsByStatus(true, false)
        if(requestsPending.isEmpty()){
            return emptyList()
        }
        val tripIds = requestsPending.map { it.tripId }.distinct()

        var query: Query = Collections.travels
        query = query.whereIn("travelId", tripIds)

        val todayStr = today.format(firebaseFormatter)
        query = query.whereLessThan("endDate", todayStr)

        query = query.orderBy("startDate", Query.Direction.DESCENDING)
        if(lastStartDateFirebaseFound!=null){
            query = query.startAfter(lastStartDateFirebaseFound)
        }
        query = query.limit(5)
        val travelsSnapshot = query.get().await()

        val travels = travelsSnapshot.documents.mapNotNull { doc ->
            try {
                doc.toObject(TravelFirestoreModel::class.java)
            } catch (e: Exception) {
                Log.e("getToReviewTravels", "Error converting travel: ${doc.id}", e)
                null
            }
        }

        val result = travels.filter { travel ->
            travel.travelReviews?.none { review -> review.authorId == user.userId } ?: true
        }
        Log.d("getToReviewTravels", "Returning ${result.size} travels to review")
        return result.map { travelFirestore-> travelFirestore.toAppModel(isTravelLite = true, statusForUser = TO_REVIEW) }
    }

    suspend fun getTravelImages(travelId: String): List<TravelImage>? {
        return try {
            val docSnapshot = Collections.travels
                .document(travelId)
                .get()
                .await()

            if (!docSnapshot.exists()) {
                Log.d("getTravelImages", "Travel document not found for ID: $travelId")
                return null
            }

            val urls = docSnapshot.get("travelImages") as? List<String>

            if (urls.isNullOrEmpty()) {
                Log.d("getTravelImages", "No TravelImages found for travel ID: $travelId")
                return null
            }
            imageListToAppModelTravel(urls)
        } catch (e: Exception) {
            Log.e("getTravelImages", "Error fetching images for travel ID: $travelId", e)
            null
        }
    }

}


class TheRequestModel() {

    suspend fun manageRequest(request: Request, isAcceped: Boolean): List<String> {
        val requestIds=mutableListOf<String>(request.id)
        val batch = Firebase.firestore.batch()

        request.lastUpdate = LocalDate.now()
        val reqFirestore = request.toFirestoreModel()
        val reqRef = Collections.requests.document(request.id)
        batch.update(reqRef, "responseMessage", reqFirestore.responseMessage)
        batch.update(reqRef, "lastUpdate", reqFirestore.lastUpdate)

        if(isAcceped) {
            batch.update(reqRef, "accepted", true)
            val travelRef = Collections.travels.document(reqFirestore.tripId)
            val comp = TravelCompanionFirestoreModel(reqFirestore.authorId, request.spots - 1)
            batch.update(travelRef, "travelCompanions", FieldValue.arrayUnion(comp))

            var chatMessageFirebase = ChatMessage(
                system_light,
                LocalDateTime.now(),
                getSystemMessageJoined(request.author)
            ).toFirestoreModel()
            batch.update(travelRef, "travelChat", FieldValue.arrayUnion(chatMessageFirebase))

            val travelDocSnap = Collections.travels.document(reqFirestore.tripId).get().await()
            if (travelDocSnap.exists()) {
                val firestoreModel = travelDocSnap.toObject(TravelFirestoreModel::class.java)
                var numJoined = 0
                firestoreModel!!.travelCompanions!!.forEach { c ->
                    numJoined = numJoined + 1 + c.extras
                }
                val spotsLeft = firestoreModel!!.maxPeople?.minus(numJoined)
                if(spotsLeft!=null && spotsLeft>=0){
                    var query: Query = Collections.requests
                    query = query.whereEqualTo("tripId", reqFirestore.tripId)
                    query = query.whereEqualTo("refused", false)
                    query = query.whereEqualTo("accepted", false)
                    query = query.whereGreaterThan("spots", spotsLeft)

                    val requestSnapshots = query.get().await()


                    for (document in requestSnapshots.documents) {
                        val docId = document.id
                        if(docId==request.id) continue;
                        requestIds.add(docId)

                        val reqRef = document.reference
                        batch.update(reqRef, "refused", true)
                        batch.update(reqRef, "responseMessage", "The travel no longer has enough spots for your request")
                        batch.update(reqRef, "lastUpdate", reqFirestore.lastUpdate)
                    }

                }
            }
        } else {
            batch.update(reqRef, "refused", true)
        }

        batch.commit().await()
        return requestIds
    }


    suspend fun deleteRequest(travelId: String) {
        val user : User = AppState.myProfile.value
        val userId = user.userId

        var query: Query = Collections.requests
        query = query.whereEqualTo("authorId", userId)
        query = query.whereEqualTo("tripId", travelId)
        val requestSnapshot = query.get().await()

        for (doc in requestSnapshot.documents) {
            doc.reference.delete().await()
        }
        println("Successfully deleted ${requestSnapshot.size()} request(s).")
    }

    suspend fun leaveTrip(travelId: String){
        deleteRequest(travelId)
        val user : User = AppState.myProfile.value
        val userId = user.userId

        val travelRef = Collections.travels.document(travelId)
        val snapshot = travelRef.get().await()

        val travelCompanionsRaw = snapshot.get("travelCompanions") as? List<Map<String, Any>> ?: emptyList()

        val travelCompanions = travelCompanionsRaw.mapNotNull { map ->
            try {
                // Convert each map to a JSON string and then deserialize
                val json = Gson().toJson(map)
                Gson().fromJson(json, TravelCompanionFirestoreModel::class.java)
            } catch (e: Exception) {
                Log.e("leaveTrip", "Error converting travelCompanion", e)
                null
            }
        }

        val travelCompanionsUpdated = travelCompanions.filter { it.userId!=userId }
        travelRef.update("travelCompanions", travelCompanionsUpdated).await()

        var chatMessageFirebase = ChatMessageFirestoreModel(
            "system",
            LocalDateTime.now().format(firebaseChatFormatter),
            getSystemMessageLeft(user)
        )
        travelRef.update("travelChat",
            FieldValue.arrayUnion(chatMessageFirebase)
        ).await()

    }

}