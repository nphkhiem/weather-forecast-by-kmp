package com.example.my_weather_forecast.presentation.search

sealed interface SearchEvent {
    data object Added : SearchEvent
    data object AtLimit : SearchEvent
    data class ShowMessage(val message: String) : SearchEvent
}
