package com.example.my_weather_forecast.testutil

import app.cash.turbine.test
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlin.test.assertEquals

class TurbineHarnessTest {

    @Test
    fun givenMutableStateFlow_whenValuesAreEmitted_thenTurbineObservesEachInOrder() = runMainDispatcherTest {
        val state = MutableStateFlow(0)

        state.test {
            assertEquals(0, awaitItem())

            state.value = 1

            assertEquals(1, awaitItem())
        }
    }
}
