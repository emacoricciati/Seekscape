package it.polito.mad.lab5g10.seekscape.ui.travels

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import it.polito.mad.lab5g10.seekscape.ui._common.components.TextBox
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import it.polito.mad.lab5g10.seekscape.models.AppState
import it.polito.mad.lab5g10.seekscape.models.Request
import it.polito.mad.lab5g10.seekscape.models.SingleRequestViewModel
import it.polito.mad.lab5g10.seekscape.models.SingleReviewViewModelFactory
import it.polito.mad.lab5g10.seekscape.models.Travel
import it.polito.mad.lab5g10.seekscape.ui._common.components.ConfirmRequestButton
import it.polito.mad.lab5g10.seekscape.ui._common.components.SelectNumberSpots
import it.polito.mad.lab5g10.seekscape.ui._common.components.TravelResumeCard

@Composable
fun ApplyToJoinView(travel: Travel, navCont: NavHostController) {
    val profile = AppState.myProfile.collectAsState().value
    val viewModel: SingleRequestViewModel

    val request = Request(
        "",
        profile,
        travel,
        "",
        false,
        false
    )

    viewModel = viewModel(
        factory = SingleReviewViewModelFactory(request)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Verify and continue",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(10.dp))
        TravelResumeCard(travel)
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = "Say something to the creator of the travel",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextBox(viewModel, travel.creator.name + travel.creator.surname)
        Spacer(modifier = Modifier.height(10.dp))
        if (travel.travelCompanions != null && travel.maxPeople != null) {
            SelectNumberSpots(
                1,
                travel.maxPeople!! - travel.travelCompanions!!.size,
                viewModel
            )
        }
        ConfirmRequestButton(viewModel, navCont)
    }
}