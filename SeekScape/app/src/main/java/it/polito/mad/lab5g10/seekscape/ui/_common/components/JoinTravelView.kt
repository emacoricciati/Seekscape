package it.polito.mad.lab5g10.seekscape.ui._common.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.Request
import it.polito.mad.lab5g10.seekscape.models.SingleRequestViewModel
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.ui._theme.GraySecondaryLight
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import kotlinx.coroutines.launch

@Composable
fun TravelResumeCard(travel: Travel) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(30.dp, shape = RoundedCornerShape(30.dp))
            .padding(start = 15.dp, end = 15.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        travel.travelImages?.firstOrNull()?.let { image ->
            val painter = when (image) {
                is TravelImage.Resource -> painterResource(id = image.resId)
                is TravelImage.Url -> rememberAsyncImagePainter(model = image.value)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painter,
                    contentDescription = "Travel Image for travel",
                    modifier = Modifier
                        .width(120.dp)
                        .height(120.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 10.dp, top = 10.dp)
                ) {
                    Text(
                        text = travel.title ?: "Untitled Travel",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                        IconLocation(travel.country ?: "Unknown Location")
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                        IconTravelType(travel)
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.padding(start = 10.dp)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Travel details",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                        IconDateRange(travel.startDate!!, travel.endDate!!)
                    }
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                        IconPeopleJoined(
                            travel.travelCompanions!!,
                            travel.maxPeople!!
                        )     //travel.travelCompanions!!, travel.maxPeople!!
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.padding(start = 10.dp)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Price range details",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
                        IconCost(travel.priceMin!!, travel.priceMax!!)
                    }
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}

@Composable
fun SelectNumberSpots(
    selectedNumber: Int = 1,
    maxCompanion: Int,
    vm: SingleRequestViewModel
) {
    var showDropdown by remember { mutableStateOf(false) }
    var currentSelection by remember { mutableStateOf(selectedNumber) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(start = 40.dp)
        ) {
            Text(
                text = "Spots requested:",
                modifier = Modifier
                    .padding(end = 8.dp)
            )
            Button(
                onClick = { showDropdown = true },
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .height(30.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = currentSelection.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(end = 8.dp)

                )
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = "Select number",
                    tint = MaterialTheme.colorScheme.surface
                )
            }


            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false }
            ) {
                (1..(maxCompanion-1)).forEach { number ->
                    DropdownMenuItem(
                        text = { Text(number.toString()) },
                        onClick = {
                            vm.setSpots(number)
                            currentSelection = number
                            showDropdown = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TextBox(vm: SingleRequestViewModel, name: String){
    var text by remember { mutableStateOf("") }

    ElevatedCard (
        modifier = Modifier
            .height(240.dp)
            .shadow(30.dp, shape = RoundedCornerShape(30.dp))
            .padding(start = 30.dp, end = 30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ){
        TextField(
            value = text,
            onValueChange = { text = it
                            vm.setReqMessage(text)
                            },
            placeholder = { Text("Hi ${name} I'd like to join your group...",
                style = MaterialTheme.typography.bodyMedium,
                color = GraySecondaryLight
            ) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun ConfirmRequestButton(vm: SingleRequestViewModel, navCont: NavHostController){

    val author by vm.author.collectAsState()
    val trip by vm.trip.collectAsState()
    val reqMessage by vm.reqMessage.collectAsState()
    val isAcc by vm.isAccepted.collectAsState()
    val isRef by vm.isRefused.collectAsState()
    val spots by vm.spots.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val actions = remember(navCont){ Actions(navCont) }
    val request = Request(
        "",
        author,
        trip,
        reqMessage,
        isAcc,
        isRef,
        spots
    )

    Row(
        modifier = Modifier
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background.copy(alpha = 1.0f)
                    )
                )
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                scope.launch{
                    CommonModel.InsertRequestDB(request).onSuccess {
                        Toast.makeText(
                            context,
                            "Request sent",
                            Toast.LENGTH_SHORT
                        ).show()

                        navCont.previousBackStackEntry?.savedStateHandle?.set("updated_travel", true)
                        actions.backToHome()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(vertical = 20.dp)
        ) {
            Text("Confirm", style = MaterialTheme.typography.titleSmall)
        }
    }
}