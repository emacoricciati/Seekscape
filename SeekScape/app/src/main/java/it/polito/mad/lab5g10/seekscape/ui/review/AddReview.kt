package it.polito.mad.lab5g10.seekscape.ui.review

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.TravelCompanion
import it.polito.mad.lab5g10.seekscape.models.TravelReviewViewModel
import it.polito.mad.lab5g10.seekscape.models.User
import it.polito.mad.lab5g10.seekscape.ui._common.components.IconDateRange
import it.polito.mad.lab5g10.seekscape.ui._common.components.IconLocation
import it.polito.mad.lab5g10.seekscape.ui._common.components.UserImage
import it.polito.mad.lab5g10.seekscape.ui.add.ImagePickerWithPreview
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions


@Composable
fun AddReviewScreen(vm: TravelReviewViewModel, navCont: NavHostController) {
    val context = LocalContext.current
    val currentUser: User = AppState.myProfile.collectAsState().value
    val travelReviewText by vm.travelReviewText.collectAsState()
    val rating by vm.rating.collectAsState()
    val reviewImages by vm.reviewImages.collectAsState()
    val companionReviews by vm.companionReviews.collectAsState()
    var page by remember { mutableStateOf(0)}
    val travel by vm.travel.collectAsState()

    if (travel == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

    } else {

        val currentTravel = travel!!
        val organizer = TravelCompanion(user = currentTravel.creator)
        val otherUsers = currentTravel.travelCompanions?.filter {
            it.user.userId != currentUser.userId && it.user.userId != organizer.user.userId
        } ?: emptyList()
        val updatedOtherUsers = listOf(organizer) + otherUsers

        val actions = remember(navCont) { Actions(navCont) }

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                when (page) {
                    0->{
                        //TRAVEL_TITLE----------------------------------------
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = currentTravel.title ?: "No Title",
                            style = MaterialTheme.typography.headlineMedium,
                        )

                        Spacer(modifier = Modifier.height(15.dp))
                        //TRAVEL_LOCATION & TRAVEL_DATE------------------------
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconLocation(currentTravel.country ?: "Unknown")
                            Spacer(modifier = Modifier.weight(1f))

                            currentTravel.startDate?.let { start ->
                                currentTravel.endDate?.let { end ->
                                    IconDateRange(start, end)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(45.dp))

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(20.dp))
                        //STARS-------------------------------------------------
                        Text(
                            text = "Rating",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        StarRatingBar(
                            rating = rating,
                            onRatingChanged = { vm.setRating(it) }
                        )
                        if(vm.ratingError.isNotBlank()){
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                vm.ratingError,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(20.dp))
                        //REVIEW_TRAVEL_TEXT-------------------------------------------------
                        Text("Review", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(20.dp))
                        OutlinedTextField(
                            value =  travelReviewText,
                            onValueChange = { vm.setReviewText(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(horizontal = 16.dp),
                            placeholder = { Text("Enter your review...", fontSize = 16.sp) }
                        )
                        if(vm.reviewTextError.isNotBlank()){
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                vm.reviewTextError,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        //REVIEW_IMAGES----------------------------------------------
                        Spacer(modifier = Modifier.height(30.dp))

                        ImagePickerWithPreview(
                            imageUris = reviewImages,
                            onAddImage = { vm.addReviewImage(it) },
                            onRemoveImage = { vm.removeReviewImage(it) }
                        )
                    }
                    //in all the other cases crate as many page as the travel companions
                    in 1..updatedOtherUsers.size -> {
                        val user = updatedOtherUsers[page - 1]
                        val companionReview = companionReviews[user!!.user.userId] ?: TravelReviewViewModel.CompanionReview("", 0.0)
                        val role = if (page == 1) "Trip Organizer" else "Trip Companion"
                        val textError by vm.companionReviewTextErrors.collectAsState()
                        val ratingError by vm.companionReviewRatingErrors.collectAsState()
                        UserReviewPage(
                            user = user.user,
                            role = role,
                            initialRating = companionReview.rating,
                            initialReviewText = companionReview.reviewText,
                            onRatingChanged = { newRating ->
                                vm.setCompanionReview(user.user.userId, companionReview.reviewText, newRating)
                            },
                            onReviewTextChanged = { newText ->
                                vm.setCompanionReview(user.user.userId, newText, companionReview.rating)
                            },
                            reviewTextError = textError[user.user.userId],
                            ratingError = ratingError[user.user.userId]
                        )

                    }else -> {
                        Text("No user to review")
                    }
                }
            }
            //NAVIGATION_BUTTONS------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if(page>0) {
                            page--
                        } else {
                            actions.navigateBack()
                        }
                    },
                ) {
                    Text("Back")
                }

                Button(
                    onClick = {
                        if (page != updatedOtherUsers.size) {
                            if (page == 0) {
                                if(vm.isEmptyReviewTravel() || vm.validateReview()){
                                    page++
                                }
                            } else {
                                val user = updatedOtherUsers[page - 1]
                                if(vm.isEmptyReviewCompanion(user.user.userId) || vm.validateCompanionReviews(user.user.userId)){
                                    page++
                                }
                            }
                        } else {
                            vm.submitReview()
                            Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()
                            actions.backToTravelsTab()
                        }
                    },
                    enabled = page!=updatedOtherUsers.size || !vm.areAllEmpty()
                ) {

                    if (page==0){
                        if(vm.isEmptyReviewTravel()){
                            Text("Skip")
                        } else{
                            Text("Next")
                        }
                    } else if(page != updatedOtherUsers.size){
                        val user = updatedOtherUsers[page - 1]
                        if (vm.isEmptyReviewCompanion(user.user.userId)){
                            Text("Skip")
                        } else{
                            Text("Next")
                        }
                    } else {
                        Text("Submit")
                    }

                }
            }

        }
    }
}


@Composable
fun StarRatingBar(
    rating: Double,
    onRatingChanged: (Double) -> Unit,
    totalStars: Int = 5,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..totalStars) {
            val icon = when {
                i <= rating -> Icons.Default.Star
                i - 0.5 == rating -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Default.StarBorder
            }

            Icon(
                imageVector = icon,
                contentDescription = "Star $i",
                tint = if (i - 0.5 <= rating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clickable {
                        val newRating = when {
                            rating == i.toDouble() -> i - 0.5
                            else -> i.toDouble()
                        }
                        onRatingChanged(newRating)
                    }
            )
        }
    }
}

@Composable
fun UserReviewPage(
    user: User,
    role: String,
    initialRating: Double,
    initialReviewText: String,
    onRatingChanged: (Double) -> Unit,
    onReviewTextChanged: (String) -> Unit,
    reviewTextError: String?,
    ratingError: String?
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //USER_IMAGE & NAME_SURNAME-----------------------------
        UserImage(
            icon = user.profilePic,
            size = 100.dp,
            name = user.name,
            surname = user.surname,
            fontSize = 50
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${user.name} ${user.surname}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = role,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.Start)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider()
    //STARS--------------------------------------------------------
    Spacer(modifier = Modifier.height(8.dp))
    Text("Rating", style = MaterialTheme.typography.titleLarge)
    Spacer(modifier = Modifier.height(8.dp))
    StarRatingBar(
        rating = initialRating,
        onRatingChanged = {
            onRatingChanged(it)
        }
    )
    if (!ratingError.isNullOrEmpty()) {
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            ratingError,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
    Spacer(modifier = Modifier.height(20.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.height(20.dp))
    //USER_REVIEW_TEXT--------------------------------------------------------
    Text("Review", style = MaterialTheme.typography.titleLarge)
    Spacer(modifier = Modifier.height(20.dp))
    OutlinedTextField(
        value = initialReviewText,
        onValueChange = {
            onReviewTextChanged(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 16.dp),
        placeholder = { Text("Enter your review...", fontSize = 16.sp) }
    )
    if (!reviewTextError.isNullOrEmpty()) {
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            reviewTextError,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}




