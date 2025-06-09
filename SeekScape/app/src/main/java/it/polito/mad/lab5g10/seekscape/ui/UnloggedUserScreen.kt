package it.polito.mad.lab5g10.seekscape.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.services.AccountService
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import kotlinx.coroutines.launch

//suspend fun onSignInWithGoogle(credential: Credential): Boolean? {
//    val accountService = AccountService()
//    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
//        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
//        return accountService.signInWithGoogle(googleIdTokenCredential.idToken)
//    } else {
//        Log.e("Error", "Unexpected credential type: ${credential.type}")
//    }
//    return null
//}

@Composable
fun UnloggedUserScreen(navHostController: NavHostController){
    val coroutineScope = rememberCoroutineScope()
    val actions = remember (navHostController){ Actions(navHostController) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "You're not signed in. Sign in to unlock all features.",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Jetpack compose Button to navigate to login screen
        Button(
            onClick = {
                AppState.updateCurrentTab(MainDestinations.PROFILE_ROUTE)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Log In", style = MaterialTheme.typography.titleMedium)
        }
        // Button Login with Google
//        AuthenticationButton{ cred ->  coroutineScope.launch {
//            val isNewUser = onSignInWithGoogle(cred)
//            if (isNewUser != null){
//                AppState.setUserAsLogged()
//            if (isNewUser){
//                Log.d("UnloggedUserScreen", "New user signed in, navigating to profile setup")
//                val start = navHostController.graph.startDestinationRoute
//                if (start != null) {
//                    navHostController.popBackStack(start, inclusive = false)
//                }
//                actions.navigateTo("complete_registration")
//            }
//            else {
//                Log.d("UnloggedUserScreen", "Existing user signed in, navigating to home")
//                AppState.updateCurrentTab(MainDestinations.HOME_ROUTE)
//            }
//        }
//        }
        }
    }