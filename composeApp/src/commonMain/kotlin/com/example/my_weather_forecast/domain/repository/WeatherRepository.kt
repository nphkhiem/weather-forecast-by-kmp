package com.example.my_weather_forecast.domain.repository

import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun observe(location: Location, units: Units): Flow<ForecastObservation>
}
