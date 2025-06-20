package it.polito.mad.lab5g10.seekscape.ui.profile


import android.content.Context
import android.content.Intent
import android.provider.Settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.ChangeCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.GroupAdd
import androidx.compose.material.icons.outlined.Reviews
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.firebase.TheUserModel
import it.polito.mad.lab5g10.seekscape.models.UserNotificationSettings
import it.polito.mad.lab5g10.seekscape.ui._common.NotificationItemView
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import it.polito.mad.lab5g10.seekscape.ui.navigation.navigateToNotificationAction
import kotlinx.coroutines.launch

fun Context.openAppNotificationSettings() {
    val intent =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
    startActivity(intent)
}


@Composable
fun NotificationScreenView(navCont: NavHostController) {
    val actions = remember(navCont) { Actions(navCont) }
    val isLoggedIn by AppState.isLogged.collectAsState()
    val myProfile = AppState.myProfile.collectAsState().value
    val notificationSettings: UserNotificationSettings = myProfile.notificationSettings?: UserNotificationSettings()
    val notifications = myProfile.notifications
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val theUserModel = TheUserModel()
    if (!isLoggedIn) {
        actions.navigateTo("login")
    }

    LaunchedEffect(Unit){
        if(isLoggedIn){
            val p = CommonModel.getUser(myProfile.userId)
            if(p!=null) AppState.updateMyProfile(p)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Manage your notifications",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(6.dp))

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            //SYSTEM
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        context.openAppNotificationSettings()
                    }
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "System",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Manage notifications on your device",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())


            // NOTIFICATION APPLY ( NOT_APPLY )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.GroupAdd,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Notify for apply",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Someone applies on your travel",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    modifier = Modifier.size(18.dp).padding(end=24.dp),
                    checked = notificationSettings.apply,
                    onCheckedChange = {
                        scope.launch {
                            val newNotificationSettings = notificationSettings.copy(apply=it)
                            theUserModel.updateNotificationSettings(myProfile.userId, newNotificationSettings)
                            AppState.updateMyProfileNotSettings(newNotificationSettings)
                        }
                    }
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())


            // NOTIFICATION REQUEST MANAGED ( NOT_REQ_DEN && NOT_REQ_ACC )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChangeCircle,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Notify for accept/decline",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Someone answered your apply",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    modifier = Modifier.size(18.dp).padding(end=24.dp),
                    checked = notificationSettings.applyAnswer,
                    onCheckedChange = {
                        scope.launch {
                            val newNotificationSettings = notificationSettings.copy(applyAnswer=it)
                            theUserModel.updateNotificationSettings(myProfile.userId, newNotificationSettings)
                            AppState.updateMyProfileNotSettings(newNotificationSettings)
                        }
                    }
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())


            // NOTIFICATION MESSAGE ( NOT_MSG )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Notify for messages",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "New messages in a travel chat",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    modifier = Modifier.size(18.dp).padding(end=24.dp),
                    checked = notificationSettings.msg,
                    onCheckedChange = {
                        scope.launch {
                            val newNotificationSettings = notificationSettings.copy(msg=it)
                            theUserModel.updateNotificationSettings(myProfile.userId, newNotificationSettings)
                            AppState.updateMyProfileNotSettings(newNotificationSettings)
                        }
                    }
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())


            // NOTIFICATION REVIEWS ( NOT_MY_PROFILE_REV && NOT_MY_TRAVEL_REV )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Reviews,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Notify for reviews",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "New review (on you or travel)",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    modifier = Modifier.size(18.dp).padding(end=24.dp),
                    checked = notificationSettings.review,
                    onCheckedChange = {
                        scope.launch {
                            val newNotificationSettings = notificationSettings.copy(review=it)
                            theUserModel.updateNotificationSettings(myProfile.userId, newNotificationSettings)
                            AppState.updateMyProfileNotSettings(newNotificationSettings)
                        }
                    }
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.outline, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Timelapse,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Notify last minute",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = "Last minute travels opportunities",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    modifier = Modifier.size(18.dp).padding(end=24.dp),
                    checked = notificationSettings.lastMinute,
                    onCheckedChange = {
                        scope.launch {
                            val newNotificationSettings = notificationSettings.copy(lastMinute=it)
                            theUserModel.updateNotificationSettings(myProfile.userId, newNotificationSettings)
                            AppState.updateMyProfileNotSettings(newNotificationSettings)
                        }
                    }
                )
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }

        Box(modifier = Modifier.weight(1f)){
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(16.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 1f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            ) {
                if(notifications.isEmpty()){
                    Box(
                        modifier = Modifier.fillMaxWidth().height(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("You have no notifications")
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .wrapContentWidth()
                        .fillMaxHeight()
                        .padding(vertical = 4.dp, horizontal = 6.dp),
                ) {
                    itemsIndexed(notifications) { index, notification ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            NotificationItemView(
                                notification = notification,
                                onClick = {
                                    navigateToNotificationAction(notification)
                                },
                                canDelete = true
                            )
                        }
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
}