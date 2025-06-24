package it.polito.mad.lab5g10.seekscape.models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.polito.mad.lab5g10.seekscape.daysBetween
import it.polito.mad.lab5g10.seekscape.firebase.TheTravelModel
import it.polito.mad.lab5g10.seekscape.firebase.firebaseFormatter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.Serializable
import java.time.LocalDate
import kotlin.collections.filterNot


data class Search (
    val text: String = "",
    val available: Boolean = true,
    val place: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val minDuration: Int = MIN_DURATION,
    val maxDuration: Int = MAX_DURATION,
    val minPrice: Int = MIN_PRICE,
    val maxPrice: Int = MAX_PRICE,
    val minCompanions: Int = MIN_COMPANIONS,
    val maxCompanions: Int = MAX_COMPANIONS,
    val travelTypes: List<String> = emptyList(),
) : Serializable


class SearchModel(search: Search) {
    private fun <T> createStateFlow(initialValue: T) = MutableStateFlow(initialValue)


    val filterTriggeredValue = createStateFlow(false)

    val lastStartDateFirebaseFoundValue = createStateFlow("")
    val textValue = createStateFlow(search.text)
    val placeValue = createStateFlow(search.place?: "")
    val availableValue = createStateFlow(search.available)
    val startDateValue = createStateFlow(search.startDate)
    val endDateValue = createStateFlow(search.endDate)
    val minDurationValue = createStateFlow(search.minDuration)
    val maxDurationValue = createStateFlow(search.maxDuration)
    val minPriceValue = createStateFlow(search.minPrice)
    val maxPriceValue = createStateFlow(search.maxPrice)
    val minCompanionsValue = createStateFlow(search.minCompanions)
    val maxCompanionsValue = createStateFlow(search.maxCompanions)
    val travelTypesValue = createStateFlow(search.travelTypes)


    fun updateFilterTriggeredValue(value: Boolean) = filterTriggeredValue.tryEmit(value)


    fun updateLastStartDateFirebaseFound(value: String) = lastStartDateFirebaseFoundValue.tryEmit(value)
    fun updateText(value: String) = textValue.tryEmit(value)
    fun updateAvailable(value: Boolean) = availableValue.tryEmit(value)
    fun updatePlace(value: String) = placeValue.tryEmit(value)
    fun updateStartDate(value: LocalDate?) = startDateValue.tryEmit(value)
    fun updateEndDate(value: LocalDate?) = endDateValue.tryEmit(value)
    fun updateMinDuration(value: Int) = minDurationValue.tryEmit(value)
    fun updateMaxDuration(value: Int) = maxDurationValue.tryEmit(value)
    fun updateMinPrice(value: Int) = minPriceValue.tryEmit(value)
    fun updateMaxPrice(value: Int) = maxPriceValue.tryEmit(value)
    fun updateMinCompanions(value: Int) = minCompanionsValue.tryEmit(value)
    fun updateMaxCompanions(value: Int) = maxCompanionsValue.tryEmit(value)
    fun updateTravelTypes(value: List<String>) = travelTypesValue.tryEmit(value)
    fun addTravelTypes(travelType: String) {
        if (travelTypesValue.value.contains(travelType)){
            return
        }
        travelTypesValue.value = travelTypesValue.value.plus(travelType)
    }
    fun removeTravelTypes(travelType: String) {
        travelTypesValue.value = travelTypesValue.value.filterNot { it == travelType }
    }
}

sealed class TravelUiState {
    object Loading : TravelUiState()
    data class Success(val travels: List<Travel>) : TravelUiState()
    object Empty : TravelUiState()
}


@OptIn(FlowPreview::class)
class SearchViewModel(private val model: SearchModel) : ViewModel() {

    var isAddingPlace by mutableStateOf(false)

    private val _isLoadingMore = MutableStateFlow<Boolean>(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore


    private val theTravelModel = TheTravelModel()

    private val _travelUiState = MutableStateFlow<TravelUiState>(TravelUiState.Loading)
    val travelUiState: StateFlow<TravelUiState> = _travelUiState

    private val _filterChangeEvent = MutableSharedFlow<Unit>()

    val filterTriggered: StateFlow<Boolean> = model.filterTriggeredValue

    val text: StateFlow<String> = model.textValue
    val available: StateFlow<Boolean> = model.availableValue
    val place: StateFlow<String?> = model.placeValue
    val startDate: StateFlow<LocalDate?> = model.startDateValue
    val endDate: StateFlow<LocalDate?> = model.endDateValue
    val minDuration: StateFlow<Int> = model.minDurationValue
    val maxDuration: StateFlow<Int> = model.maxDurationValue
    val minPrice: StateFlow<Int> = model.minPriceValue
    val maxPrice: StateFlow<Int> = model.maxPriceValue
    val minCompanions: StateFlow<Int> = model.minCompanionsValue
    val maxCompanions: StateFlow<Int> = model.maxCompanionsValue
    val travelTypes: StateFlow<List<String>> = model.travelTypesValue

    fun setText(newText: String) {
        model.updateText(newText)
        triggerFilter()
    }

    fun setAvailable(newAvailable: Boolean) {
        model.updateAvailable(newAvailable)
        triggerFilter()
    }

    fun setPlace(newPlace: String) {
        model.updatePlace(newPlace)
        triggerFilter()
        isAddingPlace = false
    }

    fun removePlace() {
        model.updatePlace("")
        triggerFilter()
    }

    fun setStartDate(newStartDate: LocalDate?) {
        model.updateStartDate(newStartDate)
        triggerFilter()
    }

    fun setEndDate(newEndDate: LocalDate?) {
        model.updateEndDate(newEndDate)
        triggerFilter()
    }

    fun setMinDuration(newMinPriceDuration: Int) {
        model.updateMinDuration(newMinPriceDuration)
        triggerFilter()
    }

    fun setMaxDuration(newMaxDuration: Int) {
        model.updateMaxDuration(newMaxDuration)
        triggerFilter()
    }

    fun setMinPrice(newMinPrice: Int) {
        model.updateMinPrice(newMinPrice)
        triggerFilter()
    }

    fun setMaxPrice(newMaxPrice: Int) {
        model.updateMaxPrice(newMaxPrice)
        triggerFilter()
    }

    fun setMinCompanions(newMinCompanions: Int) {
        model.updateMinCompanions(newMinCompanions)
        triggerFilter()
    }

    fun setMaxCompanions(newMaxCompanions: Int) {
        model.updateMaxCompanions(newMaxCompanions)
        triggerFilter()
    }

    fun setTravelTypes(newTravelTypes: List<String>) {
        model.updateTravelTypes(newTravelTypes)
        triggerFilter()
    }
    fun addTravelTypes(travelType: String){
        model.addTravelTypes(travelType)
        triggerFilter()
    }
    fun removeTravelTypes(travelType: String){
        model.removeTravelTypes(travelType)
        triggerFilter()
    }

    private var filterJob: Job? = null
    init {
        _filterChangeEvent
            .debounce(300)
            .onEach {
                Log.d("SearchViewModel", "Debounced event triggered")
                launchFilter()
            }
            .launchIn(viewModelScope)

        filterJob = viewModelScope.launch {
            filterTravels(null, true)
        }
    }

    fun search(){
        launchFilter()
    }
    private fun launchFilter() {
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            filterTravels(null)
        }
    }

    fun toggleIsAddingLocation() {
        isAddingPlace = !isAddingPlace
    }

    private fun triggerFilter() {
        model.updateFilterTriggeredValue(true)
        viewModelScope.launch {
            _filterChangeEvent.emit(Unit)
        }
    }


    fun loadMore(){
        Log.d("loadMore", "loading more on explore")
        filterJob?.cancel()
        filterJob = viewModelScope.launch {
            filterTravels(model.lastStartDateFirebaseFoundValue.value)
        }
    }

    private suspend fun filterTravels(lastStartDateFirebaseFound: String?, isLoadBack: Boolean=false) {
        if(isLoadBack && !filterTriggered.value && AppState.lastSearchResults.value!=null) {
            _travelUiState.value = TravelUiState.Success(AppState.lastSearchResults.value!!)

        } else {
            val textFilter = model.textValue.value
            val availableFilter = model.availableValue.value
            val placeFilter = model.placeValue.value
            val startDateFilter = model.startDateValue.value
            val endDateFilter = model.endDateValue.value
            val minDurationFilter = model.minDurationValue.value
            val maxDurationFilter = model.maxDurationValue.value
            val minPriceFilter = model.minPriceValue.value
            val maxPriceFilter = model.maxPriceValue.value
            val minCompanionsFilter = model.minCompanionsValue.value
            val maxCompanionsFilter = model.maxCompanionsValue.value
            val travelTypesFilter = model.travelTypesValue.value

            val newSearch = Search(
                text = textFilter, available = availableFilter, place = placeFilter,
                startDate = startDateFilter, endDate = endDateFilter,
                minDuration = minDurationFilter, maxDuration = maxDurationFilter,
                minPrice = minPriceFilter, maxPrice = maxPriceFilter,
                minCompanions = minCompanionsFilter, maxCompanions = maxCompanionsFilter,
                travelTypes = travelTypesFilter
            )

            var lastStartDate = lastStartDateFirebaseFound
            var filteredTot = listOf<Travel>()
            var foundEnough = false
            val limitNotFound = 3
            var indexNotFound = 0
            if(lastStartDate==null){
                _travelUiState.value = TravelUiState.Loading
            } else {
                _isLoadingMore.value=true
            }

            while(!foundEnough && indexNotFound<=limitNotFound) {
                val filteredTravels: List<Travel> = theTravelModel.getSearchTravels(newSearch, lastStartDate)
                Log.d("allTravels", "Total travels fetched from getSearchTravels(): ${filteredTravels.size}")

                if(filteredTravels.isNotEmpty()){
                    lastStartDate = filteredTravels.last().startDate!!.format(firebaseFormatter)?: ""
                }

                val filtered = filteredTravels.filter { travel ->
                    var creatorNotMe = true
                    if (AppState.isLogged.value){
                        creatorNotMe = travel.creator.userId != AppState.myProfile.value.userId
                    }
                    val textMatch = textFilter.isBlank() ||
                            travel.title?.contains(textFilter, ignoreCase = true) == true ||
                            travel.description?.contains(textFilter, ignoreCase = true) == true

                    val placeMatchTravel =
                        placeFilter.isEmpty() || travel.country.equals(placeFilter, ignoreCase = true)
                    val placeMatchItinerary = placeFilter.isEmpty() || travel.travelItinerary!!.any {
                        it.places.any {
                            it.equals(
                                placeFilter,
                                ignoreCase = true
                            )
                        }
                    }
                    val placeMatch = placeMatchTravel || placeMatchItinerary

                    val durationMatch = if (travel.startDate != null && travel.endDate != null) {
                        val duration = daysBetween(travel.startDate!!, travel.endDate!!)
                        duration in minDurationFilter..maxDurationFilter
                    } else {
                        false// or false, depending on whether you want to allow unknown durations
                    }


                    var availableMatch = true
                    if (availableFilter && AppState.isLogged.value){
                        availableMatch = !travel.travelCompanions!!.any{ it.user.userId == AppState.myProfile.value.userId }
                    }
                    /*
                    val startDateMatch =
                        startDateFilter == null || (travel.startDate != null && !travel.startDate!!.isBefore(
                            startDateFilter
                        ))
                    val endDateMatch =
                        endDateFilter == null || (travel.endDate != null && !travel.endDate!!.isAfter(
                            endDateFilter
                        ))
                    */

                    val priceMatch = (travel.priceMin ?: 0) >= minPriceFilter && (travel.priceMax
                        ?: Int.MAX_VALUE) <= maxPriceFilter

                    val typeMatch =
                        travelTypesFilter.isEmpty() || travel.travelTypes?.any { it in travelTypesFilter } == true

                    val companionsMatch =
                        (travel.maxPeople ?: 0) in minCompanionsFilter..maxCompanionsFilter

                    val isMatch = creatorNotMe && durationMatch && textMatch && placeMatch && companionsMatch  && typeMatch  && priceMatch//&& availableMatch && startDateMatch && endDateMatch
                    isMatch
                }

                indexNotFound=indexNotFound+1
                if(filtered.isNotEmpty()){
                    filteredTot = filteredTot + filtered
                    foundEnough = filteredTot.size>3
                }
            }

            if(lastStartDateFirebaseFound==null){
                AppState.updateLastSearchResults(filteredTot)
                _travelUiState.value =
                    if (filteredTot.isEmpty()) TravelUiState.Empty
                    else TravelUiState.Success(filteredTot)
            } else {
                _isLoadingMore.value=false
                val current = _travelUiState.value
                if (current is TravelUiState.Success) {
                    val merged = current.travels + filteredTot
                    _travelUiState.value = TravelUiState.Success(merged)
                }
            }
            model.updateFilterTriggeredValue(false)
            model.updateLastStartDateFirebaseFound(lastStartDate?:"")
        }
    }
}

class SearchViewModelFactory(
    private val search: Search
) : ViewModelProvider.Factory {
    private val model = SearchModel(search)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SearchViewModel::class.java) ->
                SearchViewModel(model) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}