package com.example.my_weather_forecast.core.time

import kotlin.test.Test
import kotlin.test.assertTrue

class SystemTimeProviderTest {

    @Test
    fun givenSystemTimeProvider_whenNowEpochMillis_thenReturnsPositiveEpochValue() {
        val provider = SystemTimeProvider()

        assertTrue(provider.nowEpochMillis() > 0L)
    }
}
