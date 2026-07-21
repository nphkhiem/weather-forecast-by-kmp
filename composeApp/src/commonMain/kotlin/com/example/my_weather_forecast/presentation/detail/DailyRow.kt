package com.example.my_weather_forecast.presentation.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.daily_accessibility
import myweatherforecast.composeapp.generated.resources.daily_summary_line
import myweatherforecast.composeapp.generated.resources.daily_temp_range
import myweatherforecast.composeapp.generated.resources.day_fri
import myweatherforecast.composeapp.generated.resources.day_mon
import myweatherforecast.composeapp.generated.resources.day_sat
import myweatherforecast.composeapp.generated.resources.day_sun
import myweatherforecast.composeapp.generated.resources.day_thu
import myweatherforecast.composeapp.generated.resources.day_today
import myweatherforecast.composeapp.generated.resources.day_tue
import myweatherforecast.composeapp.generated.resources.day_wed
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DailyRow(daily: DailyForecast, today: LocalDate, units: Units, modifier: Modifier = Modifier) {
    val dayLabel = daily.date.dayLabel(today)
    val conditionName = daily.condition.icon.readableName()
    val windUnitLabel = stringResource(units.windSpeedUnitLabelRes())
    val tempMax = daily.tempMax.roundToInt()
    val tempMin = daily.tempMin.roundToInt()
    val popPercent = (daily.pop * 100).roundToInt()
    val windSpeed = daily.windSpeed.roundToInt()
    val accessibilityDescription = stringResource(
        Res.string.daily_accessibility,
        dayLabel, conditionName, tempMax, tempMin, popPercent, windSpeed, windUnitLabel, daily.humidity,
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .semantics(mergeDescendants = true) { contentDescription = accessibilityDescription },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = dayLabel,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.widthIn(min = 48.dp),
            )
            Icon(
                painter = painterResource(daily.condition.icon.toDrawableResource()),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
            )
            Text(
                text = stringResource(Res.string.daily_temp_range, tempMax, tempMin),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Text(
            text = stringResource(Res.string.daily_summary_line, popPercent, windSpeed, windUnitLabel, daily.humidity),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 60.dp),
        )
    }
}

@Composable
private fun LocalDate.dayLabel(today: LocalDate): String =
    if (this == today) stringResource(Res.string.day_today) else stringResource(dayOfWeek.shortLabelRes())

private fun DayOfWeek.shortLabelRes(): StringResource = when (this) {
    DayOfWeek.MONDAY -> Res.string.day_mon
    DayOfWeek.TUESDAY -> Res.string.day_tue
    DayOfWeek.WEDNESDAY -> Res.string.day_wed
    DayOfWeek.THURSDAY -> Res.string.day_thu
    DayOfWeek.FRIDAY -> Res.string.day_fri
    DayOfWeek.SATURDAY -> Res.string.day_sat
    DayOfWeek.SUNDAY -> Res.string.day_sun
}
