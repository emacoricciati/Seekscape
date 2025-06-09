package it.polito.mad.lab5g10.seekscape.firebase

import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.firestore.FieldValue
import it.polito.mad.lab5g10.seekscape.models.NotificationItem
import it.polito.mad.lab5g10.seekscape.models.ProfilePic
import it.polito.mad.lab5g10.seekscape.models.Review
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.models.TravelReview
import it.polito.mad.lab5g10.seekscape.models.User
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class TheReviewModel() {


    suspend fun addReviews(travelReview: TravelReview?, userReviews: MutableMap<String, Review>): Result<Pair<Result<Void?>?, List<Result<Void?>>>> {
        return try {

            val travelReviewResult = travelReview?.let {
                addTravelReview(it)
            }
            val userReviewResults = coroutineScope {
                userReviews.map { (userId, userReview) ->
                    async {
                        addUserReview(userReview, userId)
                    }
                }.map { it.await() }
            }

            // Return a pair of the travel review result and all user review results
            Result.success(Pair(travelReviewResult, userReviewResults))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addTravelReview(travelReview: TravelReview): Result<Void?> {
        return try {
            var travelReviewFirebase = travelReview.toFirestoreModel()
            val docRef = Collections.travels.document(travelReview.travel_id)

            val uploadedReviewUrls = coroutineScope {
                travelReview.reviewImages?.mapIndexedNotNull { index, image ->
                    if (image is TravelImage.Url) {
                        Log.d("Inserting Review IMAGE", "Travel id: "+travelReview.travel_id)
                        async {
                            CommonModel.uploadImageToFirebase(
                                image.value.toUri(),
                                "travels/t_${travelReview.travel_id}/reviewImages/r_${travelReviewFirebase.date}"
                            )
                        }
                    } else null
                }?.mapNotNull { it.await() } ?: emptyList()
            }

            travelReviewFirebase.reviewImages = uploadedReviewUrls

            val snapshot = docRef.get().await()
            val existingTravelReviews = snapshot.get("travelReviews") as? List<Map<String, Any>> ?: emptyList()
            val newTravelReviews = listOf(travelReviewFirebase) + existingTravelReviews
            docRef.update("travelReviews", newTravelReviews).await()

            Log.d("addTravelReview", "Review added to array successfully.")
            Result.success(null)
        } catch (e: Exception) {
            Log.e("addTravelReview", "Failed to review tag", e)
            Result.failure(e)
        }
    }

    suspend fun addUserReview(userReview: Review, userId: String): Result<Void?> {
        return try {
            var userReviewFirebase = userReview.toFirestoreModel()
            val docRef = Collections.users.document(userId)

            val snapshot = docRef.get().await()
            val existingReviews = snapshot.get("reviews") as? List<Map<String, Any>> ?: emptyList()
            val newReviewList = listOf(userReviewFirebase) + existingReviews
            docRef.update("reviews", newReviewList).await()

            Log.d("addUserReview", "Review added to array successfully.")
            Result.success(null)
        } catch (e: Exception) {
            Log.e("addUserReview", "Failed to review tag", e)
            Result.failure(e)
        }
    }

}
