package com.example.my_weather_forecast.domain.usecase

import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.repository.SavedLocationRepository
import kotlinx.coroutines.flow.Flow

class ObserveSavedLocationsUseCase(
    private val repository: SavedLocationRepository,
) {
    operator fun invoke(): Flow<List<Location>> = repository.observeAll()
}
