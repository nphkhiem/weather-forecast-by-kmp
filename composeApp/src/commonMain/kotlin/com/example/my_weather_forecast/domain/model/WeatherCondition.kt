package com.example.my_weather_forecast.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherCondition(
    val owmCode: Int,
    val group: String,
    val description: String,
    val icon: WeatherIcon,
    val isDaytime: Boolean,
)
