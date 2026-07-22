package com.example.my_weather_forecast.testutil

import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.core.preference.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeThemePreference(initial: ThemeMode = ThemeMode.SYSTEM) : ThemePreference {
    private val _mode = MutableStateFlow(initial)
    override val mode: StateFlow<ThemeMode> = _mode.asStateFlow()

    override fun setMode(mode: ThemeMode) {
        _mode.value = mode
    }
}
