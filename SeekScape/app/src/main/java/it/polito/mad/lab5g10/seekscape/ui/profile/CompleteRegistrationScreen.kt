package it.polito.mad.lab5g10.seekscape.ui.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.messaging.FirebaseMessaging
import it.polito.mad.lab5g10.seekscape.firebase.TheUserModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.ProfileViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.UserInfoViewModel
import it.polito.mad.lab5g10.seekscape.ui._common.Calendar
import it.polito.mad.lab5g10.seekscape.ui._common.components.AddLocation
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@Composable
fun CompleteRegistrationScreen(navCont: NavHostController) {
    var birthDate by remember { mutableStateOf(LocalDate.parse("2000-12-12")) }
    val showLocationScreen = remember { mutableStateOf(false) }
    var birthdateError by remember { mutableStateOf("") }
    var nationalityError by remember { mutableStateOf("") }
    var cityError by remember { mutableStateOf("") }
    var languageError by remember { mutableStateOf("") }
    val user = AppState.myProfile.collectAsState().value
    val userModel = TheUserModel()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Launched effect to reset errors when the screen is recomposed
    LaunchedEffect(user) {
        birthdateError = ""
        nationalityError = ""
        cityError = ""
        languageError = ""
    }

    val vm: UserInfoViewModel =
        viewModel(factory = ProfileViewModelFactory(user, true))

    // Launched Effect to reset errors when the user types in the fields
    LaunchedEffect(vm.nationalityValue.collectAsState().value, vm.cityValue.collectAsState().value, vm.languageValue.collectAsState().value) {
        if (nationalityError.isNotEmpty() && vm.nationalityValue.value.isNotEmpty()) {
            nationalityError = ""
        }
        if (cityError.isNotEmpty() && vm.cityValue.value.isNotEmpty()) {
            cityError = ""
        }
        if (languageError.isNotEmpty() && vm.languageValue.value.isNotEmpty()) {
            languageError = ""
        }
    }

    if (showLocationScreen.value) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            AddLocation(
                onCancel = { showLocationScreen.value = false },
                onLocationSelected = { location ->
                    vm.addLocation(location.name)
                    showLocationScreen.value = false
                },
            )
        }
    } else {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                Text(
                    text = "Some more information about you",
                    style = MaterialTheme.typography.headlineSmall
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Birth Date",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Calendar(
                        "",
                        birthDate,
                        null,
                        modifier = Modifier.fillMaxWidth(),
                        true,
                        birthdateError

                    ) {
                        birthDate = it
                        val today = LocalDate.now()
                        val age = Period.between(birthDate, today).years
                        vm.setAge(age.toString())
                        val minimumBirthDate = today.minusYears(16)
                        if (birthDate.isBefore(minimumBirthDate)) {
                            birthdateError = ""
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Nationality",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = vm.nationalityValue.collectAsState().value,
                        onValueChange = { vm.setNationality(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter your nationality...", fontSize = 16.sp)
                        },
                        isError = nationalityError.isNotEmpty(),
                        supportingText = {
                            if (nationalityError.isNotEmpty()) {
                                Text(nationalityError, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    Text(
                        text = "City",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = vm.cityValue.collectAsState().value,
                        onValueChange = { vm.setCity(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter your city...", fontSize = 16.sp)
                        },
                        isError = cityError.isNotEmpty(),
                        supportingText = {
                            if (cityError.isNotEmpty()) {
                                Text(cityError, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    Text(
                        text = "Language",
                        style = MaterialTheme.typography.titleMedium
                    )
                    OutlinedTextField(
                        value = vm.languageValue.collectAsState().value,
                        onValueChange = { vm.setLanguage(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Enter your language...", fontSize = 16.sp)
                        },
                        isError = languageError.isNotEmpty(),
                        supportingText = {
                            if (languageError.isNotEmpty()) {
                                Text(languageError, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                }

                Column(modifier = Modifier.offset(x = (-16).dp)) {
                    EditableUserPersonality(vm)
                    EditableUserDestinations(vm, showLocationScreen)
                }

                Button(
                    onClick = {
                        var isError = false
                        val today = LocalDate.now()
                        val minimumBirthDate = today.minusYears(16)
                        if (birthDate.isAfter(minimumBirthDate)) {
                            birthdateError = "You must be at least 16 years old"
                            isError = true
                        }
                        println("city ${vm.cityValue.value.isBlank()}")
                        if (vm.nationalityValue.value.isBlank()){
                            nationalityError = "Nationality cannot be empty"
                            isError = true
                        }
                        if (vm.cityValue.value.isBlank()) {
                            cityError = "City cannot be empty"
                            isError = true
                        }
                        if (vm.languageValue.value.isBlank()) {
                            languageError = "Language cannot be empty"
                            isError = true
                        }
                        if (isError){
                            return@Button
                        }
                        coroutineScope.launch {
                            try {
                                val age = Period.between(birthDate, today).years
                                user.age = age
                                user.nationality = vm.nationalityValue.value
                                user.city = vm.cityValue.value
                                user.language = vm.languageValue.value
                                user.personality = vm.personality.value
                                user.desiredDestinations = vm.desiredDestinations.value
                                userModel.addNewUser(user, birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                                val token = FirebaseMessaging.getInstance().token.await()
                                userModel.addTokenUserByAuthId(user.authUID!!, token)
                                navCont.popBackStack(
                                    route = navCont.graph.startDestinationRoute ?: return@launch,
                                    inclusive = false
                                )
                                AppState.updateCurrentTab(MainDestinations.HOME_ROUTE)

                            } catch (e: Exception){
                                Toast.makeText(
                                    context,
                                    "Error completing registration, please try again later",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    },
                    enabled = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Complete", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}