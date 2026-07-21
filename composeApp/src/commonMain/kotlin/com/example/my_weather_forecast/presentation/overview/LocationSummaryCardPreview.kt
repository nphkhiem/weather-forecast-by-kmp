package com.example.my_weather_forecast.presentation.overview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

private val previewArea = AreaSummary(
    id = 1,
    name = "Chicago",
    currentTemp = 21.0,
    icon = WeatherIcon.RAIN,
    isDaytime = true,
    todayHigh = 24.0,
    todayLow = 15.0,
    rainChance = 0.6,
    stale = false,
)

@Preview
@Composable
private fun LocationSummaryCardLightPreview() {
    WeatherForecastTheme(darkTheme = false) {
        Surface {
            LocationSummaryCard(area = previewArea, onClick = {}, onRemove = {}, modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview
@Composable
private fun LocationSummaryCardDarkPreview() {
    WeatherForecastTheme(darkTheme = true) {
        Surface {
            LocationSummaryCard(area = previewArea, onClick = {}, onRemove = {}, modifier = Modifier.padding(16.dp))
        }
    }
}
