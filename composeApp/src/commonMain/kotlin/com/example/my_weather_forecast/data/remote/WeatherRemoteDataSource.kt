package com.example.my_weather_forecast.data.remote

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units

interface WeatherRemoteDataSource {
    suspend fun fetchForecast(location: Location, units: Units): AppResult<Forecast>
}
