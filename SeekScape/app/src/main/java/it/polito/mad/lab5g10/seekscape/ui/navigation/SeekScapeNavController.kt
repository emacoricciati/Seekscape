package it.polito.mad.lab5g10.seekscape.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

object MainDestinations {
    const val HOME_ROUTE = "explore"
    const val TRAVELS_ROUTE = "travels"
    const val ADD_ROUTE = "add"
    const val PROFILE_ROUTE = "profile"
}

@Composable
fun rememberSeekScapeNavController(
    navController: NavHostController = rememberNavController()
): SeekScapeNavController = remember(navController) {
    SeekScapeNavController(navController)
}

@Stable
class SeekScapeNavController (val navController: NavHostController) {

}
