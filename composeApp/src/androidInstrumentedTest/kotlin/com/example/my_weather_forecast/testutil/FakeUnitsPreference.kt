package com.example.my_weather_forecast.testutil

import com.example.my_weather_forecast.core.preference.UnitsPreference
import com.example.my_weather_forecast.domain.model.Units
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUnitsPreference(initial: Units = Units.METRIC) : UnitsPreference {
    private val _units = MutableStateFlow(initial)
    override val units: StateFlow<Units> = _units.asStateFlow()

    override fun setUnits(units: Units) {
        _units.value = units
    }
}
