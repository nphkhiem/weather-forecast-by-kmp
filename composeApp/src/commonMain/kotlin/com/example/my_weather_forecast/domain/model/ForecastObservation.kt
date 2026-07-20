package com.example.my_weather_forecast.domain.model

sealed interface ForecastObservation {
    data object Loading : ForecastObservation
    data class Success(val forecast: Forecast, val stale: Boolean) : ForecastObservation
}
