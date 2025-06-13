package it.polito.mad.lab5g10.seekscape.firebase

import android.util.Log
import it.polito.mad.lab5g10.seekscape.calculateAge
import it.polito.mad.lab5g10.seekscape.models.Activity
import it.polito.mad.lab5g10.seekscape.models.Itinerary
import it.polito.mad.lab5g10.seekscape.models.NotificationItem
import it.polito.mad.lab5g10.seekscape.models.ProfilePic
import it.polito.mad.lab5g10.seekscape.models.Request
import it.polito.mad.lab5g10.seekscape.models.Review
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelCompanion
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.models.TravelReview
import it.polito.mad.lab5g10.seekscape.models.User
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.text.format


val firebaseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun imageListToFirestoreModel(travelImageList: List<TravelImage>?=null): List<String>? {
    val imgUrlList = mutableListOf<String>()
    if (travelImageList==null || travelImageList.isEmpty()) {
        return null;
    }
    for (travelImage in travelImageList) {
        val imgUrl = imageToFirestoreModel(travelImage = travelImage)
        if (imgUrl != null && imgUrl != "") {
            imgUrlList.add(imgUrl)
        }
    }
    if(imgUrlList.isEmpty())
        return null;
    return imgUrlList
}

fun imageToFirestoreModel(profilePic: ProfilePic?=null, travelImage: TravelImage?=null): String? {
    if (profilePic!=null && (profilePic is ProfilePic.Url)) {
        return profilePic.value
    }

    if (travelImage!=null && (travelImage is TravelImage.Url)) {
        return travelImage.value
    }
    return null
}


fun imageListToAppModelTravel(imgUrlList: List<String>?): List<TravelImage>? {
    if (imgUrlList.isNullOrEmpty()) return null

    return imgUrlList.mapNotNull { imgUrl ->
        imageToTravelImage(imgUrl)
    }.takeIf { it.isNotEmpty() }
}

fun imageListToAppModelTravelLite(imgUrlList: List<String>?): List<TravelImage>? {
    if (imgUrlList.isNullOrEmpty()) return null
    var travelImg:TravelImage? = null;
    for(imgUrl in imgUrlList){
        travelImg = imageToTravelImage(imgUrl)
        if(travelImg!=null)
            return listOf<TravelImage>(travelImg)
    }
    return listOf()
}

fun imageListToAppModelProfile(imgUrlList: List<String>?): List<ProfilePic>? {
    if (imgUrlList.isNullOrEmpty()) return null

    return imgUrlList.mapNotNull { imgUrl ->
        imageToProfilePic(imgUrl)
    }.takeIf { it.isNotEmpty() }
}

fun imageToTravelImage(imgUrl: String?): TravelImage? {
    if (imgUrl.isNullOrBlank()) return null
    return TravelImage.Url(imgUrl)
}

fun imageToProfilePic(imgUrl: String?): ProfilePic? {
    if (imgUrl.isNullOrBlank()) return null
    return ProfilePic.Url(imgUrl)
}



fun imageToAppModel(imgUrl:String?, isProfilePic: Boolean=false, isTravelImage: Boolean=false): Any?{
    if(imgUrl==null || imgUrl=="")
        return null

    if(isProfilePic){
        return ProfilePic.Url(imgUrl)
    }
    if(isTravelImage){
        return TravelImage.Url(imgUrl)
    }
    return null;
}



data class UserFirestoreModel(
    var userId: String = "",
    var authUID: String = "", // INSERT authUID
    var fcmTokens: List<String> = emptyList(),
    var nickname: String = "",
    var name: String = "",
    var surname: String = "",
    var birthDay: String = "", // CAST calculate age from birthDay
    var nationality: String = "",
    var city: String = "",
    var language: String = "",
    var phoneNumber: String? = null,
    var email: String?  = null,
    var profilePic: String? = null, // CAST ProfilePic data structure ????
    var bio: String?  = null,
    var personality: List<String> = emptyList(),
    var travelPreferences: List<String>? = null,
    var reviews: List<ReviewFirestoreModel>? = emptyList(), // CAST Review - ReviewFirestoreModel
    var desiredDestinations: List<String>? = null,
    var numTravels: Int =0,
    var notifications: List<NotificationItem>? = emptyList() // NotificationItem does not need casting
) : Serializable


fun User.toFirestoreModel(): UserFirestoreModel {
    return UserFirestoreModel(
        userId = this.userId,
        authUID = this.authUID ?: "",
        nickname = this.nickname,
        name = this.name,
        surname = this.surname,
        birthDay = "",
        nationality = this.nationality,
        city = this.city,
        language = this.language,
        phoneNumber = this.phoneNumber,
        email = this.email,
        profilePic = imageToFirestoreModel(profilePic=this.profilePic),
        bio = this.bio,
        personality = this.personality,
        travelPreferences = this.travelPreferences,
        reviews = this.reviews?.map { it.toFirestoreModel() },
        desiredDestinations = this.desiredDestinations,
        numTravels = this.numTravels,
        notifications = this.notifications
    )
}


suspend fun UserFirestoreModel.toAppModel(isMyProfile:Boolean = false): User {
    return User(
        userId = this.userId,
        nickname = this.nickname,
        name = this.name,
        surname = this.surname,
        age = calculateAge(this.birthDay),
        nationality = this.nationality,
        city = this.city,
        language = this.language,
        phoneNumber = if(isMyProfile) this.phoneNumber else "",
        email = if(isMyProfile) this.email else "",
        profilePic = imageToAppModel(this.profilePic, isProfilePic = true) as ProfilePic?,
        bio = this.bio,
        personality = this.personality,
        travelPreferences = this.travelPreferences,
        reviews = this.reviews?.map { it.toAppModel() },
        desiredDestinations = this.desiredDestinations,
        numTravels = this.numTravels,
        notifications = if (isMyProfile) this.notifications ?: listOf() else listOf()
    )
}


data class ReviewFirestoreModel(
    var date: String = "",
    var reviewText: String = "",
    var rating: Any? = 0.0,
    var authorId: String  = ""  // CAST User(light version) - userId
) : Serializable


fun Review.toFirestoreModel(): ReviewFirestoreModel {
    return ReviewFirestoreModel(
        this.date.format(firebaseFormatter),
        this.reviewText,
        this.rating,
        this.author.userId
    )
}

suspend fun ReviewFirestoreModel.toAppModel(): Review {
    val convertedRating = when (this.rating) {
        is Double -> (this.rating as Double)
        is Long -> (this.rating as Long).toDouble()
        is Int -> (this.rating as Int).toDouble()
        else -> {
            Log.e("ReviewModel", "Unexpected type for rating: ${this.rating?.javaClass?.name}, value: ${this.rating}")
            0.0
        }
    }
    return Review(
        LocalDate.parse(this.date, firebaseFormatter)!!,
        this.reviewText,
        convertedRating,
        CommonModel.getLiteUser(this.authorId)!!,
    )
}




data class TravelFirestoreModel(
    var travelId: String = "",
    var creatorId: String = "",
    var title: String? = null,
    var description: String? = null,
    var country: String? = null,
    var priceMin: Int? = null,
    var priceMax: Int? = null,
    var status: String? =null,
    var distance: String? = null,
    var startDate: String? = null,
    var endDate: String? = null,
    var maxPeople: Int? = null,
    var travelImages: List<String>? = emptyList(),
    var travelTypes: List<String>? = emptyList(),
    var travelItinerary: List<ItineraryFirestoreModel>? = emptyList(),
    var travelCompanions: List<TravelCompanionFirestoreModel>? = emptyList(),
    var travelReviews: List<TravelReviewFirestoreModel>? = emptyList(),
    var travelRating: Double? = null
) : Serializable




fun Travel.toFirestoreModel(): TravelFirestoreModel {
    return TravelFirestoreModel(
        travelId = this.travelId,
        creatorId = this.creator.userId,
        title = this.title,
        description = this.description,
        country = this.country,
        priceMin = this.priceMin,
        priceMax = this.priceMax,
        status = this.status,
        distance = this.distance,
        startDate = this.startDate?.format(firebaseFormatter),
        endDate = this.endDate?.format(firebaseFormatter),
        maxPeople = this.maxPeople,
        travelImages = imageListToFirestoreModel(this.travelImages),
        travelTypes = this.travelTypes,
        travelItinerary = this.travelItinerary?.map { it.toFirestoreModel() },
        travelCompanions = this.travelCompanions?.map { it.toFirestoreModel() },
        travelReviews = this.travelReviews?.map { it.toFirestoreModel() },
        travelRating = this.travelRating
    )
}

suspend fun TravelFirestoreModel.toAppModel(isTravelLite:Boolean = false, statusForUser:String=""): Travel {
    return Travel(
        travelId = this.travelId,
        creator = CommonModel.getLiteUser(this.creatorId)!!,
        title = this.title,
        description = this.description,
        country = this.country,
        priceMin = this.priceMin,
        priceMax = this.priceMax,
        distance = this.distance,
        status = this.status,
        statusForUser = statusForUser,
        startDate = LocalDate.parse(this.startDate, firebaseFormatter)!!,
        endDate = LocalDate.parse(this.endDate, firebaseFormatter)!!,
        maxPeople = this.maxPeople,
        travelImages = if(isTravelLite) imageListToAppModelTravelLite(this.travelImages) else imageListToAppModelTravel(this.travelImages),
        travelTypes = this.travelTypes,
        travelItinerary = if(isTravelLite) listOf() else this.travelItinerary?.map { it.toAppModel() },
        travelCompanions = this.travelCompanions?.map { it.toAppModel(isTravelLite) },
        travelReviews = if(isTravelLite) listOf() else this.travelReviews?.map { it.toAppModel() },
        travelRating = this.travelRating
    )
}



data class ItineraryFirestoreModel(
    var itineraryId: Int = 0,
    var name: String = "",
    var startDate: String = "", // LocalDate as String
    var endDate: String? = null,
    var places: List<String> = emptyList(),
    var description: String = "",
    var itineraryImages: List<String>? = emptyList(),
    var activities: List<Activity> = emptyList()
) : Serializable


fun Itinerary.toFirestoreModel(): ItineraryFirestoreModel {
    return ItineraryFirestoreModel(
        itineraryId = this.itineraryId,
        name = this.name,
        startDate = this.startDate.format(firebaseFormatter),
        endDate = this.endDate?.format(firebaseFormatter),
        places = this.places,
        description = this.description,
        itineraryImages = null,//TODO
        activities = this.activities
    )
}

fun ItineraryFirestoreModel.toAppModel(): Itinerary {
    val parsedStartDate = this.startDate.takeIf { it.isNotBlank() }?.let {
        LocalDate.parse(it, firebaseFormatter)
    } ?: throw IllegalArgumentException("Start date must not be null or blank")

    val parsedEndDate = this.endDate?.takeIf { it.isNotBlank() }?.let {
        LocalDate.parse(it, firebaseFormatter)
    }

    return Itinerary(
        itineraryId = this.itineraryId,
        name = this.name,
        startDate = parsedStartDate,
        endDate = parsedEndDate,
        places = this.places,
        description = this.description,
        itineraryImages = null,
        activities = this.activities
    )
}


data class TravelReviewFirestoreModel(
    var travelReviewText: String? = null,
    var rating: Double? = null,
    var authorId: String="", // CAST User(light version) - userId
    var reviewImages: List<String>? = null, // CAST TravelImage data structure ????
    var date: String  = ""// CAST of type LocalDate - String
) : Serializable



fun TravelReview.toFirestoreModel(): TravelReviewFirestoreModel {
    return TravelReviewFirestoreModel(
        travelReviewText = this.travelReviewText,
        rating = this.rating,
        authorId = this.author.userId,
        reviewImages = imageListToFirestoreModel(this.reviewImages),
        date = this.date.format(firebaseFormatter)
    )
}

suspend fun TravelReviewFirestoreModel.toAppModel(): TravelReview {
    return TravelReview(
        "","",
        this.travelReviewText,
        this.rating,
        CommonModel.getLiteUser(this.authorId)!!,
        imageListToAppModelTravel(this.reviewImages),
        LocalDate.parse(this.date, firebaseFormatter)!!
    )
}


data class TravelCompanionFirestoreModel(
    var userId: String = "", // CAST User(light version) - userId
    var extras: Int = 0,
) : Serializable



fun TravelCompanion.toFirestoreModel(): TravelCompanionFirestoreModel {
    return TravelCompanionFirestoreModel(
        this.user.userId,
        this.extras
    )
}

suspend fun TravelCompanionFirestoreModel.toAppModel(isTravelLite:Boolean=false): TravelCompanion {
    return TravelCompanion(
        if(isTravelLite) unknown_User else CommonModel.getLiteUser(this.userId)!!,
        this.extras
    )
}




data class RequestFirestoreModel(
    var id: String = "",
    var authorId: String = "",
    var tripId: String = "",
    val reqMessage: String? = null,
    val accepted: Boolean = false,
    val refused: Boolean = false,
    val spots: Int = 1,
    val responseMessage: String? = null,
    val lastUpdate: String?=null
) : Serializable

fun Request.toFirestoreModel(): RequestFirestoreModel {
    return RequestFirestoreModel(
        id = this.id,
        authorId = this.author.userId,
        tripId = this.trip.travelId,
        reqMessage = this.reqMessage,
        accepted = this.isAccepted,
        refused = this.isRefused,
        spots = this.spots,
        responseMessage = this.responseMessage,
        lastUpdate = this.lastUpdate.format(firebaseFormatter)
    )
}

suspend fun RequestFirestoreModel.toAppModel(): Request? {
    val author = CommonModel.getLiteUser(this.authorId)
    val trip = CommonModel.getTravelById(this.tripId)

    if (author == null) {
        Log.e("toAppModel", "author is null for request $id")
        return null
    } else if (trip==null) {
        Log.e("toAppModel", "trip is null for request $id with tripId: ${this.tripId}")
        return null
    } else {
        return Request(
            id = this.id,
            author = author,
            trip = trip,
            reqMessage = this.reqMessage ?: "",
            isAccepted = this.accepted,
            isRefused = this.refused,
            spots = this.spots,
            responseMessage = this.responseMessage ?: "",
            lastUpdate = LocalDate.parse(this.lastUpdate, firebaseFormatter)!!
        )
    }

}