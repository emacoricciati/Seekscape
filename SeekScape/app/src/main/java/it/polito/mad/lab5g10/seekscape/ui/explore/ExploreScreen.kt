package it.polito.mad.lab5g10.seekscape.ui.explore

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

import it.polito.mad.lab5g10.seekscape.dayMonthFormat
import it.polito.mad.lab5g10.seekscape.ui.navigation.Actions
import it.polito.mad.lab5g10.seekscape.models.MAX_COMPANIONS
import it.polito.mad.lab5g10.seekscape.models.MAX_DURATION
import it.polito.mad.lab5g10.seekscape.models.MAX_PRICE
import it.polito.mad.lab5g10.seekscape.models.MIN_COMPANIONS
import it.polito.mad.lab5g10.seekscape.models.MIN_DURATION
import it.polito.mad.lab5g10.seekscape.models.MIN_PRICE
import it.polito.mad.lab5g10.seekscape.models.SearchViewModel
import it.polito.mad.lab5g10.seekscape.models.TRAVEL_TYPES
import it.polito.mad.lab5g10.seekscape.models.TravelUiState
import it.polito.mad.lab5g10.seekscape.ui._common.Calendar
import it.polito.mad.lab5g10.seekscape.ui._common.components.AddLocation
import it.polito.mad.lab5g10.seekscape.ui._common.components.BottomDialog
import it.polito.mad.lab5g10.seekscape.ui._common.components.IconMoney
import it.polito.mad.lab5g10.seekscape.ui._common.components.PillButtonEditable
import it.polito.mad.lab5g10.seekscape.ui._common.components.TravelCard
import kotlin.math.roundToInt

@Composable
fun ExploreTravelsScreen(vm: SearchViewModel, navController: NavHostController) {
    var showDialog by remember { mutableStateOf(false) }
    val searchText by vm.text.collectAsState()
    val isLoadingMore by vm.isLoadingMore.collectAsState()
    val actions = remember(navController) { Actions(navController) }

    if (!vm.isAddingPlace) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .requiredSize(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "filters",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.Companion
                            .fillMaxSize()
                            .padding(8.dp)
                    )
                }

                if (showDialog) {
                    FilterDialog(vm) { showDialog = false }
                }

                Spacer(modifier = Modifier.width(5.dp))

                TextField(
                    value = searchText,
                    onValueChange = { vm.setText(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    shape = RoundedCornerShape(28.dp),
                    placeholder = { Text("Search a travel") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
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

            Spacer(modifier = Modifier.height(7.dp))
            FilterChipsReadOnly(vm)

            val state by vm.travelUiState.collectAsState()
            when (val uiState = state) {
                is TravelUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is TravelUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(vertical = 25.dp, horizontal = 10.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No travels found",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }

                is TravelUiState.Success -> {
                    val listState = rememberLazyListState()
                    var hasTriggeredLoad by remember { mutableStateOf(false) }
                    LaunchedEffect(listState, uiState.travels.size) {
                        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                            .collect { visibleItems ->
                                val secondToLastIndex = uiState.travels.size - 2
                                val isSecondToLastVisible = visibleItems.any { it.index == secondToLastIndex }

                                if (isSecondToLastVisible && !hasTriggeredLoad) {
                                    hasTriggeredLoad = true
                                    vm.loadMore()
                                }
                            }
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize(), state=listState) {
                        itemsIndexed(uiState.travels) { index, travel ->
                            TravelCard(
                                travel,
                                { actions.seeTravel(travel.travelId) },
                                null,
                                navController
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            if(index==uiState.travels.lastIndex && isLoadingMore){
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(28.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
    else {
        Column (
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ){
            AddLocation(
                onCancel = { vm.toggleIsAddingLocation() },
                onLocationSelected = { location -> vm.setPlace(location.name) }
            )
        }
    }
}

@Composable
fun FilterChipsReadOnly(vm: SearchViewModel) {
    val place by vm.place.collectAsState()
    val startDate by vm.startDate.collectAsState()
    val endDate by vm.endDate.collectAsState()
    val minDuration by vm.minDuration.collectAsState()
    val maxDuration by vm.maxDuration.collectAsState()
    val minPrice by vm.minPrice.collectAsState()
    val maxPrice by vm.maxPrice.collectAsState()
    val minCompanions by vm.minCompanions.collectAsState()
    val maxCompanions by vm.maxCompanions.collectAsState()
    val travelTypes by vm.travelTypes.collectAsState()

    val chips: MutableList<Pair<String, () -> Unit>> = mutableListOf()


    if (place!=null && place!="") {
        val chipData: Pair<String, () -> Unit> = Pair(place!!, { vm.setPlace("") })
        chips.add(chipData)
    }

    if (startDate!=null && endDate!=null) {
        val chipData: Pair<String, () -> Unit> = Pair("${dayMonthFormat(startDate!!)}-${dayMonthFormat(endDate!!)}", { vm.setEndDate(null); vm.setStartDate(null) })
        chips.add(chipData)
    } else if(startDate!=null) {
        val chipData: Pair<String, () -> Unit> = Pair("after ${dayMonthFormat(startDate!!)}", { vm.setEndDate(null); vm.setStartDate(null) })
        chips.add(chipData)
    } else if(endDate!=null) {
        val chipData: Pair<String, () -> Unit> = Pair("before ${dayMonthFormat(endDate!!)}", { vm.setEndDate(null); vm.setStartDate(null) })
        chips.add(chipData)
    }

    if (minDuration!=MIN_DURATION || maxDuration!=MAX_DURATION) {
        val chipData: Pair<String, () -> Unit> = Pair("$minDuration-$maxDuration days", { vm.setMinDuration(MIN_DURATION); vm.setMaxDuration(MAX_DURATION) })
        chips.add(chipData)
    }

    if (minPrice!=MIN_PRICE || maxPrice!=MAX_PRICE) {
        val chipData: Pair<String, () -> Unit> = Pair("$minPrice-$maxPrice €", { vm.setMinPrice(MIN_PRICE); vm.setMaxPrice(MAX_PRICE) })
        chips.add(chipData)
    }
    if (minCompanions!=MIN_COMPANIONS || maxCompanions!=MAX_COMPANIONS) {
        val chipData: Pair<String, () -> Unit> = Pair("$minCompanions-$maxCompanions people", { vm.setMinCompanions(MIN_COMPANIONS); vm.setMaxCompanions(MAX_COMPANIONS) })
        chips.add(chipData)
    }
    for(type in travelTypes) {
        val chipData: Pair<String, () -> Unit> = Pair(type, { vm.removeTravelTypes(type) })
        chips.add(chipData)
    }

    if(chips.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            for(t in chips) {
                val text: String = t.first
                val deleteSearch: () -> Unit = t.second
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ){
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    deleteSearch()
                                },
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
        Spacer(modifier=Modifier.height(10.dp))
    }else{
        Spacer(modifier=Modifier.height(3.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterDialog(vm: SearchViewModel, onDismissRequest: () -> Unit) {
    val context = LocalContext.current
    val available by vm.available.collectAsState()
    val place by vm.place.collectAsState()
    val startDate by vm.startDate.collectAsState()
    val endDate by vm.endDate.collectAsState()
    val minDuration by vm.minDuration.collectAsState()
    val maxDuration by vm.maxDuration.collectAsState()
    val minPrice by vm.minPrice.collectAsState()
    val maxPrice by vm.maxPrice.collectAsState()
    val minCompanions by vm.minCompanions.collectAsState()
    val maxCompanions by vm.maxCompanions.collectAsState()
    val travelTypes by vm.travelTypes.collectAsState()

    val scrollState = rememberScrollState()
    BottomDialog(
        onDismissRequest = { onDismissRequest() }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
        ) {
            //HEADER
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 3.dp, bottom = 10.dp)
            ) {
                Spacer(Modifier.width(1.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = "filters",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = "Select Filters",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(start=15.dp)
                    )
                }
                IconButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier
                        .requiredSize(24.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "close",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.Companion.fillMaxSize()
                    )
                }
            }

            //AVIABLE
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.EditCalendar,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Available travels only",
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = available,
                    onCheckedChange = { isChecked ->
                        vm.setAvailable(isChecked)
                    },
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.outline
            )

            //LOCATION
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Location",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            if (place!!.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    PillButtonEditable(place!!) { vm.removePlace() }
                }
            } else {

                Button(
                    onClick = { vm.toggleIsAddingLocation() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Outlined.Place, contentDescription = "Add Location")
                        Text("Add Location")
                    }
                }
            }




            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.outline
            )


            //DATES
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Date Range",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Dates",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Row (
                modifier = Modifier.padding(bottom = 0.dp)
            ) {
                Calendar("start", startDate, null, modifier = Modifier.weight(1f)) { vm.setStartDate(it) }
                Spacer(modifier = Modifier.width(6.dp))
                Calendar("end", endDate, startDate, modifier = Modifier.weight(1f)) { vm.setEndDate(it) }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, top = 6.dp),
                color = MaterialTheme.colorScheme.outline
            )

            //DURATION
            val durationRange = MIN_DURATION..MAX_DURATION
            val durationSteps = durationRange.endInclusive - durationRange.start - 1
            var durationCurrentRange by remember { mutableStateOf(minDuration..maxDuration) }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = "Duration",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Duration (days)",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(end = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .width(40.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(text = "${durationCurrentRange.start}")
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "-")
                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .width(40.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(text = "${durationCurrentRange.endInclusive}")
                    }
                }
            }
            RangeSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = durationCurrentRange.start.toFloat()..durationCurrentRange.endInclusive.toFloat(),
                onValueChange = { newRange ->
                    durationCurrentRange = newRange.start.roundToInt()..newRange.endInclusive.roundToInt()
                },
                valueRange = durationRange.start.toFloat()..durationRange.endInclusive.toFloat(),
                steps = durationSteps,
                onValueChangeFinished = {
                    vm.setMinDuration(durationCurrentRange.start)
                    vm.setMaxDuration(durationCurrentRange.endInclusive)
                }
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.outline
            )

            val priceRange = MIN_PRICE..MAX_PRICE
            val pricestepSize = 50
            val priceSteps = ((priceRange.endInclusive - priceRange.start) / pricestepSize) - 1
            var priceCurrentRange by remember { mutableStateOf(minPrice..maxPrice) }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconMoney(Icons.Filled.Euro)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Price Range",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(end = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .width(65.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(text = "${priceCurrentRange.start}")
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "-")
                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .width(65.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(text = "${priceCurrentRange.endInclusive}")
                    }
                }
            }
            RangeSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = priceCurrentRange.start.toFloat()..priceCurrentRange.endInclusive.toFloat(),
                onValueChange = { newRange ->
                    var newStart = (newRange.start / pricestepSize).roundToInt() * pricestepSize
                    val newEnd = (newRange.endInclusive / pricestepSize).roundToInt() * pricestepSize.coerceAtMost(newStart)
                    newStart = newStart.coerceAtMost(newEnd)
                    priceCurrentRange = newStart..newEnd
                },
                valueRange = priceRange.start.toFloat()..priceRange.endInclusive.toFloat(),
                steps = priceSteps,
                onValueChangeFinished = {
                    vm.setMinPrice(priceCurrentRange.start)
                    vm.setMaxPrice(priceCurrentRange.endInclusive)
                }
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.outline
            )

            //COMPANIONS
            val companionsRange = MIN_COMPANIONS..MAX_COMPANIONS
            val companionsSteps = companionsRange.endInclusive - companionsRange.start - 1
            var companionsCurrentRange by remember { mutableStateOf(minCompanions..maxCompanions) }
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Duration",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Companions",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(end = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .width(40.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.CenterEnd
                    ){
                        Text(text = "${companionsCurrentRange.start}")
                    }

                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "-")
                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier
                            .wrapContentHeight()
                            .width(40.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.CenterEnd
                    ){
                        Text(text = "${companionsCurrentRange.endInclusive}")
                    }
                }
            }
            RangeSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = companionsCurrentRange.start.toFloat()..companionsCurrentRange.endInclusive.toFloat(),
                onValueChange = { newRange ->
                    companionsCurrentRange = newRange.start.roundToInt()..newRange.endInclusive.roundToInt()
                },
                valueRange = companionsRange.start.toFloat()..companionsRange.endInclusive.toFloat(),
                steps = companionsSteps,
                onValueChangeFinished = {
                    vm.setMinCompanions(companionsCurrentRange.start)
                    vm.setMaxCompanions(companionsCurrentRange.endInclusive)
                }
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.outline
            )

            //TRAVEL TYPE
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Flight,
                    contentDescription = "Travel type",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Travel type",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            var showTravelTypeDialog by remember { mutableStateOf(false) }

            Spacer(Modifier.height(5.dp))

            if (travelTypes.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .padding(top=5.dp)
                ) {

                    travelTypes.forEach { PillButtonEditable(it) { vm.removeTravelTypes(it) } }
                }

                TextButton(
                    onClick = { showTravelTypeDialog = true },
                ) {
                    Text(text = "Select more", style = MaterialTheme.typography.bodySmall)
                }

            } else {
                Button(
                    onClick = { showTravelTypeDialog = true },
                    modifier = Modifier
                        .padding(top = 2.dp, bottom = 4.dp)
                        .height(40.dp)
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        text= "Select Travel Type",
                    )
                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }

            if (showTravelTypeDialog) {
                AlertDialog(
                    onDismissRequest = { showTravelTypeDialog = false },
                    title = { Text("Select More Travel Types") },
                    text = {
                        Column {
                            TRAVEL_TYPES.forEach { type ->
                                TextButton(onClick = {
                                    if (!travelTypes.contains(type)) {
                                        vm.addTravelTypes(type)
                                    } else {
                                        // Show toast for duplicate entry
                                        Toast.makeText(
                                            context,
                                            "$type already selected",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }) {
                                    Text(type)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showTravelTypeDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }

        }
    }
}
