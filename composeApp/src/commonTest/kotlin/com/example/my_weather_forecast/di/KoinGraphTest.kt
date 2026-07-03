package com.example.my_weather_forecast.di

import com.example.my_weather_forecast.core.dispatcher.DispatcherProvider
import com.example.my_weather_forecast.core.time.TimeProvider
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.koin.core.context.stopKoin

class KoinGraphTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun givenInitKoin_whenResolvingCoreDependencies_thenTheyAreProvided() {
        val koin = initKoin().koin

        assertNotNull(koin.get<DispatcherProvider>())
        assertNotNull(koin.get<TimeProvider>())
    }
}
