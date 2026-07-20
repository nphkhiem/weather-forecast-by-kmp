package com.example.my_weather_forecast.data.repository

import com.example.my_weather_forecast.data.local.SavedLocationLocalDataSource
import com.example.my_weather_forecast.data.local.WeatherLocalDataSource
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.repository.SavedLocationRepository
import kotlinx.coroutines.flow.Flow

class SavedLocationRepositoryImpl(
    private val savedLocationLocalDataSource: SavedLocationLocalDataSource,
    private val weatherLocalDataSource: WeatherLocalDataSource,
) : SavedLocationRepository {

    override fun observeAll(): Flow<List<Location>> = savedLocationLocalDataSource.observeAll()

    override suspend fun add(location: Location) {
        savedLocationLocalDataSource.insert(location)
    }

    override suspend fun remove(id: Long) {
        savedLocationLocalDataSource.deleteById(id)
        weatherLocalDataSource.deleteByLocationId(id)
    }
}
