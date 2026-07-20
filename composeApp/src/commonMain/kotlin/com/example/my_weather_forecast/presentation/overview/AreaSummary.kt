package com.example.my_weather_forecast.presentation.overview

import com.example.my_weather_forecast.domain.model.WeatherIcon

data class AreaSummary(
    val id: Long,
    val name: String,
    val currentTemp: Double,
    val icon: WeatherIcon,
    val todayHigh: Double,
    val todayLow: Double,
    val rainChance: Double,
    val stale: Boolean,
)
