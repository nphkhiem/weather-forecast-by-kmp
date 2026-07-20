package com.example.my_weather_forecast.presentation.overview

import com.example.my_weather_forecast.core.result.WeatherError

sealed interface OverviewUiState {
    data object Loading : OverviewUiState
    data object Empty : OverviewUiState
    data class Success(val areas: List<AreaSummary>) : OverviewUiState
    data class Error(val error: WeatherError) : OverviewUiState
}
