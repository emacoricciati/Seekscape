package it.polito.mad.lab5g10.seekscape.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.TheTravelModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.CREATOR_TRAVEL_MODE
import it.polito.mad.lab5g10.seekscape.models.EXPLORE_TRAVEL_MODE
import it.polito.mad.lab5g10.seekscape.models.OwnedTravelViewModel
import it.polito.mad.lab5g10.seekscape.models.OwnedTravelsViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.ProfileViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.Request
import it.polito.mad.lab5g10.seekscape.models.RequestViewModel
import it.polito.mad.lab5g10.seekscape.models.RequestsViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.models.TravelViewModel
import it.polito.mad.lab5g10.seekscape.models.TravelViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.UserInfoViewModel
import it.polito.mad.lab5g10.seekscape.ui._common.ArrowBackIcon
import it.polito.mad.lab5g10.seekscape.ui._common.FullscreenImageViewer
import it.polito.mad.lab5g10.seekscape.ui.profile.UserProfile
import it.polito.mad.lab5g10.seekscape.ui.travels.ChangeModeButton
import it.polito.mad.lab5g10.seekscape.ui.travels.MyTravelsScreen
import it.polito.mad.lab5g10.seekscape.ui.travels.TravelProposalScreen
import it.polito.mad.lab5g10.seekscape.ui.travels.ViewItineraryScreen


@Composable // "travels" and "travels/action/{action}"
fun RouteTravels(entry: NavBackStackEntry, navCont: NavHostController) {
    val theTravelModel = remember { TheTravelModel() }
    val action = entry.arguments?.getString("action")

    val currentMode = AppState.myTravelMode.collectAsState().value

    // Load owned travels and requests asynchronously
    val ownedTravelsState = produceState<List<Travel>?>(initialValue = null) {
        Log.d("OwnedTravels", "Loading owned travels...")
        value = theTravelModel.getOwnedTravels()
        Log.d("OwnedTravels", "Owned travels loaded: ${value?.size ?: 0}")
    }
    val requestsState = produceState<List<Request>?>(initialValue = null) {
        Log.d("RequestTab", "Loading requests...")
        value = theTravelModel.getRequestsToMyTrips()
        Log.d("RequestTab", "Requests loaded: ${value?.size ?: 0}")
    }

    if (ownedTravelsState.value != null && requestsState.value != null) {
        val ownedTravelViewModel: OwnedTravelViewModel =
            viewModel(factory = OwnedTravelsViewModelFactory(ownedTravelsState.value!!))

        val requestViewModel: RequestViewModel =
            viewModel(factory = RequestsViewModelFactory(requestsState.value!!.toMutableList()))

        MyTravelsScreen(
            ownedTravelViewModel = ownedTravelViewModel,
            requestViewModel = requestViewModel,
            navCont,
            currentMode,
            action
        )
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    Box(Modifier.fillMaxSize()) {
        ChangeModeButton(
            {
                if (currentMode == CREATOR_TRAVEL_MODE) {
                    AppState.updateMyTravelMode(EXPLORE_TRAVEL_MODE)
                    AppState.updateMyTravelTab("Upcoming")
                } else {
                    AppState.updateMyTravelMode(CREATOR_TRAVEL_MODE)
                    AppState.updateMyTravelTab("My trips")
                }
            }, if (currentMode == "Creator") "Explorer" else "Creator",
            modifier = Modifier
                .padding(end = 20.dp)
                .align(Alignment.BottomEnd)
        )
    }
}

@Composable // "travel/{travelId}" and "travel/{travelId}/action/{action}"
fun RouteTravel(entry: NavBackStackEntry, navCont: NavHostController) {
    val actions = remember(navCont) { Actions(navCont) }
    val id = entry.arguments?.getString("travelId")
    val action = entry.arguments?.getString("action")

    if (id != null) {
        val viewModel: TravelViewModel =
            viewModel(factory = TravelViewModelFactory(travelId=id) )

        TravelProposalScreen(viewModel, navCont, action)
        Box(modifier = Modifier.padding(16.dp)) {
            val clickFunc = {
                actions.navigateBack()
            }
            ArrowBackIcon(clickFunc = clickFunc)
        }
    }
}


@Composable // "travel/{travelId}/itinerary/{itineraryId}"
fun RouteTravelItinerary(entry: NavBackStackEntry, navCont: NavHostController) {
    val id = entry.arguments?.getString("travelId")
    val itineraryId = entry.arguments?.getInt("itineraryId")

    if (id != null) {
        val travelTab = AppState.getTravelOfTab()

        val travel: Travel? =
            if(travelTab!=null && id==travelTab.travelId) travelTab
            else produceState<Travel?>(initialValue = null) {
                value = CommonModel.getTravelById(id)
            }.value

        if(travel != null){
            val itinerary = travel.travelItinerary?.firstOrNull{
                it.itineraryId == itineraryId
            }

            if (itinerary != null) {
                ViewItineraryScreen(travel, itinerary)
            }
        }
    }
}

@Composable // "travel/{travelId}/fullscreen/{imageIndex}"
fun RouteTravelImages(entry: NavBackStackEntry, navCont: NavHostController) {
    val actions = remember(navCont) { Actions(navCont) }
    val theTravelModel = remember { TheTravelModel() }

    val id = entry.arguments?.getString("travelId")
    val index = entry.arguments?.getInt("imageIndex") ?: 0

    if (id != null) {
        val travelTab = AppState.getTravelOfTab()

        val travelImages: List<TravelImage>? =
            if(travelTab!=null && id==travelTab.travelId) travelTab.travelImages
            else produceState<List<TravelImage>?>(initialValue = null) {
                Log.e("getTravelImages", "Loading travel images...")
                value = theTravelModel.getTravelImages(id)
                Log.e("getTravelImages", "Travel Images loaded: ${value?.size ?: 0}")
            }.value

        when{
            travelImages == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            travelImages.isNotEmpty() -> {
                FullscreenImageViewer(travelImages, startIndex = index)

                Box(modifier = Modifier.padding(16.dp)) {
                    val clickFunc = {
                        actions.navigateBack()
                    }
                    ArrowBackIcon(clickFunc = clickFunc)
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No images available.")
                }
            }
        }
    }
}

@Composable // "profile/{userId}"
fun RouteUser(entry: NavBackStackEntry, navCont: NavHostController) {
    val id = entry.arguments?.getString("userId")

    if (id != null) {
        val viewModel: UserInfoViewModel =
            viewModel(factory = ProfileViewModelFactory(userInfo=null, isOwnProfile=false, userId=id))
        UserProfile(vm = viewModel, true, {}, {}, navCont)
    }
}
/*

@Composable //
fun Route(entry: NavBackStackEntry, navCont: NavHostController) {

}
*/
