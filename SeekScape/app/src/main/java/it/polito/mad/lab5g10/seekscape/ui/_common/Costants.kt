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

// List of all prefixes for phone numbers in the world
val phonePrefixes = listOf(
    "+1", "+7", "+20", "+27", "+30", "+31", "+32", "+33", "+34", "+36", "+39",
    "+40", "+41", "+43", "+44", "+45", "+46", "+47", "+48", "+49", "+51", "+52",
    "+53", "+54", "+55", "+56", "+57", "+58", "+60", "+61", "+62", "+63", "+64",
    "+65", "+66", "+81", "+82", "+84", "+86", "+90", "+91", "+92", "+93", "+94",
    "+95", "+98", "+211", "+212", "+213", "+216", "+218", "+220", "+221", "+222",
    "+223", "+224", "+225", "+226", "+227", "+228", "+229", "+230", "+231", "+232",
    "+233", "+234", "+235", "+236", "+237", "+238", "+239", "+240", "+241", "+242",
    "+243", "+244", "+245", "+246", "+248", "+249", "+250", "+251", "+252", "+253",
    "+254", "+255", "+256", "+257", "+258", "+260", "+261", "+262", "+263", "+264",
    "+265", "+266", "+267", "+268", "+269", "+290", "+291", "+297", "+298", "+299",
    "+350", "+351", "+352", "+353", "+354", "+355", "+356", "+357", "+358", "+359",
    "+370", "+371", "+372", "+373", "+374", "+375", "+376", "+377", "+378", "+380",
    "+381", "+382", "+383", "+385", "+386", "+387", "+389", "+420", "+421", "+423",
    "+500", "+501", "+502", "+503", "+504", "+505", "+506", "+507", "+508", "+509",
    "+590", "+591", "+592", "+593", "+594", "+595", "+596", "+597", "+598", "+599",
    "+670", "+672", "+673", "+674", "+675", "+676", "+677", "+678", "+679", "+680",
    "+681", "+682", "+683", "+685", "+686", "+687", "+688", "+689", "+690", "+691",
    "+692", "+850", "+852", "+853", "+855", "+856", "+870", "+880", "+886", "+960",
    "+961", "+962", "+963", "+964", "+965", "+966", "+967", "+968", "+970", "+971",
    "+972", "+973", "+974", "+975", "+976", "+977", "+992", "+993", "+994", "+995",
    "+996", "+998"
)