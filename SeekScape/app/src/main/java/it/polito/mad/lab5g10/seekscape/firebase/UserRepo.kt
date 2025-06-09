package it.polito.mad.lab5g10.seekscape.firebase

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import it.polito.mad.lab5g10.seekscape.models.ProfilePic
import it.polito.mad.lab5g10.seekscape.models.User
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Field

class TheUserModel() {

    suspend fun getMyProfile(userId: String): User? {
        val docSnapshot = Collections.users.document(userId).get().await()
        if(docSnapshot.exists()){
            val userFirebase = docSnapshot.toObject(UserFirestoreModel::class.java)
            return userFirebase?.toAppModel(isMyProfile = true)
        }
        return null
    }

    suspend fun getUserByAuthId(authId: String): User? {
        val querySnapshot = Collections.users.whereEqualTo("authUID", authId).get().await()
        if (querySnapshot.isEmpty) {
            return null
        }
        val userFirebase = querySnapshot.documents.first().toObject(UserFirestoreModel::class.java)
        return userFirebase?.toAppModel(isMyProfile = false)
    }

    suspend fun addTokenUserById(id: String, token: String) {
        val docRef = Collections.users.document(id)

        val userFirebaseSnapshot = docRef.get().await()

        if(!userFirebaseSnapshot.exists()){
            return
        }

        docRef.update("fcmTokens",
                FieldValue.arrayUnion(token)
        ).await()
    }

    suspend fun addTokenUserByAuthId(authId: String, token: String?) {
        val querySnapshot = Collections.users.whereEqualTo("authUID", authId).get().await()

        if (querySnapshot.isEmpty) {
            return
        }

        val userFirebaseSnapshot = querySnapshot.documents.first()

        val docRef = userFirebaseSnapshot.reference

        docRef.update("fcmTokens",
            FieldValue.arrayUnion(token)
        ).await()
    }

    suspend fun addNewUser(user: User, birthdate: String = ""): Result<Void?> {
        return try {
            val newId = Firebase.firestore.collection("users").document().id
            user.userId = newId
            val userFirebase = user.toFirestoreModel()
            userFirebase.birthDay = birthdate
            Collections.users.document(userFirebase.userId)
                .set(userFirebase)
                .await()

            println("User with ID ${user.userId} successfully added (suspend)!")
            println(user)
            Result.success(null)
        } catch (e: Exception) {
            println("Error adding user with ID ${user.userId} (suspend): $e")
            Result.failure(e)
        }
    }

    suspend fun updatePhoneNumber(userId: String, phoneNumber: String): Result<Void?> {
        return try {
            Collections.users.document(userId)
                .update("phoneNumber", phoneNumber).await()
            println("Phone number for user with ID $userId successfully updated (suspend)!")
            Result.success(null)
        } catch (e: Exception) {
            println("Error updating phone number for user with ID $userId (suspend): $e")
            Result.failure(e)
        }
    }

    suspend fun updateEmail(userId: String, email: String): Result<Void?> {
        return try {
            Collections.users.document(userId)
                .update("email", email).await()
            println("Email for user with ID $userId successfully updated (suspend)!")
            Result.success(null)
        } catch (e: Exception) {
            println("Error updating email for user with ID $userId (suspend): $e")
            Result.failure(e)
        }
    }

    suspend fun updateMyProfile(user: User) {
        try {
            val userFirebase: UserFirestoreModel = user.toFirestoreModel()

            val updateMap = mutableMapOf<String, Any?>(
                "nickname" to userFirebase.nickname,
                "bio" to userFirebase.bio,
                "travelPreferences" to userFirebase.travelPreferences,
                "desiredDestinations" to userFirebase.desiredDestinations,
                "nationality" to userFirebase.nationality,
                "city" to userFirebase.city,
                "language" to userFirebase.language,
                "personality" to userFirebase.personality,
            ).apply {
                if (!CommonModel.isFirebaseOrGoogleImageUrl(userFirebase.profilePic) &&
                    user.profilePic is ProfilePic.Url
                ) {
                    val firebaseUrl = CommonModel.uploadImageToFirebase(
                        (user.profilePic as ProfilePic.Url).value.toUri(), "profile"
                    )
                    put("profilePic", firebaseUrl)
                }
            }

            Collections.users.document(userFirebase.userId)
                .update(updateMap).await()

            println("User with ID ${user.userId} successfully updated (suspend)!")
        } catch (e: Exception) {
            println("Error updating user with ID ${user.userId} (suspend): $e")
        }
    }

}
