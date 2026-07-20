package com.example.my_weather_forecast.data.repository

import app.cash.turbine.test
import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.testutil.FakeDispatcherProvider
import com.example.my_weather_forecast.testutil.FakeTimeProvider
import com.example.my_weather_forecast.testutil.FakeWeatherLocalDataSource
import com.example.my_weather_forecast.testutil.FakeWeatherRemoteDataSource
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherRepositoryImplTest {

    private val location = Location(
        id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
    )
    private val timeProvider = FakeTimeProvider(currentEpochMillis = NOW)
    private val localDataSource = FakeWeatherLocalDataSource()

    private fun TestScope.repository(remote: FakeWeatherRemoteDataSource) =
        WeatherRepositoryImpl(remote, localDataSource, timeProvider, FakeDispatcherProvider(StandardTestDispatcher(testScheduler)))

    @Test
    fun givenFreshCache_whenObserve_thenEmitsCacheAndRemoteIsNeverCalled() = runTest {
        val cached = sampleForecast(location, fetchedAtEpochMillis = NOW - 5.minutesInMillis)
        localDataSource.upsert(location.id, cached)
        val remote = FakeWeatherRemoteDataSource { _, _ -> error("remote must never be called for a fresh cache") }

        repository(remote).observe(location, Units.METRIC).test {
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

        repository(remote).observe(location, Units.METRIC).test {
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

        repository(remote).observe(location, Units.METRIC).test {
            assertEquals(ForecastObservation.Loading, awaitItem())
            assertEquals(ForecastObservation.Success(fetched, stale = false), awaitItem())
            expectNoEvents()
        }
        assertEquals(1, remote.fetchForecastCallCount)
    }

    @Test
    fun givenStaleCacheAndOffline_whenObserve_thenEmitsCachedForecastFlaggedStaleWithNetworkErrorNoCrash() = runTest {
        val stale = sampleForecast(location, fetchedAtEpochMillis = NOW - 40.minutesInMillis)
        localDataSource.upsert(location.id, stale)
        val remote = FakeWeatherRemoteDataSource { _, _ -> AppResult.Failure(WeatherError.Network) }

        repository(remote).observe(location, Units.METRIC).test {
            assertEquals(ForecastObservation.Success(stale, stale = true), awaitItem())
            assertEquals(ForecastObservation.Success(stale, stale = true, error = WeatherError.Network), awaitItem())
            expectNoEvents()
        }
        assertEquals(1, remote.fetchForecastCallCount)
    }

    @Test
    fun givenStaleCacheAndOnlineButRefreshFails_whenObserve_thenEmitsCachedForecastFlaggedStaleWithMappedErrorNoCrash() = runTest {
        val stale = sampleForecast(location, fetchedAtEpochMillis = NOW - 40.minutesInMillis)
        localDataSource.upsert(location.id, stale)
        val remote = FakeWeatherRemoteDataSource { _, _ -> AppResult.Failure(WeatherError.RateLimited) }

        repository(remote).observe(location, Units.METRIC).test {
            assertEquals(ForecastObservation.Success(stale, stale = true), awaitItem())
            assertEquals(ForecastObservation.Success(stale, stale = true, error = WeatherError.RateLimited), awaitItem())
            expectNoEvents()
        }
        assertEquals(1, remote.fetchForecastCallCount)
    }

    @Test
    fun givenTwoConcurrentObservers_whenBothTriggerRefresh_thenRemoteCalledExactlyOnce() = runTest {
        val stale = sampleForecast(location, fetchedAtEpochMillis = NOW - 40.minutesInMillis)
        localDataSource.upsert(location.id, stale)
        val fresh = sampleForecast(location, fetchedAtEpochMillis = NOW)
        val remote = FakeWeatherRemoteDataSource { _, _ -> AppResult.Success(fresh) }
        val repository = repository(remote)

        val job1 = launch { repository.observe(location, Units.METRIC).take(2).toList() }
        val job2 = launch { repository.observe(location, Units.METRIC).take(2).toList() }
        job1.join()
        job2.join()

        assertEquals(1, remote.fetchForecastCallCount)
    }

    @Test
    fun givenFreshCache_whenManualRefresh_thenRemoteIsCalled() = runTest {
        val fresh = sampleForecast(location, fetchedAtEpochMillis = NOW - 5.minutesInMillis)
        localDataSource.upsert(location.id, fresh)
        val refreshed = sampleForecast(location, fetchedAtEpochMillis = NOW)
        val remote = FakeWeatherRemoteDataSource { _, _ -> AppResult.Success(refreshed) }

        val result = repository(remote).refresh(location, Units.METRIC)

        assertEquals(AppResult.Success(Unit), result)
        assertEquals(1, remote.fetchForecastCallCount)
    }

    private val Int.minutesInMillis: Long get() = this * 60_000L

    private companion object {
        const val NOW = 1704114000_000L
    }
}
