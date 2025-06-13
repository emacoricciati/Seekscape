package it.polito.mad.lab5g10.seekscape.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.navigation.NavHostController
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.messaging.FirebaseMessaging
import it.polito.mad.lab5g10.seekscape.authentication.GoogleButton
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.TheUserModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.services.AccountService
import it.polito.mad.lab5g10.seekscape.services.SignInResult
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

suspend fun onSignInWithGoogle(credential: Credential): SignInResult {
    val accountService = AccountService()
    if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        return accountService.signInWithGoogle(googleIdTokenCredential.idToken)
    } else {
        throw IllegalArgumentException("Invalid credential type: ${credential.type}")
    }
}

@Composable
fun LoginScreen(navHostController: NavHostController) {

    var email by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val actions = remember(navHostController) { Actions(navHostController) }
    val accountService = AccountService()
    val context = LocalContext.current
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    val userModel = TheUserModel()

    // Launched effect to reset errors when the screen is displayed
    LaunchedEffect(Unit) {
        emailError = ""
        passwordError = ""
    }

    // Launched effect to clear errors when the user starts typing
    LaunchedEffect(email, password) {
        if (emailError.isNotEmpty() && email.isNotEmpty()) emailError = ""
        if (passwordError.isNotEmpty() && password.isNotEmpty()) passwordError = ""
    }
    val scrollState = rememberScrollState()

    Column(modifier = Modifier
        .verticalScroll(scrollState)
        .padding(horizontal = 20.dp, vertical = 55.dp)) {

        Text(text = "Sign in to your account", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(15.dp))
        Text(
            text = "Enter your email and password to log in",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Email",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            isError = emailError.isNotEmpty(),
            supportingText = {
                if (emailError.isNotEmpty()) {
                    Text(
                        text = emailError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password", color = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            isError = passwordError.isNotEmpty(),
            supportingText = {
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            // Eye icon to toggle password visibility
            trailingIcon = {
                Icon(
                    imageVector = if (showPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = "Toggle password visibility",
                    modifier = Modifier
                        .clickable { showPassword = !showPassword }
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
        )
        // Login button
        Spacer(Modifier.height(30.dp))
        Button(
            onClick = {
                var isError = false
                val emailTrimmed = email.trim()
                val passwordTrimmed = password.trim()
                if (emailTrimmed.matches(
                        Regex(
                            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
                        )
                    ).not()
                ) {
                    emailError = "Please enter a valid email address."
                    isError = true
                }
                if (passwordTrimmed.length < 6) {
                    passwordError = "Password must be at least 6 characters long."
                    isError = true
                }
                if (isError) {
                    return@Button
                }
                coroutineScope.launch {
                    try {
                        val user = accountService.signIn(emailTrimmed, passwordTrimmed)

                        val token = FirebaseMessaging.getInstance().token.await()
                        userModel.addTokenUserById(user.userId, token)

                        AppState.setUserAsLogged()
                        AppState.updateMyProfile(user)

                        AppState.updateCurrentTab(MainDestinations.HOME_ROUTE)
                    } catch (e: Exception) {
                        val errorMessage = when {
                            e.message?.contains(
                                "auth credential is incorrect",
                                ignoreCase = true
                            ) == true -> {
                                "Email or password is incorrect."
                            }

                            e.message?.contains("has expired", ignoreCase = true) == true -> {
                                "Session expired. Try again."
                            }

                            else -> {
                                "Login failed. Please check your credentials."
                            }
                        }
                        Toast.makeText(
                            context,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Log In", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(30.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outline,
                thickness = 1.dp
            )
            Text(
                text = "Or",
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            Divider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outline,
                thickness = 1.dp
            )
        }
        Spacer(Modifier.height(30.dp))
        GoogleButton { cred ->
            coroutineScope.launch {
                try {
                    val result = onSignInWithGoogle(cred)

                    val token = FirebaseMessaging.getInstance().token.await()
                    userModel.addTokenUserById(result.user.userId, token)

                    AppState.setUserAsLogged()
                    AppState.updateMyProfile(result.user)

                    if (!result.isNew) {
                        Log.d("UnloggedUserScreen", "Existing user signed in, navigating to home")
                        AppState.updateCurrentTab(MainDestinations.HOME_ROUTE)
                    } else {
                        Log.d(
                            "UnloggedUserScreen",
                            "New user signed in, navigating to profile setup"
                        )
                        val start = navHostController.graph.startDestinationRoute
                        if (start != null) {
                            navHostController.popBackStack(start, inclusive = false)
                        }
                        actions.navigateTo("complete_registration")
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Google sign-in failed. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.fillMaxWidth().padding(top=10.dp), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Don't have an account? Sign up",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable {
                        actions.navigateTo("signup")
                    },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}