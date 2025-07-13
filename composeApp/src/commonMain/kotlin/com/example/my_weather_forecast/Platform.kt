package com.example.my_weather_forecast

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform