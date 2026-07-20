package com.example.my_weather_forecast.data.local

import com.example.my_weather_forecast.domain.model.Forecast
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun observe(locationId: Long): Flow<Forecast?>
    suspend fun upsert(locationId: Long, forecast: Forecast)
    suspend fun deleteByLocationId(locationId: Long)
}
