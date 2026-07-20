package com.example.my_weather_forecast.domain.usecase

import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.testutil.FakeSavedLocationRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveSavedLocationsUseCaseTest {

    @Test
    fun givenSavedLocations_whenInvoked_thenEmitsRepositoryState() = runTest {
        val location = Location(
            id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
        )
        val repository = FakeSavedLocationRepository(listOf(location))
        val useCase = ObserveSavedLocationsUseCase(repository)

        assertEquals(listOf(location), useCase().first())
    }
}
