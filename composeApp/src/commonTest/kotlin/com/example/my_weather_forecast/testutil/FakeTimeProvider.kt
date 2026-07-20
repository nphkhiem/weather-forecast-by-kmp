package com.example.my_weather_forecast.testutil

import com.example.my_weather_forecast.core.time.TimeProvider

class FakeTimeProvider(var currentEpochMillis: Long = 0L) : TimeProvider {
    override fun nowEpochMillis(): Long = currentEpochMillis
}
