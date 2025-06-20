package it.polito.mad.lab5g10.seekscape.ui._common.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class Location(
    var name: String,
    var distance: Double
)

val locations = listOf(
    Location("Rome, Italy", 0.0),
    Location("Paris, France", 1105.0),
    Location("New York, USA", 6900.0),
    Location("Casablanca, Morocco", 1760.0),
    Location("Tokyo, Japan", 9900.0),
    Location("Athens, Greece", 1285.0),
    Location("London, UK", 1435.0),
    Location("Berlin, Germany", 1180.0),
    Location("Madrid, Spain", 1360.0),
    Location("Cairo, Egypt", 2140.0),
    Location("Buenos Aires, Argentina", 11300.0),
    Location("Beijing, China", 8150.0),
    Location("Sydney, Australia", 16100.0),
    Location("Los Angeles, USA", 10160.0),
    Location("Toronto, Canada", 7100.0),
    Location("Moscow, Russia", 2370.0),
    Location("Dubai, UAE", 4300.0),
    Location("Bangkok, Thailand", 8700.0),
    Location("Cape Town, South Africa", 8300.0),
    Location("Mexico City, Mexico", 10300.0),
    Location("Singapore, Singapore", 9900.0),
    Location("Seoul, South Korea", 9200.0),
    Location("Lagos, Nigeria", 3800.0),
    Location("Jakarta, Indonesia", 10500.0),
    Location("Nairobi, Kenya", 5400.0),
    Location("Tehran, Iran", 3400.0),
    Location("Istanbul, Turkey", 1370.0),
    Location("San Francisco, USA", 10080.0),
    Location("Hong Kong, China", 9600.0),
    Location("Riyadh, Saudi Arabia", 3400.0),
    Location("Hanoi, Vietnam", 8900.0),
    Location("Delhi, India", 6200.0)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddLocation(
    onCancel: () -> Unit,
    onLocationSelected: (Location) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .border(
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(28.dp)
                    ),
                shape = RoundedCornerShape(28.dp),
                placeholder = { Text("Search for a location") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .padding(top=17.dp),
        ) {
            Spacer(Modifier.width(10.dp))
            Text(
                "Cancel",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.clickable { onCancel() }
            )
        }
    }

    val filteredLocations = locations.filter { location ->
        location.name.contains(searchText, ignoreCase = true)
    }

    Spacer(Modifier.height(10.dp))

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp),
    ) {
        items(filteredLocations) { location ->
            LocationItem(location = location, onLocationSelected = onLocationSelected)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun LocationItem(location: Location, onLocationSelected: (Location) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable { onLocationSelected(location) }
    ) {
        Text(location.name)
        Text("${location.distance} km", style = MaterialTheme.typography.bodyMedium)
    }
}
