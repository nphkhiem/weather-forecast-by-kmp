package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.domain.model.DailyForecast
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.presentation.theme.readableName
import com.example.my_weather_forecast.presentation.theme.toDrawableResource
import kotlin.math.roundToInt
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.painterResource

@Composable
fun DailyRow(daily: DailyForecast, today: LocalDate, units: Units, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .semantics(mergeDescendants = true) { contentDescription = daily.accessibilityDescription(today, units) },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = daily.date.dayLabel(today),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.width(48.dp),
            )
            Icon(
                painter = painterResource(daily.condition.icon.toDrawableResource()),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = "${daily.tempMax.roundToInt()}° / ${daily.tempMin.roundToInt()}°",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Text(
            text = "${(daily.pop * 100).roundToInt()}% rain  ·  Wind ${daily.windSpeed.roundToInt()} ${units.windSpeedUnitLabel()}  ·  " +
                "Humidity ${daily.humidity}%",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 60.dp),
        )
    }
}

private fun DailyForecast.accessibilityDescription(today: LocalDate, units: Units): String {
    val day = date.dayLabel(today)
    return "$day, ${condition.icon.readableName()}, high ${tempMax.roundToInt()}, low ${tempMin.roundToInt()}, " +
        "${(pop * 100).roundToInt()} percent chance of rain, wind ${windSpeed.roundToInt()} ${units.windSpeedUnitLabel()}, " +
        "$humidity percent humidity"
}

private fun LocalDate.dayLabel(today: LocalDate): String = if (this == today) "Today" else dayOfWeek.shortLabel()

private fun DayOfWeek.shortLabel(): String = when (this) {
    DayOfWeek.MONDAY -> "Mon"
    DayOfWeek.TUESDAY -> "Tue"
    DayOfWeek.WEDNESDAY -> "Wed"
    DayOfWeek.THURSDAY -> "Thu"
    DayOfWeek.FRIDAY -> "Fri"
    DayOfWeek.SATURDAY -> "Sat"
    DayOfWeek.SUNDAY -> "Sun"
}
