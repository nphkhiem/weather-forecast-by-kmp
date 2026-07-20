package com.example.my_weather_forecast.domain.usecase

import com.example.my_weather_forecast.domain.repository.SavedLocationRepository

class RemoveLocationUseCase(
    private val repository: SavedLocationRepository,
) {
    suspend operator fun invoke(id: Long) {
        repository.remove(id)
    }
}
