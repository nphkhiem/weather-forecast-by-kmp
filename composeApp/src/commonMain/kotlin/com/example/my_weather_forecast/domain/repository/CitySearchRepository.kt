package com.example.my_weather_forecast.domain.repository

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.domain.model.Location

interface CitySearchRepository {
    suspend fun searchCity(query: String): AppResult<List<Location>>
}
