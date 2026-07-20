package com.example.my_weather_forecast.data.remote

import com.example.my_weather_forecast.BuildKonfig
import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.time.SystemTimeProvider
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.fail

/**
 * Manually-run, gated smoke test: hits the real OpenWeatherMap One Call 3.0 endpoint once to
 * confirm the local.properties key actually has the One Call 3.0 subscription. Disabled by
 * default so CI and normal test runs never make a real network call or spend call budget;
 * un-ignore and run locally once after adding a key, then re-ignore.
 */
class LiveOneCallSmokeTest {

    @Ignore
    @Test
    fun givenTheRealKey_whenCallingOneCallOnce_thenReturnsSuccess() = runTest {
        val dataSource = KtorWeatherRemoteDataSource(
            httpClient = HttpClientFactory.create(),
            apiKey = BuildKonfig.OWM_API_KEY,
            timeProvider = SystemTimeProvider(),
        )
        val london = Location(
            id = 0, name = "London", country = "GB", state = null, lat = 51.5074, lon = -0.1278, sortOrder = 0,
        )

        val result = dataSource.fetchForecast(london, Units.METRIC)

        when (result) {
            is AppResult.Success -> Unit
            is AppResult.Failure -> fail(
                "Expected a successful One Call response but got ${result.error}. If this is " +
                    "Unauthorized, the key lacks the One Call 3.0 subscription; resolve before Phase 2.",
            )
        }
    }
}
