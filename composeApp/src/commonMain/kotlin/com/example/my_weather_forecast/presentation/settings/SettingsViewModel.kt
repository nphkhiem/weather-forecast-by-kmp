package com.example.my_weather_forecast.presentation.settings

import androidx.lifecycle.ViewModel
import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.core.preference.ThemePreference
import com.example.my_weather_forecast.core.preference.UnitsPreference
import com.example.my_weather_forecast.domain.model.Units
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val unitsPreference: UnitsPreference,
    private val themePreference: ThemePreference,
) : ViewModel() {

    val units: StateFlow<Units> = unitsPreference.units
    val themeMode: StateFlow<ThemeMode> = themePreference.mode

    fun setUnits(units: Units) {
        unitsPreference.setUnits(units)
    }

    fun setThemeMode(mode: ThemeMode) {
        themePreference.setMode(mode)
    }
}
