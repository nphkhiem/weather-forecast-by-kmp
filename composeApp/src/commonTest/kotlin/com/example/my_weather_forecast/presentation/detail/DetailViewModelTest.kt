package com.example.my_weather_forecast.presentation.detail

import app.cash.turbine.test
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.testutil.FakeSavedLocationRepository
import com.example.my_weather_forecast.testutil.FakeWeatherRepository
import com.example.my_weather_forecast.testutil.runMainDispatcherTest
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DetailViewModelTest {

    private val savedLocationRepository = FakeSavedLocationRepository()
    private val weatherRepository = FakeWeatherRepository()

    private val chicago = Location(
        id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
    )

    private fun testDetail(locationId: Long = chicago.id, body: suspend TestScope.(DetailViewModel) -> Unit) =
        runMainDispatcherTest {
            val viewModel = DetailViewModel(
                locationId = locationId,
                savedLocationRepository = savedLocationRepository,
                weatherRepository = weatherRepository,
            )
            body(viewModel)
        }

    @Test
    fun givenALocationId_whenObserved_thenSuccessWithTheFullForecast() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        val forecast = sampleForecast(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(forecast, stale = false))

        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            val success = awaitItem()
            assertIs<DetailUiState.Success>(success)
            assertEquals(forecast, success.forecast)
            assertEquals(forecast.daily, success.forecast.daily)
            assertEquals(forecast.hourly, success.forecast.hourly)
        }
    }

    @Test
    fun givenAStaleCache_whenObserved_thenSuccessFlaggedStaleWithLastUpdated() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        val forecast = sampleForecast(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(forecast, stale = true))

        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            val success = awaitItem()
            assertIs<DetailUiState.Success>(success)
            assertEquals(true, success.stale)
            assertEquals(forecast.fetchedAt, success.lastUpdated)
        }
    }

    @Test
    fun givenNoCacheAndOffline_whenObserved_thenError() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Error(WeatherError.Network))

        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            assertEquals(DetailUiState.Error(WeatherError.Network), awaitItem())
        }
    }

    @Test
    fun givenTheAreaIsNoLongerSaved_whenObserved_thenError() = testDetail { viewModel ->
        viewModel.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            assertEquals(DetailUiState.Error(WeatherError.NotFound), awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun whenRefreshCalled_thenWeatherRepositoryRefreshesTheObservedLocation() = testDetail { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))

        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
        viewModel.refresh()
        advanceUntilIdle()

        assertEquals(1, weatherRepository.refreshCallCount)
    }
}
