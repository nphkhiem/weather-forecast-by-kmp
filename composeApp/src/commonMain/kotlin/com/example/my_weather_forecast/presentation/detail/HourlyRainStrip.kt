package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.domain.model.HourlyForecast
import kotlin.math.roundToInt
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.hourly_pop_accessibility
import myweatherforecast.composeapp.generated.resources.hourly_pop_percent
import org.jetbrains.compose.resources.stringResource

@Composable
fun HourlyRainStrip(hourly: List<HourlyForecast>, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(hourly, key = { it.time.toEpochMilliseconds() }) { hour ->
            HourlyRainItem(hour)
        }
    }
}

@Composable
private fun HourlyRainItem(hour: HourlyForecast, modifier: Modifier = Modifier) {
    val hourLabel = hour.time.toHourLabel()
    val popPercent = (hour.pop * 100).roundToInt()
    val accessibilityDescription = stringResource(Res.string.hourly_pop_accessibility, hourLabel, popPercent)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .widthIn(min = 56.dp)
            .padding(vertical = 4.dp)
            .semantics(mergeDescendants = true) { contentDescription = accessibilityDescription },
    ) {
        Text(hourLabel, style = MaterialTheme.typography.bodySmall)
        Text(stringResource(Res.string.hourly_pop_percent, popPercent), style = MaterialTheme.typography.bodyMedium)
    }
}
