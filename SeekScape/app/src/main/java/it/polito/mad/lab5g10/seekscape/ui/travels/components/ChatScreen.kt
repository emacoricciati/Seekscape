package it.polito.mad.lab5g10.seekscape.ui.travels.components

import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import it.polito.mad.lab5g10.seekscape.firebase.CommonModel
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.CREATOR_TRAVEL_MODE
import it.polito.mad.lab5g10.seekscape.models.ChatMessage
import it.polito.mad.lab5g10.seekscape.models.ChatMessageViewModel
import it.polito.mad.lab5g10.seekscape.models.PAST
import it.polito.mad.lab5g10.seekscape.models.TO_REVIEW
import it.polito.mad.lab5g10.seekscape.models.chatHourFormat
import it.polito.mad.lab5g10.seekscape.ui._common.components.IconDateRange
import it.polito.mad.lab5g10.seekscape.ui._common.components.IconLocation
import it.polito.mad.lab5g10.seekscape.ui._common.components.UserImage
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions


@Composable
fun TravelChatScreen(vm: ChatMessageViewModel, navController: NavHostController) {
    val messages by vm.messages.collectAsState()
    val travel by vm.travel.collectAsState()
    val myProfile by AppState.myProfile.collectAsState()
    val isChatLoaded by vm.isChatLoaded.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 55.dp, end=30.dp, top=10.dp)
        ) {
            if (travel != null) {
                Text(
                    text = travel!!.title ?: "Untitled Travel",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp, top = 4.dp, end = 3.dp)
                        ) {
                            IconLocation(travel!!.country ?: "Unknown Location")
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp, top = 4.dp)
                        ) {
                            IconDateRange(travel!!.startDate!!, travel!!.endDate!!)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(5.dp))

        if(isChatLoaded && messages.isEmpty()){
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
                    .padding(vertical = 25.dp, horizontal = 10.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Still no messages",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        } else if (isChatLoaded){
            val listState = rememberLazyListState()
            var hasTriggeredLoad by remember { mutableStateOf(false) }
            LaunchedEffect(listState, messages.size) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                    .collect { visibleItems ->
                        val secondToLastIndex = messages.size - 2
                        val isSecondToLastVisible =
                            visibleItems.any { it.index == secondToLastIndex }

                        if (isSecondToLastVisible && !hasTriggeredLoad) {
                            hasTriggeredLoad = true
                            vm.loadPreviousMessages()
                        }
                    }
            }
            LazyColumn(modifier = Modifier.weight(1f), state=listState) {

                itemsIndexed(messages) { index, msg ->
                    val currentDate = msg.date.toLocalDate()
                    val previousDate = messages.getOrNull(index - 1)?.date?.toLocalDate()

                    if (index == 0 || currentDate != previousDate) {
                        NewDayBox(currentDate)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    MessageBox(msg, navController)
                    Spacer(modifier = Modifier.height(4.dp))
                }

            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        InputMessage(vm)
    }

    if(travel!=null && isChatLoaded){

        if(myProfile.userId==travel!!.creator.userId){
            AppState.updateMyTravelTab("My trips")
            AppState.updateMyTravelMode(CREATOR_TRAVEL_MODE)

        } else if(LocalDate.now().isAfter(travel!!.endDate)){
            AppState.updateMyTravelTab("Past")
        }

        var possibleNotificationId = "msg_${travel!!.travelId}"

        LaunchedEffect(possibleNotificationId, myProfile.notifications) {
            scope.launch {
                if (possibleNotificationId.isNotEmpty() && myProfile.notifications.any { it.id == possibleNotificationId }) {
                    CommonModel.removeNotificationById(myProfile.userId, possibleNotificationId)
                }
            }
        }
    }
}


@Composable
fun NewDayBox(date: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d")
    val formattedDate = date.format(formatter)
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 1.dp)
        )
    }
}

@Composable
fun MessageBox(msg: ChatMessage, navCont: NavHostController) {
    val actions = remember(navCont){Actions(navCont)}
    val myProfile by AppState.myProfile.collectAsState()
    val isCurrentUser = msg.author.userId == myProfile.userId
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isCurrentUser) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .clickable {
                            if(msg.author.userId!="system")
                                actions.seeProfile(msg.author.userId)
                        }
                ) {
                    UserImage(
                        msg.author.profilePic,
                        size = 30.dp,
                        msg.author.name,
                        msg.author.surname
                    )
                }
            }
            Spacer(modifier = Modifier.width(5.dp))
        } else {
            Spacer(modifier = Modifier.width(30.dp))
        }

        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .wrapContentWidth()
                .wrapContentHeight(),
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
        ) {
            Text(
                text = msg.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = msg.date.format(chatHourFormat),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        if (isCurrentUser) {
            Spacer(modifier = Modifier.width(5.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .clickable {
                            actions.seeProfile(msg.author.userId)
                        }
                        .padding(top=5.dp)
                ) {
                    UserImage(
                        msg.author.profilePic,
                        size = 30.dp,
                        msg.author.name,
                        msg.author.surname
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.width(30.dp))
        }
    }
}


@Composable
fun InputMessage(vm: ChatMessageViewModel) {
    val travel by vm.travel.collectAsState()
    val pause by vm.pause.collectAsState()
    var text by remember{ mutableStateOf("") }
    val isEnabled = !pause && travel!=null && travel!!.status!=PAST && travel!!.status!=TO_REVIEW

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Type a message...") },
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp),
            maxLines = 4,
            singleLine = false,
            enabled = isEnabled
        )
        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    vm.sendMessage(text)
                    text = ""
                }
            },
            enabled = isEnabled && text.isNotBlank(),
            modifier = Modifier
                .padding(start = 2.dp, end=0.dp)
                .size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send Message",
                tint= if(isEnabled && text.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }
    }
}