package com.example.my_weather_forecast.presentation.theme

import androidx.compose.ui.graphics.Color
import com.example.my_weather_forecast.domain.model.WeatherCondition
import com.example.my_weather_forecast.domain.model.WeatherIcon

/**
 * A soft, two-stop gradient for a weather condition, plus a paired content color for legibility.
 * Gradients are intentionally close in lightness ("Muted Pastel" direction) rather than bold.
 */
data class ConditionPalette(
    val gradientStart: Color,
    val gradientEnd: Color,
    val onGradient: Color,
)

private val InkLight = Color(0xFF20242B)
private val InkDark = Color(0xFFEDEAF2)

// Daytime: each condition gets its own soft, distinct hue.
private val ClearDay = ConditionPalette(Color(0xFFFFE3C2), Color(0xFFEAF4FF), InkLight)
private val CloudsDay = ConditionPalette(Color(0xFFDCE3E8), Color(0xFFEFF2F4), InkLight)
private val RainDay = ConditionPalette(Color(0xFFCFE4E6), Color(0xFFE9F2F1), InkLight)
private val DrizzleDay = ConditionPalette(Color(0xFFD8E8EA), Color(0xFFEEF5F5), InkLight)
private val ThunderstormDay = ConditionPalette(Color(0xFFD9D3E3), Color(0xFFEDEAF2), InkLight)
private val SnowDay = ConditionPalette(Color(0xFFE4EEF5), Color(0xFFF5FAFD), InkLight)
private val AtmosphereDay = ConditionPalette(Color(0xFFE2E2DE), Color(0xFFF1F1EE), InkLight)
private val UnknownDay = ConditionPalette(Color(0xFFE7E7E5), Color(0xFFF4F4F2), InkLight)

// Nighttime: collapsed into three calmer buckets rather than one per condition.
private val ClearNight = ConditionPalette(Color(0xFF2E3348), Color(0xFF48445F), InkDark)
private val StormyNight = ConditionPalette(Color(0xFF242C38), Color(0xFF3A4652), InkDark)
private val QuietNight = ConditionPalette(Color(0xFF2A2E36), Color(0xFF40454E), InkDark)

// Per-condition icon accent colors, matching the approved "Muted Pastel" draft direction.
private val ClearAccentDay = Color(0xFFE08A3E)
private val CloudsAccentDay = Color(0xFF6C7A85)
private val RainAccentDay = Color(0xFF4C8A8F)
private val DrizzleAccentDay = Color(0xFF5FA0A4)
private val ThunderstormAccentDay = Color(0xFF6E5E96)
private val SnowAccentDay = Color(0xFF6FA3C9)
private val AtmosphereAccentDay = Color(0xFF8C8C7E)
private val UnknownAccentDay = Color(0xFF9A9A94)

private val ClearAccentNight = Color(0xFFEDEBF3)
private val CloudsAccentNight = Color(0xFFB7BCC7)
private val StormyAccentNight = Color(0xFF9FB9BD)
private val QuietAccentNight = Color(0xFFA9ADB6)

fun WeatherIcon.accentColor(isDaytime: Boolean): Color = if (isDaytime) {
    when (this) {
        WeatherIcon.CLEAR -> ClearAccentDay
        WeatherIcon.CLOUDS -> CloudsAccentDay
        WeatherIcon.RAIN -> RainAccentDay
        WeatherIcon.DRIZZLE -> DrizzleAccentDay
        WeatherIcon.THUNDERSTORM -> ThunderstormAccentDay
        WeatherIcon.SNOW -> SnowAccentDay
        WeatherIcon.ATMOSPHERE -> AtmosphereAccentDay
        WeatherIcon.UNKNOWN -> UnknownAccentDay
    }
} else {
    when (this) {
        WeatherIcon.CLEAR -> ClearAccentNight
        WeatherIcon.CLOUDS -> CloudsAccentNight
        WeatherIcon.RAIN, WeatherIcon.DRIZZLE, WeatherIcon.THUNDERSTORM -> StormyAccentNight
        WeatherIcon.SNOW, WeatherIcon.ATMOSPHERE, WeatherIcon.UNKNOWN -> QuietAccentNight
    }
}

fun WeatherCondition.palette(): ConditionPalette = icon.conditionPalette(isDaytime)

fun WeatherIcon.conditionPalette(isDaytime: Boolean): ConditionPalette = if (isDaytime) {
    when (this) {
        WeatherIcon.CLEAR -> ClearDay
        WeatherIcon.CLOUDS -> CloudsDay
        WeatherIcon.RAIN -> RainDay
        WeatherIcon.DRIZZLE -> DrizzleDay
        WeatherIcon.THUNDERSTORM -> ThunderstormDay
        WeatherIcon.SNOW -> SnowDay
        WeatherIcon.ATMOSPHERE -> AtmosphereDay
        WeatherIcon.UNKNOWN -> UnknownDay
    }
} else {
    when (this) {
        WeatherIcon.CLEAR, WeatherIcon.CLOUDS -> ClearNight
        WeatherIcon.RAIN, WeatherIcon.DRIZZLE, WeatherIcon.THUNDERSTORM -> StormyNight
        WeatherIcon.SNOW, WeatherIcon.ATMOSPHERE, WeatherIcon.UNKNOWN -> QuietNight
    }
}
