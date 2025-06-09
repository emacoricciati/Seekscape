package it.polito.mad.lab5g10.seekscape.ui._common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import it.polito.mad.lab5g10.seekscape.models.ProfilePic
import it.polito.mad.lab5g10.seekscape.models.TravelImage
import it.polito.mad.lab5g10.seekscape.models.User
import it.polito.mad.lab5g10.seekscape.ui._common.FullscreenImageViewer
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions

//import it.polito.mad.lab5g10.seekscape.ui.travels.ItineraryScreen


@Composable
fun UserDetail(
    name: String,
    surname: String,
    icon: ProfilePic?,
    time: String,
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Column {
            UserImage(icon, 50.dp, name, surname, modifier = Modifier.clickable(onClick = onImageClick))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(text = "$name $surname", style = MaterialTheme.typography.titleMedium)
            Text(text = time, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun UserReview(
    user: User,
    time: String,
    text: String,
    rating:Double?= null,
    navCont: NavHostController,
    images: List<TravelImage>? = null
){
    var showDialog by remember { mutableStateOf(false) }
    var fullscreenIndex by remember { mutableStateOf<Int?>(null) }
    val actions = remember(navCont){ Actions(navCont) }

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier
            .size(width = 280.dp, height = 220.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Row (
                modifier = Modifier
                    .fillMaxSize()
                    .weight(3f),
            ) {
                Text(text=text, style = MaterialTheme.typography.bodyLarge, overflow = TextOverflow.Ellipsis)
            }
            Row (
                Modifier.fillMaxSize().weight(1f)
            ){
                UserDetail(user.name, user.surname,user.profilePic, time, modifier = Modifier.weight(1f), onImageClick = { actions.seeProfile(user.userId)})
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .requiredSize(22.dp)
                        .clip(CircleShape)
                        .clickable {
                            showDialog = true
                        }
                        .align(Alignment.CenterVertically)
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
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    UserDetail(user.name, user.surname, user.profilePic, time, onImageClick = { actions.seeProfile(user.userId)})
                    Spacer(modifier = Modifier.height(6.dp))

                    if (rating != null) {
                        RatingBar(rating = rating)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    images?.takeIf { it.isNotEmpty() }?.let { imageList ->
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp)
                        ) {
                            items(imageList) { image ->
                                val imageModifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        fullscreenIndex = imageList.indexOf(image)
                                    }
                                when (image) {
                                    is TravelImage.Url -> {
                                        Image(
                                            painter = rememberAsyncImagePainter(image.value),
                                            contentDescription = null,
                                            modifier = imageModifier
                                                .size(120.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    is TravelImage.Resource -> {
                                        Image(
                                            painter = painterResource(id = image.resId),
                                            contentDescription = null,
                                            modifier = imageModifier
                                                .size(120.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close")
                }
            }
        )
        // To see the image in full
        if (fullscreenIndex != null) {
            Dialog(onDismissRequest = { fullscreenIndex = null }) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    FullscreenImageViewer(
                        images = images ?: emptyList(),
                        startIndex = fullscreenIndex!!
                    )
                }
            }
        }
    }

}

@Composable
fun RatingBar(
    rating: Double,
    modifier: Modifier = Modifier,
    starSize: Dp = 20.dp,
    spaceBetween: Dp = 2.dp
) {
    Row(modifier = modifier) {
        val fullStars = rating.toInt()
        val hasHalfStar = (rating % 1f) >= 0.5f
        val emptyStars = 5 - fullStars - if (hasHalfStar) 1 else 0

        repeat(fullStars) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(starSize)
            )
            Spacer(modifier = Modifier.width(spaceBetween))
        }

        if (hasHalfStar) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.StarHalf,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(starSize)
            )
            Spacer(modifier = Modifier.width(spaceBetween))
        }

        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Default.StarBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(starSize)
            )
            if (it != emptyStars - 1) {
                Spacer(modifier = Modifier.width(spaceBetween))
            }
        }
    }
}
