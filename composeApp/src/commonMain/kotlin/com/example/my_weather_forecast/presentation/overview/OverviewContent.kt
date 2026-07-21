package com.example.my_weather_forecast.presentation.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.result.WeatherError
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.error_generic_pull_refresh
import myweatherforecast.composeapp.generated.resources.error_network_pull_refresh
import myweatherforecast.composeapp.generated.resources.error_not_found_weather
import myweatherforecast.composeapp.generated.resources.error_rate_limited
import myweatherforecast.composeapp.generated.resources.error_unauthorized_weather
import myweatherforecast.composeapp.generated.resources.overview_empty
import org.jetbrains.compose.resources.stringResource

const val OVERVIEW_CONTENT_TEST_TAG = "overview_content"

@Composable
fun OverviewContent(
    uiState: OverviewUiState,
    onAreaClick: (Long) -> Unit,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val taggedModifier = modifier.testTag(OVERVIEW_CONTENT_TEST_TAG)
    when (uiState) {
        is OverviewUiState.Loading -> LoadingContent(taggedModifier)
        is OverviewUiState.Empty -> EmptyContent(taggedModifier)
        is OverviewUiState.Error -> ErrorContent(uiState.error, taggedModifier)
        is OverviewUiState.Success -> SuccessContent(uiState.areas, onAreaClick, onRemove, taggedModifier)
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(Res.string.overview_empty),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ErrorContent(error: WeatherError, modifier: Modifier = Modifier) {
    val message = error.toMessage()
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun WeatherError.toMessage(): String = when (this) {
    WeatherError.Network -> stringResource(Res.string.error_network_pull_refresh)
    WeatherError.RateLimited -> stringResource(Res.string.error_rate_limited)
    WeatherError.Unauthorized -> stringResource(Res.string.error_unauthorized_weather)
    WeatherError.NotFound -> stringResource(Res.string.error_not_found_weather)
    WeatherError.AtLimit, WeatherError.AlreadySaved, is WeatherError.Unknown ->
        stringResource(Res.string.error_generic_pull_refresh)
}

@Composable
private fun SuccessContent(
    areas: List<AreaSummary>,
    onAreaClick: (Long) -> Unit,
    onRemove: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(areas, key = { it.id }) { area ->
            LocationSummaryCard(
                area = area,
                onClick = { onAreaClick(area.id) },
                onRemove = onRemove,
            )
        }
    }
}
