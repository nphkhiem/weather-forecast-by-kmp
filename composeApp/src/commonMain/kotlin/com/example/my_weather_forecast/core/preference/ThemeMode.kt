package com.example.my_weather_forecast.core.preference

/** Persisted theme preference. [SYSTEM] (the default) always follows the OS setting. */
enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK,
}

/**
 * The OS setting always wins: an explicit [ThemeMode.LIGHT]/[ThemeMode.DARK] override only lasts
 * until the system theme actually changes, at which point it resets to [ThemeMode.SYSTEM].
 */
internal fun nextThemeMode(current: ThemeMode, systemThemeChanged: Boolean): ThemeMode =
    if (systemThemeChanged) ThemeMode.SYSTEM else current
