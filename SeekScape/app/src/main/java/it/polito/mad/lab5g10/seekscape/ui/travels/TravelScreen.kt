package it.polito.mad.lab5g10.seekscape.ui.travels

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelViewModel
import it.polito.mad.lab5g10.seekscape.ui._common.components.TravelButton
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import it.polito.mad.lab5g10.seekscape.ui.travels.components.TravelDescription
import it.polito.mad.lab5g10.seekscape.ui.travels.components.TravelImages
import kotlinx.coroutines.launch

@Composable
fun TravelProposalScreen(vm: TravelViewModel, navController: NavHostController, action: String?=null) {
    val imageUris by vm.imageUrisValues.collectAsState()
    val scrollState = rememberScrollState()
    val actions = remember(navController) {
        Actions(navController)
    }
    val creator = vm.creatorValue.collectAsState()
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val scope = rememberCoroutineScope()

    val isTravelLoaded by vm.isTravelLoaded.collectAsState()

    LaunchedEffect(Unit) {
        val liveData = savedStateHandle?.getLiveData<Boolean>("updated_travel")

        liveData?.observeForever { it ->
            if(it == true){
                scope.launch{
                    val updatedTravel: Travel? = CommonModel.getTravelById(vm.travelIdValue.value)

                    if(updatedTravel != null){
                        vm.setTravelId(updatedTravel.travelId)
                        vm.setCreator(updatedTravel.creator)
                        updatedTravel.title?.let {it1  -> vm.setTitle(it1) }
                        updatedTravel.description?.let { it1 -> vm.setDescription(it1) }
                        updatedTravel.country?.let { it1 -> vm.setLocation(it1) }
                        updatedTravel.status?.let { it1 -> vm.setStatus(it1) }
                        updatedTravel.distance?.let { it1 -> vm.setDistance(it1) }
                        updatedTravel.startDate?.let { it1 -> vm.setDateStart(it1) }
                        updatedTravel.endDate?.let { it1 -> vm.setDateEnd(it1) }
                        updatedTravel.priceMin?.let { it1 -> vm.setPriceStart(it1) }
                        updatedTravel.priceMax?.let { it1 -> vm.setPriceEnd(it1) }
                        updatedTravel.travelCompanions?.let { it1 -> vm.setParticipants(it1.size) }
                        updatedTravel.travelImages?.let { it1 -> vm.setImageUris(it1) }
                        updatedTravel.travelTypes?.let { it1 -> vm.setTravelTypes(it1) }
                        updatedTravel.travelItinerary?.let { it1 -> vm.setTravelItinerary(it1) }
                        updatedTravel.travelCompanions?.let { it1 -> vm.setTravelCompanions(it1) }
                    }
                }
            }
        }
    }
    if(isTravelLoaded){
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize()
        ) {
            if (imageUris.isNotEmpty()) {
                TravelImages(
                    imageResources = imageUris,
                    modifier = Modifier,
                    onImageSelected = { selectedIndex ->
                        Log.d("ImageCarousel", "Selected image at index: $selectedIndex")
                    },
                    onOpenInFull = { index ->
                        actions.navigateToFullScreen(vm.travelIdValue.value, index)
                    },
                    actions,
                    vm.travelIdValue.collectAsState().value,
                    vm.titleValue.collectAsState().value
                )
            }
            TravelDescription(
                vm = vm,
                modifier = Modifier,
                navController,
                action
            )
        }
        TravelButton(vm, onButtonClick = {
        }, navController)


        val currentTab by AppState.currentTab.collectAsState()
        if (currentTab==MainDestinations.TRAVELS_ROUTE) {
            val myTravelTab by AppState.myTravelTab.collectAsState()
            val myProfile by AppState.myProfile.collectAsState()
            val travelId by vm.travelIdValue.collectAsState()
            var possibleNotificationId = ""
            if(myTravelTab=="Upcoming") {
                possibleNotificationId = "request_accepted_${myProfile.userId}_${travelId}"

            } else if(myTravelTab=="Rejected") {
                possibleNotificationId = "request_denied_${myProfile.userId}_${travelId}"

            } else if(myTravelTab=="My trips") {
                possibleNotificationId = "my_travel_review_${travelId}"
            }
            LaunchedEffect(possibleNotificationId, myProfile.notifications) {
                scope.launch {
                    if (possibleNotificationId.isNotEmpty() && myProfile.notifications.any { it.id == possibleNotificationId }) {
                        CommonModel.removeNotificationById(myProfile.userId, possibleNotificationId)
                    }
                }
            }

        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }


}