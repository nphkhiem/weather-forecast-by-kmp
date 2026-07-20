package com.example.my_weather_forecast.domain.model

import kotlinx.datetime.Instant

data class Forecast(
    val location: Location,
    val current: CurrentConditions,
    /** Index 0 = today, 1..7 = the next 7 days; ordering is guaranteed by the mapper that builds it. */
    val daily: List<DailyForecast>,
    /** The next ~48 hours, used by the Detail screen's hourly rain strip. */
    val hourly: List<HourlyForecast>,
    val units: Units,
    val fetchedAt: Instant,
)
