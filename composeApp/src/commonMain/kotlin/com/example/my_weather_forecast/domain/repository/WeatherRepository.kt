package com.example.my_weather_forecast.domain.repository

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun observe(location: Location, units: Units): Flow<ForecastObservation>

    /** Bypasses TTL; still coalesces with any refresh already in flight for this area. */
    suspend fun refresh(location: Location, units: Units): AppResult<Unit>
}
