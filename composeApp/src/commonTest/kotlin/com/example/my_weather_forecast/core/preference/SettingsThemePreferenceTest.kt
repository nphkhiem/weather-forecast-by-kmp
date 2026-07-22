package com.example.my_weather_forecast.core.preference

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsThemePreferenceTest {

    @Test
    fun givenNothingStored_whenRead_thenDefaultsToSystem() {
        val preference = SettingsThemePreference(MapSettings())

        assertEquals(ThemeMode.SYSTEM, preference.mode.value)
    }

    @Test
    fun givenModeSet_whenRead_thenReflectsTheNewValue() {
        val preference = SettingsThemePreference(MapSettings())

        preference.setMode(ThemeMode.DARK)

        assertEquals(ThemeMode.DARK, preference.mode.value)
    }

    @Test
    fun givenModePersisted_whenANewInstanceReadsTheSameSettings_thenPreferenceSurvives() {
        val settings = MapSettings()
        SettingsThemePreference(settings).setMode(ThemeMode.LIGHT)

        val reloaded = SettingsThemePreference(settings)

        assertEquals(ThemeMode.LIGHT, reloaded.mode.value)
    }
}
