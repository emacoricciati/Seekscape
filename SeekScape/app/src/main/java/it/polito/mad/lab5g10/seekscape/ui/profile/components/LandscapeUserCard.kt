package it.polito.mad.lab5g10.seekscape.ui.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
fun LandscapeUserCard(vm: UserInfoViewModel) {
    val ratingsAverage by vm.ratingsAverage.collectAsState()
    val reviews by vm.reviews.collectAsState()
    val numTravels by vm.numTravels.collectAsState()
    val personality by vm.personality.collectAsState()
    val desiredDestinations by vm.desiredDestinations.collectAsState()
    ElevatedCard(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserPersonalInfo(vm)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                UserData(reviews?.size ?: 0, ratingsAverage, numTravels)
            }
            Spacer(Modifier.width(20.dp))
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                UserDetails(vm)
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            color = MaterialTheme.colorScheme.outline
        )
        UserPersonality(personality)
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            color = MaterialTheme.colorScheme.outline
        )
        UserDestinations(desiredDestinations)
        Spacer(Modifier.height(20.dp))
    }
}