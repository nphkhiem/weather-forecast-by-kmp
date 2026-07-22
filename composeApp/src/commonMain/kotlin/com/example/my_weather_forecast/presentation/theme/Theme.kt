package com.example.my_weather_forecast.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.my_weather_forecast.core.preference.ThemeMode
import com.example.my_weather_forecast.core.preference.ThemePreference
import com.example.my_weather_forecast.core.preference.nextThemeMode

private val SkyBlue = Color(0xFF0061A4)
private val SkyBlueLight = Color(0xFFD1E4FF)
private val SkyBlueDark = Color(0xFF9ECAFF)
private val Amber = Color(0xFF7C5800)
private val AmberLight = Color(0xFFFFDDAE)
private val AmberDark = Color(0xFFF5BD6F)
private val ErrorRed = Color(0xFFBA1A1A)
private val ErrorRedDark = Color(0xFFFFB4AB)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,
    primaryContainer = SkyBlueLight,
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Amber,
    onSecondary = Color.White,
    secondaryContainer = AmberLight,
    onSecondaryContainer = Color(0xFF271900),
    error = ErrorRed,
    onError = Color.White,
    background = Color(0xFFFDFCFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDFCFF),
    onSurface = Color(0xFF1A1C1E),
)

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueDark,
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = SkyBlueLight,
    secondary = AmberDark,
    onSecondary = Color(0xFF422C00),
    secondaryContainer = Color(0xFF5E4200),
    onSecondaryContainer = AmberLight,
    error = ErrorRedDark,
    onError = Color(0xFF690005),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
)

@Composable
fun WeatherForecastTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content,
    )
}

/**
 * Resolves [ThemeMode] against the live system setting. An explicit Light/Dark override is only
 * good until the OS theme actually changes, at which point it's cleared back to System (see
 * [nextThemeMode]) — the OS setting always wins over a stale in-app choice.
 */
@Composable
fun rememberEffectiveDarkTheme(themePreference: ThemePreference): Boolean {
    val systemDark = isSystemInDarkTheme()
    val mode by themePreference.mode.collectAsStateWithLifecycle()
    var previousSystemDark by remember { mutableStateOf(systemDark) }

    LaunchedEffect(systemDark) {
        val systemThemeChanged = systemDark != previousSystemDark
        if (systemThemeChanged) {
            val next = nextThemeMode(mode, systemThemeChanged = true)
            if (next != mode) themePreference.setMode(next)
        }
        previousSystemDark = systemDark
    }

    return when (mode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemDark
    }
}
