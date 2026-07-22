package com.example.my_weather_forecast.presentation.theme

import androidx.compose.ui.graphics.luminance
import com.example.my_weather_forecast.domain.model.WeatherIcon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConditionPaletteTest {

    @Test
    fun givenEveryDaytimeCondition_whenMapped_thenEachHasADistinctGradientStart() {
        val starts = WeatherIcon.entries.map { it.conditionPalette(isDaytime = true).gradientStart }

        assertEquals(WeatherIcon.entries.size, starts.toSet().size)
    }

    @Test
    fun givenConditionsThatCommonlyOccurTogetherAtNight_whenMapped_thenTheyShareACalmerCollapsedPalette() {
        val rain = WeatherIcon.RAIN.conditionPalette(isDaytime = false)
        val drizzle = WeatherIcon.DRIZZLE.conditionPalette(isDaytime = false)
        val thunderstorm = WeatherIcon.THUNDERSTORM.conditionPalette(isDaytime = false)

        assertEquals(rain, drizzle)
        assertEquals(rain, thunderstorm)
    }

    @Test
    fun givenClearAndCloudsAtNight_whenMapped_thenTheyShareTheSameNightPalette() {
        assertEquals(
            WeatherIcon.CLEAR.conditionPalette(isDaytime = false),
            WeatherIcon.CLOUDS.conditionPalette(isDaytime = false),
        )
    }

    @Test
    fun givenADaytimePalette_whenReadingItsOnGradientColor_thenItIsDarkForLegibilityOnALightGradient() {
        val palette = WeatherIcon.CLEAR.conditionPalette(isDaytime = true)

        assertTrue(palette.onGradient.luminance() < 0.5f)
    }

    @Test
    fun givenANighttimePalette_whenReadingItsOnGradientColor_thenItIsLightForLegibilityOnADarkGradient() {
        val palette = WeatherIcon.CLEAR.conditionPalette(isDaytime = false)

        assertTrue(palette.onGradient.luminance() > 0.5f)
    }

    @Test
    fun givenTheSamePaletteRequestedTwice_whenCompared_thenGradientStaysWithinASoftLightnessRange() {
        val palette = WeatherIcon.RAIN.conditionPalette(isDaytime = true)

        val startLuminance = palette.gradientStart.luminance()
        val endLuminance = palette.gradientEnd.luminance()
        assertTrue((endLuminance - startLuminance) < 0.2f, "gradient should be soft, not a dramatic jump")
    }

    @Test
    fun givenEveryDaytimeCondition_whenReadingAccentColor_thenEachHasADistinctColor() {
        val accents = WeatherIcon.entries.map { it.accentColor(isDaytime = true) }

        assertEquals(WeatherIcon.entries.size, accents.toSet().size)
    }

    @Test
    fun givenClearDaytime_whenReadingAccentColor_thenItDiffersFromItsOwnNighttimeAccent() {
        val day = WeatherIcon.CLEAR.accentColor(isDaytime = true)
        val night = WeatherIcon.CLEAR.accentColor(isDaytime = false)

        assertTrue(day != night, "day and night accents for the same condition should differ")
    }
}
