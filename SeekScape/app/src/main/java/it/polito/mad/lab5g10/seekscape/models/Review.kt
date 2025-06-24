package it.polito.mad.lab5g10.seekscape.models

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.TheReviewModel
import it.polito.mad.lab5g10.seekscape.firebase.TheTravelModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.Serializable
import java.time.LocalDate
import kotlin.collections.plus
import kotlin.collections.set

data class TravelReview(
    val travel_review_id: String,
    val travel_id: String,
    val travelReviewText: String? = null,
    val rating: Double? = null,
    val author: User,
    val reviewImages: List<TravelImage>? = null,
    val date: LocalDate
) : Serializable

fun getBlankTravelReview(author: User, travelId: String): TravelReview {
    return TravelReview(
        travel_review_id = "",
        travel_id = travelId,
        travelReviewText = "",
        rating = 0.0,
        author = author,
        reviewImages = null,
        date = LocalDate.now()
    )
}


class TravelReviewModel(review: TravelReview){
    private fun <T> createStateFlow(initialValue: T) = MutableStateFlow(initialValue)

    val isTravelLoadedValue = createStateFlow(false)
    val travelValue = createStateFlow<Travel?>(null)

    val travel_review_id_value = createStateFlow(review.travel_review_id)

    val travel_id_value = createStateFlow(review.travel_id)
    val travelReviewTextValue = createStateFlow(review.travelReviewText?: "")
    val ratingValue = createStateFlow(review.rating?: 0.0)
    val authorValue = createStateFlow(review.author)
    val reviewImagesValue = MutableStateFlow(review.reviewImages ?: emptyList())
    val dateValue = createStateFlow(review.date)


    // Update functions

    fun loadTravel(travel: Travel){
        updateTravel(travel)
        updateisTravelLoaded(true)
    }

    fun updateisTravelLoaded(value: Boolean) { isTravelLoadedValue.tryEmit(value) }
    fun updateTravel(value: Travel){travelValue.tryEmit(value)}

    fun updateTravelReviewId(value: String){travel_review_id_value.tryEmit(value)}
    fun updateTravelID(value: String){travel_id_value.tryEmit(value)}
    fun updateReviewText(value: String){travelReviewTextValue.tryEmit(value)}
    fun updateRating(value: Double){ratingValue.tryEmit(value)}
    fun updateAuthor(value: User){authorValue.tryEmit(value)}
    fun updateReviewImages(value: List<TravelImage>) { reviewImagesValue.tryEmit(value) }
    fun updateDate(value: LocalDate){dateValue.tryEmit(value)}

}

class TravelReviewViewModel(private val model: TravelReviewModel): ViewModel() {

    private val theReviewModel = TheReviewModel()
    init {
        if(!model.isTravelLoadedValue.value) {
            viewModelScope.launch {
                val travel = CommonModel.getTravelById(model.travel_id_value.value)
                if(travel!=null){
                    model.loadTravel(travel)
                }
            }

        }
    }


    data class CompanionReview(
        val reviewText: String,
        val rating: Double
    )

    private val _companionReviews = MutableStateFlow<Map<String, CompanionReview>>(emptyMap())
    val companionReviews = _companionReviews

    private val _companionReviewTextErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val companionReviewTextErrors: StateFlow<Map<String, String>> = _companionReviewTextErrors

    private val _companionReviewRatingErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val companionReviewRatingErrors: StateFlow<Map<String, String>> = _companionReviewRatingErrors


    fun setCompanionReview(userId: String, text: String, rating: Double) {
        _companionReviews.value = _companionReviews.value.toMutableMap().apply {
            this[userId] = CompanionReview(reviewText = text, rating = rating)
        }
    }


    // Expose Model data
    val travel = model.travelValue
    val travel_review_id= model.travel_review_id_value
    val travel_id = model.travel_id_value
    val travelReviewText= model.travelReviewTextValue
    val rating= model.ratingValue
    val author= model.authorValue
    val reviewImages = model.reviewImagesValue
    val date = model.dateValue

    fun setTravelReviewId(value: String) = model.updateTravelReviewId(value)
    fun setTravelID(value: String) = model.updateTravelID(value)
    fun setReviewText(value: String) = model.updateReviewText(value)
    fun setRating(value: Double) = model.updateRating(value)
    fun setAuthor(value: User) = model.updateAuthor(value)
    fun setReviewImages(value: List<TravelImage>) = model.updateReviewImages(value)

    fun addReviewImage(image: TravelImage) {
        val currentImages = reviewImages.value
        if (currentImages.size < 5) {
            setReviewImages(currentImages + image)
        }
    }

    fun removeReviewImage(image: TravelImage) {
        setReviewImages(reviewImages.value - image)
    }

    fun submitReview() {

        val myProfile = AppState.myProfile.value

        val travelReview = if(isEmptyReviewTravel()) null else TravelReview(
            travel_review_id = travel_review_id.value,
            travel_id = travel_id.value,
            travelReviewText = travelReviewText.value,
            rating = rating.value,
            author = author.value,
            reviewImages = reviewImages.value,
            date = date.value
        )

        val userReviews = mutableMapOf<String, Review>()
        companionReviews.value.forEach { (userId, companionReview) ->
            if (!isEmptyReviewCompanion(userId)) {
                val newReview = Review(
                    date = LocalDate.now(),
                    reviewText = companionReview.reviewText,
                    rating = companionReview.rating,
                    author = myProfile
                )
                userReviews.put(userId, newReview)
                Log.d("ReviewDebug", "Created review: $newReview")
            }
        }
        viewModelScope.launch {
            theReviewModel.addReviews(travelReview, userReviews)
            AppState.setTravelToTab(null, "travels")
        }
    }


    fun validateCompanionReviews(expectedUserIds: List<String>): Boolean {
        val textErrors = mutableMapOf<String, String>()
        val ratingErrors = mutableMapOf<String, String>()

        expectedUserIds.forEach { userId ->
            val review = companionReviews.value[userId]
            if (review == null) {
                textErrors[userId] = "Review missing"
                ratingErrors[userId] = "Rating missing"
            } else {

                if (review.reviewText.isBlank()) {
                    textErrors[userId] = "Review text cannot be blank"
                }
                if (review.rating <= 0.0) {
                    ratingErrors[userId] = "Rating must be greater than 0"
                }
            }
        }

        _companionReviewTextErrors.value = textErrors
        _companionReviewRatingErrors.value = ratingErrors

        return textErrors.isEmpty() && ratingErrors.isEmpty()
    }

    fun validateCompanionReviews(userId: String): Boolean {
        val textErrors = mutableMapOf<String, String>()
        val ratingErrors = mutableMapOf<String, String>()

        val review = companionReviews.value[userId]
        if (review == null) {
            textErrors[userId] = "Review missing"
            ratingErrors[userId] = "Rating missing"
        } else {

            if (review.reviewText.isBlank()) {
                textErrors[userId] = "Review text cannot be blank"
            }
            if (review.rating <= 0.0) {
                ratingErrors[userId] = "Rating must be greater than 0"
            }
        }

        _companionReviewTextErrors.value = textErrors
        _companionReviewRatingErrors.value = ratingErrors

        return textErrors.isEmpty() && ratingErrors.isEmpty()
    }

    fun validateReview(): Boolean {
        var errorCount = 0

        listOf(
            Triple(model.travelReviewTextValue.value.isBlank(), "Review text cannot be blank", ::validateReviewText),
            Triple(model.ratingValue.value <= 0.0, "Rating must be greater than 0", ::validateRating),
        ).forEach { (shouldShowError, errorMessage, validateFunc) ->
            if (shouldShowError) {
                validateFunc(errorMessage)
                errorCount++
            } else {
                validateFunc("")
            }
        }
        return errorCount == 0
    }

    fun isEmptyReviewTravel(): Boolean {
        return travelReviewText.value.isBlank() && rating.value==0.0 && reviewImages.value.isEmpty()
    }

    fun isEmptyReviewCompanion(userId: String): Boolean {
        val rev = _companionReviews.value[userId]
        if(rev==null)
            return true;

        return rev.reviewText.isBlank() && rev.rating==0.0
    }

    fun areAllEmpty(): Boolean {
        var allEmpty = isEmptyReviewTravel()
        companionReviews.value.forEach { (userId, companionReview) ->
            allEmpty = allEmpty && isEmptyReviewCompanion(userId)
        }
        return allEmpty;
    }


    // Validation error messages
    var reviewTextError by mutableStateOf("")
    var ratingError by mutableStateOf("")

    private fun validateReviewText(error: String) { reviewTextError = error }
    private fun validateRating(error: String) { ratingError = error }


}

class TravelReviewViewModelFactory(
    private val review: TravelReview
) : ViewModelProvider.Factory {
    private val model = TravelReviewModel(review)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(TravelReviewViewModel::class.java) ->
                TravelReviewViewModel(model) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}