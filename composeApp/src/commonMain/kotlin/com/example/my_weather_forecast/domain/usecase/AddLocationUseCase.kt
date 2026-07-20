package com.example.my_weather_forecast.domain.usecase

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.repository.SavedLocationRepository
import kotlin.math.abs
import kotlinx.coroutines.flow.first

class AddLocationUseCase(
    private val repository: SavedLocationRepository,
) {
    suspend operator fun invoke(candidate: Location): AppResult<Unit> {
        val saved = repository.observeAll().first()

        return when {
            saved.size >= MAX_SAVED_LOCATIONS -> AppResult.Failure(WeatherError.AtLimit)
            saved.any { it.isNearDuplicateOf(candidate) } -> AppResult.Failure(WeatherError.AlreadySaved)
            else -> {
                repository.add(candidate.copy(sortOrder = saved.size))
                AppResult.Success(Unit)
            }
        }
    }

    private fun Location.isNearDuplicateOf(other: Location): Boolean =
        abs(lat - other.lat) < DUPLICATE_THRESHOLD_DEGREES && abs(lon - other.lon) < DUPLICATE_THRESHOLD_DEGREES

    private companion object {
        const val MAX_SAVED_LOCATIONS = 6
        const val DUPLICATE_THRESHOLD_DEGREES = 0.01
    }
}
