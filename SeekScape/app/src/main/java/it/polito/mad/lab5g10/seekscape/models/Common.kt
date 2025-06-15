package it.polito.mad.lab5g10.seekscape.models

import android.app.Activity.MODE_PRIVATE
import android.content.Context
import it.polito.mad.lab5g10.seekscape.firebase.unknown_User
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.collections.listOf


val TRAVEL_TYPES = listOf<String>("Relax","Road Trips", "Culture", "Party", "Adventure", "Sport", "Beach", "Mountain")


const val MIN_DURATION = 1
const val MAX_DURATION = 21
const val MIN_PRICE = 100
const val MAX_PRICE = 5000
const val MIN_COMPANIONS = 2
const val MAX_COMPANIONS = 20

const val CREATOR_TRAVEL_MODE = "Creator"
const val EXPLORE_TRAVEL_MODE = "Explore"


//travel GENERAL statuses:
const val AVAILABLE = "available"
const val FULL = "full"
const val DELETED = "deleted"
const val PAST = "past"

//travel statuses for each user:
const val OWNED = "owned"
const val PENDING = "pending"
const val JOINED = "joined"
const val DENIED = "denied"
const val TO_REVIEW = "to-review"


object AppState {

    // Initialize data from shared preferences
    fun initialize(context: Context) {
        val sharedPreferences = context.getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)
        updateIsDarkMode(sharedPreferences.getBoolean("dark_mode", false), context)
    }

    //------------AUTHENTICATION-----------------------------------------------------------------
    private val _isLogged = MutableStateFlow<Boolean>(false)
    val isLogged: StateFlow<Boolean> = _isLogged.asStateFlow()

    private val _loggedStatusChanged = MutableStateFlow<Boolean>(false)
    val loggedStatusChanged: StateFlow<Boolean> = _loggedStatusChanged.asStateFlow()

    private val _doneFirstFetch = MutableStateFlow<Boolean>(false)
    val doneFirstFetch: StateFlow<Boolean> = _doneFirstFetch.asStateFlow()


    fun doneFirstFetch(){
        _doneFirstFetch.value=true
    }
    fun setUserAsLogged(){
        _isLogged.value = true
        _loggedStatusChanged.value = true
    }
    fun setUserAsUnlogged(){
        _isLogged.value = false
        _loggedStatusChanged.value = true
    }

    fun actionDoneForSwitchLoggedStatus(){
        _loggedStatusChanged.value = false
    }


    //------------MY PROFILE-----------------------------------------------------------------
    private val _myProfile = MutableStateFlow<User>(unknown_User)
    val myProfile: StateFlow<User> = _myProfile.asStateFlow()

    fun updateMyProfile(new: User) {
        _myProfile.value = new
    }

    fun isNotificationPresent(notificationId: String): Boolean {
        val notifications = _myProfile.value.notifications
        return notifications.isNotEmpty() && notifications.any { it.id == notificationId }
    }

    fun removeNotification(notificationId: String) {
        val user = _myProfile.value
        val updatedNotifications = user.notifications.filter { it.id != notificationId }
        _myProfile.value = user.copy(notifications = updatedNotifications)
    }


    private val _actualThemeIsDark = MutableStateFlow<Boolean?>(null)
    val actualThemeIsDark: StateFlow<Boolean?> = _actualThemeIsDark.asStateFlow()
    fun updateActualThemeIsDark(new: Boolean) {
        _actualThemeIsDark.value = new
    }

    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    fun updateIsDarkMode(new: Boolean, context: Context) {
        _isDarkMode.value = new
        val sharedPreferences = context.getSharedPreferences("shared_preferences", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("dark_mode", new)
            apply()
        }
    }

//------------NAVIGATION-----------------------------------------------------------------

    val tabs = listOf(
        MainDestinations.HOME_ROUTE,
        MainDestinations.TRAVELS_ROUTE,
        MainDestinations.ADD_ROUTE,
        MainDestinations.PROFILE_ROUTE
    )

    // GLOBALLY TRIGGERS NAVIGATION BETWEEN TABS
    private val _currentTab = MutableStateFlow<String>(MainDestinations.HOME_ROUTE)
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()
    fun updateCurrentTab(new: String) {
        if(_doneFirstFetch.value && tabs.contains(new) && new!=_currentTab.value){
            _currentTab.value = new
        }
    }

    private val _redirectPath = MutableStateFlow<String>("")
    val redirectPath: StateFlow<String> = _redirectPath.asStateFlow()
    fun updateRedirectPath(new: String) {
        _redirectPath.value = new
    }

    // SAVES INNER NAVIGATION INSIDE THE TRAVELS TAB
    private val _myTravelMode = MutableStateFlow<String>(EXPLORE_TRAVEL_MODE)
    private val _myTravelTab = MutableStateFlow<String>("Upcoming")

    val myTravelMode: StateFlow<String> = _myTravelMode.asStateFlow()
    val myTravelTab: StateFlow<String> = _myTravelTab.asStateFlow()

    fun updateMyTravelMode(new: String) {
        _myTravelMode.value = new
    }
    fun updateMyTravelTab(new: String) {
        _myTravelTab.value = new
    }


//------------SAVES DATA TO AVOID MULTIPLE QUERIES-----------------------------------------------------------------

    private val _travelsTabMap = MutableStateFlow<MutableMap<String, Travel?>>(
        mutableMapOf(
            MainDestinations.HOME_ROUTE to null,
            MainDestinations.TRAVELS_ROUTE to null,
            MainDestinations.ADD_ROUTE to null,
            MainDestinations.PROFILE_ROUTE to null
        )
    )
    fun setTravelToTab(travel: Travel, tab: String=_currentTab.value) {
        val updatedMap = _travelsTabMap.value.toMutableMap()
        updatedMap[tab] = travel
        _travelsTabMap.value = updatedMap
    }
    fun getTravelOfTab(tab: String=_currentTab.value): Travel? {
        return _travelsTabMap.value[tab]
    }


    private val _lastSearchResults = MutableStateFlow<List<Travel>?>(null)
    val lastSearchResults: StateFlow<List<Travel>?> = _lastSearchResults.asStateFlow()
    fun updateLastSearchResults(new: List<Travel>) {
        _lastSearchResults.value = new
    }

    private val _travelsListTabMap = MutableStateFlow<MutableMap<String, List<Travel>?>>(
        mutableMapOf(
            "Upcoming" to null,
            "Pending" to null,
            "Rejected" to null,
            "To Review" to null,
            "Past" to null
        )
    )

    fun setTravelListToTab(travels: List<Travel>, tab: String) {
        val updatedMap = _travelsListTabMap.value.toMutableMap()
        updatedMap[tab] = travels
        _travelsListTabMap.value = updatedMap
    }
    fun getTravelListOfTab(tab: String): List<Travel>? {
        return _travelsListTabMap.value[tab]
    }

}