package com.example.my_weather_forecast.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Starts Koin with the shared [appModule]. Platform callers may pass [appDeclaration] to add
 * platform configuration (e.g. `androidContext`) or extra modules as the app grows.
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication =
    startKoin {
        appDeclaration()
        modules(appModule)
    }
