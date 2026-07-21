package com.example.my_weather_forecast.core.preference

import com.example.my_weather_forecast.domain.model.Units
import kotlinx.coroutines.flow.StateFlow

/** Persists the user's metric/imperial choice; injected so ViewModels can react to changes live. */
interface UnitsPreference {
    val units: StateFlow<Units>
    fun setUnits(units: Units)
}
