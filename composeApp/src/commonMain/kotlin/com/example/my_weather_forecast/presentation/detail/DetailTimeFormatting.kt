package com.example.my_weather_forecast.presentation.detail

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal fun Instant.toClockLabel(): String {
    val time = toLocalDateTime(TimeZone.currentSystemDefault()).time
    return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}

internal fun Instant.toHourLabel(): String {
    val hour = toLocalDateTime(TimeZone.currentSystemDefault()).time.hour
    return "${hour.toString().padStart(2, '0')}:00"
}
