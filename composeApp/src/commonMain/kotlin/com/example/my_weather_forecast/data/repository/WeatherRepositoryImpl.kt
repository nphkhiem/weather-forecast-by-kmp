package com.example.my_weather_forecast.data.repository

import com.example.my_weather_forecast.core.dispatcher.DispatcherProvider
import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.core.time.TimeProvider
import com.example.my_weather_forecast.data.local.WeatherLocalDataSource
import com.example.my_weather_forecast.data.remote.WeatherRemoteDataSource
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
    private val timeProvider: TimeProvider,
    dispatcherProvider: DispatcherProvider,
) : WeatherRepository {

    // Owned independently of any single observer's lifecycle, so an in-flight refresh survives
    // one caller cancelling while another is still awaiting the same coalesced result.
    private val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.default)
    private val inFlightMutex = Mutex()
    private val inFlightRefreshes = mutableMapOf<Long, Deferred<WeatherError?>>()

    override fun observe(location: Location, units: Units): Flow<ForecastObservation> = flow {
        var emittedSomething = false

        localDataSource.observe(location.id).collect { cached ->
            val usable = cached != null && cached.units == units
            if (usable) {
                emittedSomething = true
                val stale = isStale(cached)
                emit(ForecastObservation.Success(cached, stale))
                if (stale) {
                    val error = coalescedRefresh(location, units)
                    if (error != null) {
                        emit(ForecastObservation.Success(cached, stale = true, error = error))
                    }
                }
            } else {
                if (!emittedSomething) emit(ForecastObservation.Loading)
                val error = coalescedRefresh(location, units)
                if (error != null) {
                    emittedSomething = true
                    emit(ForecastObservation.Error(error))
                }
            }
        }
    }

    override suspend fun refresh(location: Location, units: Units): AppResult<Unit> {
        val error = coalescedRefresh(location, units)
        return if (error == null) AppResult.Success(Unit) else AppResult.Failure(error)
    }

    private suspend fun coalescedRefresh(location: Location, units: Units): WeatherError? {
        val deferred = inFlightMutex.withLock {
            inFlightRefreshes.getOrPut(location.id) {
                repositoryScope.async { fetchAndCache(location, units) }
            }
        }
        val error = deferred.await()
        inFlightMutex.withLock {
            if (inFlightRefreshes[location.id] === deferred) {
                inFlightRefreshes.remove(location.id)
            }
        }
        return error
    }

    private suspend fun fetchAndCache(location: Location, units: Units): WeatherError? =
        when (val result = remoteDataSource.fetchForecast(location, units)) {
            is AppResult.Success -> {
                localDataSource.upsert(location.id, result.data)
                null
            }
            is AppResult.Failure -> result.error
        }

    private fun isStale(forecast: Forecast): Boolean {
        val ageMillis = timeProvider.nowEpochMillis() - forecast.fetchedAt.toEpochMilliseconds()
        return ageMillis >= TTL_MILLIS
    }

    private companion object {
        const val TTL_MILLIS = 30 * 60 * 1000L
    }
}
