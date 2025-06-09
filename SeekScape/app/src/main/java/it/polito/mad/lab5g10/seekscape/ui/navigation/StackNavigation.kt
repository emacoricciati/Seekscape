package it.polito.mad.lab5g10.seekscape.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*

import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import it.polito.mad.lab5g10.seekscape.cleanStack
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.TheTravelModel
import it.polito.mad.lab5g10.seekscape.firebase.TheUserModel
import it.polito.mad.lab5g10.seekscape.firebase.unknown_User
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.ItineraryViewModel
import it.polito.mad.lab5g10.seekscape.models.ItineraryViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.CREATOR_TRAVEL_MODE
import it.polito.mad.lab5g10.seekscape.models.EXPLORE_TRAVEL_MODE
import it.polito.mad.lab5g10.seekscape.models.OwnedTravelViewModel
import it.polito.mad.lab5g10.seekscape.models.OwnedTravelsViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.ProfileViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.Request
import it.polito.mad.lab5g10.seekscape.models.RequestViewModel
import it.polito.mad.lab5g10.seekscape.models.RequestsViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.Search
import it.polito.mad.lab5g10.seekscape.models.SearchViewModel
import it.polito.mad.lab5g10.seekscape.models.SearchViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelCompanion
import it.polito.mad.lab5g10.seekscape.models.TravelDuplicator
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.models.TravelReviewViewModel
import it.polito.mad.lab5g10.seekscape.models.TravelReviewViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.TravelViewModel
import it.polito.mad.lab5g10.seekscape.models.TravelViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.User
import it.polito.mad.lab5g10.seekscape.models.UserInfoViewModel
import it.polito.mad.lab5g10.seekscape.models.deepCopy
import it.polito.mad.lab5g10.seekscape.models.getBlankItinerary
import it.polito.mad.lab5g10.seekscape.models.getBlankTravel
import it.polito.mad.lab5g10.seekscape.models.getBlankTravelReview
import it.polito.mad.lab5g10.seekscape.services.AccountService
import it.polito.mad.lab5g10.seekscape.ui.profile.CompleteRegistrationScreen
import it.polito.mad.lab5g10.seekscape.ui.profile.LoginScreen
import it.polito.mad.lab5g10.seekscape.ui.UnloggedUserScreen
import it.polito.mad.lab5g10.seekscape.ui._common.ArrowBackIcon
import it.polito.mad.lab5g10.seekscape.ui._common.FullscreenImageViewer
import it.polito.mad.lab5g10.seekscape.ui.add.AddItinerary
import it.polito.mad.lab5g10.seekscape.ui.add.AddTravelsScreen
import it.polito.mad.lab5g10.seekscape.ui.explore.ExploreTravelsScreen
import it.polito.mad.lab5g10.seekscape.ui.profile.EditAccountScreen
import it.polito.mad.lab5g10.seekscape.ui.profile.ProfileTabScreenView
import it.polito.mad.lab5g10.seekscape.ui.profile.SignupScreen
import it.polito.mad.lab5g10.seekscape.ui.profile.UserProfile
import it.polito.mad.lab5g10.seekscape.ui.profile.UserProfileScreen
import it.polito.mad.lab5g10.seekscape.ui.review.AddReviewScreen
import it.polito.mad.lab5g10.seekscape.ui.travels.ApplyToJoinView
import it.polito.mad.lab5g10.seekscape.ui.travels.ChangeModeButton
import it.polito.mad.lab5g10.seekscape.ui.travels.MyTravelsScreen
import it.polito.mad.lab5g10.seekscape.ui.travels.TravelProposalScreen
import it.polito.mad.lab5g10.seekscape.ui.travels.ViewItineraryScreen
import kotlinx.coroutines.launch

object Destinations {
    const val TRAVEL = "travel"
    const val APPLY = "applyToJoin"
    const val EDIT = "edit"
    const val ITINERARY = "itinerary"
    const val ADD = "add"
    const val PROFILE = "profile"
    const val EXPLORE = "explore"
    const val REVIEW = "review"
    const val TRAVELS = "travels"
    const val COPY = "copy"

}

class Actions(private val navCont: NavHostController) {

    val navigateTo: (String) -> Unit = { path ->
        navCont.navigate(path)
    }

    val seeTravel: (String) -> Unit = { id ->
        navCont.navigate(Destinations.TRAVEL + "/${id}")
    }

    val seeProfile: (String) -> Unit = {
        id->
        navCont.navigate(Destinations.PROFILE + "/${id}")
    }

    val applyToJoin: (String) -> Unit = { id ->
        navCont.navigate(Destinations.TRAVEL + "/${id}" + "/" + Destinations.APPLY)
    }

    val seeTravelItinerary: (String, Int) -> Unit = {
        idTravel, idIndex ->
        navCont.navigate(Destinations.TRAVEL + "/${idTravel}" + "/" + Destinations.ITINERARY + "/${idIndex}")
    }

    val editTravel: (String) -> Unit = { id ->
        navCont.navigate(Destinations.TRAVEL + "/${id}" + "/" + Destinations.EDIT)
    }

    /*
    val addItineraryfromCopy: (String) -> Unit = { id ->
        navCont.navigate(Destinations.TRAVEL + "/" + id + "/" + Destinations.COPY + "/" + Destinations.ITINERARY)
    }

    val editItineraryfromCopy: (String, Int) -> Unit = { travelId, itineraryId ->
        navCont.navigate(Destinations.TRAVEL + "/" +  travelId + "/" +  Destinations.COPY + "/" + Destinations.ITINERARY + "/" + itineraryId + "/" + Destinations.EDIT)
    }
     */

    val navigateToCopyTravelItinerary: (String) -> Unit = { travelId ->
        navCont.navigate(Destinations.ADD + "/${travelId}" + "/" + Destinations.COPY + "/" + Destinations.ITINERARY)
    }

    val addItineraryfromEdit: (String) -> Unit = { id ->
        navCont.navigate(Destinations.TRAVEL + "/${id}" + "/" + Destinations.EDIT + "/" + Destinations.ITINERARY)
    }

    val editItineraryfromEdit: (String, Int) -> Unit = { travelId, itineraryId ->
        navCont.navigate(Destinations.TRAVEL + "/${travelId}" + "/" + Destinations.EDIT + "/" + Destinations.ITINERARY + "/" + itineraryId + "/" + Destinations.EDIT)
    }

    val addNewTravel: () -> Unit = {
        navCont.navigate(Destinations.ADD)
    }

    val addItinerary: () -> Unit = {
        navCont.navigate(Destinations.ADD + "/" + Destinations.ITINERARY)
    }

    val editItineraryFromAdd: (Int) -> Unit = { id ->
        navCont.navigate(Destinations.ADD + "/" + Destinations.ITINERARY + "/${id}" + "/" + Destinations.EDIT)
    }

    val editProfile: () -> Unit = {
        navCont.navigate(Destinations.PROFILE + "/" + Destinations.EDIT)
    }

    val navigateToFullScreen: (String, Int) -> Unit = { id, index ->
        navCont.navigate(Destinations.TRAVEL + "/${id}" + "/" + "fullscreen" + "/${index}")
    }

    val reviewTravel: (String) -> Unit = {id ->
        navCont.navigate(Destinations.TRAVEL + "/${id}" + "/" + Destinations.REVIEW)
    }

    val backToHome: () -> Unit = {
        navCont.navigate(Destinations.EXPLORE)
    }

    val backToTravelsTab: () -> Unit = {
        navCont.navigate(Destinations.TRAVELS)
    }
    val backToProfileTab: () -> Unit = {
        navCont.navigate(Destinations.PROFILE)
    }

    val navigateBack: () -> Unit = {

        val navigated = navCont.popBackStack()
        if (!navigated) {
            val tab = AppState.currentTab.value
            if(tab==MainDestinations.HOME_ROUTE) {
                backToHome()
            }
            if(tab==MainDestinations.TRAVELS_ROUTE) {
                backToTravelsTab()
            }
            if(tab==MainDestinations.ADD_ROUTE) {
                addNewTravel()
            }
            if(tab==MainDestinations.PROFILE_ROUTE) {
                backToProfileTab()
            }
        }
    }

}

@Composable
fun StackNavigation(
    navCont: NavHostController,
    tab: String
): Actions {
    val actions = remember(navCont) { Actions(navCont) }
    val theUserModel = remember { TheUserModel() }
    val theTravelModel = remember { TheTravelModel() }
    val scope = rememberCoroutineScope()

    when (tab) {
        "explore" ->
            NavHost(navController = navCont, startDestination = "explore") {

                composable("explore") {
                    val search = Search()
                    val vm: SearchViewModel = viewModel(factory = SearchViewModelFactory(search))
                    ExploreTravelsScreen(vm = vm, navController = navCont)
                }

                composable(
                    "travel/{travelId}",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("travelId")
                    if (id != null) {
                        val viewModel: TravelViewModel =
                            viewModel(factory = TravelViewModelFactory(travelId=id) )

                        TravelProposalScreen(viewModel, navCont)
                        Box(modifier = Modifier.padding(16.dp)) {
                            val clickFunc = {
                                actions.navigateBack()
                            }
                            ArrowBackIcon(clickFunc = clickFunc)
                        }
                    }
                }

                composable("travel/{travelId}/itinerary/{itineraryId}",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType },
                        navArgument("itineraryId") { type = NavType.IntType })){
                    entry ->
                        val id = entry.arguments?.getString("travelId")
                        val itineraryId = entry.arguments?.getInt("itineraryId")

                        if (id != null) {
                            val travel = produceState<Travel?>(initialValue = null) {
                                value = CommonModel.getTravelById(id)
                            }
                            if(travel.value != null){
                                val itinerary = travel.value!!.travelItinerary?.firstOrNull{
                                    it.itineraryId == itineraryId
                                }

                                if (itinerary != null) {
                                    ViewItineraryScreen(travel.value!!, itinerary)
                                }
                            }
                        }
                }

                composable(
                    "travel/{travelId}/applyToJoin",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("travelId")

                    if (id != null) {
                        val travel = produceState<Travel?>(initialValue = null) {
                            value = CommonModel.getTravelById(id)
                        }

                        if(travel.value != null)
                            ApplyToJoinView(travel.value!!, navCont)
                    }
                }

                composable(
                    "travel/{travelId}/fullscreen/{imageIndex}",
                    arguments = listOf(
                        navArgument("travelId") { type = NavType.StringType },
                        navArgument("imageIndex") { type = NavType.IntType }
                    )
                ) { entry ->
                    val id = entry.arguments?.getString("travelId")
                    val index = entry.arguments?.getInt("imageIndex") ?: 0

                    if (id != null) {
                        val travelImagesState = produceState<List<TravelImage>?>(initialValue = null) {
                            Log.e("getTravelImages", "Loading travel images...")
                            value = theTravelModel.getTravelImages(id)
                            Log.e("getTravelImages", "Travel Images loaded: ${value?.size ?: 0}")
                        }
                        //val travel=getBlankTravel(unknown_User)//TODO change this, get the travel by id
                        val travelImages = travelImagesState.value
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

                composable(
                    "profile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("userId")

                    if (id != null) {
                        val user = AppState.myProfile.collectAsState().value
                        val viewModel: UserInfoViewModel =
                            viewModel(factory = ProfileViewModelFactory(userInfo=null, isOwnProfile=false, userId=id))
                        UserProfile(vm = viewModel, true, {}, {}, navCont)
                    }
                }
                composable("profile/unlogged") {
                    UnloggedUserScreen(navCont)
                }
            }

        "travels" ->
            NavHost(navController = navCont, startDestination = "travels") {
                composable("travels") {

                    if (!AppState.isLogged.collectAsState().value) {
                        actions.navigateTo("profile/unlogged")
                        return@composable
                    }

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
                            currentMode
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
                            },
                            if (currentMode == "Creator") "Explorer" else "Creator",
                            modifier = Modifier
                                .padding(end = 20.dp)
                                .align(Alignment.BottomEnd)
                        )
                    }
                }

                composable(
                    "travels/action/{action}",
                    arguments = listOf(navArgument("action") { type = NavType.StringType })
                ) { entry ->
                    val action = entry.arguments?.getString("action")

                    if (!AppState.isLogged.collectAsState().value) {
                        actions.navigateTo("profile/unlogged")
                        return@composable
                    }

                    val currentMode = AppState.myTravelMode.collectAsState().value

                    // Load owned travels and requests asynchronously
                    val ownedTravelsState = produceState<List<Travel>?>(initialValue = null) {
                        Log.e("OwnedTravels", "Loading owned travels...")
                        value = theTravelModel.getOwnedTravels()
                        Log.e("OwnedTravels", "Owned travels loaded: ${value?.size ?: 0}")
                    }
                    val requestsState = produceState<List<Request>?>(initialValue = null) {
                        Log.e("RequestTab", "Loading requests...")
                        value = theTravelModel.getRequestsToMyTrips()
                        Log.e("RequestTab", "Requests loaded: ${value?.size ?: 0}")
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

                composable(
                    "travel/{travelId}/action/{action}",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
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

                //NEW
                composable(
                    "travel/{travelId}",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("travelId")
                    if (id != null) {
                        val viewModel: TravelViewModel =
                            viewModel(factory = TravelViewModelFactory(travelId=id) )

                        TravelProposalScreen(viewModel, navCont)
                        Box(modifier = Modifier.padding(16.dp)) {
                            val clickFunc = {
                                actions.navigateBack()
                            }
                            ArrowBackIcon(clickFunc = clickFunc)
                        }
                    }
                }

                composable("travel/{travelId}/itinerary/{itineraryId}",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType },
                        navArgument("itineraryId") { type = NavType.IntType })){
                        entry ->
                    val id = entry.arguments?.getString("travelId")
                    val itineraryId = entry.arguments?.getInt("itineraryId")

                    if (id != null) {
                        val travel = produceState<Travel?>(initialValue = null) {
                            value = CommonModel.getTravelById(id)
                            Log.d("ITINERARY ID RICEVUTO", value?.travelItinerary?.find { it.itineraryId == itineraryId }
                                .toString())
                        }
                        if(travel.value != null){
                            val itinerary = travel.value!!.travelItinerary?.firstOrNull{
                                it.itineraryId == itineraryId
                            }

                            if (itinerary != null) {
                                ViewItineraryScreen(travel.value!!, itinerary)
                            }
                        }
                    }
                }

                composable("travel/{travelId}/review", arguments = listOf(
                    navArgument("travelId") { type = NavType.StringType })){entry ->
                    val id = entry.arguments?.getString("travelId")

                    if (id != null) {
                        val review = getBlankTravelReview(
                            AppState.myProfile.collectAsState().value, id)
                        val viewModel: TravelReviewViewModel =
                            viewModel(factory = TravelReviewViewModelFactory(review))
                        AddReviewScreen(viewModel, navCont)
                    }

                }

                composable(
                    "travel/{travelId}/fullscreen/{imageIndex}",
                    arguments = listOf(
                        navArgument("travelId") { type = NavType.StringType },
                        navArgument("imageIndex") { type = NavType.IntType }
                    )
                ) { entry ->
                    val id = entry.arguments?.getString("travelId")
                    val index = entry.arguments?.getInt("imageIndex") ?: 0

                    if (id != null) {

                        val travel=getBlankTravel(unknown_User)//TODO change this, get the travel by id

                        travel.travelImages?.takeIf { it.isNotEmpty() }?.let { images ->
                            FullscreenImageViewer(images, startIndex = index)
                            Box(modifier = Modifier.padding(16.dp)) {
                                val clickFunc = {
                                    actions.navigateBack()
                                }
                                ArrowBackIcon(clickFunc = clickFunc)
                            }
                        }
                    }
                }

                composable(
                    "travel/{travelId}/edit",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("travelId")

                    if (id != null) {

                        val viewModel: TravelViewModel =
                            viewModel(factory = TravelViewModelFactory(travel=null, travelId = id))

                        AddTravelsScreen(viewModel, navCont, "edit")
                    }
                }

                composable(
                    "travel/{travelId}/edit/itinerary",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val parentEntry = remember(navCont) {
                        navCont.getBackStackEntry("travel/{travelId}/edit")
                    }
                    val travelViewModel = viewModel<TravelViewModel>(
                        parentEntry
                    )
                    val viewModel: ItineraryViewModel =
                        viewModel(
                            backStackEntry,
                            factory = ItineraryViewModelFactory(getBlankItinerary())
                        )

                    val itineraryId = travelViewModel.travelItineraryValues.collectAsState().value.size
                    AddItinerary(viewModel, travelViewModel, navCont, itineraryId+ 1)
                }

                composable(
                    "travel/{travelId}/edit/itinerary/{itineraryId}/edit",
                    arguments = listOf(navArgument("itineraryId") { type = NavType.IntType })
                ) { entry ->
                    val parentEntry = remember(navCont) {
                        navCont.getBackStackEntry("travel/{travelId}/edit")
                    }
                    val travelViewModel = viewModel<TravelViewModel>(
                        parentEntry
                    )
                    val itineraryId = entry.arguments?.getInt("itineraryId")

                    if (itineraryId != null) {
                        val itinerary = travelViewModel.travelItineraryValues.collectAsState().value
                            .firstOrNull { it.itineraryId == itineraryId }
                        if (itinerary != null) {
                            val viewModel: ItineraryViewModel =
                                viewModel(factory = ItineraryViewModelFactory(itinerary))
                            AddItinerary(viewModel, travelViewModel, navCont, itineraryId)
                        }
                    }
                }

                composable(
                    "profile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("userId")

                    if (id != null) {
                        val user = AppState.myProfile.collectAsState().value
                        val viewModel: UserInfoViewModel =
                            viewModel(factory = ProfileViewModelFactory(userInfo=null, isOwnProfile=false, userId=id))
                        UserProfile(vm = viewModel, true, {}, {}, navCont)
                    }
                }

                composable("profile/unlogged") {
                    UnloggedUserScreen(navCont)
                }
            }

        "add" ->{
            LaunchedEffect(AppState.redirectPath.collectAsState().value) {
                val path = AppState.redirectPath.value
                if (path.isNotBlank()) {
                    navCont.navigate(path)
                    AppState.updateRedirectPath("")
                }
            }
            NavHost(navController = navCont, startDestination = "add") {
                composable("add") { backStackEntry ->

                    val currentUser = AppState.myProfile.collectAsState().value
                    val blankTravel = getBlankTravel(currentUser)
                    val viewModel: TravelViewModel =
                        viewModel(backStackEntry, factory = TravelViewModelFactory(blankTravel))
                    AddTravelsScreen(viewModel, navCont, "add")
                }

                composable("add/itinerary") { backStackEntry ->
                    val parentEntry = remember(navCont) {
                        navCont.getBackStackEntry("add")
                    }
                    val travelViewModel = viewModel<TravelViewModel>(
                        parentEntry
                    )
                    val blankItinerary = getBlankItinerary()
                    val viewModel: ItineraryViewModel =
                        viewModel(
                            backStackEntry,
                            factory = ItineraryViewModelFactory(blankItinerary)
                        )
                    val itineraryId = travelViewModel.travelItineraryValues.collectAsState().value.size
                    AddItinerary(viewModel, travelViewModel, navCont, itineraryId+ 1)
                }

                composable(
                    "add/itinerary/{itineraryId}/edit",
                    arguments = listOf(navArgument("itineraryId") { type = NavType.IntType })
                ) { entry ->
                    val parentEntry = remember(navCont) {
                        navCont.getBackStackEntry("add")
                    }
                    val travelViewModel = viewModel<TravelViewModel>(
                        parentEntry
                    )
                    val itineraryId = entry.arguments?.getInt("itineraryId")

                    if (itineraryId != null) {
                        val itinerary = travelViewModel.travelItineraryValues.collectAsState().value
                            .firstOrNull { it.itineraryId == itineraryId }
                        if (itinerary != null) {
                            val viewModel: ItineraryViewModel =
                                viewModel(factory = ItineraryViewModelFactory(itinerary))
                            AddItinerary(viewModel, travelViewModel, navCont, itineraryId)
                        }
                    }
                }

                composable(
                    "add/{travelId}/copy", arguments = listOf(
                        navArgument("travelId") { type = NavType.StringType })
                ){entry->
                    val context = LocalContext.current
                    val id = entry.arguments?.getString("travelId")
                    val duplicator = TravelDuplicator(context)
                    if (id != null) {
                        val newTravel = produceState<Travel?>(initialValue = null) {
                            value = duplicator.duplicateTravel(id)
                        }

                        newTravel.value?.let {
                            val viewModel = viewModel<TravelViewModel>(
                                viewModelStoreOwner = entry,
                                factory = TravelViewModelFactory(it)
                            )
                            AddTravelsScreen(viewModel, navCont, "copy")
                        }
                    }
                }

                composable(
                    "add/{travelId}/copy/itinerary",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navCont.previousBackStackEntry
                    }

                    if (parentEntry != null) {
                        val travelViewModel = viewModel<TravelViewModel>(parentEntry)
                        val viewModel: ItineraryViewModel = viewModel(
                            backStackEntry,
                            factory = ItineraryViewModelFactory(getBlankItinerary())
                        )
                        val itineraryId = travelViewModel.travelItineraryValues.collectAsState().value.size
                        AddItinerary(viewModel, travelViewModel, navCont, itineraryId+ 1)
                    } else {
                        Log.e("Navigation", "Parent entry not found")
                    }
                }

                composable(
                    "add/{travelId}/copy/itinerary/{itineraryId}/edit",
                    arguments = listOf(navArgument("itineraryId") { type = NavType.IntType })
                ) { entry ->
                    val parentEntry = remember(navCont) {
                        navCont.getBackStackEntry("add/{travelId}/copy")
                    }
                    val travelViewModel = viewModel<TravelViewModel>(
                        parentEntry
                    )
                    val itineraryId = entry.arguments?.getInt("itineraryId")
                    println(itineraryId)
                    if (itineraryId != null) {
                        val itinerary = travelViewModel.travelItineraryValues.collectAsState().value
                            .firstOrNull { it.itineraryId == itineraryId }
                        if (itinerary != null) {
                            val viewModel: ItineraryViewModel =
                                viewModel(factory = ItineraryViewModelFactory(itinerary))
                            AddItinerary(viewModel, travelViewModel, navCont, itineraryId)
                        }
                    }
                }

                composable(
                    "profile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("userId")

                    if (id != null) {
                        val user = AppState.myProfile.collectAsState().value
                        val viewModel: UserInfoViewModel =
                            viewModel(factory = ProfileViewModelFactory(userInfo=null, isOwnProfile=false, userId=id))
                        UserProfile(vm = viewModel, true, {}, {}, navCont)
                    }
                }

                composable("profile/unlogged") {
                    UnloggedUserScreen(navCont)
                }

            }
        }


        "profile" ->
            NavHost(navController = navCont, startDestination = "profile") {
                composable("profile") {



                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            ProfileTabScreenView(navCont)
                        }
                    }
                }

                composable("login") {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            LoginScreen(navCont)
                        }
                    }
                }

                composable("signup") {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            SignupScreen(navCont)
                        }
                    }
                }

                composable(
                    "profile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("userId")
                    var user: User? = null

                    if (id != null) {
                        val viewModel: UserInfoViewModel =
                            viewModel(factory = ProfileViewModelFactory(userInfo=null, isOwnProfile=false, userId=id))
                        UserProfile(vm = viewModel, true, {}, {}, navCont)
                    }
                }

                composable("profile/edit"){
                    val user = AppState.myProfile.collectAsState().value
                    val viewModel: UserInfoViewModel =
                        viewModel(factory = ProfileViewModelFactory(userInfo=user, isOwnProfile=true))

                    viewModel.isEditing = true

                    UserProfileScreen(viewModel, true, navCont)
                }

                composable(
                    "travel/{travelId}",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("travelId")
                    var travel: Travel? = null

                    if (id != null) {
                        LaunchedEffect(id) {
                            scope.launch {
                                    travel = CommonModel.getTravelById(id)
                            }
                        }

                        val viewModel: TravelViewModel =
                            viewModel(factory = travel?.let { TravelViewModelFactory(it) })

                        TravelProposalScreen(viewModel, navCont)
                        Box(modifier = Modifier.padding(16.dp)) {
                            val clickFunc = {
                                actions.navigateBack()
                            }
                            ArrowBackIcon(clickFunc = clickFunc)
                        }
                    }
                }

                composable("complete_registration") {
                    CompleteRegistrationScreen(navCont)
                }

                composable("profile/account") {
                    val accountService = AccountService()
                    val isGoogleAccount = accountService.isGoogleAccount()
                    EditAccountScreen(navCont, isGoogleAccount)
                }

                composable("profile/unlogged") {
                    UnloggedUserScreen(navCont)
                }
            }
    }

    val selectedTab = AppState.currentTab.collectAsState().value
    val redirectPath = AppState.redirectPath.collectAsState().value
    LaunchedEffect(navCont, redirectPath) {
        if (redirectPath.isNotEmpty() && selectedTab == tab) {
            cleanStack(navCont, redirectPath)
            AppState.updateRedirectPath("")
        }
    }


    return actions
}