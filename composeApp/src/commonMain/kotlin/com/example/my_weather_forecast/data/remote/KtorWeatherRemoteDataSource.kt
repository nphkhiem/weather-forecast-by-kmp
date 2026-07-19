package com.example.my_weather_forecast.data.remote

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.core.time.TimeProvider
import com.example.my_weather_forecast.data.mapper.toDomain
import com.example.my_weather_forecast.data.remote.dto.OneCallResponseDto
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.datetime.Instant
import kotlinx.io.IOException

class KtorWeatherRemoteDataSource(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val timeProvider: TimeProvider,
) : WeatherRemoteDataSource {

    override suspend fun fetchForecast(location: Location, units: Units): AppResult<Forecast> = try {
        val response = httpClient.get(ONE_CALL_URL) {
            parameter("lat", location.lat)
            parameter("lon", location.lon)
            parameter("exclude", "minutely,alerts")
            parameter("units", units.toOwmUnits())
            parameter("appid", apiKey)
        }

        when {
            response.status.isSuccess() -> {
                val dto = response.body<OneCallResponseDto>()
                val fetchedAt = Instant.fromEpochMilliseconds(timeProvider.nowEpochMillis())
                AppResult.Success(dto.toDomain(location, units, fetchedAt))
            }
            response.status == HttpStatusCode.Unauthorized -> AppResult.Failure(WeatherError.Unauthorized)
            response.status == HttpStatusCode.TooManyRequests -> AppResult.Failure(WeatherError.RateLimited)
            else -> AppResult.Failure(WeatherError.Unknown(response.status.toString()))
        }
    } catch (e: IOException) {
        AppResult.Failure(WeatherError.Network)
    }

    private fun Units.toOwmUnits(): String = when (this) {
        Units.METRIC -> "metric"
        Units.IMPERIAL -> "imperial"
    }

    private companion object {
        const val ONE_CALL_URL = "https://api.openweathermap.org/data/3.0/onecall"
    }
}
