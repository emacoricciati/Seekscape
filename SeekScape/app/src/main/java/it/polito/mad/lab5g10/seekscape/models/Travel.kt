package it.polito.mad.lab5g10.seekscape.models

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.TheTravelModel
import it.polito.mad.lab5g10.seekscape.firebase.unknown_User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.Serializable
import java.net.URL
import java.time.LocalDate
import java.util.UUID

data class Activity(
    var name: String = "",
    var optional: Boolean = false,
    var icon: String = ""
) : Serializable {
    override fun toString(): String {
        return name
    }
}


data class Itinerary (
    var itineraryId: Int,
    var name:String,
    var startDate: LocalDate,
    var endDate: LocalDate? = null,
    var places: List<String>,
    var description: String,
    var itineraryImages: List<Int>? = null,
    var activities: List<Activity>,
) : Serializable

fun getBlankItinerary(): Itinerary{
    return Itinerary(
        itineraryId = 0,
        name = "",
        places = listOf(),
        description = "",
        itineraryImages = listOf(),
        activities = listOf(),
        startDate = LocalDate.now(),
    )
}


class ItineraryModel(itinerary: Itinerary){
    private fun <T> createStateFlow(initialValue: T) = MutableStateFlow(initialValue)

    val nameValue = createStateFlow(itinerary.name)
    val startDateValue = createStateFlow(itinerary.startDate)
    val endDateValue = createStateFlow(itinerary.endDate)
    val descriptionValue = createStateFlow(itinerary.description)
    val itineraryImagesValue = createStateFlow(itinerary.itineraryImages)
    val activities = createStateFlow(itinerary.activities)
    val places = createStateFlow(itinerary.places)

    fun updateName(value: String) = nameValue.tryEmit(value)
    fun updateStartDate(value: LocalDate) = startDateValue.tryEmit(value)
    fun updateEndDate(value: LocalDate?) = endDateValue.tryEmit(value)
    fun updateDescription(value: String) = descriptionValue.tryEmit(value)
    fun updateItineraryImages(value: List<Int>?) = itineraryImagesValue.tryEmit(value)
    fun removeActivity(act: Activity) {
        activities.value = activities.value.minus(act)
    }
    fun addActivities(acts: List<Activity>) {
        activities.value = acts
    }
    fun toggleOptional(act: Activity){
        activities.value = activities.value.map {
            if (it == act) {
                it.copy(optional = !it.optional)
            } else {
                it
            }
        }
    }

    fun addPlace(text: String) {
        if (places.value.contains(text)){
            return
        }
        places.value = places.value.plus(text)
    }
    fun removePlace(text: String) { places.value = places.value.filterNot { it == text } }

}

class ItineraryViewModel(private val model: ItineraryModel) : ViewModel() {

    var isAddingLocation by mutableStateOf(false)
    var selectEndDate by mutableStateOf(false)

    fun validate(): Boolean {
        var errorCount = 0
        listOf(
            Triple(model.nameValue.value.isBlank(), "Itinerary name cannot be blank", ::validateFieldName),
            Triple(model.descriptionValue.value.isBlank(), "Itinerary description cannot be blank", ::validateFieldDescription),
            Triple(model.activities.value.isEmpty(), "Select at least one activity", ::validateFieldActivities),
            Triple(model.places.value.isEmpty(), "Select at least one location", ::validateFieldPlaces),
            Triple(model.endDateValue.value == null && selectEndDate, "End date cannot be empty", ::validateFieldEndDate),
        ).forEach { (shouldShowError, errorMessage, validateFunc) ->
            if (shouldShowError) {
                validateFunc(errorMessage)
                errorCount++
            }
            else {
                validateFunc("")
            }
        }
        return errorCount == 0
    }

    // Validation errors
    var nameError by mutableStateOf("")
    var descriptionError by mutableStateOf("")
    var activitiesError by mutableStateOf("")
    var placesError by mutableStateOf("")
    var startDateError by mutableStateOf("")
    var endDateError by mutableStateOf("")

    private fun validateFieldName(error: String) {
        nameError = error
    }

    private fun validateFieldDescription(error: String) {
        descriptionError = error
    }

    private fun validateFieldActivities(error: String) {
        activitiesError = error
    }

    private fun validateFieldPlaces(error: String) {
        placesError = error
    }

    private fun validateFieldEndDate(error: String) {
        endDateError = error
    }

    // export model data
    val nameValue = model.nameValue
    val startDateValue = model.startDateValue
    val endDateValue = model.endDateValue
    val descriptionValue = model.descriptionValue
    val itineraryImagesValue = model.itineraryImagesValue
    val activities = model.activities
    val places = model.places

    // delegate methods
    fun setName(value: String) = model.updateName(value)
    fun setStartDate(value: LocalDate) = model.updateStartDate(value)
    fun setEndDate(value: LocalDate?) = model.updateEndDate(value)
    fun setDescription(value: String) = model.updateDescription(value)
    fun setItineraryImages(value: List<Int>?) = model.updateItineraryImages(value)
    fun removeActivity(act: Activity) = model.removeActivity(act)
    fun addActivities(acts: List<Activity>) = model.addActivities(acts)
    fun toggleOptional(act: Activity) = model.toggleOptional(act)
    fun addPlace(text: String) {
        model.addPlace(text)
        isAddingLocation = false
    }
    fun removePlace(text: String) = model.removePlace(text)
    fun toggleIsAddingLocation() {
        isAddingLocation = !isAddingLocation
    }
    fun toggleSelectEndDate() {
        selectEndDate = !selectEndDate
    }
}

// Factory class to instantiate ViewModel
class ItineraryViewModelFactory(
    private val itineraryInfo: Itinerary,
) : ViewModelProvider.Factory {
    private val model: ItineraryModel = ItineraryModel(itineraryInfo)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ItineraryViewModel::class.java) ->
                ItineraryViewModel(model) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}


sealed class TravelImage : Serializable {
    data class Url(val value: String) : TravelImage()
    data class Resource(val resId: Int) : TravelImage()
}

data class TravelCompanion(
    var user: User,
    var extras: Int = 0,
) : Serializable



data class Travel(
    val travelId: String,
    var creator: User,
    var title: String? = null,
    var description: String? = null,
    var country: String? = null,
    var priceMin: Int? = null,
    var priceMax: Int? = null,
    var status: String? = null,
    var statusForUser: String = "",
    var distance: String? = null,
    var startDate: LocalDate? = null,
    var endDate: LocalDate? = null,
    var maxPeople: Int? = null,
    var travelImages: List<TravelImage>? = null,
    var travelTypes: List<String>? = null,
    var travelItinerary: List<Itinerary>? = null,
    var travelCompanions: List<TravelCompanion>? = null,
    var travelReviews: List<TravelReview>? = null,
    var travelRating: Double? = null
) : Serializable


fun getBlankTravel(creator: User): Travel {
    val creatorComp = TravelCompanion(creator)
    return Travel(
        travelId = "0",
        creator = creator,
        title = null,
        description = null,
        country = null,
        priceMin = MIN_PRICE,
        priceMax = MAX_PRICE,
        status = null,
        statusForUser = OWNED,
        distance = null,
        startDate = null,
        endDate = null,
        maxPeople = MIN_COMPANIONS,
        travelImages = null,
        travelTypes = listOf(),
        travelItinerary = listOf(),
        travelCompanions = listOf(creatorComp),
        travelReviews = listOf(),
        travelRating = null
    )
}


class TravelModel(travel: Travel?=null, travelId: String?=null) {
    private fun <T> createStateFlow(initialValue: T) = MutableStateFlow(initialValue)

    val isTravelLoadedValue = createStateFlow(travel!=null)

    val statusForUserValue = createStateFlow(if(isTravelLoadedValue.value) travel!!.statusForUser else "")
    val travelIdValue = createStateFlow(if(isTravelLoadedValue.value) travel!!.travelId else travelId?:"")
    val creatorValue = createStateFlow(if(isTravelLoadedValue.value) travel!!.creator else unknown_User)
    val titleValue = createStateFlow(if(isTravelLoadedValue.value) travel!!.title ?: "" else "")
    val descriptionValue = createStateFlow(if(isTravelLoadedValue.value) travel!!.description ?: "" else "")
    val locationValue = createStateFlow(if(isTravelLoadedValue.value) travel!!.country ?: "" else "")
    val statusValue = MutableStateFlow(if(isTravelLoadedValue.value) travel!!.status ?: "" else "")
    val distanceValue = createStateFlow(if(isTravelLoadedValue.value) travel!!.distance ?: "" else "")
    val dateStartValue = MutableStateFlow(if(isTravelLoadedValue.value) travel!!.startDate else LocalDate.now())
    val dateEndValue = MutableStateFlow(if(isTravelLoadedValue.value) travel!!.endDate else LocalDate.now())
    val priceStartValue = MutableStateFlow(if(isTravelLoadedValue.value) travel!!.priceMin ?: 0 else 0)
    val priceEndValue = MutableStateFlow(if(isTravelLoadedValue.value) travel!!.priceMax ?: 0 else 0)
    val nParticipantsValue = MutableStateFlow(if(isTravelLoadedValue.value) travel!!.maxPeople ?: 0 else 0)
    val imageUrisValues = MutableStateFlow(if(isTravelLoadedValue.value) travel!!.travelImages ?: emptyList() else emptyList())
    val travelTypesValues = MutableStateFlow(if(isTravelLoadedValue.value) travel!!.travelTypes ?: emptyList() else emptyList())
    val travelItineraryValues = MutableStateFlow(if(isTravelLoadedValue.value) travel!!.travelItinerary ?: emptyList() else emptyList())
    val travelCompanionsValues = MutableStateFlow(if(isTravelLoadedValue.value) travel!!.travelCompanions ?: emptyList() else emptyList())
    val travelReviewsValue =  MutableStateFlow(if(isTravelLoadedValue.value) travel!!.travelReviews ?: emptyList() else emptyList())
    val travelRatingValue =  createStateFlow(if(isTravelLoadedValue.value) travel!!.travelRating ?: 0.0 else 0.0)

    fun loadTravel(travel: Travel){
        updateTravelId(travel.travelId)
        updateCreator(travel.creator)
        updateTitle(travel.title?: "")
        updateDescription(travel.description?: "")
        updateLocation(travel.country ?: "")
        updateStatus(travel.status ?: "")
        updateDistance(travel.distance ?: "")
        updateDateStart(travel.startDate?: LocalDate.now())
        updateDateEnd(travel.endDate?: LocalDate.now())
        updatePriceStart(travel.priceMin ?: 0)
        updatePriceEnd(travel.priceMax ?: 0)
        updateParticipants(travel.maxPeople ?: 0)
        updateImageUris(travel.travelImages ?: emptyList())
        updateTravelTypes(travel.travelTypes ?: emptyList())
        updateTravelItinerary(travel.travelItinerary ?: emptyList())
        updateTravelCompanions(travel.travelCompanions ?: emptyList())
        updateTravelReviews(travel.travelReviews ?: emptyList())
        updatetravelRating(travel.travelRating ?: 0.0)
        updateStatusForUser(travel.statusForUser)
        updateisTravelLoaded(true)
    }

    // Update functions

    fun updateisTravelLoaded(value: Boolean) { isTravelLoadedValue.tryEmit(value) }


    fun updateStatusForUser(value: String) { statusForUserValue.tryEmit(value) }
    fun updateTravelId(value: String) { travelIdValue.tryEmit(value) }
    fun updateCreator(value: User) { creatorValue.tryEmit(value) }
    fun updateTitle(value: String) { titleValue.tryEmit(value) }
    fun updateDescription(value: String) { descriptionValue.tryEmit(value) }
    fun updateLocation(value: String) { locationValue.tryEmit(value) }
    fun updateStatus(value: String) { statusValue.tryEmit(value) }
    fun updateDistance(value: String) { distanceValue.tryEmit(value) }
    fun updateDateStart(value: LocalDate) { dateStartValue.tryEmit(value) }
    fun updateDateEnd(value: LocalDate) { dateEndValue.tryEmit(value) }
    fun updatePriceStart(value: Int) { priceStartValue.tryEmit(value) }
    fun updatePriceEnd(value: Int) { priceEndValue.tryEmit(value) }
    fun updateParticipants(value: Int) { nParticipantsValue.tryEmit(value) }
    fun updateImageUris(value: List<TravelImage>) { imageUrisValues.tryEmit(value) }
    fun updateTravelTypes(value: List<String>) { travelTypesValues.tryEmit(value) }
    fun updateTravelItinerary(value: List<Itinerary>) { travelItineraryValues.tryEmit(value) }
    fun updateTravelCompanions(value: List<TravelCompanion>) { travelCompanionsValues.tryEmit(value) }
    fun updateTravelReviews(value: List<TravelReview>){travelReviewsValue.tryEmit(value)}
    fun updatetravelRating(value: Double){travelRatingValue.tryEmit(value)}

    fun cleanDateStart(){ dateStartValue.tryEmit(null)}
    fun cleanDateEnd(){ dateEndValue.tryEmit(null)}

}

class TravelViewModel(private val model: TravelModel) : ViewModel() {
    var isAddingLocation by mutableStateOf(false)

    init{
        if(!model.isTravelLoadedValue.value){
            viewModelScope.launch {
                val travel = CommonModel.getTravelById(model.travelIdValue.value)
                if(travel!=null){
                    AppState.setTravelToTab(travel)
                    model.loadTravel(travel)
                }
            }

        }
    }

    val statusForUser = model.statusForUserValue
    fun validateTravel(): Boolean {

        var errorCount = 0
        listOf(
            Triple(model.titleValue.value.isBlank(), "Title cannot be blank", ::validateTitle),
            Triple(model.descriptionValue.value.isBlank(), "Description cannot be blank", ::validateDescription),
            Triple(model.locationValue.value.isBlank(), "Country is required", ::validateCountry),
            Triple((model.priceStartValue.value == 0 || model.priceEndValue.value == 0 || model.priceStartValue.value > model.priceEndValue.value), "Price range is invalid", ::validatePrice),
            Triple((model.dateStartValue.value == null || model.dateEndValue.value == null || model.dateStartValue.value!!.isAfter(model.dateEndValue.value) ), "Date range is invalid", ::validateDate),
            Triple(
                (model.dateStartValue.value != null && model.dateEndValue.value != null &&
                        model.dateStartValue.value!!.isBefore(model.dateEndValue.value) &&
                        LocalDate.now().isAfter(model.dateStartValue.value)),
                "Date cannot be in the past",
                ::validateDateToday
            ),
            Triple((model.nParticipantsValue.value <= 1), "The number of partecipants must be greater than 1", ::validatePeople),
            Triple(model.travelTypesValues.value.isEmpty(), "At least one travel type is required", ::validateTypes),
            Triple(model.travelItineraryValues.value.isEmpty(), "At least one itinerary is required", ::validateItinerary),
            //not used for now in images
            Triple(model.imageUrisValues.value.isEmpty(), "At least one image is required", ::validateImages)
        ).forEach { (shouldShowError, errorMessage, validateFunc) ->
            if (shouldShowError) {
                validateFunc(errorMessage)
                errorCount++
            } else {
                validateFunc("") // Clear previous errors if validation passes
            }
        }

        return errorCount == 0
    }

    // Validation error messages
    var titleError by mutableStateOf("")
    var descriptionError by mutableStateOf("")
    var countryError by mutableStateOf("")
    var priceError by mutableStateOf("")
    var dateError by mutableStateOf("")
    var peopleError by mutableStateOf("")
    var typesError by mutableStateOf("")
    var itineraryError by mutableStateOf("")
    var companionsError by mutableStateOf("")
    var imagesError by mutableStateOf("")
    var todayError by mutableStateOf("")

    private fun validateTitle(error: String) { titleError = error }
    private fun validateDescription(error: String) { descriptionError = error }
    private fun validateCountry(error: String) { countryError = error }
    private fun validatePrice(error: String) { priceError = error }
    private fun validateDate(error: String) { dateError = error }
    private fun validatePeople(error: String) { peopleError = error }
    private fun validateTypes(error: String) { typesError = error }
    private fun validateItinerary(error: String) { itineraryError = error }
    private fun validateCompanions(error: String) { companionsError = error }
    private fun validateImages(error: String) { imagesError = error }
    private fun validateDateToday(error: String) {todayError = error}



    // Expose Model data

    val isTravelLoaded = model.isTravelLoadedValue

    val travelIdValue = model.travelIdValue
    val creatorValue = model.creatorValue
    val titleValue = model.titleValue
    val descriptionValue = model.descriptionValue
    val locationValue = model.locationValue
    val statusValue = model.statusValue
    val distanceValue = model.distanceValue
    val dateStartValue = model.dateStartValue
    val dateEndValue = model.dateEndValue
    val priceStartValue = model.priceStartValue
    val priceEndValue = model.priceEndValue
    val nParticipantsValue = model.nParticipantsValue
    val imageUrisValues = model.imageUrisValues
    val travelTypesValues = model.travelTypesValues
    val travelItineraryValues = model.travelItineraryValues
    val travelCompanionsValues = model.travelCompanionsValues
    val travelReviewsValues = model.travelReviewsValue
    val travelRatingValue = model.travelRatingValue

    // Delegate update methods
    fun setTravelId(value: String) = model.updateTravelId(value)
    fun setCreator(value: User) = model.updateCreator(value)
    fun setTitle(value: String) = model.updateTitle(value)
    fun setDescription(value: String) = model.updateDescription(value)
    fun setLocation(value: String) = model.updateLocation(value)
    fun setStatus(value: String) = model.updateStatus(value)
    fun setDistance(value: String) = model.updateDistance(value)
    fun setDateStart(value: LocalDate) = model.updateDateStart(value)
    fun setDateEnd(value: LocalDate) = model.updateDateEnd(value)
    fun setPriceStart(value: Int) = model.updatePriceStart(value)
    fun setPriceEnd(value: Int) = model.updatePriceEnd(value)
    fun setParticipants(value: Int) = model.updateParticipants(value)
    fun setImageUris(value: List<TravelImage>) = model.updateImageUris(value)
    fun setTravelTypes(value: List<String>) = model.updateTravelTypes(value)
    fun setTravelItinerary(value: List<Itinerary>) = model.updateTravelItinerary(value)
    fun setTravelCompanions(value: List<TravelCompanion>) = model.updateTravelCompanions(value)
    fun setTravelReviews(value: List<TravelReview>) = model.updateTravelReviews(value)
    fun setTravelRatingValue(value: Double) = model.updatetravelRating(value)

    fun cleanDateStart() = model.cleanDateStart()
    fun cleanDateEnd() = model.cleanDateEnd()

    // logic for add and remove listObject

    fun setLocationAndCloseAdding(value: String) {
        setLocation(value)
        isAddingLocation = false
    }

    fun removeLocation() {
        model.updateLocation("")
    }

    fun addImageUri(image: TravelImage) {
        val currentImages = imageUrisValues.value
        if (currentImages.size < 5) {
            setImageUris(currentImages + image)
        }
    }
    fun removeImageUri(image: TravelImage) {
        setImageUris(imageUrisValues.value - image)
    }

    fun addTravelType(type: String) {
        if (!travelTypesValues.value.contains(type)) {
            travelTypesValues.value += type
        }
    }

    fun addTravelReview(review: TravelReview) {
        setTravelReviews(travelReviewsValues.value.orEmpty() + review)
    }

    fun removeTravelType(value: String) {
        travelTypesValues.value = travelTypesValues.value.filterNot { it == value }
    }

    fun addItinerary(itinerary: Itinerary) {
        setTravelItinerary(travelItineraryValues.value.orEmpty() + itinerary)
    }
    fun removeItinerary(itinerary: Itinerary) {
        setTravelItinerary(travelItineraryValues.value.orEmpty().filterNot { it == itinerary })
    }

    fun addCompanion(comp: TravelCompanion) {
        setTravelCompanions(travelCompanionsValues.value.orEmpty() + comp)
    }
    fun removeCompanion(comp: TravelCompanion) {
        setTravelCompanions(travelCompanionsValues.value.orEmpty().filterNot { it == comp })
    }

    fun clean(){
        val blankTravelModel = TravelModel(getBlankTravel(AppState.myProfile.value))

        setTravelId(blankTravelModel.travelIdValue.value)
        setCreator(blankTravelModel.creatorValue.value)
        setTitle(blankTravelModel.titleValue.value)
        setDescription(blankTravelModel.descriptionValue.value)
        setLocation(blankTravelModel.locationValue.value)
        setStatus(blankTravelModel.statusValue.value)
        setDistance(blankTravelModel.distanceValue.value)
        cleanDateStart()
        cleanDateEnd()
        setPriceStart(MIN_PRICE)
        setPriceEnd(MAX_PRICE)
        setParticipants(blankTravelModel.nParticipantsValue.value)
        setImageUris(blankTravelModel.imageUrisValues.value)
        setTravelTypes(listOf())
        setTravelItinerary(blankTravelModel.travelItineraryValues.value)
        setTravelCompanions(blankTravelModel.travelCompanionsValues.value)
        setTravelReviews(blankTravelModel.travelReviewsValue.value)
        setTravelRatingValue(blankTravelModel.travelRatingValue.value)
    }

    fun toggleIsAddingLocation() {
        isAddingLocation = !isAddingLocation
    }

}

class TravelViewModelFactory(
    private val travel: Travel?=null,
    private val travelId: String?=null
) : ViewModelProvider.Factory {
    private val model = TravelModel(travel, travelId)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TravelViewModel::class.java) ->
                TravelViewModel(model) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

//COPY FUNCTIONS
class TravelDuplicator(private val appContext: Context) {
    suspend fun duplicateTravel(travelId: String): Travel? {
        try {
            val originalTravel = CommonModel.getTravelById(travelId)

            if (originalTravel != null) {
                val newImages = mutableListOf<TravelImage>()

                originalTravel.travelImages?.forEach { image ->
                    if (image is TravelImage.Url) {
                        val imageUrl = image.value

                        try {
                            val downloadedFile = downloadImageToTempFile(imageUrl)
                            if (downloadedFile != null) {
                                newImages.add(TravelImage.Url(downloadedFile))
                            } else {
                                println("Error: Impossible to download the image")
                            }
                        } catch (e: Exception) {
                            println("Error: Impossible to download the image")
                        }
                    }
                }

                val newTravel = originalTravel.deepCopy()
                val myProfile = AppState.myProfile.value
                newTravel.creator = myProfile
                newTravel.travelImages = newImages
                newTravel.travelCompanions = listOf(TravelCompanion(myProfile))

                return newTravel
            }

            return null
        } catch (e: Exception) {
            println("Error on duplicating images during a copy operation")
            return null
        }
    }

    private suspend fun downloadImageToTempFile(imageUrl: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection()
                connection.connect()

                val inputStream = connection.getInputStream()
                val tempFile = File(appContext.cacheDir, "temp_image_${UUID.randomUUID()}.jpg")

                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                inputStream.close()
                tempFile.toUri().toString()
            } catch (e: Exception) {
                println("Errore durante il download dell'immagine da $imageUrl: ${e.message}")
                null
            }
        }
    }
}

fun Travel.deepCopy(): Travel {
    return this.copy(
        travelId = "",
        creator = this.creator.copy(),
        travelImages = this.travelImages?.map {
            when (it) {
                is TravelImage.Url -> it.copy()
                is TravelImage.Resource -> it.copy()
            }
        }?.toMutableList(),
        travelTypes = this.travelTypes?.toMutableList(),
        travelItinerary = this.travelItinerary?.map { it.deepCopy(it.itineraryId) }?.toMutableList(),
        travelCompanions = this.travelCompanions?.map { it.deepCopy() }?.toMutableList()
    )
}

fun Itinerary.deepCopy(id: Int): Itinerary {
    return this.copy(
        itineraryId = id,
        places = this.places.toMutableList(),
        itineraryImages = this.itineraryImages?.toMutableList(),
        activities = this.activities.map { it.copy() }.toMutableList()
    )
}

fun TravelCompanion.deepCopy(): TravelCompanion {
    return this.copy(
        user = this.user.copy()
    )
}


