package com.example.my_weather_forecast.domain.model

data class WeatherCondition(
    val owmCode: Int,
    val group: String,
    val description: String,
    val icon: WeatherIcon,
)
