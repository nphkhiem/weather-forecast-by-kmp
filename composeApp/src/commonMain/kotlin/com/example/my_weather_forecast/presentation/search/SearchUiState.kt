package com.example.my_weather_forecast.presentation.search

import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Location

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Results(val locations: List<Location>) : SearchUiState
    data object Empty : SearchUiState
    data class Error(val error: WeatherError) : SearchUiState
}
