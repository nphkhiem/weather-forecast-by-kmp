package com.example.my_weather_forecast.testutil

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.data.remote.WeatherRemoteDataSource
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units

class FakeWeatherRemoteDataSource(
    private val fetchForecastResult: (Location, Units) -> AppResult<Forecast>,
) : WeatherRemoteDataSource {
    var fetchForecastCallCount = 0
        private set

    override suspend fun fetchForecast(location: Location, units: Units): AppResult<Forecast> {
        fetchForecastCallCount++
        return fetchForecastResult(location, units)
    }

    override suspend fun searchCity(query: String): AppResult<List<Location>> {
        error("not used by these tests")
    }
}
