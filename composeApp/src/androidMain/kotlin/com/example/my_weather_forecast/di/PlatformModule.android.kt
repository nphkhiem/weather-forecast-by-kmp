package com.example.my_weather_forecast.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.my_weather_forecast.data.local.WeatherDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> { AndroidSqliteDriver(WeatherDatabase.Schema, androidContext(), "weather.db") }
}
