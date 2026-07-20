package com.example.my_weather_forecast.data.repository

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.time.TimeProvider
import com.example.my_weather_forecast.data.local.WeatherLocalDataSource
import com.example.my_weather_forecast.data.remote.WeatherRemoteDataSource
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource,
    private val timeProvider: TimeProvider,
) : WeatherRepository {

    override fun observe(location: Location, units: Units): Flow<ForecastObservation> = flow {
        var emittedSomething = false

        localDataSource.observe(location.id).collect { cached ->
            val usable = cached != null && cached.units == units
            if (usable) {
                emittedSomething = true
                val stale = isStale(cached)
                emit(ForecastObservation.Success(cached, stale))
                if (stale) refreshAndCache(location, units)
            } else {
                if (!emittedSomething) emit(ForecastObservation.Loading)
                refreshAndCache(location, units)
            }
        }
    }

    private suspend fun refreshAndCache(location: Location, units: Units) {
        val result = remoteDataSource.fetchForecast(location, units)
        if (result is AppResult.Success) {
            localDataSource.upsert(location.id, result.data)
        }
    }

    private fun isStale(forecast: Forecast): Boolean {
        val ageMillis = timeProvider.nowEpochMillis() - forecast.fetchedAt.toEpochMilliseconds()
        return ageMillis >= TTL_MILLIS
    }

    private companion object {
        const val TTL_MILLIS = 30 * 60 * 1000L
    }
}
