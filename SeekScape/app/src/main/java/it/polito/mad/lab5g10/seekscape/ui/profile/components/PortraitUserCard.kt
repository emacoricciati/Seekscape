package it.polito.mad.lab5g10.seekscape.ui.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.polito.mad.lab5g10.seekscape.models.UserInfoViewModel
import it.polito.mad.lab5g10.seekscape.ui.profile.UserData
import it.polito.mad.lab5g10.seekscape.ui.profile.UserDestinations
import it.polito.mad.lab5g10.seekscape.ui.profile.UserDetails
import it.polito.mad.lab5g10.seekscape.ui.profile.UserPersonalInfo
import it.polito.mad.lab5g10.seekscape.ui.profile.UserPersonality

@Composable
fun PortraitUserCard(vm: UserInfoViewModel) {
    val ratingsAverage by vm.ratingsAverage.collectAsState()
    val reviews by vm.reviews.collectAsState()
    val numTravels by vm.numTravels.collectAsState()
    val personality by vm.personality.collectAsState()
    val desiredDestinations by vm.desiredDestinations.collectAsState()

    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    UserPersonalInfo(vm)
                }
                Spacer(Modifier.width(20.dp))
                Column(horizontalAlignment = Alignment.End) {
                    UserData(reviews?.size ?: 0, ratingsAverage, numTravels)
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.height(10.dp))
            UserDetails(vm)
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.height(10.dp))
            UserPersonality(personality)
            Spacer(Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.height(10.dp))
            UserDestinations(desiredDestinations)
        }
        Spacer(Modifier.height(20.dp))
    }

}