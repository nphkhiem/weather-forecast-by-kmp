package com.example.my_weather_forecast.core.preference

import com.example.my_weather_forecast.domain.model.Units
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsUnitsPreference(private val settings: Settings) : UnitsPreference {
    private val _units = MutableStateFlow(readUnits())
    override val units: StateFlow<Units> = _units.asStateFlow()

    override fun setUnits(units: Units) {
        settings.putString(KEY, units.name)
        _units.value = units
    }

    private fun readUnits(): Units {
        val stored = settings.getStringOrNull(KEY) ?: return Units.METRIC
        return Units.entries.find { it.name == stored } ?: Units.METRIC
    }

    private companion object {
        const val KEY = "units"
    }
}
