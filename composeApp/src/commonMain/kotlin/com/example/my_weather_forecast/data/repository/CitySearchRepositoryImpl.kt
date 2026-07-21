package com.example.my_weather_forecast.data.repository

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.data.remote.WeatherRemoteDataSource
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.repository.CitySearchRepository

class CitySearchRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource,
) : CitySearchRepository {
    override suspend fun searchCity(query: String): AppResult<List<Location>> = remoteDataSource.searchCity(query)
}
