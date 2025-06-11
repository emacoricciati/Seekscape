package it.polito.mad.lab5g10.seekscape.models

import android.app.Activity.MODE_PRIVATE
import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import it.polito.mad.lab5g10.seekscape.LocalDateAdapter
import it.polito.mad.lab5g10.seekscape.ProfilePicAdapter
import it.polito.mad.lab5g10.seekscape.TravelImageAdapter
import it.polito.mad.lab5g10.seekscape.firebase.unknown_User
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
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

    //TODO initialize, fetch dark mode boolean




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


//----------
    private val _myProfile = MutableStateFlow<User>(unknown_User)
    val myProfile: StateFlow<User> = _myProfile.asStateFlow()

    fun updateMyProfile(new: User) {
        _myProfile.value = new
    }


    private val _isDarkMode = MutableStateFlow<Boolean?>(null)
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    fun updateIsDarkMode(new: Boolean) {
        _isDarkMode.value = new
        //make persistant on phone
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

}