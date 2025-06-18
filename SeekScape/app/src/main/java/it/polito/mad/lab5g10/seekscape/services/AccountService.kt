package it.polito.mad.lab5g10.seekscape.services

import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.dynamiclinks.DynamicLink
import it.polito.mad.lab5g10.seekscape.firebase.TheUserModel
import it.polito.mad.lab5g10.seekscape.models.ProfilePic
import it.polito.mad.lab5g10.seekscape.models.User
import it.polito.mad.lab5g10.seekscape.models.getBlankUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import com.google.firebase.dynamiclinks.dynamicLinks
import it.polito.mad.lab5g10.seekscape.models.AppState

data class SignInResult(
    val user: User,
    val isNew: Boolean
)


class AccountService {

    private val theUserModel = TheUserModel()

    fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun isGoogleAccount(): Boolean {
        val user = Firebase.auth.currentUser ?: return false
        return user.providerData.any { it.providerId == "google.com" }
    }

    suspend fun getUserProfile(): User? {
        return Firebase.auth.currentUser.toAppUser()
    }

    suspend fun createAccount(email: String, password: String, displayName: String): String {
        val authId = Firebase.auth.createUserWithEmailAndPassword(email, password).await().user?.uid
            ?: throw Exception("Account creation failed")
        Firebase.auth.currentUser!!.updateProfile(userProfileChangeRequest {
            this.displayName = displayName
        }).await()
        return authId
    }

    suspend fun signIn(email: String, password: String): User {
        val fbUser = Firebase.auth.signInWithEmailAndPassword(email, password).await().user
            ?: throw Exception("Sign in failed")
        return theUserModel.getUserByAuthId(fbUser.uid) ?: throw Exception("User not found")
    }

    suspend fun updateEmail(newEmail: String) {
        val id = AppState.myProfile.value.userId
        val dynamicLink = Firebase.dynamicLinks.createDynamicLink()
            .setLink("https://seekscapeapp.page.link/profile/reset_email_completed?uid=$id&email=$newEmail".toUri())
            .setDomainUriPrefix("https://seekscapeapp.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("it.polito.mad.lab5g10.seekscape").build()
            )
            .buildDynamicLink()
        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl(dynamicLink.uri.toString())
            .setAndroidPackageName(
                "it.polito.mad.lab5g10.seekscape",
                true,  // installIfNotAvailable
                null   // minimumVersion
            )
            .setHandleCodeInApp(true)
            .setDynamicLinkDomain("seekscapeapp.page.link")
            .build()
        Firebase.auth.currentUser!!.verifyBeforeUpdateEmail(newEmail, actionCodeSettings).await()
    }

    suspend fun updatePassword(newPassword: String) {
        Firebase.auth.currentUser!!.updatePassword(newPassword).await()
        // Re-authentication is required for some operations, so we might need to sign in again
        Firebase.auth.signOut()
    }

    suspend fun signInWithGoogle(idToken: String): SignInResult {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val fbUser = Firebase.auth.signInWithCredential(firebaseCredential).await().user
            ?: throw Exception("Sign in with Google failed")
        var myUser =  theUserModel.getUserByAuthId(fbUser.uid)
        val isNewUser = myUser == null
        if (myUser == null) {
            // If the user does not exist, create a new user
            val newUser = getBlankUser()
            newUser.authUID = fbUser.uid
            val parts = fbUser.displayName?.split(" ")
            newUser.name = parts?.getOrNull(0) ?: ""
            newUser.surname = parts?.getOrNull(1) ?: ""
            newUser.nickname = "${newUser.name.lowercase()}${newUser.surname.lowercase()}"
            newUser.email = fbUser.email ?: ""
            newUser.phoneNumber = fbUser.phoneNumber ?: ""
            newUser.profilePic = if (fbUser.photoUrl != null) ProfilePic.Url(fbUser.photoUrl.toString()) else null
            myUser = newUser
        }
        return SignInResult(myUser, isNewUser)
    }


    suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            Firebase.auth.signOut()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun deleteAccount() {
        Firebase.auth.currentUser!!.delete().await()
    }

    private suspend fun FirebaseUser?.toAppUser(): User? {
        val blankUser = getBlankUser()
        if (this?.uid == null) {
            return null
        }
        val user = theUserModel.getUserByAuthId(this.uid)
        return user
    }
}