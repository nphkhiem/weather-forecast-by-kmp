package com.example.my_weather_forecast.core.preference

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsThemePreference(private val settings: Settings) : ThemePreference {
    private val _mode = MutableStateFlow(readMode())
    override val mode: StateFlow<ThemeMode> = _mode.asStateFlow()

    override fun setMode(mode: ThemeMode) {
        settings.putString(KEY, mode.name)
        _mode.value = mode
    }

    private fun readMode(): ThemeMode {
        val stored = settings.getStringOrNull(KEY) ?: return ThemeMode.SYSTEM
        return ThemeMode.entries.find { it.name == stored } ?: ThemeMode.SYSTEM
    }

    private companion object {
        const val KEY = "theme_mode"
    }
}
