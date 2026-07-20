package com.example.my_weather_forecast.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.my_weather_forecast.core.dispatcher.DispatcherProvider
import com.example.my_weather_forecast.domain.model.Forecast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightWeatherLocalDataSource(
    private val queries: ForecastCacheQueries,
    private val dispatcherProvider: DispatcherProvider,
) : WeatherLocalDataSource {

    override fun observe(locationId: Long): Flow<Forecast?> =
        queries.selectByLocationId(locationId)
            .asFlow()
            .mapToOneOrNull(dispatcherProvider.io)
            .map { row -> row?.payload }

    override suspend fun upsert(locationId: Long, forecast: Forecast) {
        withContext(dispatcherProvider.io) {
            queries.upsert(
                locationId = locationId,
                units = forecast.units.name,
                fetchedAtEpochMs = forecast.fetchedAt.toEpochMilliseconds(),
                payload = forecast,
            )
        }
    }

    override suspend fun deleteByLocationId(locationId: Long) {
        withContext(dispatcherProvider.io) {
            queries.deleteByLocationId(locationId)
        }
    }
}
