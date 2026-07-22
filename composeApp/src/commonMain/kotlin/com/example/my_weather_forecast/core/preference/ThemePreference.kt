package com.example.my_weather_forecast.core.preference

import kotlinx.coroutines.flow.StateFlow

interface ThemePreference {
    val mode: StateFlow<ThemeMode>
    fun setMode(mode: ThemeMode)
}
