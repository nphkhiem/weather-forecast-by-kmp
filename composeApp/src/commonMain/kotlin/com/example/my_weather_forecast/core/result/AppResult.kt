package com.example.my_weather_forecast.core.result

/** Outcome of a one-shot operation, carrying a typed [WeatherError] on failure instead of a Throwable. */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: WeatherError) : AppResult<Nothing>
}
