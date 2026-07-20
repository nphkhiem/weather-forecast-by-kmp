package com.example.my_weather_forecast.presentation.overview

sealed interface OverviewEvent {
    data class OpenDetail(val locationId: Long) : OverviewEvent
    data class ShowMessage(val message: String) : OverviewEvent
}
