package com.example.my_weather_forecast.presentation.detail

import com.example.my_weather_forecast.domain.model.HourlyForecast
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Keeps only the hours that fall on [today] in [zone], so the hourly strip doesn't silently
 * spill into tomorrow's hours from the ~48-hour fetch window.
 */
internal fun List<HourlyForecast>.todayOnly(
    today: LocalDate,
    zone: TimeZone = TimeZone.currentSystemDefault(),
): List<HourlyForecast> = filter { it.time.toLocalDateTime(zone).date == today }
