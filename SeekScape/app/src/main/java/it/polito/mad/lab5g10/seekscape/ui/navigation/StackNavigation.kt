package it.polito.mad.lab5g10.seekscape.ui.navigation

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import it.polito.mad.lab5g10.seekscape.cleanStack
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.ItineraryViewModel
import it.polito.mad.lab5g10.seekscape.models.ItineraryViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.ProfileViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.Search
import it.polito.mad.lab5g10.seekscape.models.SearchViewModel
import it.polito.mad.lab5g10.seekscape.models.SearchViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelDuplicator
import it.polito.mad.lab5g10.seekscape.models.TravelReviewViewModel
import it.polito.mad.lab5g10.seekscape.models.TravelReviewViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.TravelViewModel
import it.polito.mad.lab5g10.seekscape.models.TravelViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.UserInfoViewModel
import it.polito.mad.lab5g10.seekscape.models.getBlankItinerary
import it.polito.mad.lab5g10.seekscape.models.getBlankTravel
import it.polito.mad.lab5g10.seekscape.models.getBlankTravelReview
import it.polito.mad.lab5g10.seekscape.services.AccountService
import it.polito.mad.lab5g10.seekscape.ui.profile.CompleteRegistrationScreen
import it.polito.mad.lab5g10.seekscape.ui.profile.LoginScreen
import it.polito.mad.lab5g10.seekscape.ui.UnloggedUserScreen
import it.polito.mad.lab5g10.seekscape.ui.add.AddItinerary
import it.polito.mad.lab5g10.seekscape.ui.add.AddTravelsScreen
import it.polito.mad.lab5g10.seekscape.ui.explore.ExploreTravelsScreen
import it.polito.mad.lab5g10.seekscape.ui.profile.EditAccountScreen
import it.polito.mad.lab5g10.seekscape.ui.profile.NotificationScreenView
import it.polito.mad.lab5g10.seekscape.ui.profile.ProfileTabScreenView
import it.polito.mad.lab5g10.seekscape.ui.profile.ResetEmailCompletedScreen
import it.polito.mad.lab5g10.seekscape.ui.profile.SignupScreen
import it.polito.mad.lab5g10.seekscape.ui.profile.UserProfileScreen
import it.polito.mad.lab5g10.seekscape.ui.review.AddReviewScreen
import it.polito.mad.lab5g10.seekscape.ui.travels.ApplyToJoinView

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

    val seeTravelChat: (String) -> Unit = { id ->
        navCont.navigate(Destinations.TRAVEL + "/${id}/chat")
    }

    val seeProfile: (String) -> Unit = {
        id->
        if(id!=""){
            navCont.navigate(Destinations.PROFILE + "/${id}")
        }
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

    when (tab) {
        "explore" ->
            NavHost(navController = navCont, startDestination = "explore") {

                composable("explore") {
                    val search = Search()
                    val vm: SearchViewModel = viewModel(factory = SearchViewModelFactory(search))
                    ExploreTravelsScreen(vm = vm, navController = navCont)
                }

                composable(
                    "travel/{travelId}/applyToJoin",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("travelId")

                    if (id != null) {
                        val travelTab = AppState.getTravelOfTab()

                        val travel: Travel? =
                            if(travelTab!=null && id==travelTab.travelId) travelTab
                            else produceState<Travel?>(initialValue = null) {
                                value = CommonModel.getTravelById(id)
                            }.value

                        if(travel != null)
                            ApplyToJoinView(travel, navCont)
                    }
                }


                // Common routes, same implementation

                composable(
                    "travel/{travelId}",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType }),
                    deepLinks = listOf(
                        navDeepLink {
                            uriPattern = "app://travel/{travelId}"          //insert the url
                            action = Intent.ACTION_VIEW
                        }
                    )

                ) { entry ->
                    val doneFirstFetch = AppState.doneFirstFetch.collectAsState().value

                    if(doneFirstFetch)
                     RouteTravel(entry, navCont)
                    else
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                }

                composable("travel/{travelId}/itinerary/{itineraryId}",
                    arguments = listOf(
                        navArgument("travelId") { type = NavType.StringType },
                        navArgument("itineraryId") { type = NavType.IntType }
                    )
                ) { entry ->
                    RouteTravelItinerary(entry, navCont)
                }

                composable(
                    "travel/{travelId}/chat",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    RouteTravelChat(entry, navCont)
                }

                composable(
                    "travel/{travelId}/fullscreen/{imageIndex}",
                    arguments = listOf(
                        navArgument("travelId") { type = NavType.StringType },
                        navArgument("imageIndex") { type = NavType.IntType }
                    )
                ) { entry ->
                    RouteTravelImages(entry, navCont)
                }

                composable(
                    "profile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { entry ->
                    RouteUser(entry, navCont)
                }

                composable("profile/unlogged") {
                    UnloggedUserScreen(navCont)
                }

                composable(
                    route = "profile/reset_email_completed?uid={uid}&email={email}",
                    arguments = listOf(
                        navArgument("uid") { type = NavType.StringType; defaultValue = "" },
                        navArgument("email") { type = NavType.StringType; defaultValue = "" }
                    )
                ) { backStackEntry ->
                    val uid = backStackEntry.arguments?.getString("uid") ?: ""
                    val email = backStackEntry.arguments?.getString("email") ?: ""

                    ResetEmailCompletedScreen(navCont, uid, email)
                }
            }



        "travels" ->
            NavHost(navController = navCont, startDestination = "travels") {

                composable("travels") { entry ->
                    if (!AppState.isLogged.collectAsState().value) {
                        actions.navigateTo("profile/unlogged")
                        return@composable
                    }
                    RouteTravels(entry, navCont)
                }

                composable(
                    "travels/action/{action}",
                    arguments = listOf(navArgument("action") { type = NavType.StringType })
                ) { entry ->
                    RouteTravels(entry, navCont)
                }

                composable(
                    "travel/{travelId}/review", arguments = listOf(
                        navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("travelId")

                    if (id != null) {
                        val review = getBlankTravelReview(
                            AppState.myProfile.collectAsState().value, id
                        )
                        val viewModel: TravelReviewViewModel =
                            viewModel(factory = TravelReviewViewModelFactory(review))
                        AddReviewScreen(viewModel, navCont)
                    }

                }

                composable(
                    "travel/{travelId}/edit",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    val id = entry.arguments?.getString("travelId")

                    if (id != null) {

                        val viewModel: TravelViewModel =
                            viewModel(
                                factory = TravelViewModelFactory(
                                    travel = null,
                                    travelId = id
                                )
                            )

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

                    val itineraryId =
                        travelViewModel.travelItineraryValues.collectAsState().value.size
                    AddItinerary(viewModel, travelViewModel, navCont, itineraryId + 1)
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


                // Common routes, same implementation

                composable(
                    "travel/{travelId}",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    RouteTravel(entry, navCont)
                }

                composable(
                    "travel/{travelId}/action/{action}",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    RouteTravel(entry, navCont)
                }

                composable(
                    "travel/{travelId}/chat",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    RouteTravelChat(entry, navCont)
                }

                composable("travel/{travelId}/itinerary/{itineraryId}",
                    arguments = listOf(
                        navArgument("travelId") { type = NavType.StringType },
                        navArgument("itineraryId") { type = NavType.IntType }
                    )
                ) { entry ->
                    RouteTravelItinerary(entry, navCont)
                }

                composable(
                    "travel/{travelId}/fullscreen/{imageIndex}",
                    arguments = listOf(
                        navArgument("travelId") { type = NavType.StringType },
                        navArgument("imageIndex") { type = NavType.IntType }
                    )
                ) { entry ->
                    RouteTravelImages(entry, navCont)
                }

                composable(
                    "profile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { entry ->
                    RouteUser(entry, navCont)
                }

                composable("profile/unlogged") {
                    UnloggedUserScreen(navCont)
                }
            }



        "add" -> {

            LaunchedEffect(AppState.redirectPath.collectAsState().value) {
                val path = AppState.redirectPath.value
                if (path.isNotBlank()) {
                    navCont.navigate(path)
                    AppState.updateRedirectPath("")
                }
            }

            NavHost(navController = navCont, startDestination = "add") {

                composable("add") { entry ->
                    val currentUser = AppState.myProfile.collectAsState().value
                    val blankTravel = getBlankTravel(currentUser)
                    val viewModel: TravelViewModel =
                        viewModel(entry, factory = TravelViewModelFactory(blankTravel))
                    AddTravelsScreen(viewModel, navCont, "add")
                }

                composable("add/itinerary") { entry ->
                    val parentEntry = remember(navCont) {
                        navCont.getBackStackEntry("add")
                    }
                    val travelViewModel = viewModel<TravelViewModel>(
                        parentEntry
                    )
                    val blankItinerary = getBlankItinerary()
                    val viewModel: ItineraryViewModel =
                        viewModel(
                            entry,
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
                    "add/{travelId}/copy", arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry->
                    val context = LocalContext.current
                    val id = entry.arguments?.getString("travelId")
                    val duplicator = TravelDuplicator(context)
                    val isLoading = remember { mutableStateOf(true) }
                    if (id != null) {
                        val newTravel = produceState<Travel?>(initialValue = null){
                            value = duplicator.duplicateTravel(id)
                            isLoading.value = false
                        }

                        if(isLoading.value){
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        else{
                            newTravel.value?.let {
                                val viewModel = viewModel<TravelViewModel>(
                                    viewModelStoreOwner = entry,
                                    factory = TravelViewModelFactory(it)
                                )
                                AddTravelsScreen(viewModel, navCont, "copy")
                            }
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
                    RouteUser(entry, navCont)
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

                composable("profile/edit"){
                    val user = AppState.myProfile.collectAsState().value
                    val viewModel: UserInfoViewModel =
                        viewModel(factory = ProfileViewModelFactory(userInfo=user, isOwnProfile=true))

                    viewModel.isEditing = true

                    UserProfileScreen(viewModel, true, navCont)
                }

                composable("profile/account") {
                    val accountService = AccountService()
                    val isGoogleAccount = accountService.isGoogleAccount()
                    EditAccountScreen(navCont, isGoogleAccount)
                }

                composable("profile/notifications") {
                    NotificationScreenView(navCont)
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

                composable("complete_registration") {
                    CompleteRegistrationScreen(navCont)
                }

                composable("profile/unlogged") {
                    UnloggedUserScreen(navCont)
                }


                // Common routes, same implementation

                composable(
                    "travel/{travelId}",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    RouteTravel(entry, navCont)
                }

                composable(
                    "travel/{travelId}/chat",
                    arguments = listOf(navArgument("travelId") { type = NavType.StringType })
                ) { entry ->
                    RouteTravelChat(entry, navCont)
                }

                composable("travel/{travelId}/itinerary/{itineraryId}",
                    arguments = listOf(
                        navArgument("travelId") { type = NavType.StringType },
                        navArgument("itineraryId") { type = NavType.IntType }
                    )
                ) { entry ->
                    RouteTravelItinerary(entry, navCont)
                }

                composable(
                    "travel/{travelId}/fullscreen/{imageIndex}",
                    arguments = listOf(
                        navArgument("travelId") { type = NavType.StringType },
                        navArgument("imageIndex") { type = NavType.IntType }
                    )
                ) { entry ->
                    RouteTravelImages(entry, navCont)
                }

                composable(
                    "profile/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { entry ->
                    RouteUser(entry, navCont)
                }
            }


    }

    val selectedTab = AppState.currentTab.collectAsState().value
    val doneFirstFetch = AppState.doneFirstFetch.collectAsState().value
    val redirectPath = AppState.redirectPath.collectAsState().value

    LaunchedEffect(navCont, redirectPath, doneFirstFetch) {
        if (redirectPath.isNotEmpty() && selectedTab == tab && doneFirstFetch) {
            cleanStack(navCont, redirectPath)
            AppState.updateOpenNotification(false)
            AppState.updateRedirectPath("")
        }
    }

    return actions
}