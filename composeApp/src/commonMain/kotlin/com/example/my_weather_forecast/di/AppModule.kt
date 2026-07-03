package com.example.my_weather_forecast.di

import com.example.my_weather_forecast.core.dispatcher.DefaultDispatcherProvider
import com.example.my_weather_forecast.core.dispatcher.DispatcherProvider
import com.example.my_weather_forecast.core.time.SystemTimeProvider
import com.example.my_weather_forecast.core.time.TimeProvider
import org.koin.dsl.module

val appModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<TimeProvider> { SystemTimeProvider() }
}
