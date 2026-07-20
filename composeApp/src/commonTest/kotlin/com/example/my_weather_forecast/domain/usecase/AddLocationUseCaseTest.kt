package com.example.my_weather_forecast.domain.usecase

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.testutil.FakeSavedLocationRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AddLocationUseCaseTest {

    private fun location(id: Long, lat: Double, lon: Double, sortOrder: Int = 0) = Location(
        id = id, name = "City$id", country = "US", state = null, lat = lat, lon = lon, sortOrder = sortOrder,
    )

    @Test
    fun given6Saved_whenAddLocation_thenRejectedWithAtLimitAndCountStays6() = runTest {
        val existing = (1..6).map { location(id = it.toLong(), lat = it.toDouble(), lon = it.toDouble(), sortOrder = it - 1) }
        val repository = FakeSavedLocationRepository(existing)
        val useCase = AddLocationUseCase(repository)

        val result = useCase(location(id = 0, lat = 50.0, lon = 50.0))

        assertEquals(AppResult.Failure(WeatherError.AtLimit), result)
        assertEquals(6, repository.observeAll().first().size)
    }

    @Test
    fun givenFewerThan6Saved_whenAddLocation_thenPersistedWithNextSortOrderAndObservable() = runTest {
        val existing = (1..2).map { location(id = it.toLong(), lat = it.toDouble(), lon = it.toDouble(), sortOrder = it - 1) }
        val repository = FakeSavedLocationRepository(existing)
        val useCase = AddLocationUseCase(repository)
        val candidate = location(id = 0, lat = 50.0, lon = 50.0)

        val result = useCase(candidate)

        assertIs<AppResult.Success<Unit>>(result)
        val saved = repository.observeAll().first()
        assertEquals(3, saved.size)
        assertEquals(2, saved.last().sortOrder)
    }

    @Test
    fun givenASavedLocation_whenAddLocationWithANearDuplicateLatLon_thenRejectedAsAlreadySavedAndCountUnchanged() = runTest {
        val existing = listOf(location(id = 1, lat = 41.85, lon = -87.65, sortOrder = 0))
        val repository = FakeSavedLocationRepository(existing)
        val useCase = AddLocationUseCase(repository)
        val nearDuplicate = location(id = 0, lat = 41.855, lon = -87.655)

        val result = useCase(nearDuplicate)

        assertEquals(AppResult.Failure(WeatherError.AlreadySaved), result)
        assertEquals(1, repository.observeAll().first().size)
    }

    @Test
    fun givenASavedLocation_whenAddLocationFarEnoughAway_thenPersisted() = runTest {
        val existing = listOf(location(id = 1, lat = 41.85, lon = -87.65, sortOrder = 0))
        val repository = FakeSavedLocationRepository(existing)
        val useCase = AddLocationUseCase(repository)
        val farAway = location(id = 0, lat = 51.5074, lon = -0.1278)

        val result = useCase(farAway)

        assertIs<AppResult.Success<Unit>>(result)
        assertEquals(2, repository.observeAll().first().size)
    }
}
