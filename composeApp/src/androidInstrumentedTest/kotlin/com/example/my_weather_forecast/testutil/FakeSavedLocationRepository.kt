package com.example.my_weather_forecast.testutil

import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.repository.SavedLocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSavedLocationRepository(
    initial: List<Location> = emptyList(),
) : SavedLocationRepository {
    private val locations = MutableStateFlow(initial)

    override fun observeAll(): Flow<List<Location>> = locations.asStateFlow()

    override suspend fun add(location: Location) {
        locations.value = locations.value + location
    }

    override suspend fun remove(id: Long) {
        locations.value = locations.value.filterNot { it.id == id }
    }
}
