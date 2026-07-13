package com.example.my_weather_forecast.di

import org.koin.core.module.Module

/**
 * Platform-specific Koin bindings (e.g. the SQLDelight driver in Phase 2). Empty today;
 * androidContext is registered separately via initKoin's appDeclaration, not here.
 */
expect val platformModule: Module
