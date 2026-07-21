package com.example.my_weather_forecast.testutil

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.repository.CitySearchRepository

class FakeCitySearchRepository : CitySearchRepository {
    var result: AppResult<List<Location>> = AppResult.Success(emptyList())
    var searchCallCount = 0
        private set

    override suspend fun searchCity(query: String): AppResult<List<Location>> {
        searchCallCount++
        return result
    }
}
