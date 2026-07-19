package com.example.my_weather_forecast.testutil

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * JUnit4's MainDispatcherRule isn't multiplatform, so ViewModel tests use this instead:
 * sets Dispatchers.Main to a StandardTestDispatcher for the test body, then resets it.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun runMainDispatcherTest(testBody: suspend TestScope.() -> Unit) = runTest {
    Dispatchers.setMain(StandardTestDispatcher(testScheduler))
    try {
        testBody()
    } finally {
        Dispatchers.resetMain()
    }
}
