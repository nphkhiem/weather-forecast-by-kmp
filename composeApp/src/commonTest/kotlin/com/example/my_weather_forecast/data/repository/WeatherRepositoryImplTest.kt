package com.example.my_weather_forecast.data.repository

import app.cash.turbine.test
import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.testutil.FakeTimeProvider
import com.example.my_weather_forecast.testutil.FakeWeatherLocalDataSource
import com.example.my_weather_forecast.testutil.FakeWeatherRemoteDataSource
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherRepositoryImplTest {

    private val location = Location(
        id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
    )
    private val timeProvider = FakeTimeProvider(currentEpochMillis = NOW)
    private val localDataSource = FakeWeatherLocalDataSource()

    @Test
    fun givenFreshCache_whenObserve_thenEmitsCacheAndRemoteIsNeverCalled() = runTest {
        val cached = sampleForecast(location, fetchedAtEpochMillis = NOW - 5.minutesInMillis)
        localDataSource.upsert(location.id, cached)
        val remote = FakeWeatherRemoteDataSource { _, _ -> error("remote must never be called for a fresh cache") }
        val repository = WeatherRepositoryImpl(remote, localDataSource, timeProvider)

        repository.observe(location, Units.METRIC).test {
            assertEquals(ForecastObservation.Success(cached, stale = false), awaitItem())
            expectNoEvents()
        }
        assertEquals(0, remote.fetchForecastCallCount)
    }

    @Test
    fun givenStaleCacheAndOnline_whenObserve_thenRemoteCalledOnceCacheUpsertedAndFlowReEmitsFresh() = runTest {
        val stale = sampleForecast(location, fetchedAtEpochMillis = NOW - 40.minutesInMillis)
        localDataSource.upsert(location.id, stale)
        val fresh = sampleForecast(location, fetchedAtEpochMillis = NOW)
        val remote = FakeWeatherRemoteDataSource { _, _ -> AppResult.Success(fresh) }
        val repository = WeatherRepositoryImpl(remote, localDataSource, timeProvider)

        repository.observe(location, Units.METRIC).test {
            assertEquals(ForecastObservation.Success(stale, stale = true), awaitItem())
            assertEquals(ForecastObservation.Success(fresh, stale = false), awaitItem())
            expectNoEvents()
        }
        assertEquals(1, remote.fetchForecastCallCount)
    }

    @Test
    fun givenNoCacheAndOnline_whenObserve_thenLoadingThenRemoteThenSuccess() = runTest {
        val fetched = sampleForecast(location, fetchedAtEpochMillis = NOW)
        val remote = FakeWeatherRemoteDataSource { _, _ -> AppResult.Success(fetched) }
        val repository = WeatherRepositoryImpl(remote, localDataSource, timeProvider)

        repository.observe(location, Units.METRIC).test {
            assertEquals(ForecastObservation.Loading, awaitItem())
            assertEquals(ForecastObservation.Success(fetched, stale = false), awaitItem())
            expectNoEvents()
        }
        assertEquals(1, remote.fetchForecastCallCount)
    }

    private val Int.minutesInMillis: Long get() = this * 60_000L

    private companion object {
        const val NOW = 1704114000_000L
    }
}
