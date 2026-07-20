package com.example.my_weather_forecast.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class DailyForecast(
    val date: LocalDate,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val windSpeed: Double,
    /** Probability of precipitation, 0.0..1.0. */
    val pop: Double,
    val condition: WeatherCondition,
)
