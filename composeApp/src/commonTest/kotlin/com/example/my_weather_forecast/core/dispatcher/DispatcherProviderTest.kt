package com.example.my_weather_forecast.core.dispatcher

import kotlin.test.Test
import kotlin.test.assertNotNull

class DispatcherProviderTest {

    @Test
    fun givenDefaultDispatcherProvider_whenRead_thenIoAndDefaultAreProvided() {
        val provider = DefaultDispatcherProvider()

        assertNotNull(provider.io)
        assertNotNull(provider.default)
    }
}
