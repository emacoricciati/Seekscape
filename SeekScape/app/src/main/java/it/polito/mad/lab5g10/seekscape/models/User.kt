package it.polito.mad.lab5g10.seekscape.models

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.TheTravelModel
import it.polito.mad.lab5g10.seekscape.firebase.TheUserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.Serializable
import java.time.LocalDate

data class User(
    var userId: String,
    var authUID: String? = null,
    var nickname: String,
    var name: String,
    var surname: String,
    var age: Int,
    var nationality: String,
    var city: String,
    var language: String,
    var phoneNumber: String?,
    var email: String?,
    var profilePic: ProfilePic? = null,
    val bio: String?,
    var personality: List<String>,
    val travelPreferences: List<String>? = null,
    var reviews: List<Review>? = null,
    var trips: List<Travel>? = emptyList(),
    val requests: List<Request>? = emptyList(),
    var desiredDestinations: List<String>?,
    val numTravels: Int,
    val notifications: List<NotificationItem> = listOf<NotificationItem>(),
    var notificationSettings: UserNotificationSettings? = null
) : Serializable

fun getBlankUser(): User {
    return User(
        userId = "0",
        authUID = "",
        nickname = "",
        name = "",
        surname = "",
        age = 0,
        nationality = "",
        city = "",
        language = "",
        phoneNumber = null,
        email = null,
        profilePic = null,
        bio = null,
        personality = emptyList(),
        travelPreferences = emptyList(),
        reviews = emptyList(),
        trips = emptyList(),
        requests = emptyList(),
        desiredDestinations = emptyList(),
        numTravels = 0
    )
}

sealed class ProfilePic : Serializable {
    data class Url(val value: String) : ProfilePic()
    data class Resource(val resId: Int) : ProfilePic()
}

data class Review(
    val date: LocalDate,
    val reviewText: String,
    val rating: Double,
    val author: User
) : Serializable

data class Request(
    val id: String,
    val author: User,
    val trip: Travel,
    val reqMessage: String,
    val isAccepted: Boolean,
    val isRefused: Boolean,
    val spots: Int = 1,
    var responseMessage: String? = "",
    var lastUpdate: LocalDate = LocalDate.now()
) : Serializable

class RequestInfoModel(requestInfo: Request) {
    private fun <T> createStateFlow(initialValue: T) = MutableStateFlow(initialValue)

    val idValue = createStateFlow(requestInfo.id)
    val authorValue = createStateFlow(requestInfo.author)
    val tripValue = createStateFlow(requestInfo.trip)
    val reqMessageValue = createStateFlow(requestInfo.reqMessage)
    val isAcceptedValue = createStateFlow(requestInfo.isAccepted)
    val isRefusedValue = createStateFlow(requestInfo.isRefused)
    val spots = createStateFlow(requestInfo.spots)
    val responseMessageValue = createStateFlow(requestInfo.responseMessage)

    fun updateAuthor(value: User) = authorValue.tryEmit(value)
    fun updateTrip(value: Travel) = tripValue.tryEmit(value)
    fun updateReq(value: String) = reqMessageValue.tryEmit(value)
    fun updateIsAcc(value: Boolean) = isAcceptedValue.tryEmit(value)
    fun updateIsRef(value: Boolean) = isRefusedValue.tryEmit(value)
    fun updateNumSpots(value: Int) = spots.tryEmit(value)
    fun updateResponseMessage(value: String) = responseMessageValue.tryEmit(value)
}

class OwnedTravelViewModel() : ViewModel() {
    private val _travels = MutableStateFlow<List<TravelModel>>(emptyList())
    val travels: StateFlow<List<TravelModel>> = _travels


    private val _fetched = MutableStateFlow<Boolean>(false)
    val fetched: StateFlow<Boolean> = _fetched


    private val _isLoadingBack = MutableStateFlow<Boolean>(false)
    val isLoadingBack: StateFlow<Boolean> = _isLoadingBack

    fun setToFetch() {
        _fetched.value=false
    }

    val theTravelModel = TheTravelModel()
    fun refresh() {
        viewModelScope.launch {
            try {
                if(_fetched.value==true){
                    _isLoadingBack.value=true
                }
                val updated = theTravelModel.getOwnedTravels()
                _travels.value = updated.map { TravelModel(it) }
                _fetched.value = true
                _isLoadingBack.value=false
            } catch (e: Exception) {
                Log.e("OwnedTravelVM", "Failed to refresh travels", e)
            }
        }
    }
}


class ExploreModeTravelViewModel() : ViewModel() {
    val theTravelModel = TheTravelModel()

    private val _travelUiStates = mutableStateMapOf<String, TravelUiState>(
            "Upcoming" to TravelUiState.Loading,
            "Pending" to TravelUiState.Loading,
            "Rejected" to TravelUiState.Loading,
            "To Review" to TravelUiState.Loading,
            "Past" to TravelUiState.Loading
    )
    fun getUiState(tab: String): State<TravelUiState> {
        return derivedStateOf { _travelUiStates[tab]!! }
    }

    private val _isLoadingBack = mutableStateMapOf<String, Boolean>(
        "Upcoming" to false,
        "Pending" to false,
        "Rejected" to false,
        "To Review" to false,
        "Past" to false
    )
    fun getLoadingBack(tab: String): State<Boolean> {
        return derivedStateOf { _isLoadingBack[tab]!! }
    }

    private val _isLoadingMore = mutableStateMapOf<String, Boolean>(
        "Upcoming" to false,
        "Pending" to false,
        "Rejected" to false,
        "To Review" to false,
        "Past" to false
    )
    fun getLoadingMore(tab: String): State<Boolean> {
        return derivedStateOf { _isLoadingMore[tab]!! }
    }


    fun fetchTravels(tab: String="", lastStartDateFirebaseFound: String?=null) {
        if(lastStartDateFirebaseFound==null){
            val travelSaved: List<Travel>? = AppState.getTravelListOfTab(tab)
            if(travelSaved!=null){
                _travelUiStates[tab] =
                    if (travelSaved.isEmpty()) TravelUiState.Empty
                    else TravelUiState.Success(travelSaved)
                _isLoadingBack[tab] = true
            } else {
                _travelUiStates[tab] = TravelUiState.Loading
            }
        }else{
            _isLoadingMore[tab] = true
        }

        viewModelScope.launch {
            try {
                val travels = when (tab) {
                    "Upcoming" -> theTravelModel.getJoinedTravels(lastStartDateFirebaseFound)
                    "Pending" -> theTravelModel.getPendingTravels(lastStartDateFirebaseFound)
                    "Rejected" -> theTravelModel.getDeniedTravels(lastStartDateFirebaseFound)
                    "To Review" -> theTravelModel.getToReviewTravels(lastStartDateFirebaseFound)
                    "Past" -> theTravelModel.getPastTravels(lastStartDateFirebaseFound)
                    else -> emptyList()
                }

                if(lastStartDateFirebaseFound==null){
                    _isLoadingBack[tab] = false
                    AppState.setTravelListToTab(travels, tab)
                    _travelUiStates[tab] =
                        if (travels.isEmpty()) TravelUiState.Empty
                        else TravelUiState.Success(travels)

                } else {
                    _isLoadingMore[tab] = false
                    val current = _travelUiStates[tab]
                    if (current is TravelUiState.Success) {
                        val merged = current.travels + travels
                        _travelUiStates[tab] = TravelUiState.Success(merged)
                    }
                }

            } catch (e: Exception) {
                Log.e("TabDataDebug", "Error loading travels", e)
            }
        }
    }
}

class RequestViewModel() : ViewModel() {
    private val _requests = MutableStateFlow<List<RequestInfoModel>>(emptyList())
    val requests: StateFlow<List<RequestInfoModel>> = _requests

    private val _fetched = MutableStateFlow<Boolean>(false)
    val fetched: StateFlow<Boolean> = _fetched

    private val _isLoadingBack = MutableStateFlow<Boolean>(false)
    val isLoadingBack: StateFlow<Boolean> = _isLoadingBack

    fun setToFetch() {
        _fetched.value=false
    }
    val theTravelModel = TheTravelModel()

    fun updateRequests(){
        viewModelScope.launch {
            if(_fetched.value==true){
                _isLoadingBack.value=true
            }
            val requestsInfo = theTravelModel.getRequestsToMyTrips()
            val requestModels: List<RequestInfoModel> = requestsInfo.map { RequestInfoModel(it) }
            _requests.value = requestModels
            _fetched.value=true
            _isLoadingBack.value=false
        }
    }

    fun getRequest(requestId: String): RequestInfoModel?{
        val requests: List<RequestInfoModel> = _requests.value.filter { it.idValue.value == requestId }
        if(requests.size==1){
            return requests.first()
        }
        return null
    }

    fun removeReqFromList(requestIds: List<String>){
        _requests.value = _requests.value.filter { !requestIds.contains(it.idValue.value) }
    }

    fun getRequestObject(requestId: String): Request? {
        val requestInfoModel:RequestInfoModel? = getRequest(requestId)
        if(requestInfoModel==null) return null;
        return Request(
            requestInfoModel.idValue.value,
            requestInfoModel.authorValue.value,
            requestInfoModel.tripValue.value,
            requestInfoModel.reqMessageValue.value,
            requestInfoModel.isAcceptedValue.value,
            requestInfoModel.isRefusedValue.value,
            requestInfoModel.spots.value,
            requestInfoModel.responseMessageValue.value
        )
    }

}

data class UserArgs(
    val userInfo: User,
    val isOwnProfile: Boolean
) : Serializable

// UserInfoModel
class UserInfoModel(userInfo: User?=null, userId: String?=null) {

    private fun <T> createStateFlow(initialValue: T) = MutableStateFlow(initialValue)

    val isUserLoadedValue = createStateFlow(userInfo!=null)

    val idValue = createStateFlow(if(isUserLoadedValue.value) userInfo!!.userId else userId?:"")
    val nameValue = createStateFlow(if(isUserLoadedValue.value) userInfo!!.name else "")
    val surnameValue = createStateFlow(if(isUserLoadedValue.value) userInfo!!.surname else "")
    val nicknameValue = createStateFlow(if(isUserLoadedValue.value) userInfo!!.nickname else "")
    val ratingsAverage = createStateFlow(if(isUserLoadedValue.value) userInfo!!.reviews
        ?.map { it.rating }
        ?.takeIf { it.isNotEmpty() }
        ?.average() ?: 0.0 else 0.0)
    val numTravelsValue = createStateFlow(if(isUserLoadedValue.value) userInfo!!.numTravels else 0)
    val ageValue = createStateFlow(if(isUserLoadedValue.value) userInfo!!.age.toString() else "")
    val nationalityValue = createStateFlow(if(isUserLoadedValue.value) userInfo!!.nationality else "")
    val cityValue = createStateFlow(if(isUserLoadedValue.value) userInfo!!.city else "")
    val languageValue = createStateFlow(if(isUserLoadedValue.value) userInfo!!.language else "")
    val bioValue = createStateFlow(if(isUserLoadedValue.value) userInfo!!.bio else "")
    val personality = createStateFlow(if(isUserLoadedValue.value) userInfo!!.personality else emptyList())
    val desiredDestinations = createStateFlow(if(isUserLoadedValue.value) userInfo!!.desiredDestinations else emptyList())
    val reviews = createStateFlow(if(isUserLoadedValue.value) userInfo!!.reviews else emptyList())
    val trips = createStateFlow(if(isUserLoadedValue.value) userInfo!!.trips else emptyList())
    val requests = createStateFlow(if(isUserLoadedValue.value) userInfo!!.requests else emptyList())

    private val _profilePic = createStateFlow(if(isUserLoadedValue.value) userInfo!!.profilePic else null)
    val profilePic = _profilePic

    fun loadUser(user: User){
        if(AppState.myProfile.value.userId==user.userId){
            AppState.updateMyProfile(user);
        }
        updateName(user.name)
        updateSurname(user.surname)
        updateProfilePic(user.profilePic)
        updateBio(user.bio?:"")
        updateNickName(user.nickname)
        updateAge(user.age.toString())
        updateNationality(user.nationality)
        updateCity(user.city)
        updateLanguage(user.language)
        updatePersonality(user.personality)
        updateLocation(user.desiredDestinations?:emptyList())
        updateRequests(user.requests?:emptyList())
        updateTrips(user.trips?:emptyList())
        updateReviews(user.reviews?:emptyList())
        updateNumTravels(user.numTravels)
        updateRatingAverage(user.reviews
            ?.map { it.rating }
            ?.takeIf { it.isNotEmpty() }
            ?.average() ?: 0.0)

        updateisUserLoaded(true)
    }

    // Methods to update values
    fun updateisUserLoaded(value: Boolean) { isUserLoadedValue.tryEmit(value) }

    fun updateName(value: String) = nameValue.tryEmit(value)
    fun updateSurname(value: String) = surnameValue.tryEmit(value)
    fun updateProfilePic(value: ProfilePic?) = profilePic.tryEmit(value)
    fun updateNickName(value: String) = nicknameValue.tryEmit(value)
    fun updateAge(value: String) = ageValue.tryEmit(value)
    fun updateNationality(value: String) = nationalityValue.tryEmit(value)
    fun updateCity(value: String) = cityValue.tryEmit(value)
    fun updateLanguage(value: String) = languageValue.tryEmit(value)
    fun updateReviews(value: List<Review>) = reviews.tryEmit(value)
    fun updateRatingAverage(value: Double) = ratingsAverage.tryEmit(value)
    fun updateBio(value: String) = bioValue.tryEmit(value)
    fun updateNumTravels(value: Int) = numTravelsValue.tryEmit(value)

    fun updatePersonality(value: List<String>) = personality.tryEmit(value)
    fun addPersonality(ps: List<String>) { personality.value = ps }
    fun removePersonality(text: String) { personality.value -= text }

    fun updateLocation(value: List<String>) = desiredDestinations.tryEmit(value)
    fun addLocation(text: String) { desiredDestinations.value = desiredDestinations.value?.plus(text) }
    fun removeLocation(text: String) { desiredDestinations.value = desiredDestinations.value?.filterNot { it == text } }

    fun updateRequests(value: List<Request>) = requests.tryEmit(value)
    fun addRequest(request: Request) {requests.value = requests.value?.plus(request)}
    fun removeRequest(request: Request) {requests.value = requests.value?.filterNot{ it == request }}

    fun updateTrips(value: List<Travel>) = trips.tryEmit(value)
    fun addTrip(trip: Travel) {trips.value = trips.value?.plus(trip)}
    fun removeTrip(trip: Travel) {trips.value = trips.value?.filterNot{ it == trip }}
}

// ViewModel class
class UserInfoViewModel(private val model: UserInfoModel, private val _isOwnProfile: Boolean) : ViewModel() {

    private val theUserModel = TheUserModel()

    val isUserLoadedValue = model.isUserLoadedValue
    init{
        if(!model.isUserLoadedValue.value){
            viewModelScope.launch {
                val user = CommonModel.getUser(model.idValue.value)
                if(user!=null){
                    model.loadUser(user)
                }
            }
        }
    }

    var isValid by mutableStateOf(true)
    var isEditing by mutableStateOf(false)
    private var counterValid = 0
    val isOwnProfile = _isOwnProfile

    // Taking photo
    var capturedImageUri by mutableStateOf<Uri?>(null)
    val galleryPermissionGranted = mutableStateOf(false)
    val cameraPermissionGranted = mutableStateOf(false)

    //end taking photo
    //new for imageProfile Selection in UserImage.kt
    private val _toastMessage = mutableStateOf<String?>(null)
    val toastMessage: String? get() = _toastMessage.value

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun toastShown() {
        _toastMessage.value = null
    }
    //Precedent toast used for showing the selection clicked now not used
    fun onSelectFromGallery() {
        showToast("Gallery selected")
        Log.d("UserImage", "Gallery selected")
    }

    fun onTakePhoto() {
        showToast("Camera selected")
        Log.d("UserImage", "Camera selected")
    }
    //endnew

    fun editProfile() {
        isEditing = true
    }

    fun validate(): Boolean {
        validateFields()
        var isOk = false
        if (counterValid == 0) {
            cleanErrors()
            isOk=true
        }
        counterValid = 0
        return isOk
    }

    // Validation errors
    var nickNameError by mutableStateOf("")
    var nationalityError by mutableStateOf("")
    var cityError by mutableStateOf("")
    var languageError by mutableStateOf("")

    private fun cleanErrors() {
        nickNameError = ""
        nationalityError = ""
        cityError = ""
        languageError = ""
    }

    private fun validateFields() {
        listOf(
            Triple(model.nicknameValue, "Nickname cannot be blank", ::validateFieldNickname),
            Triple(
                model.nationalityValue,
                "Nationality cannot be blank",
                ::validateFieldNationality
            ),
            Triple(model.cityValue, "City cannot be blank", ::validateFieldCity),
            Triple(model.languageValue, "Language cannot be blank", ::validateFieldLanguage)
        ).forEach { (value, error, validateFunc) ->
            if (value.value.isBlank()) {
                validateFunc(error)
            }
        }
    }

    private fun validateFieldNickname(error: String) {
        nickNameError = error
        counterValid++
    }

    private fun validateFieldNationality(error: String) {
        nationalityError = error
        counterValid++
    }

    private fun validateFieldCity(error: String) {
        cityError = error
        counterValid++
    }

    private fun validateFieldLanguage(error: String) {
        languageError = error
        counterValid++
    }

    // Expose Model data
    val idValue = model.idValue
    val nameValue = model.nameValue
    val surnameValue = model.surnameValue
    val nicknameValue = model.nicknameValue
    val ageValue = model.ageValue
    val ratingsAverage = model.ratingsAverage
    val nationalityValue = model.nationalityValue
    val cityValue = model.cityValue
    val languageValue = model.languageValue
    val bioValue = model.bioValue
    val personality = model.personality
    val desiredDestinations = model.desiredDestinations
    val reviews = model.reviews
    val numTravels = model.numTravelsValue
    var profilePic = model.profilePic
    val trips = model.trips
    val requests = model.requests

    fun setName(value: String) = model.updateName(value)
    fun setProfilePic(value: ProfilePic) = model.updateProfilePic(value)
    fun setSurname(value: String) = model.updateSurname(value)
    fun setNickName(value: String) = model.updateNickName(value)
    fun setAge(value: String) = model.updateAge(value)
    fun setNationality(value: String) = model.updateNationality(value)
    fun setCity(value: String) = model.updateCity(value)
    fun setLanguage(value: String) = model.updateLanguage(value)
    fun setBio(value: String) = model.updateBio(value)

    fun addPersonality(ps: List<String>) = model.addPersonality(ps)
    fun removePersonality(text: String) = model.removePersonality(text)
    fun addTrip(trip: Travel) = model.addTrip(trip)
    fun removeTrip(trip: Travel) = model.removeTrip(trip)
    fun addLocation(text: String) = model.addLocation(text)
    fun removeLocation(text: String) = model.removeLocation(text)
    fun addRequest(request: Request) = model.addRequest(request)
    fun removeRequest(request: Request) = model.removeRequest(request)

}

class SingleRequestViewModel(private val requestInfoModel: RequestInfoModel) : ViewModel() {
    val author = requestInfoModel.authorValue
    val trip = requestInfoModel.tripValue
    val reqMessage = requestInfoModel.reqMessageValue
    val isAccepted = requestInfoModel.isAcceptedValue
    val isRefused = requestInfoModel.isRefusedValue
    val spots = requestInfoModel.spots
    val responseMessage = requestInfoModel.responseMessageValue

    fun setSpots(num: Int) = requestInfoModel.updateNumSpots(num)
    fun setReqMessage(msg: String) = requestInfoModel.updateReq(msg)
}

// Factory class to instantiate ViewModel for a user
class ProfileViewModelFactory(
    private val userInfo: User?=null,
    private val isOwnProfile: Boolean=false,
    private val userId: String?=null
) : ViewModelProvider.Factory {
    private val model: UserInfoModel = UserInfoModel(userInfo, userId)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserInfoViewModel::class.java) ->
                UserInfoViewModel(model, isOwnProfile) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

//Factory class to instantiate ViewModel for requests
class RequestsViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RequestViewModel::class.java) ->
                RequestViewModel() as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

//Factory class to instantiate ViewModel for owned travels
class OwnedTravelsViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(OwnedTravelViewModel::class.java) ->
                OwnedTravelViewModel() as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

//Factory class to instantiate ViewModel for owned travels
class ExploreModeTravelsViewModelFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ExploreModeTravelViewModel::class.java) ->
                ExploreModeTravelViewModel() as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

//Factory class to instantiate ViewModel for a single Review
class SingleReviewViewModelFactory(
    private val request: Request
) : ViewModelProvider.Factory {
    private val model: RequestInfoModel = RequestInfoModel(request)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SingleRequestViewModel::class.java) ->
                SingleRequestViewModel(model) as T

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
