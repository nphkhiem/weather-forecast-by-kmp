package com.example.my_weather_forecast.core.preference

import kotlin.test.Test
import kotlin.test.assertEquals

class ThemeModeTest {

    @Test
    fun givenAnExplicitOverride_whenTheSystemThemeActuallyChanges_thenItResetsToSystem() {
        val next = nextThemeMode(current = ThemeMode.DARK, systemThemeChanged = true)

        assertEquals(ThemeMode.SYSTEM, next)
    }

    @Test
    fun givenAlreadyFollowingSystem_whenTheSystemThemeChanges_thenItStaysOnSystem() {
        val next = nextThemeMode(current = ThemeMode.SYSTEM, systemThemeChanged = true)

        assertEquals(ThemeMode.SYSTEM, next)
    }

    @Test
    fun givenAnExplicitOverride_whenTheSystemThemeHasNotChanged_thenTheOverrideIsPreserved() {
        val next = nextThemeMode(current = ThemeMode.LIGHT, systemThemeChanged = false)

        assertEquals(ThemeMode.LIGHT, next)
    }
}
