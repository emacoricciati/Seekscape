package it.polito.mad.lab5g10.seekscape.ui.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.getBlankUser
import it.polito.mad.lab5g10.seekscape.services.AccountService
import it.polito.mad.lab5g10.seekscape.ui._common.phonePrefixes
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(navHostController: NavHostController) {

    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedPrefix by remember { mutableStateOf(phonePrefixes.first()) } // Default prefix
    var phoneNumber by remember { mutableStateOf("") }
    val accountService = AccountService()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val actions = remember(navHostController) { Actions(navHostController) }
    var nicknameError by remember { mutableStateOf("") }
    var fullNameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    LaunchedEffect (Unit) {
        // Reset errors when the screen is recomposed
        nicknameError = ""
        fullNameError = ""
        emailError = ""
        phoneNumberError = ""
        passwordError = ""
    }

    // Launched effect to delete errors while typing
    LaunchedEffect(nickname, fullName, email, phoneNumber, password) {
        if (nicknameError.isNotEmpty() && nickname.isNotEmpty()) {
            nicknameError = ""
        }
        if (fullNameError.isNotEmpty() && fullName.isNotEmpty()) {
            fullNameError = ""
        }
        if (emailError.isNotEmpty() && email.isNotEmpty()) {
            emailError = ""
        }
        if (phoneNumberError.isNotEmpty() && phoneNumber.isNotEmpty()) {
            phoneNumberError = ""
        }
        if (passwordError.isNotEmpty() && password.isNotEmpty()) {
            passwordError = ""
        }
    }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 55.dp)
    ) {
        // Arrow back
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { actions.navigateBack() },
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(15.dp))
        Text(text = "Sign up", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(15.dp))
        Text(
            text = "Create an account to continue!",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Nickname",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            isError = nicknameError.isNotEmpty(),
            supportingText = {
                if (nicknameError.isNotEmpty()) {
                    Text(
                        text = nicknameError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(
                    text = "Full Name",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            isError = fullNameError.isNotEmpty(),
            supportingText = {
                if (fullNameError.isNotEmpty()) {
                    Text(
                        text = fullNameError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
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
            }
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {phoneNumber = it},
            modifier = Modifier.fillMaxWidth(),
            isError = phoneNumberError.isNotEmpty(),
            supportingText = {
                if (phoneNumberError.isNotEmpty()) {
                    Text(
                        text = phoneNumberError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            label = {
                Text(
                    text = "Phone Number",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone
            ),
            leadingIcon = {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },

                ) {
                    OutlinedTextField(
                        value = selectedPrefix,
                        onValueChange = {selectedPrefix = it},
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .width(
                                100.dp
                            )
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        phonePrefixes.forEach { prefix ->
                            DropdownMenuItem(
                                text = { Text(prefix) },
                                onClick = {
                                    selectedPrefix = prefix
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password", color = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError.isNotEmpty(),
            visualTransformation =  if (showPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
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
            supportingText = {
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                var isError = false
                if (email.matches(
                        Regex(
                            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
                        )
                    ).not()) {
                    emailError = "Invalid email format"
                    isError = true
                }
                if (password.length < 6) {
                    passwordError = "Password must be at least 6 characters long"
                    isError = true
                }
                if (nickname.isBlank()) {
                    nicknameError = "Nickname cannot be empty"
                    isError = true
                }
                if (fullName.isBlank()) {
                    fullNameError = "Full name cannot be empty"
                    isError = true
                }
                if (phoneNumber.isBlank()) {
                    phoneNumberError = "Phone number cannot be empty"
                    isError = true
                } else if (!phoneNumber.matches(Regex("\\d+"))) {
                    phoneNumberError = "Phone number must contain only digits"
                    isError = true
                }
                if (isError) {
                    return@Button
                }
                coroutineScope.launch {
                    try {
                        val authId = accountService.createAccount(email, password, nickname)
                        val newUser = getBlankUser()
                        newUser.authUID = authId
                        newUser.email = email
                        newUser.nickname = nickname
                        newUser.name = fullName.split(" ").getOrNull(0) ?: ""
                        newUser.surname = fullName.split(" ").getOrNull(1) ?: ""
                        newUser.phoneNumber = selectedPrefix + phoneNumber
                        AppState.updateMyProfile(newUser)
                        AppState.setUserAsLogged()
                        actions.navigateTo("complete_registration")

                    } catch (e: Exception) {
                        val errorMessage = e.message ?: "An error occurred"
                        println("email: $email, password: $password")
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
            Text("Register", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.weight(1f))
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account? Login",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable {
                        actions.navigateTo("login")
                    },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}