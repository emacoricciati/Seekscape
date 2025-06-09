package it.polito.mad.lab5g10.seekscape.ui._common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.ui.graphics.vector.ImageVector
import it.polito.mad.lab5g10.seekscape.models.Activity

val personalities = listOf(
    "Extrovert",
    "Introvert",
    "Urban",
    "Nature Lover",
    "Spender",
    "Budget-conscious",
    "Independent",
    "Adventurous",
    "Chill",
    "Tech-savvy",
    "Prudent",
    "Mature"
)

val activities = listOf(
    Activity(icon = "landscape", name ="Landscape"),
    Activity(icon = "food", name = "Try the typical cuisine"),
    Activity(icon = "car", name = "People can rent a own vehicle"),
    Activity(icon = "audioguide", name = "People can take a audio guide"),
    Activity(icon = "boat", name = "Take a boat trip"),
    Activity(icon = "wine", name = "Enjoy a wine tasting"),
    Activity(icon = "garden", name = "Relax in a botanical garden"),
    Activity(icon = "bookstore", name = "Visit a local bookstore"),
    Activity(icon = "nightlife", name = "Experience the nightlife"),
    Activity(icon = "dance", name = "Join a traditional dance show"),
    Activity(icon = "palace", name = "Tour a historical palace"),
    Activity(icon = "district", name = "Explore a cultural district"),
    Activity(icon = "pastry", name = "Taste local pastries"),
    Activity(icon = "view", name = "Enjoy a panoramic viewpoint")
)

val activtyIcons = hashMapOf<String, ImageVector>(
    "default" to Icons.Filled.DirectionsRun,
    "nightlife" to Icons.Filled.Nightlife,
    "drink" to Icons.Filled.LocalBar,
    "dance" to Icons.Filled.MusicNote,
    "bar" to Icons.Filled.LocalDrink,
    "palace" to Icons.Filled.AccountBalance,
    "garden" to Icons.Filled.Nature,
    "hike" to Icons.Filled.Terrain,
    "pastry" to Icons.Filled.Cake,
    "district" to Icons.Filled.LocationCity,
    "wine" to Icons.Filled.WineBar,
    "bookstore" to Icons.Filled.MenuBook,
    "boat" to Icons.Filled.DirectionsBoat,
    "view" to Icons.Filled.Visibility,
    "food" to Icons.Filled.Restaurant,
    "car" to Icons.Filled.DirectionsCar,
    "landscape" to Icons.Filled.Landscape,
    "audioguide" to Icons.Filled.Headset,
)