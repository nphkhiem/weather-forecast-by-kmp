package com.example.my_weather_forecast.presentation.detail

import com.example.my_weather_forecast.domain.model.Units

internal fun Units.windSpeedUnitLabel(): String = when (this) {
    Units.METRIC -> "m/s"
    Units.IMPERIAL -> "mph"
}
