package it.polito.mad.lab5g10.seekscape.ui.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.firebase.TheUserModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.services.AccountService
import it.polito.mad.lab5g10.seekscape.ui._common.phonePrefixes
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAccountScreen(navCont: NavHostController, isGoogleAccount: Boolean) {

    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var isEmailSent by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedPrefix by remember { mutableStateOf("+39") }
    var showPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }


    val userId = AppState.myProfile.collectAsState().value.userId
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val accountService = AccountService()
    val userModel = TheUserModel()
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Manage your account",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (!isGoogleAccount) {
                Text(
                    text = "Update email",
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Email", color = MaterialTheme.colorScheme.primary)
                    },
                    isError = emailError.isNotEmpty(),
                    supportingText = {
                        if (emailError.isNotEmpty()) {
                            Text(emailError, color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
                Button(
                    enabled = !isLoading.value,
                    onClick = {
                        var isError = false
                        val emailTrimmed = email.trim()
                        if (emailTrimmed.matches(
                                Regex(
                                    "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
                                )
                            ).not()
                        ) {
                            emailError = "Please enter a valid email address."
                            isError = true
                        }
                        if (isError) {
                            return@Button
                        }
                        isLoading.value = true
                        coroutineScope.launch {
                            try {
                                isEmailSent = false
                                accountService.updateEmail(email)
                                isEmailSent = true
                            } catch (e: Exception) {
                                Log.e("EditAccountScreen", "Error updating email: ${e.message}")
                                val errorMessage = "Failed to update email, please try again."
                                Toast.makeText(
                                    context,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                isLoading.value = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Update Email", style = MaterialTheme.typography.titleMedium)
                }
                if (isEmailSent) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Request sent! Please check your email for a verification link and follow the instructions to complete the email update.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(Modifier.height(30.dp))
            }
            Text(
                text = "Update phone number",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
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
                            onValueChange = { selectedPrefix = it },
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
                                .width(100.dp)
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
            Button(
                enabled = !isLoading.value,
                onClick = {
                    var isError = false
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
                    isLoading.value = true
                    coroutineScope.launch {
                        try {
                            val completeNumber = selectedPrefix + phoneNumber
                            userModel.updatePhoneNumber(userId, completeNumber)
                            Toast.makeText(
                                context,
                                "Phone number updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            val errorMessage = "Failed to update phone number, please try again."
                            Toast.makeText(
                                context,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            isLoading.value = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Update Phone Number", style = MaterialTheme.typography.titleMedium)
            }
            if (!isGoogleAccount) {

                Spacer(Modifier.height(30.dp))
                Text(
                    text = "Update password",
                    style = MaterialTheme.typography.titleMedium,
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Password", color = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordError.isNotEmpty(),
                    visualTransformation = if (showPassword) {
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
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = {
                        Text(
                            text = "New Password",
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = newPasswordError.isNotEmpty(),
                    visualTransformation = if (showNewPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    // Eye icon to toggle password visibility
                    trailingIcon = {
                        Icon(
                            imageVector = if (showNewPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "Toggle password visibility",
                            modifier = Modifier
                                .clickable { showNewPassword = !showNewPassword }
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    supportingText = {
                        if (newPasswordError.isNotEmpty()) {
                            Text(
                                text = newPasswordError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = {
                        Text(
                            text = "Confirm New Password",
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPasswordError.isNotEmpty(),
                    visualTransformation = if (showConfirmPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    // Eye icon to toggle password visibility
                    trailingIcon = {
                        Icon(
                            imageVector = if (showConfirmPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "Toggle password visibility",
                            modifier = Modifier
                                .clickable { showConfirmPassword = !showConfirmPassword }
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    supportingText = {
                        if (confirmPasswordError.isNotEmpty()) {
                            Text(
                                text = confirmPasswordError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                )
                Button(
                    enabled = !isLoading.value,
                    onClick = {
                        var isError = false
                        if (password.length < 6) {
                            passwordError = "Password must be at least 6 characters long."
                            isError = true
                        }
                        if (newPassword != confirmPassword) {
                            confirmPasswordError = "New password and confirmation do not match."
                            isError = true
                        }
                        if (newPassword.length < 6) {
                            newPasswordError = "New password must be at least 6 characters long."
                            isError = true
                        }
                        if (confirmPassword.length < 6) {
                            confirmPasswordError =
                                "Confirm password must be at least 6 characters long."
                            isError = true
                        }
                        if (password == newPassword && newPassword == confirmPassword) {
                            confirmPasswordError =
                                "New password must be different from the current password."
                            isError = true
                        }
                        if (isError) {
                            return@Button
                        }
                        isLoading.value = true
                        coroutineScope.launch {
                            try {
                                accountService.updatePassword(newPassword)
                                AppState.updateCurrentTab(MainDestinations.HOME_ROUTE)
                                AppState.setUserAsUnlogged()
                            } catch (e: Exception) {
                                val errorMessage = "Failed to update password, please try again."
                                Toast.makeText(
                                    context,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                isLoading.value = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Update Password", style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        if(isLoading.value){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                    .zIndex(1f)
                    .clickable(onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

}