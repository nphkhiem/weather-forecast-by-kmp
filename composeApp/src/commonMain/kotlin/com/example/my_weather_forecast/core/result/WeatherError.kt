package com.example.my_weather_forecast.core.result

sealed interface WeatherError {
    /** No connectivity, or the request timed out. */
    data object Network : WeatherError

    /** HTTP 429 — the OWM call budget was hit. */
    data object RateLimited : WeatherError

    /** HTTP 401 — the API key is missing or lacks the One Call 3.0 subscription. */
    data object Unauthorized : WeatherError

    /** Geocoding returned no results, or the resource otherwise 404s. */
    data object NotFound : WeatherError

    /** Already at the max of 6 saved areas. */
    data object AtLimit : WeatherError

    /** A candidate location is within ~0.01° of an already-saved area. */
    data object AlreadySaved : WeatherError

    data class Unknown(val cause: String) : WeatherError
}
