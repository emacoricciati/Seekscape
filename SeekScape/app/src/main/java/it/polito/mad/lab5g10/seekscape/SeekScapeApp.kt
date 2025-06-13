package it.polito.mad.lab5g10.seekscape

import android.content.Intent
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.ui._common.AppTopBar
import it.polito.mad.lab5g10.seekscape.ui.navigation.StackNavigation
import it.polito.mad.lab5g10.seekscape.ui.navigation.BottomNavigationBar

@Composable
fun SeekScapeApp(
    initialIntent: Intent?
) {
    val tabs = AppState.tabs
    val navControllers = tabs.associateWith { rememberNavController() }
    val selectedTab = AppState.currentTab.collectAsState().value
    var previousTabIndex by remember { mutableIntStateOf(10) }
    val currentNavController = navControllers[selectedTab]!!
    val currentBackStackEntry by currentNavController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val currentTitle = getScreenTitle(currentDestination?.route ?: "SeekScape")
    val isLoggedIn by AppState.isLogged.collectAsState()
    val loggedStatusChanged by AppState.loggedStatusChanged.collectAsState()

    LaunchedEffect(loggedStatusChanged, isLoggedIn) {
        if(loggedStatusChanged){
            if(isLoggedIn) {
                navControllers.forEach {
                    val tab = it.key
                    val currentNav = navControllers[tab]!!
                    val currentRoute = currentNav.currentBackStackEntry?.destination?.route
                    if (currentRoute == "complete_registration") return@LaunchedEffect;

                    if (currentRoute == "profile/unlogged" || currentRoute == "login") {
                        val start = currentNav.graph.startDestinationRoute
                        if (start != null) {
                            currentNav.popBackStack(start, inclusive = false)
                        }
                    }
                }

            } else {
                navControllers.forEach { (tab, navController) ->
                    try{
                        val startRoute = navController.graph.startDestinationRoute
                        if (startRoute != null) {
                            navController.popBackStack(startRoute, inclusive = true)
                            navController.navigate(startRoute)
                        }
                    } catch (e: Exception) {}

                }
            }
            AppState.actionDoneForSwitchLoggedStatus()
        }
    }

    Scaffold(
        topBar = {
            val canGoBack = currentNavController.previousBackStackEntry != null &&
                    currentDestination?.route !in tabs && !currentDestination?.route?.contains("add_location")!!
                    && !currentDestination.route?.contains("unlogged")!! && !currentDestination.route?.contains("complete_registration")!!
            if (currentDestination?.route != "travel/{travelId}" &&
                currentDestination?.route != "travel/{travelId}/action/{action}" &&
                currentDestination?.route != "travel/{travelId}/chat" &&
                currentDestination?.route?.contains("fullscreen") != true &&
                currentDestination?.route?.contains("unlogged") != true &&
                currentDestination?.route?.contains("login") != true &&
                currentDestination?.route?.contains("signup") != true
            ) {
                AppTopBar(currentTitle, currentNavController, canGoBack)
            }
        },
        bottomBar = {
            if (currentDestination?.route?.contains("fullscreen") != true) {
                BottomNavigationBar(
                    currentRoute = selectedTab,
                    onTabSelected = {
                        if(!isLoggedIn && selectedTab != "explore"){                //reset the explore tab whenever I'm not logged in
                            val currentNav = navControllers["explore"]!!
                            val start = currentNav.graph.startDestinationRoute
                            if (start != null) {
                                currentNav.popBackStack(start, inclusive = false)
                            }
                        }
                        AppState.updateCurrentTab(it.route)
                    }
                )
            }
        }
    ) { innerPadding ->

        Box(Modifier.padding(innerPadding)) {
            tabs.forEachIndexed { index, route ->
                val visible = selectedTab == route

                val enterHorizontal = fadeIn() + if (previousTabIndex >= index) {
                    slideInHorizontally(initialOffsetX = { -it }) // Slide in from left
                } else {
                    slideInHorizontally(initialOffsetX = { it })  // Slide in from right (default)
                }
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + enterHorizontal,
                    exit = ExitTransition.None,
                ) {
                    StackNavigation(navControllers[route]!!, route)
                }
            }
        }
        tabs.forEachIndexed { index, route ->
            if (selectedTab == route)
                previousTabIndex = index
        }
    }


}

