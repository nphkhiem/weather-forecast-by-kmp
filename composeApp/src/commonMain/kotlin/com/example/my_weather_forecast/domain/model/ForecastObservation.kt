package com.example.my_weather_forecast.domain.model

import com.example.my_weather_forecast.core.result.WeatherError

sealed interface ForecastObservation {
    data object Loading : ForecastObservation

    /** [error] is set when the most recent refresh attempt failed; the cache is still served. */
    data class Success(val forecast: Forecast, val stale: Boolean, val error: WeatherError? = null) : ForecastObservation
}
