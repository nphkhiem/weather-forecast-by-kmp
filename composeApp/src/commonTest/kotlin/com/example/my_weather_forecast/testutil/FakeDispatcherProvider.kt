package com.example.my_weather_forecast.testutil

import com.example.my_weather_forecast.core.dispatcher.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher

class FakeDispatcherProvider(dispatcher: CoroutineDispatcher) : DispatcherProvider {
    override val default: CoroutineDispatcher = dispatcher
    override val io: CoroutineDispatcher = dispatcher
}
