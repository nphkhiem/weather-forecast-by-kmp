package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(56.dp)
            .padding(vertical = 4.dp)
            .semantics(mergeDescendants = true) {
                contentDescription = "$hourLabel, $popPercent percent chance of rain"
            },
    ) {
        Text(hourLabel, style = MaterialTheme.typography.bodySmall)
        Text("$popPercent%", style = MaterialTheme.typography.bodyMedium)
    }
}
