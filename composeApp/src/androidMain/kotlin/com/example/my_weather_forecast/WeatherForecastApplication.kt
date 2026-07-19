package com.example.my_weather_forecast

import android.app.Application
import com.example.my_weather_forecast.di.initKoin
import org.koin.android.ext.koin.androidContext

class WeatherForecastApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@WeatherForecastApplication)
        }
    }
}
