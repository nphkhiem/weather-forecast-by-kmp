package com.example.my_weather_forecast.data.local

import app.cash.sqldelight.ColumnAdapter
import com.example.my_weather_forecast.domain.model.Forecast
import kotlinx.serialization.json.Json

object ForecastPayloadAdapter : ColumnAdapter<Forecast, String> {
    private val json = Json { ignoreUnknownKeys = true }

    override fun decode(databaseValue: String): Forecast = json.decodeFromString(databaseValue)
    override fun encode(value: Forecast): String = json.encodeToString(value)
}
