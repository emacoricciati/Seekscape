
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.CREATOR_TRAVEL_MODE
import it.polito.mad.lab5g10.seekscape.models.EXPLORE_TRAVEL_MODE
import it.polito.mad.lab5g10.seekscape.models.NOT_ACCOUNT
import it.polito.mad.lab5g10.seekscape.models.NOT_APPLY
import it.polito.mad.lab5g10.seekscape.models.NOT_LAST_MINUTE_JOIN
import it.polito.mad.lab5g10.seekscape.models.NOT_MSG
import it.polito.mad.lab5g10.seekscape.models.NOT_MY_PROFILE_REV
import it.polito.mad.lab5g10.seekscape.models.NOT_MY_TRAVEL_REV
import it.polito.mad.lab5g10.seekscape.models.NOT_REMINDER
import it.polito.mad.lab5g10.seekscape.models.NOT_REQ_ACC
import it.polito.mad.lab5g10.seekscape.models.NOT_REQ_DEN
import it.polito.mad.lab5g10.seekscape.models.NotificationItem


fun navigateToNotificationAction(notification: NotificationItem) {
    when (notification.type) {

        NOT_MY_PROFILE_REV ->{
            AppState.updateCurrentTab(notification.tab)
            AppState.updateRedirectPath(notification.navRoute)

        }
        NOT_MY_TRAVEL_REV -> {
            AppState.updateMyTravelMode(CREATOR_TRAVEL_MODE)
            AppState.updateCurrentTab(notification.tab)
            AppState.updateRedirectPath(notification.navRoute)
        }
        NOT_LAST_MINUTE_JOIN -> {
            AppState.updateCurrentTab(notification.tab)
            AppState.updateRedirectPath(notification.navRoute)
        }

        NOT_REQ_ACC -> {
            println(notification.navRoute)
            AppState.updateMyTravelTab("Upcoming")
            AppState.updateMyTravelMode(EXPLORE_TRAVEL_MODE)
            AppState.updateCurrentTab(notification.tab)
            AppState.updateRedirectPath(notification.navRoute)
            //ADD open travel
        }

        NOT_REQ_DEN -> {
            AppState.updateMyTravelTab("Rejected")
            AppState.updateMyTravelMode(EXPLORE_TRAVEL_MODE)
            AppState.updateCurrentTab(notification.tab)
            AppState.updateRedirectPath(notification.navRoute)
        }

        NOT_APPLY -> {
            AppState.updateMyTravelTab("Requests")
            AppState.updateMyTravelMode(CREATOR_TRAVEL_MODE)
            AppState.updateCurrentTab(notification.tab)
            AppState.updateRedirectPath(notification.navRoute)
            //ADD modal apply message
        }

        NOT_REMINDER -> {
        }
        NOT_ACCOUNT -> {
        }
        NOT_MSG -> {
        }
    }
}

fun redirectIfNeeded(navCont: NavHostController){
    val redirectPath = AppState.redirectPath.value
    if(redirectPath!=""){
        AppState.updateRedirectPath("")
        navCont.navigate(redirectPath)
    }
}
/*
fun navigateToNotificationAction(notification: NotificationItem) {
    when (notification.type) {
        NOT_ACCOUNT -> {
        }
        NOT_MSG -> {
        }
        NOT_REQ_ACC -> {
            AppState.updateMyTravelTab("Upcoming")
            AppState.updateMyTravelMode(CREATOR_TRAVEL_MODE)
            val currentTab = notification.navRoute.split("/")[0]
            AppState.updateCurrentTab(currentTab)
            //ADD open travel
        }

        NOT_REQ_DEN -> {
            AppState.updateMyTravelTab("Rejected")
            AppState.updateMyTravelMode(CREATOR_TRAVEL_MODE)
            val currentTab = notification.navRoute.split("/")[0]
            AppState.updateCurrentTab(currentTab)
            //ADD open travel
        }

        NOT_APPLY -> {
            AppState.updateMyTravelTab("Requests")
            AppState.updateMyTravelMode(EXPLORE_TRAVEL_MODE)
            val currentTab = notification.navRoute.split("/")[0]
            AppState.updateCurrentTab(currentTab)
            //ADD modal apply message
        }

        NOT_REMINDER -> {
        }
    }
}
*/
