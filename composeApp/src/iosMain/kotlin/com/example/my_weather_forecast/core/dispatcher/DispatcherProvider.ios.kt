package com.example.my_weather_forecast.core.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
internal actual val ioDispatcher: CoroutineDispatcher =
    Dispatchers.Default.limitedParallelism(64)
