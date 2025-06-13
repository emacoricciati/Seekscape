package it.polito.mad.lab5g10.seekscape.ui.travels

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.firebase.TheTravelModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.OWNED
import it.polito.mad.lab5g10.seekscape.models.OwnedTravelViewModel
import it.polito.mad.lab5g10.seekscape.models.PAST
import it.polito.mad.lab5g10.seekscape.models.Request
import it.polito.mad.lab5g10.seekscape.models.RequestViewModel
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelUiState
import it.polito.mad.lab5g10.seekscape.timeAgo
import it.polito.mad.lab5g10.seekscape.ui._common.components.RequestCard
import it.polito.mad.lab5g10.seekscape.ui._common.components.RequestModal
import it.polito.mad.lab5g10.seekscape.ui._common.components.TravelCard
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions

@Composable
fun MyTravelsScreen(
    ownedTravelViewModel: OwnedTravelViewModel,
    requestViewModel: RequestViewModel,
    navController: NavHostController,
    mode: String,
    action: String? = null
) {
    val currentBackStackEntry = navController.currentBackStackEntry
    val savedStateHandle = currentBackStackEntry?.savedStateHandle
    var hasAdded by remember{ mutableStateOf(false) }
    val actions = remember(navController) { Actions(navController) }
    val theTravelModel = TheTravelModel()

    val ownedTravels = produceState<List<Travel>?>(initialValue = null) {
        value = theTravelModel.getOwnedTravels()
    }

    //Managing update of a owned travel
    LaunchedEffect(Unit) {
        navController.currentBackStackEntryFlow.collect { entry ->
            if (entry.destination.route == "travels") {
                Log.d("NAVIGATION", "Returned to trips screen, refreshing travels")
                ownedTravelViewModel.refresh()
            }
        }
    }


    //Managing the add of a new travel (refetch travels)
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect{
            hasAdded = true
        }
    }

    if(hasAdded){
        if(ownedTravels.value!= null){
            ownedTravelViewModel.updateOwnedTravels(ownedTravels.value!!)
        }
        hasAdded = false
    }



    Box {
        when (mode) {
            "Explore" -> TabSelectionExplorerMode(navController)
            "Creator" -> TabSelectionCreator(requestViewModel, ownedTravelViewModel, navController, action)
        }
    }
}

@Composable
fun TabSelectionExplorerMode(navController: NavHostController) {
    val tabTitles = listOf("Upcoming", "Pending", "Rejected","To Review", "Past")
    val myTravelTab by AppState.myTravelTab.collectAsState()
    val theTravelModel = TheTravelModel()
    var selectedTabIndex by remember { mutableStateOf(tabTitles.indexOf(myTravelTab)) }
    if (selectedTabIndex == -1) {
        selectedTabIndex = 0
        AppState.updateMyTravelTab(tabTitles[0])
    }
    var travelUiState by remember { mutableStateOf<TravelUiState>(TravelUiState.Loading) }

    LaunchedEffect(myTravelTab) {
        travelUiState = TravelUiState.Loading
        try {
            val travels = when (myTravelTab) {
                "Upcoming" -> theTravelModel.getJoinedTravels(null)
                "Pending" -> theTravelModel.getPendingTravels(null)
                "Rejected" -> theTravelModel.getDeniedTravels(null)
                "To Review" -> theTravelModel.getToReviewTravels(null)
                "Past" -> theTravelModel.getPastTravels(null)
                else -> emptyList()
            }

            travelUiState = if (travels.isEmpty()) {
                TravelUiState.Empty
            } else {
                TravelUiState.Success(travels)
            }

            Log.d("TabDataDebug", "$myTravelTab tab: ${travels.size} travels loaded")

        } catch (e: Exception) {
            Log.e("TabDataDebug", "Error loading travels", e)
        }
    }


    Column  {
        ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 0.dp
            ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        AppState.updateMyTravelTab(title)
                    }
                )
            }
        }

        when (val state = travelUiState) {
            is TravelUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TravelUiState.Empty -> {
                Text(
                    text = "No ${myTravelTab.lowercase()} travels found...",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            is TravelUiState.Success -> {
                CreatorModeTrips(state.travels, myTravelTab, navController)
            }

        }
    }
}

@Composable
fun CreatorModeTrips(travels: List<Travel>, type: String, navController: NavHostController) {
    val context = LocalContext.current
    var showMessage by remember { mutableStateOf(false) }
    val actions = remember(navController) { Actions(navController) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        if (travels.isEmpty()) {
            var text: String = ""

            when (type) {
                "Upcoming" -> text += "No upcoming travels..."
                "Pending" -> text += "No pending travels..."
                "Rejected" -> text += "No rejected travels..."
                "To Review" -> text += "No travels to be reviewed..."
                "Past" -> text += "No past travels..."
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (travels.isNotEmpty()) {
                items(travels) {
                    var text: String = ""

                    when (type) {
                        "Upcoming" -> text += "Starting " + it.startDate?.let { it1 ->
                            timeAgo(
                                it1
                            )
                        }

                        "Pending" -> text += "Waiting for approval"
                        "Rejected" -> text += "Rejected"
                        "To Review" -> text += "Waiting for a review"
                        "Past" -> text += it.endDate?.let { it1 -> timeAgo(it1) }
                    }

                    val onCardClick = {
                        actions.seeTravel(it.travelId)
                    }
                    TravelCard(it, onCardClick, text, navController)
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }

}

@Composable
fun ChangeModeButton(onClick: () -> Unit, modeLabel: String, modifier: Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(vertical = 30.dp)
        ) {
            Text("$modeLabel mode", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun TabSelectionCreator(
    requestsViewModel: RequestViewModel,
    ownedTravelViewModel: OwnedTravelViewModel,
    navController: NavHostController,
    action: String?
) {
    val tabTitles = listOf("My trips", "Requests")
    val myTravelTab by AppState.myTravelTab.collectAsState()
    var fetchRequests by remember { mutableStateOf(false) }

    LaunchedEffect(fetchRequests) {
        if(fetchRequests){
            requestsViewModel.updateRequests()
            fetchRequests=false
        }
    }

    var selectedTabIndex by remember { mutableStateOf(tabTitles.indexOf(myTravelTab)) }
    if (selectedTabIndex == -1) {
        selectedTabIndex = 0
        AppState.updateMyTravelTab(tabTitles[0])
    }
    val badgeCount = requestsViewModel.requests.collectAsState().value.count {
        !it.isAcceptedValue.collectAsState().value && !it.isRefusedValue.collectAsState().value
    }
    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    text = {
                        if (index == 1 && badgeCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge {
                                        Text(badgeCount.toString())
                                    }
                                }
                            ) {
                                Text(title)
                            }
                        } else {
                            Text(title)
                        }
                    },
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        AppState.updateMyTravelTab(title)
                        fetchRequests = index==1
                    }
                )
            }
        }

        when (myTravelTab) {
            "My trips" -> UserTripsScreen(ownedTravelViewModel, navController)
            "Requests" -> RequestsScreen(requestsViewModel, ownedTravelViewModel, action, navController)
        }
    }
}


@Composable
fun UserTripsScreen(ownedTravelViewModel: OwnedTravelViewModel, navController: NavHostController) {
    val ownedTravels by ownedTravelViewModel.travels.collectAsState()
    val actions = remember(navController) { Actions(navController) }
    if (ownedTravels.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No owned travels found...",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(10.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(ownedTravels) {
                    val travelId by it.travelIdValue.collectAsState()
                    val creator by it.creatorValue.collectAsState()
                    val title by it.titleValue.collectAsState()
                    val description by it.descriptionValue.collectAsState()
                    val country by it.locationValue.collectAsState()
                    val priceMin by it.priceStartValue.collectAsState()
                    val priceMax by it.priceEndValue.collectAsState()
                    val status by it.statusValue.collectAsState()
                    val distance by it.distanceValue.collectAsState()
                    val startDate by it.dateStartValue.collectAsState()
                    val endDate by it.dateEndValue.collectAsState()
                    val maxPeople by it.nParticipantsValue.collectAsState()
                    val travelImages by it.imageUrisValues.collectAsState()
                    val travelTypes by it.travelTypesValues.collectAsState()
                    val travelItinerary by it.travelItineraryValues.collectAsState()
                    val travelCompanions by it.travelCompanionsValues.collectAsState()

                    val travel = Travel(
                        travelId,
                        creator,
                        title,
                        description,
                        country,
                        priceMin,
                        priceMax,
                        status,
                        OWNED,
                        distance,
                        startDate,
                        endDate,
                        maxPeople,
                        travelImages,
                        travelTypes,
                        travelItinerary,
                        travelCompanions
                    )

                    val onCardClick = {//da copiare per i tab
                        actions.seeTravel(travelId)
                    }


                    var textAboveCard=""

                    if(travel.status==PAST){
                        textAboveCard="Ended " + endDate?.let { it1 ->
                            timeAgo(
                                it1
                            )
                        }
                    } else {
                        textAboveCard="Starting " + startDate?.let { it1 ->
                            timeAgo(
                                it1
                            )
                        }
                    }

                    TravelCard(travel, onCardClick, textAboveCard, navController)
                    Spacer(Modifier.height(10.dp))
                }

            }

        }
    }
}

@Composable
fun RequestsScreen(
    requestsViewModels: RequestViewModel,
    ownedTravelViewModel: OwnedTravelViewModel,
    action: String?,
    navController: NavHostController
) {
    val requests by requestsViewModels.requests.collectAsState()
    if (requests.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No requests found...",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                itemsIndexed(requests) { _, req ->
                    val reqIndex = req.idValue.collectAsState().value
                    ReqMng(
                        reqIndex,
                        requestsViewModels,
                        ownedTravelViewModel,
                        action,
                        navController
                    )
                }
            }
        }
    }
}

@Composable
fun ReqMng(index: String, vm: RequestViewModel, ownedTravelViewModel: OwnedTravelViewModel, action:String?, navController: NavHostController) {
    var showModalBottom by remember { mutableStateOf(false) }
    var openTextBox by remember { mutableStateOf(false) }
    var confirmReq by remember { mutableStateOf(false) }

    val isAccepted by vm.getRequest(index).isAcceptedValue.collectAsState()
    val isRefused by vm.getRequest(index).isRefusedValue.collectAsState()
    var actionDone by remember { mutableStateOf(false) }
    val actions = remember(navController) { Actions(navController) }

    if (!isAccepted && !isRefused) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 15.dp)
        ) {
            RequestCard(
                index,
                vm,
                ownedTravelViewModel,
                { showModalBottom = true },
                { openTextBox = true },
                { openTextBox = false },
                { confirmReq = true },
                { confirmReq = false })
        }

        if (showModalBottom) {
            RequestModal(
                index,
                vm,
                ownedTravelViewModel,
                {
                    showModalBottom = false
                },
                openTextBox,
                confirmReq
            )
        }
    }

    if(!actionDone && action!=null && action.startsWith("SHOW_APPLY_")){
        val req = vm.getRequestObject(index)
        val actionMatch = "SHOW_APPLY_${req.trip.travelId}_${req.author.userId}"
        if(actionMatch==action){
            showModalBottom = true
            openTextBox = false
            actionDone=true
        }
    }
}



