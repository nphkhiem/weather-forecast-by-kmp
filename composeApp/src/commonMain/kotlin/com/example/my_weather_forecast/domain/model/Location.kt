package com.example.my_weather_forecast.domain.model

data class Location(
    val id: Long,
    val name: String,
    val country: String,
    val state: String?,
    val lat: Double,
    val lon: Double,
    val sortOrder: Int,
)
