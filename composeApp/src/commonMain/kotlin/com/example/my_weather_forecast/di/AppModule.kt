package com.example.my_weather_forecast.di

import com.example.my_weather_forecast.BuildKonfig
import com.example.my_weather_forecast.core.dispatcher.DefaultDispatcherProvider
import com.example.my_weather_forecast.core.dispatcher.DispatcherProvider
import com.example.my_weather_forecast.core.preference.SettingsThemePreference
import com.example.my_weather_forecast.core.preference.SettingsUnitsPreference
import com.example.my_weather_forecast.core.preference.ThemePreference
import com.example.my_weather_forecast.core.preference.UnitsPreference
import com.example.my_weather_forecast.core.time.SystemTimeProvider
import com.example.my_weather_forecast.core.time.TimeProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

val OWM_API_KEY_QUALIFIER = named("owmApiKey")

val appModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single<TimeProvider> { SystemTimeProvider() }
    single<UnitsPreference> { SettingsUnitsPreference(get()) }
    single<ThemePreference> { SettingsThemePreference(get()) }
    single(OWM_API_KEY_QUALIFIER) { BuildKonfig.OWM_API_KEY }
}
