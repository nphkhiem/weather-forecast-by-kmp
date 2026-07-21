package com.example.my_weather_forecast.presentation.overview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.my_weather_forecast.domain.model.Units
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.ic_add
import myweatherforecast.composeapp.generated.resources.ic_more_vert
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(ExperimentalMaterial3Api::class, KoinExperimentalAPI::class)
@Composable
fun OverviewScreen(
    onOpenSearch: () -> Unit,
    onOpenDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OverviewViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val units by viewModel.units.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is OverviewEvent.OpenDetail -> onOpenDetail(event.locationId)
                is OverviewEvent.ShowMessage -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = if (event.undoable) "Undo" else null,
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoRemove()
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Weather") },
                actions = { UnitsMenu(units = units, onUnitsSelected = viewModel::setUnits) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onOpenSearch) {
                Icon(painter = painterResource(Res.drawable.ic_add), contentDescription = "Add area")
            }
        },
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
        ) {
            OverviewContent(
                uiState = uiState,
                onAreaClick = viewModel::onAreaClick,
                onRemove = viewModel::removeArea,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun UnitsMenu(units: Units, onUnitsSelected: (Units) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }, modifier = modifier) {
        Icon(painter = painterResource(Res.drawable.ic_more_vert), contentDescription = "Settings")
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = { Text("Metric (°C)") },
            trailingIcon = { if (units == Units.METRIC) Text("✓") },
            onClick = {
                onUnitsSelected(Units.METRIC)
                expanded = false
            },
        )
        DropdownMenuItem(
            text = { Text("Imperial (°F)") },
            trailingIcon = { if (units == Units.IMPERIAL) Text("✓") },
            onClick = {
                onUnitsSelected(Units.IMPERIAL)
                expanded = false
            },
        )
    }
}
