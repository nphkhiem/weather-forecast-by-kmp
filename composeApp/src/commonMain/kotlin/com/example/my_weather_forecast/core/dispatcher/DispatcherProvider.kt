package com.example.my_weather_forecast.core.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Abstraction over coroutine dispatchers. The IO dispatcher is provided per platform
 * because `Dispatchers.IO` does not exist in commonMain, and injecting this lets tests
 * substitute a single test dispatcher.
 */
interface DispatcherProvider {
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}

class DefaultDispatcherProvider : DispatcherProvider {
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = ioDispatcher
}

/** Platform IO dispatcher: `Dispatchers.IO` on Android, a bounded `Default` on iOS. */
internal expect val ioDispatcher: CoroutineDispatcher
