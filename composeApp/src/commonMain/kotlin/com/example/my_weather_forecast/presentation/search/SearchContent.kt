package com.example.my_weather_forecast.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Location

@Composable
fun SearchContent(
    uiState: SearchUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onLocationClick: (Location) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Search for a city") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
        when (uiState) {
            is SearchUiState.Idle -> Unit
            is SearchUiState.Loading -> LoadingRow()
            is SearchUiState.Empty -> MessageRow("No cities found. Try a different search.")
            is SearchUiState.Error -> MessageRow(uiState.error.toMessage())
            is SearchUiState.Results -> ResultsList(uiState.locations, onLocationClick)
        }
    }
}

@Composable
private fun LoadingRow(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MessageRow(message: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun WeatherError.toMessage(): String = when (this) {
    WeatherError.Network -> "No internet connection. Try again."
    WeatherError.RateLimited -> "Too many requests right now. Try again in a bit."
    WeatherError.Unauthorized -> "There's a problem reaching the search service. Please try again later."
    WeatherError.NotFound -> "No cities found. Try a different search."
    WeatherError.AtLimit, WeatherError.AlreadySaved, is WeatherError.Unknown -> "Something went wrong. Try again."
}

@Composable
private fun ResultsList(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(locations, key = { "${it.lat},${it.lon}" }) { location ->
            SearchResultRow(location = location, onClick = { onLocationClick(location) })
        }
    }
}

@Composable
private fun SearchResultRow(
    location: Location,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val subtitle = location.subtitle()
    ListItem(
        headlineContent = { Text(location.name) },
        supportingContent = { Text(subtitle) },
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { contentDescription = "${location.name}, $subtitle" },
    )
}

private fun Location.subtitle(): String = if (state != null) "$state, $country" else country
