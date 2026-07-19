package com.example.my_weather_forecast.data.remote

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.core.time.TimeProvider
import com.example.my_weather_forecast.data.mapper.toDomain
import com.example.my_weather_forecast.data.remote.dto.GeoResultDto
import com.example.my_weather_forecast.data.remote.dto.OneCallResponseDto
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.datetime.Instant
import kotlinx.io.IOException

class KtorWeatherRemoteDataSource(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val timeProvider: TimeProvider,
) : WeatherRemoteDataSource {

    override suspend fun fetchForecast(location: Location, units: Units): AppResult<Forecast> =
        request(
            url = ONE_CALL_URL,
            parameters = {
                parameter("lat", location.lat)
                parameter("lon", location.lon)
                parameter("exclude", "minutely,alerts")
                parameter("units", units.toOwmUnits())
                parameter("appid", apiKey)
            },
        ) { response ->
            val dto = response.body<OneCallResponseDto>()
            val fetchedAt = Instant.fromEpochMilliseconds(timeProvider.nowEpochMillis())
            AppResult.Success(dto.toDomain(location, units, fetchedAt))
        }

    override suspend fun searchCity(query: String): AppResult<List<Location>> =
        request(
            url = GEOCODING_URL,
            parameters = {
                parameter("q", query)
                parameter("limit", GEOCODING_RESULT_LIMIT)
                parameter("appid", apiKey)
            },
        ) { response ->
            val results = response.body<List<GeoResultDto>>()
            if (results.isEmpty()) {
                AppResult.Failure(WeatherError.NotFound)
            } else {
                AppResult.Success(results.map { it.toDomain() })
            }
        }

    private suspend fun <T> request(
        url: String,
        parameters: HttpRequestBuilder.() -> Unit,
        onSuccess: suspend (HttpResponse) -> AppResult<T>,
    ): AppResult<T> = try {
        val response = httpClient.get(url) { parameters() }
        when {
            response.status.isSuccess() -> onSuccess(response)
            response.status == HttpStatusCode.Unauthorized -> AppResult.Failure(WeatherError.Unauthorized)
            response.status == HttpStatusCode.TooManyRequests -> AppResult.Failure(WeatherError.RateLimited)
            response.status == HttpStatusCode.NotFound -> AppResult.Failure(WeatherError.NotFound)
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
        const val GEOCODING_URL = "https://api.openweathermap.org/geo/1.0/direct"
        const val GEOCODING_RESULT_LIMIT = 5
    }
}
