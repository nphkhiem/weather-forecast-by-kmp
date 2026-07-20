package com.example.my_weather_forecast

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.my_weather_forecast.presentation.navigation.WeatherNavHost
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme

@Composable
fun App() {
    WeatherForecastTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            WeatherNavHost()
        }
    }
}
