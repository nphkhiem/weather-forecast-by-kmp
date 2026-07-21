package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.CurrentConditions
import com.example.my_weather_forecast.presentation.theme.readableName
import com.example.my_weather_forecast.presentation.theme.toDrawableResource
import kotlin.math.roundToInt
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource

const val DETAIL_CONTENT_TEST_TAG = "detail_content"

@Composable
fun DetailContent(uiState: DetailUiState, modifier: Modifier = Modifier) {
    val taggedModifier = modifier.testTag(DETAIL_CONTENT_TEST_TAG)
    when (uiState) {
        is DetailUiState.Loading -> LoadingContent(taggedModifier)
        is DetailUiState.Error -> ErrorContent(uiState.error, taggedModifier)
        is DetailUiState.Success -> SuccessContent(uiState, taggedModifier)
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(error: WeatherError, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = error.toMessage(), style = MaterialTheme.typography.bodyLarge)
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
private fun SuccessContent(state: DetailUiState.Success, modifier: Modifier = Modifier) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { UpdatedBanner(state.lastUpdated, state.stale) }
        item { CurrentConditionsHeader(state.forecast.current) }
        item { HourlyRainStrip(state.forecast.hourly) }
        items(state.forecast.daily, key = { it.date.toString() }) { daily ->
            DailyRow(daily = daily, today = today)
        }
    }
}

@Composable
private fun UpdatedBanner(lastUpdated: Instant, stale: Boolean, modifier: Modifier = Modifier) {
    val label = "Updated ${lastUpdated.toClockLabel()}" + if (stale) "  ·  Data may be out of date" else ""
    Text(text = label, style = MaterialTheme.typography.labelMedium, modifier = modifier.fillMaxWidth())
}

@Composable
private fun CurrentConditionsHeader(current: CurrentConditions, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = current.accessibilityDescription() },
    ) {
        Icon(
            painter = painterResource(current.condition.icon.toDrawableResource()),
            contentDescription = null,
            modifier = Modifier.size(56.dp),
        )
        Text("${current.temp.roundToInt()}°", style = MaterialTheme.typography.displayMedium)
        Text("Feels like ${current.feelsLike.roundToInt()}°", style = MaterialTheme.typography.bodyLarge)
        Text(
            "Humidity ${current.humidity}%  ·  Wind ${current.windSpeed.roundToInt()} m/s  ·  " +
                "${(current.pop * 100).roundToInt()}% rain",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun CurrentConditions.accessibilityDescription(): String =
    "${condition.icon.readableName()}, ${temp.roundToInt()} degrees, feels like ${feelsLike.roundToInt()} degrees, " +
        "$humidity percent humidity, wind ${windSpeed.roundToInt()} meters per second, " +
        "${(pop * 100).roundToInt()} percent chance of rain"
