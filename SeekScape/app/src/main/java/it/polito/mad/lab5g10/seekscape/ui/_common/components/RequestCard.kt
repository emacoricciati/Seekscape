package it.polito.mad.lab5g10.seekscape.ui._common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import it.polito.mad.lab5g10.seekscape.models.OwnedTravelViewModel
import it.polito.mad.lab5g10.seekscape.models.ProfilePic
import it.polito.mad.lab5g10.seekscape.models.RequestViewModel
import it.polito.mad.lab5g10.seekscape.models.TravelCompanion
import it.polito.mad.lab5g10.seekscape.models.TravelImage

@Composable
fun RequestCard(index: String, vm: RequestViewModel, ownedTravelViewModel: OwnedTravelViewModel, showModal: ()->Unit, openTextBox: ()->Unit, notOpenTextBox: ()->Unit, confirmMode: ()->Unit, denyMode: ()->Unit){
    val author by vm.getRequest(index).authorValue.collectAsState()
    val trip by vm.getRequest(index).tripValue.collectAsState()
    val reqMess by vm.getRequest(index).reqMessageValue.collectAsState()
    val spots by vm.getRequest(index).spots.collectAsState()

    ElevatedCard(
        modifier = Modifier
            .fillMaxSize()
            .shadow(8.dp, shape = RoundedCornerShape(20.dp))
            .padding(start = 5.dp, end = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            TravelSmallImage(trip.travelImages!![0], author.profilePic, author.name, author.surname)
            TextTravel(trip.title!!, reqMess, spots, showModal, notOpenTextBox)
            ButtonsSection({ showModal();  openTextBox(); confirmMode()}, { showModal(); openTextBox(); denyMode(); })
        }
    }
}

@Composable
fun TravelSmallImage(tripImage: TravelImage, userImage: ProfilePic?, name: String, surname: String){
    Box(
        modifier = Modifier.size(90.dp)
            .padding(start = 5.dp, top = 5.dp)
    ) {
        val painter = when (tripImage) {
            is TravelImage.Url -> rememberAsyncImagePainter(model = tripImage.value)
            is TravelImage.Resource -> painterResource(id = tripImage.resId)
        }

        Image(
            painter = painter,
            contentDescription = "Landscape",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
        )
        Column(modifier = Modifier.align(Alignment.BottomEnd)){
            UserImage(userImage, 40.dp, name, surname)
        }
    }
}

@Composable
fun TextTravel(title: String, message: String, numSpot: Int, showModal: ()->Unit, closeTextBox: ()->Unit) {
    Column(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            .width(200.dp)
    ) {
        Text(title,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text("Spot requested: $numSpot",
            style = MaterialTheme.typography.bodyLarge)
        Text(message,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        TextButton(
            onClick = {showModal(); closeTextBox()},
            modifier = Modifier.height(35.dp)
        ){
            Text(text="Read more", style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.onBackground)
        }
    }
}
