package com.example.my_weather_forecast.domain.model

import kotlinx.serialization.Serializable

/** Semantic condition category, mirroring OWM's condition-code groups (2xx..800..804). */
@Serializable
enum class WeatherIcon {
    THUNDERSTORM,
    DRIZZLE,
    RAIN,
    SNOW,
    ATMOSPHERE,
    CLEAR,
    CLOUDS,
    UNKNOWN,
}
