package it.polito.mad.lab5g10.seekscape.ui.travels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapCalls
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.firebase.firebaseFormatter
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.ExploreModeTravelViewModel
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
fun TabSelectionExplorerMode(exploreTravelViewModel: ExploreModeTravelViewModel, navController: NavHostController) {
    val tabTitles = listOf("Upcoming", "Pending", "Rejected","To Review", "Past")
    val myTravelTab by AppState.myTravelTab.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(tabTitles.indexOf(myTravelTab)) }
    if (selectedTabIndex == -1) {
        selectedTabIndex = 0
        AppState.updateMyTravelTab(tabTitles[0])
    }
    val travelUiState = exploreTravelViewModel.getUiState(myTravelTab).value

    LaunchedEffect(Unit) {
        for(tab in tabTitles){
            if(tab!=myTravelTab){
                exploreTravelViewModel.fetchTravels(tab, null)
            }
        }
    }
    LaunchedEffect(myTravelTab) {
        exploreTravelViewModel.fetchTravels(myTravelTab, null)
    }

    Column (
        modifier = Modifier.padding(bottom = 0.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            modifier = Modifier.padding(bottom = 0.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier.padding(bottom=0.dp),
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
                ExploreModeTrips(state.travels, myTravelTab, navController, exploreTravelViewModel)
            }
        }
    }
}

@Composable
fun ExploreModeTrips(travels: List<Travel>, myTravelTab: String, navController: NavHostController, exploreTravelViewModel: ExploreModeTravelViewModel) {
    val isLoadingBack = exploreTravelViewModel.getLoadingBack(myTravelTab).value
    val isLoadingMore = exploreTravelViewModel.getLoadingMore(myTravelTab).value
    val actions = remember(navController) { Actions(navController) }

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top=8.dp)
            .fillMaxSize()
    ) {

        val listState = rememberLazyListState()
        var hasTriggeredLoad by remember { mutableStateOf(false) }
        LaunchedEffect(listState, travels.size) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                .collect { visibleItems ->
                    val secondToLastIndex = travels.size - 2
                    val isSecondToLastVisible = visibleItems.any { it.index == secondToLastIndex }

                    if (isSecondToLastVisible && !hasTriggeredLoad) {
                        hasTriggeredLoad = true
                        val lastStartDate = travels.last().startDate!!.format(firebaseFormatter)
                        exploreTravelViewModel.fetchTravels(myTravelTab, lastStartDate)
                    }
                }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            itemsIndexed(travels) { index, travel ->
                if(index==0){
                    Spacer(Modifier.height(10.dp))
                    if(isLoadingBack){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                var text: String = ""
                when (myTravelTab) {
                    "Upcoming" -> text += "Starting " + travel.startDate?.let { it1 -> timeAgo(it1) }
                    "Pending" -> text += "Waiting for approval"
                    "Rejected" -> text += "Rejected"
                    "To Review" -> text += "Waiting for a review"
                    "Past" -> text += travel.endDate?.let { it1 -> timeAgo(it1) }
                }
                val onCardClick = {
                    actions.seeTravel(travel.travelId)
                }
                val hasChat = myTravelTab!="Pending" && myTravelTab!="Rejected"
                TravelCard(travel, onCardClick, text, navController, hasChat=hasChat)
                Spacer(Modifier.height(10.dp))

                if(index==travels.lastIndex && isLoadingMore){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
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
                .wrapContentWidth()
                .padding(bottom = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SwapCalls,
                contentDescription = "Change Mode",
                modifier = Modifier.padding(end = 8.dp, start=0.dp)
            )
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

    LaunchedEffect(Unit) {
        if(myTravelTab=="My trips"){
            requestsViewModel.updateRequests()
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
    Column (
        modifier = Modifier.padding(bottom = 0.dp)
    ){
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.padding(bottom = 0.dp)
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier.padding(bottom=0.dp),
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
    val fetched by ownedTravelViewModel.fetched.collectAsState()
    val isLoadingBack by ownedTravelViewModel.isLoadingBack.collectAsState()
    LaunchedEffect(Unit) {
        ownedTravelViewModel.refresh()
    }

    Spacer(Modifier.height(8.dp))
    if(!fetched){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

    } else {
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
                    itemsIndexed(ownedTravels) { index, travel ->
                        val travelId by travel.travelIdValue.collectAsState()
                        val creator by travel.creatorValue.collectAsState()
                        val title by travel.titleValue.collectAsState()
                        val description by travel.descriptionValue.collectAsState()
                        val country by travel.locationValue.collectAsState()
                        val priceMin by travel.priceStartValue.collectAsState()
                        val priceMax by travel.priceEndValue.collectAsState()
                        val status by travel.statusValue.collectAsState()
                        val distance by travel.distanceValue.collectAsState()
                        val startDate by travel.dateStartValue.collectAsState()
                        val endDate by travel.dateEndValue.collectAsState()
                        val maxPeople by travel.nParticipantsValue.collectAsState()
                        val travelImages by travel.imageUrisValues.collectAsState()
                        val travelTypes by travel.travelTypesValues.collectAsState()
                        val travelItinerary by travel.travelItineraryValues.collectAsState()
                        val travelCompanions by travel.travelCompanionsValues.collectAsState()

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
                        if(isLoadingBack && index==0){
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        TravelCard(travel, onCardClick, textAboveCard, navController, hasChat = true)
                        Spacer(Modifier.height(10.dp))
                    }

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
    val fetched by requestsViewModels.fetched.collectAsState()
    val isLoadingBack by requestsViewModels.isLoadingBack.collectAsState()

    LaunchedEffect(Unit) {
        requestsViewModels.updateRequests()
    }

    Spacer(Modifier.height(8.dp))
    if(!fetched){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

    } else {
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
                    itemsIndexed(requests) { index, req ->
                        if(isLoadingBack && index==0){
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(28.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        val reqIndex = req.idValue.collectAsState().value
                        val req = requestsViewModels.getRequestObject(reqIndex)
                        if(req!=null){
                            ReqMng(
                                req,
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
    }
}


@Composable
fun ReqMng(req: Request, vm: RequestViewModel, ownedTravelViewModel: OwnedTravelViewModel, action:String?, navController: NavHostController) {
    var showModalBottom by remember { mutableStateOf(false) }
    var openTextBox by remember { mutableStateOf(false) }
    var confirmReq by remember { mutableStateOf(false) }
    var actionDone by remember { mutableStateOf(false) }

    if (!req.isAccepted && !req.isRefused) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 15.dp)
        ) {
            RequestCard(
                req,
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
                req,
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
        val actionMatch = "SHOW_APPLY_${req.trip.travelId}_${req.author.userId}"
        if(actionMatch==action){
            showModalBottom = true
            openTextBox = false
            actionDone=true
        }
    }
}



