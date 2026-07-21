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
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.presentation.theme.readableName
import com.example.my_weather_forecast.presentation.theme.toDrawableResource
import kotlin.math.roundToInt
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.current_accessibility
import myweatherforecast.composeapp.generated.resources.current_summary_line
import myweatherforecast.composeapp.generated.resources.error_generic_pull_refresh
import myweatherforecast.composeapp.generated.resources.error_network_pull_refresh
import myweatherforecast.composeapp.generated.resources.error_not_found_weather
import myweatherforecast.composeapp.generated.resources.error_rate_limited
import myweatherforecast.composeapp.generated.resources.error_unauthorized_weather
import myweatherforecast.composeapp.generated.resources.feels_like
import myweatherforecast.composeapp.generated.resources.stale_suffix
import myweatherforecast.composeapp.generated.resources.temp_degrees
import myweatherforecast.composeapp.generated.resources.updated_at
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
    val message = error.toMessage()
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
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
private fun SuccessContent(state: DetailUiState.Success, modifier: Modifier = Modifier) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { UpdatedBanner(state.lastUpdated, state.stale) }
        item { CurrentConditionsHeader(state.forecast.current, state.forecast.units) }
        item { HourlyRainStrip(state.forecast.hourly) }
        items(state.forecast.daily, key = { it.date.toString() }) { daily ->
            DailyRow(daily = daily, today = today, units = state.forecast.units)
        }
    }
}

@Composable
private fun UpdatedBanner(lastUpdated: Instant, stale: Boolean, modifier: Modifier = Modifier) {
    val label = stringResource(Res.string.updated_at, lastUpdated.toClockLabel()) +
        if (stale) stringResource(Res.string.stale_suffix) else ""
    Text(text = label, style = MaterialTheme.typography.labelMedium, modifier = modifier.fillMaxWidth())
}

@Composable
private fun CurrentConditionsHeader(current: CurrentConditions, units: Units, modifier: Modifier = Modifier) {
    val conditionName = current.condition.icon.readableName()
    val windUnitLabel = stringResource(units.windSpeedUnitLabelRes())
    val temp = current.temp.roundToInt()
    val feelsLike = current.feelsLike.roundToInt()
    val windSpeed = current.windSpeed.roundToInt()
    val popPercent = (current.pop * 100).roundToInt()
    val accessibilityDescription = stringResource(
        Res.string.current_accessibility,
        conditionName, temp, feelsLike, current.humidity, windSpeed, windUnitLabel, popPercent,
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = accessibilityDescription },
    ) {
        Icon(
            painter = painterResource(current.condition.icon.toDrawableResource()),
            contentDescription = null,
            modifier = Modifier.size(56.dp),
        )
        Text(stringResource(Res.string.temp_degrees, temp), style = MaterialTheme.typography.displayMedium)
        Text(stringResource(Res.string.feels_like, feelsLike), style = MaterialTheme.typography.bodyLarge)
        Text(
            stringResource(Res.string.current_summary_line, current.humidity, windSpeed, windUnitLabel, popPercent),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
