package com.example.my_weather_forecast.testutil

import com.example.my_weather_forecast.data.local.WeatherLocalDataSource
import com.example.my_weather_forecast.domain.model.Forecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeWeatherLocalDataSource : WeatherLocalDataSource {
    private val cache = mutableMapOf<Long, MutableStateFlow<Forecast?>>()

    private fun flowFor(locationId: Long) = cache.getOrPut(locationId) { MutableStateFlow(null) }

    override fun observe(locationId: Long): Flow<Forecast?> = flowFor(locationId).asStateFlow()

    override suspend fun upsert(locationId: Long, forecast: Forecast) {
        flowFor(locationId).value = forecast
    }

    override suspend fun deleteByLocationId(locationId: Long) {
        flowFor(locationId).value = null
    }
}
