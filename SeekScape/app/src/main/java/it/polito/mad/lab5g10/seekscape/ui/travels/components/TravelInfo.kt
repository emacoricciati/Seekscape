package it.polito.mad.lab5g10.seekscape.ui.travels.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import it.polito.mad.lab5g10.seekscape.R
import it.polito.mad.lab5g10.seekscape.cleanStack
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.toAppModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.DENIED
import it.polito.mad.lab5g10.seekscape.models.JOINED
import it.polito.mad.lab5g10.seekscape.models.Request
import it.polito.mad.lab5g10.seekscape.ui._common.SquareImage
import it.polito.mad.lab5g10.seekscape.ui._common.components.IconCost
import it.polito.mad.lab5g10.seekscape.ui._common.components.IconDateRange
import it.polito.mad.lab5g10.seekscape.ui._common.components.IconLocation
import it.polito.mad.lab5g10.seekscape.ui._common.components.IconPeopleJoined
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.models.TravelViewModel
import it.polito.mad.lab5g10.seekscape.timeAgo
import it.polito.mad.lab5g10.seekscape.ui._common.components.ShowResponseModal
import it.polito.mad.lab5g10.seekscape.ui._common.components.UserImage
import it.polito.mad.lab5g10.seekscape.ui._common.components.UserReview
import it.polito.mad.lab5g10.seekscape.ui._common.components.UserStarsAndNickname
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import it.polito.mad.lab5g10.seekscape.ui.navigation.Destinations
import kotlinx.coroutines.launch
import java.text.DecimalFormat


@Composable
fun TravelImages(
    imageResources: List<TravelImage>,
    modifier: Modifier = Modifier,
    onImageSelected: (Int) -> Unit = {},
    onOpenInFull: (Int) -> Unit = {},
    actions: Actions,
    travelId: String,
) {

    val isLoggedIn by AppState.isLogged.collectAsState()
    val selectedIndex = remember { mutableStateOf(0) }
    val travelImage = imageResources[selectedIndex.value]
    val imagePainter = when (val image = travelImage) {
        is TravelImage.Resource -> painterResource(id = image.resId)  // For drawable resources
        is TravelImage.Url -> rememberAsyncImagePainter(image.value)  // For image URLs
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 35.dp,
                    bottomEnd = 35.dp
                ),
                clip = false
            )
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 0.dp,
                bottomStart = 35.dp,
                bottomEnd = 35.dp
            ))
    ) {

        Image(
            painter = imagePainter,
            contentDescription = "Main background",
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 35.dp,
                        bottomEnd = 35.dp
                    )
                ),
            contentScale = ContentScale.Crop
        )

        if(isLoggedIn) {
            Box(
                modifier = Modifier
                    .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
                    .align(Alignment.TopEnd)
            ) {
                Surface(
                    modifier = Modifier.size(45.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    onClick = {
                        AppState.updateCurrentTab("add")
                        AppState.updateRedirectPath("add/${travelId}/copy")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier
                            .size(30.dp)
                            .padding(8.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(end = 16.dp, top = 77.dp, bottom = 16.dp)
                .align(Alignment.TopEnd)
        ) {
            Surface(
                modifier = Modifier.size(45.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                onClick = { println("Button clicked!") }
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(end = 16.dp, top = 77.dp, bottom = 16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Surface(
                modifier = Modifier.size(45.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                onClick = {
                    onOpenInFull(selectedIndex.value)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.OpenInFull,
                    contentDescription = "OpenInFull",
                    modifier = Modifier
                        .size(30.dp)
                        .padding(8.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }


        var showAllImages by remember { mutableStateOf(false) }
        LazyColumn(
            modifier = Modifier
                .heightIn(max = 320.dp)
                .align(Alignment.CenterStart)
                .padding(start = 40.dp, top = 90.dp, bottom = 0.dp)
                .width(68.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val itemsToShow = if (showAllImages) imageResources else imageResources.take(3)
            itemsIndexed(itemsToShow) { index, imageRes ->
                val isSelected = index == selectedIndex.value

                ImageThumbnail(
                    imageRes = imageRes,
                    isSelected = index == selectedIndex.value,
                    onClick = {
                        selectedIndex.value = index
                        onImageSelected(index)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (isSelected) 1f else 1.5f)
                )
            }

            if (!showAllImages && imageResources.size > 3) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f)
                            .background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { showAllImages = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+${imageResources.size - 3}",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

    }

}


@Composable
private fun ImageThumbnail(
    imageRes: TravelImage,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (isSelected) Modifier
                    .border(2.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(8.dp))
                    .shadow(8.dp, RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable(onClick = onClick),
    ) {
        val painter = when (imageRes) {
            is TravelImage.Resource -> {
                painterResource(id = imageRes.resId) // Use the resource ID
            }
            is TravelImage.Url -> {
                rememberAsyncImagePainter(imageRes.value) // Use the URL
            }
        }
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun TravelDescription(vm: TravelViewModel, modifier: Modifier = Modifier, navCont: NavHostController, action: String?=null) {
    val context = LocalContext.current
    val currentTravelState by vm.statusForUser.collectAsState()
    val actions = remember(navCont){Actions(navCont)}

    var requestFetched:Request? by remember { mutableStateOf(null) }

    val travel_id by vm.travelIdValue.collectAsState()
    val title by vm.titleValue.collectAsState()
    val country by vm.locationValue.collectAsState()
    val creator by vm.creatorValue.collectAsState()
    val distance by vm.distanceValue.collectAsState()
    val description by vm.descriptionValue.collectAsState()
    val travelItinerary by vm.travelItineraryValues.collectAsState()
    val travelCompanions by vm.travelCompanionsValues.collectAsState()
    val travelTypes by vm.travelTypesValues.collectAsState()
    val startDate by vm.dateStartValue.collectAsState()
    val endDate by vm.dateEndValue.collectAsState()
    val maxPeople by vm.nParticipantsValue.collectAsState()
    val priceMin by vm.priceStartValue.collectAsState()
    val priceMax by vm.priceEndValue.collectAsState()
    val travelReviews by vm.travelReviewsValues.collectAsState()

    Column(modifier=modifier.padding(horizontal = 16.dp)){

        //------------------------------- TITLE -------------------------------
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(10.dp),
        )

        //------------------------------- COUNTRY and DATES, PEOPLE and PRICE -------------------------------
        val paddingHorizontalScreen = 15.dp
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(paddingHorizontalScreen))
            IconLocation(country)
            Spacer(modifier = Modifier.weight(1f))
            IconDateRange(startDate!!, endDate!!)
            Spacer(modifier = Modifier.width(paddingHorizontalScreen))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(paddingHorizontalScreen))
            IconPeopleJoined(travelCompanions, maxPeople)
            Spacer(modifier = Modifier.weight(1f))
            IconCost(priceMin,priceMax)
            Spacer(modifier = Modifier.width(paddingHorizontalScreen))
        }

        //------------------------------- USER, DISTANCE, TRAVEL TYPE -------------------------------
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val circleIconSize = 40.dp
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.weight(1f)
            ) {
                if(AppState.myProfile.collectAsState().value.userId==creator.userId) {
                    UserStarsAndNickname(creator, context, circleIconSize, false, {actions.seeProfile(creator.userId)})
                }else{
                    UserStarsAndNickname(creator, context, circleIconSize, true,  {actions.seeProfile(creator.userId)})
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.weight(1f)
            ) {
                SquareImage(R.drawable.icon_distance, size = circleIconSize)
                Spacer(modifier = Modifier.width(2.dp))

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Distance",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = distance,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.weight(1f)
            ) {
                SquareImage(R.drawable.icon_travel_type, size = circleIconSize)
                Spacer(modifier = Modifier.width(2.dp))

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Travel type",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = travelTypes!![0],
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

        //------------------------------- DESCRIPTION -------------------------------
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp)
        )


        var actionDone by remember { mutableStateOf(false) }
        var showDialog by remember { mutableStateOf(false) }
        var showAcceptDenied = false
        var textBtn = ""
        if(currentTravelState!="") {
            when (currentTravelState) {
                JOINED -> {
                    showAcceptDenied = true
                    textBtn = "acceptance message"
                }
                DENIED -> {
                    showAcceptDenied = true
                    textBtn = "reject message"
                }
            }
        }
        if(showAcceptDenied){
            val myProfile by AppState.myProfile.collectAsState()
            LaunchedEffect(requestFetched) {
                if (requestFetched==null) {
                    val reqListFirestore = CommonModel.getMyRequestForTrip(travel_id, myProfile.userId)
                    if(reqListFirestore.isNotEmpty()){
                        requestFetched=reqListFirestore[0].toAppModel()
                    }
                }
            }

            if(requestFetched!=null){
                if(!actionDone && action=="SHOW_RESPONSE"){
                    showDialog=true
                    actionDone=true
                }

                TextButton(
                    onClick = { showDialog = true },
                ) {
                    Text(text = textBtn, style = MaterialTheme.typography.bodySmall)
                }
            }

        }
        if (showDialog && requestFetched!=null) {
            ShowResponseModal(requestFetched!!, creator, travel_id) {
                showDialog = false
                if(actionDone){
                    cleanStack(navCont, Destinations.TRAVEL + "/${travel_id}")
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))



        //------------------------------- ITINERARY -------------------------------
        Text(
            text = "Itinerary",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(5.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if(travelItinerary==null || travelItinerary.isEmpty()){
                Text(
                    text = "no itineraries specified",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .height(100.dp)
                        .padding(16.dp),
                )
            } else {
                for (itinerary in travelItinerary.sortedBy {
                    it.startDate
                }) {
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(150.dp)
                            .wrapContentHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = itinerary.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = itinerary.places[0],
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.weight(1f)
                                )

                                Box(
                                    modifier = Modifier
                                        .requiredSize(22.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            actions.seeTravelItinerary(vm.travelIdValue.value, itinerary.itineraryId)
                                        }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.OpenInFull,
                                        contentDescription = "Expand",
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.fillMaxSize().padding(3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))

        //------------------------------- TRAVEL REVIEWS -------------------------------
        if(travelReviews.isNotEmpty()){
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text="Travel reviews", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.width(30.dp))
                val decimalFormat = DecimalFormat("0.00")
                val rating = decimalFormat.format(vm.travelRatingValue.value ?: 0.00)
                Text(
                    text = rating,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier=Modifier.padding(top=1.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Rating Star",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp).padding(top=1.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Row (
                Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp)
            ){
                travelReviews.map {
                    UserReview(it.author, timeAgo(it.date), it.travelReviewText!!, it.rating, navCont, it.reviewImages)
                    Spacer(Modifier.width(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        //------------------------------- OTHER COMPANIONS -------------------------------
        Text(
            text = "Other Companions",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(5.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if(travelCompanions==null || travelCompanions.isEmpty()){
                Text(
                    text = "no companions yet",
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                for (companion in travelCompanions) {
                    val comp = companion.user
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .width(45.dp)
                            .clickable {
                                actions.seeProfile(comp.userId)
                            }
                    ) {
                        UserImage(comp.profilePic, size = 40.dp, comp.name, comp.surname)
                        if(companion.extras>0){
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(18.dp)
                                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+"+companion.extras,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}



