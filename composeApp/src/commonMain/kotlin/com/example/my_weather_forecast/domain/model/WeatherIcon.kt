package com.example.my_weather_forecast.domain.model

/** Semantic condition category, mirroring OWM's condition-code groups (2xx..800..804). */
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
