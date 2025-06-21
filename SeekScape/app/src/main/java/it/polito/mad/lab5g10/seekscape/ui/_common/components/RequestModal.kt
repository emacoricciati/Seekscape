package it.polito.mad.lab5g10.seekscape.ui._common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.polito.mad.lab5g10.seekscape.firebase.TheRequestModel
import it.polito.mad.lab5g10.seekscape.models.OwnedTravelViewModel
import it.polito.mad.lab5g10.seekscape.models.Request
import it.polito.mad.lab5g10.seekscape.models.RequestViewModel
import it.polito.mad.lab5g10.seekscape.models.Review
import it.polito.mad.lab5g10.seekscape.models.User
import kotlinx.coroutines.launch
import okhttp3.internal.wait

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestModal(req: Request, vm: RequestViewModel, ownedTravelViewModel: OwnedTravelViewModel, closeModal: ()-> Unit, openState: Boolean, confirmState: Boolean) {
    var openTextBox by remember { mutableStateOf(openState) }
    var confirm by remember { mutableStateOf(confirmState) }
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )

    val onDismiss = {
        scope.launch {
            sheetState.hide()
        }
        closeModal()
    }

    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(0.7f),
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .heightIn(max = 500.dp)
                .padding(start=16.dp, end=16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth()){
                        Text(req.trip.title!!,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    UserInfo(req.author, req.spots)
                }

                IconButton(
                    onClick = closeModal,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close modal",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier =  Modifier.fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp, bottom = 20.dp)
            )
            if(!openTextBox){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start=10.dp, end=10.dp)
                        .verticalScroll(scrollState)
                ){
                    Text(req.reqMessage, style = MaterialTheme.typography.bodyMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top=20.dp, end = 30.dp),
                        horizontalArrangement = Arrangement.End
                    ){
                        ButtonsSection({ openTextBox = true; confirm = true}, {openTextBox = true; confirm = false})
                    }
                }

            } else {
                EditableTextBox(req.id, vm, ownedTravelViewModel, confirm, onDismiss)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowResponseModal(request:Request, user: User, travelId: String, showModal: ()-> Unit) {
    val scope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )

    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(0.7f),
        sheetState = sheetState,
        onDismissRequest = {
            scope.launch {
                sheetState.hide()
            }
            showModal()
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 30.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = showModal,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close modal",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column() {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        request.trip.title!!,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                UserInfo(user, request.spots)
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 5.dp, end = 5.dp, bottom = 20.dp)
                )
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    request.responseMessage?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

    }
}

@Composable
fun UserInfo(author: User, spotReq: Int){
    val avgRating = if(author.reviews != null){
        author.reviews
            ?.map { it.rating }
            ?.takeIf { it.isNotEmpty() }
            ?.average() ?: 0.0
    } else{
        0.0
    }

    Row(
        modifier = Modifier.padding(bottom = 5.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        UserImage(author.profilePic, 70.dp, author.name, author.surname)
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 5.dp, top = 5.dp)
        ) {
            Text(
                text=author.name+ " " + author.surname,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text=String.format("%.1f", avgRating),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Rounded.Star, contentDescription = "Rating", modifier = Modifier.size(20.dp))
            }
            Text(
                text="Spot requested: $spotReq",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun EditableTextBox(requestId: String, vm: RequestViewModel, ownedTravelViewModel: OwnedTravelViewModel, confirm: Boolean, closeModal: ()-> Unit,){          //da sistemare che il testo che gli dovrai passare fa parte della request e sarebbe il denial message
    var text by remember { mutableStateOf("") }
    var request = vm.getRequestObject(requestId)
    var placeHolder: String
    val theRequestModel = TheRequestModel()
    val scope = rememberCoroutineScope()
    if(confirm)
        placeHolder = "I accepted your request..."
    else
        placeHolder = "I declined your request because..."

    ElevatedCard (
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .shadow(8.dp, shape = RoundedCornerShape(20.dp))
            .padding(start = 5.dp, end = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        TextField(
            value = text,
            onValueChange = { text = it},
            placeholder = {
                Text(placeHolder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
              },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, end = 30.dp),
        horizontalArrangement = Arrangement.Center
    ){
        if(confirm){
            AcceptButton {
                request!!.responseMessage=text
                scope.launch{
                    val requestsIds = theRequestModel.manageRequest(request, true)
                    vm.removeReqFromList(requestsIds)
                    closeModal()
                }
            }
        } else {
            DeclineButton {
                request!!.responseMessage=text
                scope.launch{
                    val requestsIds = theRequestModel.manageRequest(request, false)
                    vm.removeReqFromList(requestsIds)
                    closeModal()
                }
            }
        }
    }
}
