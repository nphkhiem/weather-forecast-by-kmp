package com.example.my_weather_forecast.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.my_weather_forecast.core.dispatcher.DispatcherProvider
import com.example.my_weather_forecast.data.mapper.toDomain
import com.example.my_weather_forecast.domain.model.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SqlDelightSavedLocationLocalDataSource(
    private val queries: SavedLocationQueries,
    private val dispatcherProvider: DispatcherProvider,
) : SavedLocationLocalDataSource {

    override fun observeAll(): Flow<List<Location>> =
        queries.selectAll()
            .asFlow()
            .mapToList(dispatcherProvider.io)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun insert(location: Location) {
        withContext(dispatcherProvider.io) {
            queries.insert(
                name = location.name,
                country = location.country,
                state = location.state,
                lat = location.lat,
                lon = location.lon,
                sortOrder = location.sortOrder.toLong(),
            )
        }
    }

    override suspend fun deleteById(id: Long) {
        withContext(dispatcherProvider.io) {
            queries.deleteById(id)
        }
    }
}
