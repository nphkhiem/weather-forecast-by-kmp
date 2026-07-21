package com.example.my_weather_forecast.core.preference

import com.example.my_weather_forecast.domain.model.Units
import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsUnitsPreferenceTest {

    @Test
    fun givenNothingStored_whenRead_thenDefaultsToMetric() {
        val preference = SettingsUnitsPreference(MapSettings())

        assertEquals(Units.METRIC, preference.units.value)
    }

    @Test
    fun givenUnitsSet_whenRead_thenReflectsTheNewValue() {
        val preference = SettingsUnitsPreference(MapSettings())

        preference.setUnits(Units.IMPERIAL)

        assertEquals(Units.IMPERIAL, preference.units.value)
    }

    @Test
    fun givenUnitsPersisted_whenANewInstanceReadsTheSameSettings_thenPreferenceSurvives() {
        val settings = MapSettings()
        SettingsUnitsPreference(settings).setUnits(Units.IMPERIAL)

        val reloaded = SettingsUnitsPreference(settings)

        assertEquals(Units.IMPERIAL, reloaded.units.value)
    }
}
