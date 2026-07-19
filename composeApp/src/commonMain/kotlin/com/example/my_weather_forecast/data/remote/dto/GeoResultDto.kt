package com.example.my_weather_forecast.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GeoResultDto(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String,
    val state: String? = null,
)
