package it.polito.mad.lab5g10.seekscape.ui.travels

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import it.polito.mad.lab5g10.seekscape.models.Itinerary
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.ui._common.activtyIcons
import it.polito.mad.lab5g10.seekscape.ui._common.components.IconDate
import it.polito.mad.lab5g10.seekscape.ui._common.components.IconLocation

@Composable
fun ViewItineraryScreen(travel:Travel, itinerary: Itinerary) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        //------------------------------- TITLE -------------------------------
        Text(
            text = itinerary.name,
            style = MaterialTheme.typography.displayLarge,
            )
        Spacer(modifier = Modifier.height(10.dp))

        //------------------------------- PLACE and DATES -------------------------------
        val paddingHorizontalScreen = 15.dp
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(paddingHorizontalScreen))
            IconLocation(itinerary.places[0])
            Spacer(modifier = Modifier.weight(1f))
            IconDate(itinerary.startDate)
            Spacer(modifier = Modifier.width(paddingHorizontalScreen))
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = itinerary.description,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(3.dp))

        Spacer(modifier = Modifier.height(10.dp))
        Column(horizontalAlignment = Alignment.Start){
            if(itinerary.activities==null || itinerary.activities.isEmpty()){
                Text(
                    text = "no activities specified",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .height(100.dp)
                        .padding(16.dp),
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Activities",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(5.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                for (activity in itinerary.activities) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(paddingHorizontalScreen))
                        Column(horizontalAlignment = Alignment.Start){
                            if(activity.icon=="" || !activtyIcons.containsKey(activity.icon)){
                                Icon(
                                    imageVector = activtyIcons.get("default")!!,
                                    contentDescription = "default",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(22.dp),
                                )
                            } else {
                                Icon(
                                    imageVector = activtyIcons[activity.icon]!!,
                                    contentDescription = activity.icon,
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(7.dp))
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = activity.name+ if(activity.optional) "" else " (mandatory)",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }

        }
    }
}