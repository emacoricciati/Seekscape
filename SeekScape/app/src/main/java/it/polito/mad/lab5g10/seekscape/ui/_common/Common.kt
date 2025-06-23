package it.polito.mad.lab5g10.seekscape.ui._common

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Reviews
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import it.polito.mad.lab5g10.seekscape.MainActivity
import it.polito.mad.lab5g10.seekscape.R
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.NOT_ACCOUNT
import it.polito.mad.lab5g10.seekscape.models.NOT_APPLY
import it.polito.mad.lab5g10.seekscape.models.NOT_LAST_MINUTE_JOIN
import it.polito.mad.lab5g10.seekscape.models.NOT_MSG
import it.polito.mad.lab5g10.seekscape.models.NOT_MY_PROFILE_REV
import it.polito.mad.lab5g10.seekscape.models.NOT_MY_TRAVEL_REV
import it.polito.mad.lab5g10.seekscape.models.NOT_REMINDER
import it.polito.mad.lab5g10.seekscape.models.NOT_REQ_ACC
import it.polito.mad.lab5g10.seekscape.models.NOT_REQ_DEN
import it.polito.mad.lab5g10.seekscape.models.NotificationItem
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.ui._common.components.UserImage
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import it.polito.mad.lab5g10.seekscape.ui.navigation.MainDestinations
import it.polito.mad.lab5g10.seekscape.ui.navigation.navigateToNotificationAction
import kotlinx.coroutines.launch
import java.io.Serializable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@Composable
fun SquareImage(resource: Int, size: Dp = 109.dp, hasBorder: Boolean = false, modifier: Modifier=Modifier) {
    if (hasBorder) {
        Image(
            painter = painterResource(id = resource),
            contentDescription = "img",
            modifier = modifier
                .size(size)
        )
    } else {
        val borderWidth: Dp = (size.value * 0.08).dp

        Image(
            painter = painterResource(id = resource),
            contentDescription = "img",
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .border(
                    border = BorderStroke(borderWidth, MaterialTheme.colorScheme.surface),
                    shape = CircleShape
                )
        )
    }
}


fun navigateBack(context: Context, previousActivity: Class<*>, params: HashMap<String, Serializable> = HashMap()) {
    val intent = Intent(context, previousActivity)
    for (entry in params.entries) {
        intent.putExtra(entry.key, entry.value)
    }
    context.startActivity(intent)
}

@Composable
fun ArrowBackIcon(clickFunc: () -> Unit) {
    IconButton(
        onClick = { clickFunc() },
        modifier = Modifier
            .requiredSize(45.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = CircleShape
            )
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.Companion.fillMaxSize()
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calendar(
    dateType: String,
    selectedDate: LocalDate?,
    minDate: LocalDate?,
    modifier: Modifier = Modifier,
    noLabel : Boolean = false,
    errorMessage: String = "",
    onDateSelected: (LocalDate) -> Unit
) {
    val today = remember { LocalDate.now() }
    val todayMillis = today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            ?: todayMillis,
        initialDisplayedMonthMillis = todayMillis
    )

    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var labelText = ""
    if (!noLabel){
        labelText = if (dateType == "start") "From" else "To"
    }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val formattedDate = selectedDate?.format(dateFormatter) ?: ""

    OutlinedTextField(
        value = formattedDate,
        onValueChange = {},
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 0.dp)
            .clickable { showDialog = true },
        label = { Text(
            text = labelText,
            color = MaterialTheme.colorScheme.primary
        ) },
        isError = errorMessage.isNotEmpty(),
        supportingText = {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                )
            }
        },
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "Pick date",
                    tint =  MaterialTheme.colorScheme.primary
                )
            }
        }
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selected = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

                        if (dateType == "start" && !selected.isBefore(today)) {
                            onDateSelected(selected)
                            showDialog = false
                        } else if (dateType == "end" && !selected.isBefore(minDate ?: today)) {
                            onDateSelected(selected)
                            showDialog = false
                        } else if (dateType == "start" || dateType == "end") {
                            // Show toast
                            Toast.makeText(
                                context,
                                if (dateType == "start") "The selected start date cannot be in the past."
                                else "The selected end date cannot be before the start date.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else {
                            onDateSelected(selected)
                            showDialog = false
                        }
                    }
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}



@Composable
fun ErrorScreen() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BUG",
                modifier = Modifier.padding(bottom = 30.dp),
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Button(
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                modifier = Modifier.padding(bottom = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text="back home",
                    style = MaterialTheme.typography.titleMedium
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String, currentNavController: NavHostController, canGoBack: Boolean = false) {
    val currentRoute = currentNavController.currentBackStackEntry?.destination?.route
    val isLogged by AppState.isLogged.collectAsState()
    val user = AppState.myProfile.collectAsState().value
    val isDarkMode = AppState.isDarkMode.collectAsState().value
    val actions = remember(currentNavController) { Actions(currentNavController) }



    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        navigationIcon = {
            if (canGoBack) {
                Box() {
                    ArrowBackIcon(clickFunc = { actions.navigateBack() })
                }
            } else {

                Image(
                    painter = if(isDarkMode==false) {painterResource(id = R.drawable.icon_logo)}
                    else{painterResource(id = R.drawable.icon_logo_dark_mode) },
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(
                            width = 45.dp,
                            height = 45.dp
                        )
                        .padding(8.dp)
                )
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .padding(horizontal = 10.dp)
                    .clickable {
                        if(isLogged){
                            if(currentRoute != "profile")
                                currentNavController.navigate("profile/${user.userId}")
                        }else {
                            AppState.updateCurrentTab(MainDestinations.PROFILE_ROUTE)
                        }
                    }
            ){
                if (currentRoute != null) {
                    if (isLogged && AppState.doneFirstFetch.collectAsState().value) {
                        if(!currentRoute.contains("profile")){
                            UserImage(user.profilePic, 40.dp, user.name, user.surname)
                        }
                        else if(currentRoute != "profile/edit")
                            NotificationBell(notifications = user.notifications, currentNavController)
                    }
                    else {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(46.dp)
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun NotificationBell(notifications: List<NotificationItem>, navCont: NavHostController) {
    var showPopup by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
            .padding(horizontal = 10.dp)
            .clickable { showPopup = notifications.isNotEmpty() }
    ) {
        if (notifications.isNotEmpty()) {
            BadgedBox(
                badge = {
                    Badge {
                        Text(text = notifications.size.toString())
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "${notifications.size} Notifications",
                    modifier = Modifier.size(36.dp)
                )
            }
        } else {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "No Notifications",
                modifier = Modifier.size(36.dp)
            )
        }

        if (showPopup) {
            NotificationPopup(
                notifications = notifications,
                onDismiss = { showPopup = false },
                navCont
            )
        }
    }
}

@Composable
fun NotificationPopup(
    notifications: List<NotificationItem>,
    onDismiss: () -> Unit,
    navCont: NavHostController
) {
    val context = LocalContext.current
    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismiss,
    ) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp
        Surface(
            modifier = Modifier
                .width(screenWidth * 0.7f)
                .wrapContentHeight()
                .padding(10.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 1f),
            contentColor = MaterialTheme.colorScheme.onSurface,
        ) {
            LazyColumn(
                modifier = Modifier
                    .wrapContentWidth()
                    .heightIn(max = screenHeight / 3)
                    .padding(vertical = 4.dp, horizontal = 6.dp),
            ) {
                itemsIndexed(notifications) { index, notification ->
                    NotificationItemView(
                        notification = notification,
                        onClick = {
                            navigateToNotificationAction(notification)
                            onDismiss()
                        },
                    )
                    if (index < notifications.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 3.dp, end = 3.dp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItemView(
    notification: NotificationItem,
    onClick: () -> Unit,
    canDelete:Boolean=false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        when (notification.type) {
            NOT_ACCOUNT -> {
                Icon(
                    imageVector = Icons.Outlined.ManageAccounts,
                    contentDescription = "Manage Account",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            NOT_MSG -> {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Message",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            NOT_REQ_ACC -> {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Request accepted",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            NOT_REQ_DEN -> {
                Icon(
                    imageVector = Icons.Outlined.RemoveCircleOutline,
                    contentDescription = "Request denied",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            NOT_APPLY -> {
                Icon(
                    imageVector = Icons.Outlined.GroupAdd,
                    contentDescription = "Apply request",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            NOT_REMINDER -> {
                Icon(
                    imageVector = Icons.Outlined.Event,
                    contentDescription = "Reminder",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            NOT_LAST_MINUTE_JOIN -> {
                Icon(
                    imageVector = Icons.Outlined.Timelapse,
                    contentDescription = "Reminder",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            NOT_MY_PROFILE_REV -> {
                Icon(
                    imageVector = Icons.Outlined.Reviews,
                    contentDescription = "Reviews",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            NOT_MY_TRAVEL_REV -> {
                Icon(
                    imageVector = Icons.Outlined.Reviews,
                    contentDescription = "Reviews",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notification",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
        Column (modifier = Modifier.weight(1f)){
            notification.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            notification.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if(canDelete){
            val myProfile = AppState.myProfile.collectAsState().value
            val scope = rememberCoroutineScope()
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .clickable {
                        scope.launch {
                            CommonModel.removeNotificationById(
                                myProfile.userId,
                                notification.id
                            )
                        }
                    }
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Cancel notification",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}


//Used to visualize the image of a travel in full screen
@Composable
fun FullscreenImageViewer(
    images: List<TravelImage>,
    startIndex: Int = 0,
) {
    var currentIndex by remember { mutableStateOf(startIndex) }

    val imagePainter = when (val image = images[currentIndex]) {
        is TravelImage.Resource -> painterResource(id = image.resId)
        is TravelImage.Url -> rememberAsyncImagePainter(image.value)
    }

    Column(modifier = Modifier.padding(horizontal = 5.dp)) {
        Row(
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "Full Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = { currentIndex-- },
                modifier = Modifier
                    .padding(16.dp),
                enabled = currentIndex > 0
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBackIos,
                    contentDescription = "Previous",
                    tint = if (currentIndex > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.3f
                    )
                )
            }

            IconButton(
                onClick = { currentIndex++ },
                modifier = Modifier
                    .padding(16.dp),
                enabled = currentIndex < images.size - 1
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = "Next",
                    tint = if (currentIndex < images.size - 1) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.3f
                    )
                )
            }

        }
    }
}