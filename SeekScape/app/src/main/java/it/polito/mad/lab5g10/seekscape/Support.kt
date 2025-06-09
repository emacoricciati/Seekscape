package it.polito.mad.lab5g10.seekscape

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.TheActivityModel
import it.polito.mad.lab5g10.seekscape.firebase.TheItineraryModel
import it.polito.mad.lab5g10.seekscape.firebase.TheRequestModel
import it.polito.mad.lab5g10.seekscape.firebase.TheTravelModel
import it.polito.mad.lab5g10.seekscape.firebase.TheUserModel
import it.polito.mad.lab5g10.seekscape.firebase.TheReviewModel
import it.polito.mad.lab5g10.seekscape.firebase.travel2update
import it.polito.mad.lab5g10.seekscape.models.User
import kotlinx.coroutines.launch

data class ButtonInfo(val text: String, val onClick: () -> Unit)


@Composable
fun Support() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val theUserModel = remember { TheUserModel() }
    val TheReviewModel = remember { TheReviewModel() }
    val theTravelModel = remember { TheTravelModel() }
    val theItineraryModel = remember { TheItineraryModel() }
    val theActivityModel = remember { TheActivityModel() }
    val theRequestModel = remember { TheRequestModel() }

    val resetDB = ButtonInfo("RESET DB") {
        scope.launch {
            val done = CommonModel.resetDB(context)
            done.onSuccess {
                feedbackButton(context, "DONE")
            }.onFailure { exception ->
                feedbackButton(context, "FAILED", exception = exception)
            }
        }
    }

    val insertRequestDB = ButtonInfo("ADD REQUESTS") {
        scope.launch {
            val done = CommonModel.InsertRequestsDB()
            done.onSuccess {
                feedbackButton(context, "DONE")
            }.onFailure { exception ->
                feedbackButton(context, "FAILED", exception = exception)
            }
        }
    }

    val insertUser = ButtonInfo("INSERT USER") {
        val user = User(
            userId = "2",
            nickname = "olivia",
            name = "Olivia",
            surname = "Bennett",
            phoneNumber = "N/A",
            email = "olivia.bennett@example.com",
            bio = "I’m a regular traveler who enjoys nature and peaceful destinations. I like to travel independently and live like a local with a focus on eco-travel and self-discovery.",
            travelPreferences = mutableListOf("Eco-friendly accommodations", "Access to nature", "Affordable and sustainable travel options"),
            desiredDestinations = mutableListOf("Canada", "Nature destinations"),
            age = 19,
            nationality = "Canadian",
            city = "Toronto, Canada",
            language = "English",
            numTravels = 5,
            personality = listOf("Introvert", "Nature-lover", "Budget-concious")
        )

        scope.launch {
            val done = theUserModel.addNewUser(user)
            done.onSuccess {
                feedbackButton(context, "DONE")
            }.onFailure { exception ->
                feedbackButton(context, "FAILED", exception = exception)
            }
        }
    }
    val getTravelByID = ButtonInfo("get travel by id") {
        scope.launch {
            val done = CommonModel.getTravelById("RJbqzskdMqi9NLkP0hvj")
            if (done!=null){
                feedbackButton(context, "DONE")
            }else{
                feedbackButton(context, "FAILED")
            }
        }
    }
    val getUser = ButtonInfo("GET USER") {
        scope.launch {
            val done = CommonModel.getUser("Xj7MvQaxsLcYU6whg7sB")
            if (done!=null){
                feedbackButton(context, "DONE")
            }else{
                feedbackButton(context, "FAILED")
            }
        }
    }

    val getMyProfile = ButtonInfo("GET MY PROFILE") {
        scope.launch {
            val done = theUserModel.getMyProfile("Xj7MvQaxsLcYU6whg7sB")
            if (done!=null){
                feedbackButton(context, "DONE")
            }else{
                feedbackButton(context, "FAILED")
            }
        }
    }
    val UpdateTravel = ButtonInfo("UPDATE TRAVEL") {
        scope.launch {
            val done = theTravelModel.updateTravel(travel2update)
            done.onSuccess {
                feedbackButton(context, "DONE")
            }.onFailure { exception ->
                feedbackButton(context, "FAILED", exception = exception)
            }
        }
    }


    val buttonsData = listOf(
        resetDB,
        //insertUser,
        //getUser,
        //getMyProfile,
        //getTravelByID,
        UpdateTravel,
        insertRequestDB,
    )

    ButtonsFunctions(buttonsInfoList = buttonsData)
}


fun feedbackButton(context: Context, message: String, exception: Throwable?=null){
    if(exception!=null)
        println("$message : \n $exception")
    else
        println(message)

    Toast.makeText(
        context,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

@Composable
fun ButtonsFunctions(
    modifier: Modifier = Modifier,
    buttonsInfoList: List<ButtonInfo>
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Support QUERIES",
                modifier = Modifier.padding(bottom = 30.dp),
                style = MaterialTheme.typography.displayLarge,
            )

            buttonsInfoList.forEach { buttonInfo ->
                Button(
                    onClick = buttonInfo.onClick,
                    modifier = Modifier.padding(bottom = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = buttonInfo.text,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
    }
}