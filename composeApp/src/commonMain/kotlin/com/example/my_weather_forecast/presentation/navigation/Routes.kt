package com.example.my_weather_forecast.presentation.navigation

object Routes {
    const val OVERVIEW = "overview"
    const val SEARCH = "search"
    const val DETAIL = "detail/{locationId}"

    fun detail(locationId: Long) = "detail/$locationId"
}
