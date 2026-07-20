package com.example.my_weather_forecast.domain.repository

import com.example.my_weather_forecast.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface SavedLocationRepository {
    fun observeAll(): Flow<List<Location>>
    suspend fun add(location: Location)
    suspend fun remove(id: Long)
}
