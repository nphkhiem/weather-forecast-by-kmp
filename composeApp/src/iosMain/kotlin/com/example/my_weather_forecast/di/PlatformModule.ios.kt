package com.example.my_weather_forecast.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.my_weather_forecast.data.local.WeatherDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> { NativeSqliteDriver(WeatherDatabase.Schema, "weather.db") }
}
