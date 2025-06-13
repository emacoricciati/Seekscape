package it.polito.mad.lab5g10.seekscape.ui._common.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import it.polito.mad.lab5g10.seekscape.dayMonthFormat
import it.polito.mad.lab5g10.seekscape.firebase.TheRequestModel
import it.polito.mad.lab5g10.seekscape.formatDateTravel
import it.polito.mad.lab5g10.seekscape.models.AVAILABLE
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.DELETED
import it.polito.mad.lab5g10.seekscape.models.DENIED
import it.polito.mad.lab5g10.seekscape.models.FULL
import it.polito.mad.lab5g10.seekscape.models.JOINED
import it.polito.mad.lab5g10.seekscape.models.OWNED
import it.polito.mad.lab5g10.seekscape.models.PAST
import it.polito.mad.lab5g10.seekscape.models.PENDING
import it.polito.mad.lab5g10.seekscape.models.TO_REVIEW
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.models.TravelCompanion
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.models.TravelViewModel
import it.polito.mad.lab5g10.seekscape.models.User
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import java.time.LocalDate




@Composable
fun TravelCard(travel: Travel, onCardClick: () -> Unit, textAbove: String? = null, navCont: NavHostController) {
    Log.d("TRAVEL ID", travel.travelId.toString())
    val actions = remember(navCont){Actions(navCont)}
    val context = LocalContext.current
    ElevatedCard(
        modifier = Modifier
            .fillMaxSize()
            .shadow(8.dp, shape = RoundedCornerShape(20.dp)),
        onClick = { onCardClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(12.dp)
        ) {
            if (textAbove!=null){
                Text(
                    text = textAbove,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(bottom = 3.dp, start=2.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            travel.travelImages?.firstOrNull()?.let { image ->
                val painter = when (image) {
                    is TravelImage.Resource -> painterResource(id = image.resId)
                    is TravelImage.Url -> { rememberAsyncImagePainter(model = image.value) }
                }

                Image(
                    painter = painter,
                    contentDescription = "Travel Image for ${travel.title}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = travel.title ?: "Untitled Travel",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 7.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp, end = 3.dp)) {
                        IconLocation(travel.country ?: "Unknown Location")
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)) {
                        IconPeopleJoined(travel.travelCompanions!!, travel.maxPeople!!)
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp, end = 3.dp)) {
                        IconTravelType(travel)
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier
                        .weight(1f),
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)) {
                        IconDateRange(travel.startDate!!, travel.endDate!!)
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)) {
                        IconCost(travel.priceMin!!, travel.priceMax!!)
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        if(AppState.myProfile.collectAsState().value.userId==travel.creator.userId) {
                            UserStarsAndNickname(AppState.myProfile.collectAsState().value, context, 36.dp, false, {actions.seeProfile(travel.creator.userId)})
                        }else{
                            UserStarsAndNickname(travel.creator, context, 36.dp, true, {actions.seeProfile(travel.creator.userId)})
                        }
                    }
                }
            }
        }
    }
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TravelButton(vm: TravelViewModel, onButtonClick: () -> Unit, navController: NavHostController) {
    var text: String? = null;
    var enabled = true;

    val currentTab by AppState.currentTab.collectAsState()
    val isLoggedIn by AppState.isLogged.collectAsState()
    val status by vm.statusValue.collectAsState()
    val currentTravelState by vm.statusForUser.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val actions = remember(navController) { Actions(navController) }


    val mapActions: Map<String, ()->Unit > = mapOf(
        OWNED to {
            if(status!=PAST){
                if(currentTab!=MainDestinations.TRAVELS_ROUTE){
                    AppState.updateCurrentTab(MainDestinations.TRAVELS_ROUTE)
                }
                actions.editTravel(vm.travelIdValue.value)
            }
        },
        PENDING to {
            val theRequestModel = TheRequestModel()
            coroutineScope.launch{
                theRequestModel.deleteRequest(vm.travelIdValue.value)
            }
            actions.navigateBack()
           },
        JOINED to {
            val theRequestModel = TheRequestModel()
            coroutineScope.launch{
                theRequestModel.deleteRequest(vm.travelIdValue.value)
                theRequestModel.leaveTrip(vm.travelIdValue.value)
            }
            actions.navigateBack()
          },
        DENIED to { println("Rejected") },
        AVAILABLE to {
            if(currentTab!=MainDestinations.HOME_ROUTE){
                AppState.updateCurrentTab(MainDestinations.HOME_ROUTE)
            }
            actions.applyToJoin(vm.travelIdValue.value)
          },
        TO_REVIEW to {
            if(currentTab!=MainDestinations.TRAVELS_ROUTE){
                AppState.updateCurrentTab(MainDestinations.TRAVELS_ROUTE)
            }
            actions.reviewTravel(vm.travelIdValue.value)
          },
        FULL to { println("Fully Booked") },
        PAST to { println("Past travel") },
        DELETED to { println("Deleted travel") }
    )
    var newStatus = status
    val currentUser = AppState.myProfile.collectAsState().value
    if (currentUser.userId == vm.creatorValue.value.userId){
        newStatus = OWNED
    }
    else if (currentUser.userId in vm.travelCompanionsValues.value.map { it.user.userId }){
        newStatus = JOINED
    }
    println(newStatus)
    if(currentTravelState!="") {
        when (currentTravelState) {
            OWNED -> {
                if(status!=PAST){
                    text = "Edit";
                    enabled = true;
                } else {
                    text = "Past travel";
                    enabled = false;
                }
            }
            PENDING -> {
                text = "Pending Approval - tap to cancel";
                enabled = true;
            }
            JOINED -> {
                text = "Leave Trip";
                enabled = true;
            }
            DENIED -> {
                text = "Rejected";
                enabled = false;
            }
            TO_REVIEW -> {
                Log.d("Index che fa danno", vm.travelIdValue.value.toString())
                text = "Rate your experience";
                enabled = true;
            }

            else -> {}
        }
    } else {
        when (status) {
            AVAILABLE -> {
                text = "Apply To Join";
                enabled = true;
            }
            FULL -> {
                text = "Fully Booked";
                enabled = false;
            }
            PAST -> {
                text = "Past travel";
                enabled = false;
            }
            DELETED -> {
                text = "Deleted travel";
                enabled = false;
            }
            else -> {}
        }
    }

    if(!isLoggedIn)
        text = "Sign in to apply to join"

    if(text==null)
        return;

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background.copy(alpha = 1.0f)
                        )
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .graphicsLayer {
                        alpha = 0.9f
                    }
            ) {
                Button(
                    onClick = {
                        if(isLoggedIn){
                            coroutineScope.launch {
                                mapActions[if (currentTravelState == "") newStatus else currentTravelState]?.invoke()
                            }
                        } else {
                            AppState.updateCurrentTab(MainDestinations.PROFILE_ROUTE)
                        }
                    },
                    enabled = enabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
fun IconLocation(place: String) {
    Icon(
        imageVector = Icons.Filled.Place,
        contentDescription = "Location",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp),
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = place,
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun IconDateRange(startDAte: LocalDate, endDate: LocalDate) {
    Icon(
        imageVector = Icons.Filled.DateRange,
        contentDescription = "Date Range",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = formatDateTravel(startDAte, endDate),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun IconDate(date: LocalDate) {
    Icon(
        imageVector = Icons.Filled.DateRange,
        contentDescription = "Date",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = dayMonthFormat(date),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyLarge
    )
}


@Composable
fun IconPeopleJoined(companions: List<TravelCompanion>, maxPeople: Int) {
    var numJoined=0
    companions.forEach { c -> numJoined = numJoined +1+c.extras }
    Icon(
        imageVector = Icons.Filled.Person,
        contentDescription = "People",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp),
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = numJoined.toString() + "/" + maxPeople.toString(),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun IconMoney(icon: ImageVector = Icons.Filled.Euro) {
    Icon(
        imageVector = icon,
        contentDescription = "Cost",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp),
    )
}


@Composable
fun IconCost(priceMin: Int, priceMax: Int, icon: ImageVector = Icons.Filled.Euro) {
    IconMoney(icon)
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = priceMin.toString() + " - " + priceMax.toString(),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun IconTravelType(travel: Travel) {
    Icon(
        imageVector = Icons.Filled.Flight,
        contentDescription = "Travel type",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(18.dp),
    )
    Spacer(modifier = Modifier.width(4.dp))
    Text(
        text = travel.travelTypes!!.joinToString(", "),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyLarge
    )
}


@Composable
fun UserStarsAndNickname(user: User, context: Context, circleIconSize: Dp, canNavigate: Boolean = true, navigate: ()->Unit){
    if(canNavigate){
        Box(
            modifier = Modifier
                .size(circleIconSize)
                .clickable {
                    navigate()
                }
        ) {
            UserImage(user.profilePic, size = circleIconSize, user.name, user.surname)
        }
    } else {
        Box(modifier = Modifier.size(circleIconSize)) {
            UserImage(user.profilePic, size = circleIconSize, user.name, user.surname)
        }
    }

    Spacer(modifier = Modifier.width(4.dp))
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = user.nickname,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val decimalFormat = DecimalFormat("0.00")
            val rating = decimalFormat.format(user.reviews
                ?.map { it.rating }
                ?.takeIf { it.isNotEmpty() }
                ?.average() ?: 0.0)
            Text(
                text = rating,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating Star",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}