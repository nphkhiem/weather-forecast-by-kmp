package com.example.my_weather_forecast.data.remote

import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.core.time.TimeProvider
import com.example.my_weather_forecast.data.remote.dto.fixtures.ONE_CALL_SAMPLE_JSON
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals

class KtorWeatherRemoteDataSourceTest {

    private val location = Location(
        id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
    )
    private val fakeTimeProvider = object : TimeProvider {
        override fun nowEpochMillis(): Long = 1704124800_000L
    }

    private fun dataSource(engine: MockEngine): KtorWeatherRemoteDataSource {
        val client = HttpClient(engine) { installWeatherClientPlugins() }
        return KtorWeatherRemoteDataSource(client, apiKey = "test-key", timeProvider = fakeTimeProvider)
    }

    private fun MockRequestHandleScope.jsonResponse() = respond(
        content = ByteReadChannel(ONE_CALL_SAMPLE_JSON),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )

    private fun MockRequestHandleScope.geoResponse(body: String) = respond(
        content = ByteReadChannel(body),
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )

    @Test
    fun givenLatLonAndUnits_whenFetchForecast_thenRequestHitsOneCallWithExcludeAndCorrectQuery() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            jsonResponse()
        }

        dataSource(engine).fetchForecast(location, Units.METRIC)

        val request = requireNotNull(capturedRequest)
        assertEquals("api.openweathermap.org", request.url.host)
        assertEquals("/data/3.0/onecall", request.url.encodedPath)
        assertEquals("41.85", request.url.parameters["lat"])
        assertEquals("-87.65", request.url.parameters["lon"])
        assertEquals("minutely,alerts", request.url.parameters["exclude"])
        assertEquals("metric", request.url.parameters["units"])
        assertEquals("test-key", request.url.parameters["appid"])
    }

    @Test
    fun givenA200Fixture_whenFetchForecast_thenReturnsDomainForecast() = runTest {
        val engine = MockEngine { jsonResponse() }

        val result = dataSource(engine).fetchForecast(location, Units.METRIC)

        val success = result as AppResult.Success
        assertEquals(location, success.data.location)
        assertEquals(Units.METRIC, success.data.units)
        assertEquals(8, success.data.daily.size)
    }

    @Test
    fun given401_whenFetchForecast_thenReturnsUnauthorized() = runTest {
        val engine = MockEngine { respondError(HttpStatusCode.Unauthorized) }

        val result = dataSource(engine).fetchForecast(location, Units.METRIC)

        assertEquals(AppResult.Failure(WeatherError.Unauthorized), result)
    }

    @Test
    fun given429_whenFetchForecast_thenReturnsRateLimited() = runTest {
        val engine = MockEngine { respondError(HttpStatusCode.TooManyRequests) }

        val result = dataSource(engine).fetchForecast(location, Units.METRIC)

        assertEquals(AppResult.Failure(WeatherError.RateLimited), result)
    }

    @Test
    fun givenIOException_whenFetchForecast_thenReturnsNetwork() = runTest {
        val engine = MockEngine { throw IOException("no connectivity") }

        val result = dataSource(engine).fetchForecast(location, Units.METRIC)

        assertEquals(AppResult.Failure(WeatherError.Network), result)
    }

    @Test
    fun givenAQuery_whenSearchCity_thenRequestHitsGeocodingWithQAndLimitFive() = runTest {
        var capturedRequest: HttpRequestData? = null
        val engine = MockEngine { request ->
            capturedRequest = request
            geoResponse("[]")
        }

        dataSource(engine).searchCity("Chicago")

        val request = requireNotNull(capturedRequest)
        assertEquals("api.openweathermap.org", request.url.host)
        assertEquals("/geo/1.0/direct", request.url.encodedPath)
        assertEquals("Chicago", request.url.parameters["q"])
        assertEquals("5", request.url.parameters["limit"])
        assertEquals("test-key", request.url.parameters["appid"])
    }

    @Test
    fun givenResults_whenSearchCity_thenReturnsMappedLocations() = runTest {
        val engine = MockEngine { geoResponse(GEO_RESULTS_JSON) }

        val result = dataSource(engine).searchCity("Chicago")

        val success = result as AppResult.Success
        assertEquals(2, success.data.size)
        assertEquals("Chicago", success.data.first().name)
        assertEquals("US", success.data.first().country)
        assertEquals("Illinois", success.data.first().state)
        assertEquals(41.85003, success.data.first().lat)
        assertEquals(-87.65005, success.data.first().lon)
    }

    @Test
    fun givenEmptyResults_whenSearchCity_thenReturnsNotFound() = runTest {
        val engine = MockEngine { geoResponse("[]") }

        val result = dataSource(engine).searchCity("Nonexistent City Xyz")

        assertEquals(AppResult.Failure(WeatherError.NotFound), result)
    }

    private companion object {
        val GEO_RESULTS_JSON = """
            [
              { "name": "Chicago", "lat": 41.85003, "lon": -87.65005, "country": "US", "state": "Illinois" },
              { "name": "Chicago Heights", "lat": 41.5065, "lon": -87.635, "country": "US", "state": "Illinois" }
            ]
        """.trimIndent()
    }
}
