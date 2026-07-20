package com.example.my_weather_forecast.domain.usecase

import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.testutil.FakeSavedLocationRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class RemoveLocationUseCaseTest {

    @Test
    fun givenASavedLocation_whenRemove_thenNoLongerInObserveAll() = runTest {
        val location = Location(
            id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
        )
        val repository = FakeSavedLocationRepository(listOf(location))
        val useCase = RemoveLocationUseCase(repository)

        useCase(location.id)

        assertTrue(repository.observeAll().first().isEmpty())
    }
}
