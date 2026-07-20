package com.example.my_weather_forecast.data.local

import com.example.my_weather_forecast.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface SavedLocationLocalDataSource {
    fun observeAll(): Flow<List<Location>>
    suspend fun insert(location: Location)
    suspend fun deleteById(id: Long)
}
