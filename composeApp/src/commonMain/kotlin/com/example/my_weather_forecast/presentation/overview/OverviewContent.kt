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
            text = "No saved areas yet. Tap + to add one.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ErrorContent(error: WeatherError, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(
            text = error.toMessage(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private fun WeatherError.toMessage(): String = when (this) {
    WeatherError.Network -> "No internet connection. Pull to refresh to try again."
    WeatherError.RateLimited -> "Too many requests right now. Try again in a bit."
    WeatherError.Unauthorized -> "There's a problem reaching the weather service. Please try again later."
    WeatherError.NotFound -> "Couldn't find weather data for this area."
    WeatherError.AtLimit, WeatherError.AlreadySaved, is WeatherError.Unknown ->
        "Couldn't load weather. Pull to refresh to try again."
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
