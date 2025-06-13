package it.polito.mad.lab5g10.seekscape.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.models.Itinerary
import it.polito.mad.lab5g10.seekscape.models.ItineraryViewModel
import it.polito.mad.lab5g10.seekscape.models.TravelViewModel
import it.polito.mad.lab5g10.seekscape.ui._common.Calendar
import it.polito.mad.lab5g10.seekscape.ui._common.activities
import it.polito.mad.lab5g10.seekscape.ui._common.activtyIcons
import it.polito.mad.lab5g10.seekscape.ui._common.components.AddLocation
import it.polito.mad.lab5g10.seekscape.ui._common.components.PillButtonEditable
import it.polito.mad.lab5g10.seekscape.ui._common.components.SelectionDialog
import it.polito.mad.lab5g10.seekscape.ui.add.components.ActivityCard
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddItinerary(vm: ItineraryViewModel, travelViewModel: TravelViewModel, navCont: NavHostController, itineraryId: Int) {
    var isAddingActivity by remember { mutableStateOf(false) }
    val nameValue by vm.nameValue.collectAsState()
    val description by vm.descriptionValue.collectAsState()
    val startDate by vm.startDateValue.collectAsState()
    val endDate by vm.endDateValue.collectAsState()
    val places by vm.places.collectAsState()
    val selectedActivities by vm.activities.collectAsState()
    val nextId = travelViewModel.travelItineraryValues.collectAsState().value.size

    val actions = remember(navCont) { Actions(navCont) }

    if (!vm.isAddingLocation){
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {

                Text(text = "Itinerary Day", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = "Date",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Date")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Calendar(
                        "start",
                        startDate,
                        null,
                        modifier = Modifier.weight(1f)
                    ) { vm.setStartDate(it) }
                    Spacer(modifier = Modifier.width(6.dp))
                    if (vm.selectEndDate) {
                        Calendar(
                            "end",
                            endDate,
                            startDate,
                            modifier = Modifier.weight(1f)
                        ) { vm.setEndDate(it) }
                    }
                }
                if (vm.startDateError.isNotBlank())
                    Text(
                        vm.nameError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                if (vm.endDateError.isNotBlank())
                    Text(
                        vm.endDateError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = vm.selectEndDate,
                        onCheckedChange = {
                            vm.toggleSelectEndDate()
                            if (!vm.selectEndDate) {
                                vm.endDateError = ""
                                vm.setEndDate(null)
                            }
                        },
                    )
                    Text(
                        text = "Add end date (optional)",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
                Text(text = "Title", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = nameValue,
                    onValueChange = { vm.setName(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    placeholder = { Text("Enter your title...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        errorContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                if (vm.nameError.isNotBlank())
                    Text(
                        vm.nameError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                Text(text = "Daily Description", style = MaterialTheme.typography.titleLarge)
                OutlinedTextField(
                    value = description ?: "",
                    onValueChange = { vm.setDescription(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(16.dp),
                    placeholder = { Text("Enter your description...", fontSize = 16.sp) }
                )
                if (vm.descriptionError.isNotBlank())
                    Text(
                        vm.descriptionError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
                Text(text = "Where", style = MaterialTheme.typography.titleLarge)
                if (places.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        places.forEach {
                            PillButtonEditable(it) { vm.removePlace(it) }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Add location",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable { vm.toggleIsAddingLocation() })

                if (vm.placesError.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        vm.placesError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
                Text(
                    text = "Select activity for the day",
                    style = MaterialTheme.typography.titleLarge
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Tap \"Add activities\" to select activities for the day. Activities will be added to the mandatory section by default. Use the arrow beside each activity to move it to your preferred section (optional or mandatory).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Add activities",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable { isAddingActivity = true })
                if (vm.activitiesError.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        vm.activitiesError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                if (isAddingActivity) {
                    val remainingActivities =
                        activities.filterNot { selectedActivities.contains(it) }
                    SelectionDialog(
                        "Activities",
                        selectedActivities,
                        remainingActivities,
                        { selected -> vm.addActivities(selected) },
                        { isAddingActivity = false }) { activity ->
                        activtyIcons[activity.icon] ?: activtyIcons["default"]!!
                    }
                }
                Spacer(Modifier.height(8.dp))
                // selected activities (optional)
                Text(
                    "Optional activities",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                val optionalActivities = selectedActivities.filter { it.optional }
                val mandatoryActivities = selectedActivities.filterNot { it.optional }
                if (optionalActivities.isNotEmpty()) {
                    Column(modifier = Modifier.padding(vertical = 10.dp)) {
                        selectedActivities.filter { it.optional }.map { activity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(modifier = Modifier.weight(6f)) {
                                    ActivityCard(
                                        activtyIcons[activity.icon]
                                            ?: activtyIcons["default"]!!,
                                        activity.name,
                                        true
                                    ) {
                                        vm.toggleOptional(activity)
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .weight(1f)
                                        .clickable {
                                            vm.removeActivity(activity)
                                        }
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                } else {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "No optional activities",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                // selected activities (mandatory)
                Text(
                    "Mandatory activities",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                if (mandatoryActivities.isNotEmpty()) {
                    Column(modifier = Modifier.padding(vertical = 10.dp)) {
                        mandatoryActivities.map { activity ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(modifier = Modifier.weight(6f)) {
                                    ActivityCard(
                                        activtyIcons[activity.icon]
                                            ?: activtyIcons["default"]!!,
                                        activity.name,
                                        false
                                    ) {
                                        vm.toggleOptional(activity)
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .weight(1f)
                                        .clickable {
                                            vm.removeActivity(activity)
                                        }
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                } else {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "No mandatory activities",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                Spacer(Modifier.height(80.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.background.copy(alpha = 1.0f)
                            )
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .graphicsLayer {
                            alpha = 0.9f
                        }
                ) {
                    Button(
                        onClick = {
                            if (vm.validate()) {
                                if (itineraryId == null){
                                val newItinerary = Itinerary(
                                    itineraryId = nextId,
                                    name = nameValue,
                                    description = description,
                                    startDate = startDate,
                                    endDate = endDate,
                                    places = places,
                                    activities = selectedActivities,
                                )
                                travelViewModel.addItinerary(newItinerary)
                                }
                                else {
                                    val newItinerary = Itinerary(
                                        itineraryId = itineraryId,
                                        name = nameValue,
                                        description = description,
                                        startDate = startDate,
                                        endDate = endDate,
                                        places = places,
                                        activities = selectedActivities,
                                    )
                                    val newIts = travelViewModel.travelItineraryValues.value.filter { it.itineraryId != itineraryId } + newItinerary
                                    travelViewModel.setTravelItinerary(newIts)
                                }
                                actions.navigateBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
    else {
        Column (
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ){
            AddLocation(
                onCancel = { vm.toggleIsAddingLocation() },
                onLocationSelected = { location -> vm.addPlace(location.name) }
            )
        }
    }
}