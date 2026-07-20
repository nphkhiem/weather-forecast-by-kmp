package com.example.my_weather_forecast.presentation.theme

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.my_weather_forecast.domain.model.WeatherIcon
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
private fun WeatherIconRow(modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(8.dp)) {
        WeatherIcon.entries.forEach { icon ->
            Icon(
                painter = painterResource(icon.toDrawableResource()),
                contentDescription = icon.name,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(4.dp),
            )
        }
    }
}

@Preview
@Composable
private fun WeatherIconRowLightPreview() {
    WeatherForecastTheme(darkTheme = false) {
        Surface { WeatherIconRow() }
    }
}

@Preview
@Composable
private fun WeatherIconRowDarkPreview() {
    WeatherForecastTheme(darkTheme = true) {
        Surface { WeatherIconRow() }
    }
}
