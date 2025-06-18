package it.polito.mad.lab5g10.seekscape.ui.add

import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AirplanemodeActive
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DirectionsTransit
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Route
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.MAX_COMPANIONS
import it.polito.mad.lab5g10.seekscape.models.MAX_PRICE
import it.polito.mad.lab5g10.seekscape.models.MIN_COMPANIONS
import it.polito.mad.lab5g10.seekscape.models.MIN_PRICE
import it.polito.mad.lab5g10.seekscape.models.OWNED
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.models.TravelViewModel
import it.polito.mad.lab5g10.seekscape.ui._common.Calendar
import it.polito.mad.lab5g10.seekscape.ui._common.components.PillButtonEditable
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.copyUriToInternalStorage
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.TheTravelModel
import it.polito.mad.lab5g10.seekscape.models.CREATOR_TRAVEL_MODE
import it.polito.mad.lab5g10.seekscape.models.EXPLORE_TRAVEL_MODE
import it.polito.mad.lab5g10.seekscape.models.TRAVEL_TYPES
import it.polito.mad.lab5g10.seekscape.models.TravelCompanion
import it.polito.mad.lab5g10.seekscape.ui._common.components.AddLocation
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    showDialog: Boolean
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTravelsScreen(vm: TravelViewModel, navCont: NavHostController, mode: String) {
    val currentContext by rememberUpdatedState(newValue = LocalContext.current)
    val creator by vm.creatorValue.collectAsState()
    val title by vm.titleValue.collectAsState()
    val description by vm.descriptionValue.collectAsState()
    val nParticipants by vm.nParticipantsValue.collectAsState()
    val dateStart by vm.dateStartValue.collectAsState()
    val dateEnd by vm.dateEndValue.collectAsState()
    val priceStart by vm.priceStartValue.collectAsState()
    val priceEnd by vm.priceEndValue.collectAsState()
    val travelTypesVM by vm.travelTypesValues.collectAsState()
    val imageUris by vm.imageUrisValues.collectAsState()
    val travelItinerary by vm.travelItineraryValues.collectAsState()
    val location by vm.locationValue.collectAsState()
    val distance by vm.distanceValue.collectAsState()
    val isTravelLoaded by vm.isTravelLoaded.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val theTravelModel = remember { TheTravelModel() }
    val scope = rememberCoroutineScope()


    val actions = remember(navCont) { Actions(navCont) }

    if (!AppState.isLogged.collectAsState().value) {
        actions.navigateTo("profile/unlogged")
    }

    if(isTravelLoaded){
        fun deleteTravelProposal(travelId: String) {
            scope.launch{
                theTravelModel.deleteTravel(travelId)
            }

            AppState.updateMyTravelTab("My trips")
            AppState.updateMyTravelMode(CREATOR_TRAVEL_MODE)
            AppState.updateCurrentTab("travels")
            AppState.updateRedirectPath("travels")
        }
        if (!vm.isAddingLocation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                ConfirmationDialog(
                    title= "Delete travel proposal",
                    text= "Are you sure to delete the travel proposal?",
                    onConfirm = {
                        deleteTravelProposal(vm.travelIdValue.value)
                        showDialog = false
                    },
                    onDismiss = { showDialog = false },
                    showDialog = showDialog
                )
                // Column keep all the content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Add a travel proposal",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .align(Alignment.CenterHorizontally),
                    )
                    Text(
                        text = "Add till 5 photo",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.CenterHorizontally),
                    )

                    ImagePickerWithPreview(
                        imageUris = imageUris,
                        onAddImage = { vm.addImageUri(it) },
                        onRemoveImage = { vm.removeImageUri(it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    if (vm.imagesError.isNotBlank()) {
                        Text(
                            vm.imagesError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Title
                    Text("Title", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = title ?: "",
                        onValueChange = { vm.setTitle(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        placeholder = { Text("Enter your title...", fontSize = 16.sp) }
                    )
                    if (vm.titleError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            vm.titleError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text("Description", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = description ?: "",
                        onValueChange = { vm.setDescription(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 8.dp),
                        placeholder = { Text("Enter your description...", fontSize = 16.sp) }
                    )
                    if (vm.descriptionError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            vm.descriptionError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    // Location
                    Text("Location", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(Modifier.height(8.dp))
                    if (location.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            PillButtonEditable(location) { vm.removeLocation() }
                        }
                    } else {
                        Box(modifier = Modifier.padding(horizontal = 12.dp)) {
                            Button(
                                onClick = { vm.toggleIsAddingLocation() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(12.dp)
                                    ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Outlined.Place, contentDescription = "Add Location")
                                    Text("Add Location")
                                }
                            }
                        }
                    }


                    if (vm.countryError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            vm.countryError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Dates
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = "Date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Date",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 2.dp),
                    ) {
                        Calendar(
                            "start",
                            dateStart,
                            null,
                            modifier = Modifier.weight(1f)
                        ) { vm.setDateStart(it) }
                        Spacer(modifier = Modifier.width(6.dp))
                        Calendar(
                            "end",
                            dateEnd,
                            dateStart,
                            modifier = Modifier.weight(1f)
                        ) { vm.setDateEnd(it) }
                    }
                    if (vm.dateError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            vm.dateError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        if (vm.todayError.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                vm.todayError,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Price
                    TripRangeSlider(
                        priceStart,
                        priceEnd,
                        onRangeSelected = { range ->
                            vm.setPriceStart(range.first)
                            vm.setPriceEnd(range.last)
                        }
                    )
                    if (vm.priceError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            vm.priceError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Participants
                    SelectNumbers(nParticipants) { vm.setParticipants(it) }
                    if (vm.peopleError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            vm.peopleError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))

                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Travel Types
                    UserTravelType(
                        vm = vm,
                        availableTypes = TRAVEL_TYPES,
                        onTravelTypeSelected = { vm.setTravelTypes(it) }
                    )
                    if (vm.typesError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            vm.typesError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Itinerary
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Route,
                            contentDescription = "Itinerary",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Itinerary",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Spacer(Modifier.height(5.dp))
                    if (travelItinerary.isEmpty()) {

                    //default add button
                    when(mode){
                        "copy" -> {
                            val onClickAction = {
                                actions.navigateToCopyTravelItinerary(vm.travelIdValue.value)
                            }
                            AddItineraryButton(vm, onClickAction = onClickAction)
                        }
                        "edit" -> {
                            val onClickAction = { actions.addItineraryfromEdit(vm.travelIdValue.value) }
                            AddItineraryButton(vm, onClickAction = onClickAction)
                        }
                        "add" -> {
                            val onClickAction = {actions.addItinerary()}
                            AddItineraryButton(vm, onClickAction = onClickAction)
                        }

                    }
                } else {
                    //display itineraryCard with button for adding more
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (itinerary in travelItinerary.sortedBy {
                            it.startDate
                        }) {
                            Box(
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(150.dp)
                                    .wrapContentHeight()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = itinerary.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = itinerary.places.firstOrNull() ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        //Edit Itinerary
                                        Box(
                                            modifier = Modifier
                                                .requiredSize(22.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    when(mode){
                                                        "add" -> actions.editItineraryFromAdd(itinerary.itineraryId)
                                                        "copy" -> { //AppState.updateCurrentTab("add");
                                                             actions.navigateTo("add/${vm.travelIdValue.value}/copy/itinerary/${itinerary.itineraryId}/edit")}
                                                        "edit" -> actions.editItineraryfromEdit(vm.travelIdValue.value, itinerary.itineraryId)
                                                    }
                                                }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Edit,
                                                contentDescription = "Edit Itinerary",
                                                tint = MaterialTheme.colorScheme.onBackground,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(3.dp)
                                            )
                                        }
                                        //Delete itinerry
                                            Box(
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .clickable {
                                                        vm.removeItinerary(itinerary)
                                                    }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Close,
                                                    contentDescription = "Close Itinerary",
                                                    tint = MaterialTheme.colorScheme.onBackground,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                    }
                                }
                            }
                        }
                        // Small circle button to add more itineraries
                        IconButton(
                            onClick = {
                                when(mode){
                                    "copy" -> {AppState.updateCurrentTab("add"); AppState.updateRedirectPath("add/${vm.travelIdValue.value}/copy/itinerary")}
                                    "edit" -> actions.addItineraryfromEdit(vm.travelIdValue.value)
                                    "add" -> actions.addItinerary()
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Itinerary",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                if (vm.itineraryError.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        vm.itineraryError,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }


                    val currentTravelState by vm.statusForUser.collectAsState()
                    if (currentTravelState == OWNED && mode == "edit") {
                        Spacer(modifier = Modifier.height(25.dp))
                        OutlinedButton(
                            onClick = {
                                showDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 60.dp)
                                .height(45.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(
                                text = "Delete",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(60.dp))
                    } else {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }

                // For the gradient
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
                                if (vm.validateTravel()) {

                                    if (mode == "edit") { // modify

                                        scope.launch {
                                            try{
                                                val oldTravel = CommonModel.getTravelById(vm.travelIdValue.value)
                                                val travel = Travel(
                                                    travelId = vm.travelIdValue.value,
                                                    creator = creator,
                                                    title = title,
                                                    description = description,
                                                    country = location,
                                                    priceMin = priceStart,
                                                    priceMax = priceEnd,
                                                    status = oldTravel?.status,
                                                    statusForUser = OWNED,
                                                    distance = distance,
                                                    startDate = dateStart,
                                                    endDate = dateEnd,
                                                    maxPeople = nParticipants,
                                                    travelImages = imageUris,
                                                    travelTypes = travelTypesVM,
                                                    travelItinerary = travelItinerary,
                                                    travelCompanions = oldTravel?.travelCompanions
                                                )
                                                theTravelModel.updateTravel(travel)
                                                vm.clean()
                                                Toast.makeText(
                                                    currentContext,
                                                    "Travel Proposal Edited",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navCont.previousBackStackEntry?.savedStateHandle?.set("updated_travel", true)
                                                actions.navigateBack()
                                            }catch (e: Exception){
                                                Toast.makeText(currentContext, "Failed to update travel: ${e.message}", Toast.LENGTH_LONG).show()
                                                Log.e("updateTravel", "Error updating travel", e)
                                            }
                                        }

                                        //to modify

                                    } else { // create
                                        val travel = Travel(
                                            travelId = "",
                                            creator = creator,
                                            title = title,
                                            description = description,
                                            country = location,
                                            priceMin = priceStart,
                                            priceMax = priceEnd,
                                            status = "available",
                                            statusForUser = OWNED,
                                            distance = distance,
                                            startDate = dateStart,
                                            endDate = dateEnd,
                                            maxPeople = nParticipants,
                                            travelImages = imageUris,
                                            travelTypes = travelTypesVM,
                                            travelItinerary = travelItinerary,
                                            travelCompanions = listOf(TravelCompanion(creator))
                                        )
                                        scope.launch {
                                            try {
                                                theTravelModel.addTravel(travel)
                                                vm.clean()
                                                AppState.updateMyTravelTab("My trips")
                                                AppState.updateMyTravelMode(CREATOR_TRAVEL_MODE)
                                                AppState.updateCurrentTab("travels")
                                                AppState.updateRedirectPath("travels")
                                                Toast.makeText(currentContext, "Travel Proposal Added", Toast.LENGTH_SHORT).show()
                                            } catch (e: Exception) {
                                                Toast.makeText(currentContext, "Failed to add travel: ${e.message}", Toast.LENGTH_LONG).show()
                                                Log.e("addTravel", "Error adding travel", e)
                                            }
                                        }
                                    }
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
        } else {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                AddLocation(
                    onCancel = { vm.toggleIsAddingLocation() },
                    onLocationSelected = { location ->
                        vm.setDistance(location.distance.toString())
                        vm.setLocationAndCloseAdding(location.name)
                    }
                )
            }
        }
    }else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun AddItineraryButton(vm: TravelViewModel, onClickAction: ()->Unit) {
    val creator by vm.creatorValue.collectAsState()
    val title by vm.titleValue.collectAsState()
    val description by vm.descriptionValue.collectAsState()
    val nParticipants by vm.nParticipantsValue.collectAsState()
    val dateStart by vm.dateStartValue.collectAsState()
    val dateEnd by vm.dateEndValue.collectAsState()
    val priceStart by vm.priceStartValue.collectAsState()
    val priceEnd by vm.priceEndValue.collectAsState()
    val travelTypesVM by vm.travelTypesValues.collectAsState()
    val imageUris by vm.imageUrisValues.collectAsState()
    val travelItinerary by vm.travelItineraryValues.collectAsState()
    val location by vm.locationValue.collectAsState()

    Button(
        onClick = {
            onClickAction()
        },
        modifier = Modifier
            .padding(top = 2.dp)
            .padding(horizontal = 16.dp)
            .height(40.dp)
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.DirectionsTransit,
            contentDescription = "Add Itinerary",
            modifier = Modifier.padding(start = 8.dp)
        )
        Text("Add Day")
    }

}


@Composable
fun SelectNumbers(
    selectedNumber: Int = 2,
    onNumberSelected: (Int) -> Unit = {}
) {
    var showDropdown by remember { mutableStateOf(false) }
    var currentSelection by remember { mutableStateOf(selectedNumber) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.People,
                contentDescription = "Participants",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                "Participants",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 8.dp)

        ) {
            Text(
                text = "Number:",
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
                (MIN_COMPANIONS..MAX_COMPANIONS).forEach { number ->
                    DropdownMenuItem(
                        text = { Text(number.toString()) },
                        onClick = {
                            currentSelection = number
                            showDropdown = false
                            onNumberSelected(number)
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserTravelType(
    vm: TravelViewModel,
    availableTypes: List<String>,
    onTravelTypeSelected: (List<String>) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val selectedTypes by vm.travelTypesValues.collectAsState()

    Column(
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.AirplanemodeActive,
                contentDescription = "TravelType",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                "Travel Type",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(Modifier.height(5.dp))

        if (selectedTypes.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .padding(top = 5.dp)
            ) {

                selectedTypes.forEach { PillButtonEditable(it) { vm.removeTravelType(it) } }
            }

            TextButton(
                onClick = { showDialog = true },
            ) {
                Text(text = "Select more", style = MaterialTheme.typography.bodySmall)
            }

        } else {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .padding(top = 2.dp)
                    .padding(horizontal = 16.dp)
                    .height(40.dp)
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(
                    text = "Select Travel Type",
                )
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Select More Travel Types") },
                text = {
                    Column {
                        availableTypes.forEach { type ->
                            TextButton(onClick = {
                                if (!selectedTypes.contains(type)) {
                                    vm.addTravelType(type)
                                    onTravelTypeSelected(vm.travelTypesValues.value)
                                } else {
                                    // Show toast for duplicate entry
                                    Toast.makeText(
                                        context,
                                        "$type already selected",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }) {
                                Text(type)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}


@Composable
fun TripRangeSlider(
    min: Int = MIN_PRICE,
    max: Int = MAX_PRICE,
    onRangeSelected: (IntRange) -> Unit
) {

    var localMin by remember { mutableStateOf(min.toFloat()) }
    var localMax by remember { mutableStateOf(max.toFloat()) }
    val pricestepSize = 50
    val priceSteps = ((MAX_PRICE - MIN_PRICE) / pricestepSize) - 1

    Column() {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.EuroSymbol,
                contentDescription = "Money",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                "Price Range",
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Range Slider clickable by the user
        Box(modifier = Modifier.padding(horizontal = 8.dp)) {
            RangeSlider(
                value = localMin..localMax,
                onValueChange = { range ->
                    // coerceAtMost is used to be sure that range.start is at most equal to range.endInclusive
                    // coerceAtLeast is used as coerceAtMost but opposite
                    localMin = range.start.coerceAtMost(localMax)
                    localMax = range.endInclusive.coerceAtLeast(localMin)
                    onRangeSelected(localMin.toInt()..localMax.toInt())
                },
                //This parameter is used to set the range that you can select
                valueRange = MIN_PRICE.toFloat()..MAX_PRICE.toFloat(),
                steps = priceSteps
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            OutlinedTextField(
                value = localMin.toInt().toString(),
                onValueChange = {
                    it.toIntOrNull()?.let { value ->
                        val newValue = if (value >= MIN_PRICE) value else MIN_PRICE
                        localMin = newValue.coerceAtMost(localMax.toInt()).toFloat()
                        onRangeSelected(localMin.toInt()..localMax.toInt())
                    }
                },
                label = { Text("Min") },
                modifier = Modifier.weight(1f),
                //WHen the user clicks to change the value it set a numeric-only keyboard.
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Text("€", style = MaterialTheme.typography.bodyMedium)
                }
            )

            OutlinedTextField(
                value = localMax.toInt().toString(),
                onValueChange = {
                    it.toIntOrNull()?.let { value ->
                        val newValue = if (value <= MAX_PRICE) value else MAX_PRICE
                        localMax = newValue.coerceAtLeast(localMin.toInt()).toFloat()
                        onRangeSelected(localMin.toInt()..localMax.toInt())
                    }
                },
                label = { Text("Max") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Text("€", style = MaterialTheme.typography.bodyMedium)
                }
            )
        }
    }
}


@Composable
fun ImagePickerWithPreview(
    imageUris: List<TravelImage>,
    onAddImage: (TravelImage) -> Unit,
    onRemoveImage: (TravelImage) -> Unit
) {
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val imageSize = (screenWidth - 32.dp) / 5

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            if (imageUris.size < 5) {
                val file =
                    copyUriToInternalStorage(context, it, "image_${System.currentTimeMillis()}.jpg")
                file?.let { f ->
                    onAddImage(TravelImage.Url("file://${f.absolutePath}"))
                }
            }
        }
    }

    Column {
        if (imageUris.isEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                OutlinedButton(
                    onClick = { launcher.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Image")
                        Text("Upload Images")
                    }
                }

            }
        }

        if (imageUris.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(verticalAlignment = Alignment.CenterVertically) {
                items(imageUris) { image ->
                    Box(
                        modifier = Modifier
                            .size(imageSize)
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        when (image) {
                            is TravelImage.Url -> Image(
                                painter = rememberAsyncImagePainter(model = image.value),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )

                            is TravelImage.Resource -> Image(
                                painter = painterResource(id = image.resId),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        IconButton(
                            onClick = { onRemoveImage(image) },
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove Image",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (imageUris.size < 5) {
                    item {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add more",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}





