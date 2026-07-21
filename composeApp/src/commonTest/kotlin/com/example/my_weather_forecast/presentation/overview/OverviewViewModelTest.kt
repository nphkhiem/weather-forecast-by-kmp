package com.example.my_weather_forecast.presentation.overview

import app.cash.turbine.test
import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.usecase.AddLocationUseCase
import com.example.my_weather_forecast.domain.usecase.ObserveSavedLocationsUseCase
import com.example.my_weather_forecast.domain.usecase.RemoveLocationUseCase
import com.example.my_weather_forecast.testutil.FakeSavedLocationRepository
import com.example.my_weather_forecast.testutil.FakeUnitsPreference
import com.example.my_weather_forecast.testutil.FakeWeatherRepository
import com.example.my_weather_forecast.testutil.runMainDispatcherTest
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class OverviewViewModelTest {

    private val savedLocationRepository = FakeSavedLocationRepository()
    private val weatherRepository = FakeWeatherRepository()
    private val unitsPreference = FakeUnitsPreference()

    private val chicago = Location(
        id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
    )

    private fun testOverview(body: suspend TestScope.(OverviewViewModel) -> Unit) = runMainDispatcherTest {
        val viewModel = OverviewViewModel(
            observeSavedLocationsUseCase = ObserveSavedLocationsUseCase(savedLocationRepository),
            removeLocationUseCase = RemoveLocationUseCase(savedLocationRepository),
            addLocationUseCase = AddLocationUseCase(savedLocationRepository),
            weatherRepository = weatherRepository,
            unitsPreference = unitsPreference,
        )
        body(viewModel)
    }

    @Test
    fun givenNoSavedAreas_whenObserved_thenEmpty() = testOverview { viewModel ->
        viewModel.uiState.test {
            assertEquals(OverviewUiState.Loading, awaitItem())
            assertEquals(OverviewUiState.Empty, awaitItem())
        }
    }

    @Test
    fun givenSavedAreasWithCache_whenObserved_thenSuccessWithASummaryPerArea() = testOverview { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))

        viewModel.uiState.test {
            assertEquals(OverviewUiState.Loading, awaitItem())
            val success = awaitItem()
            assertIs<OverviewUiState.Success>(success)
            assertEquals(1, success.areas.size)
            assertEquals(chicago.id, success.areas.first().id)
            assertEquals(true, success.areas.first().isDaytime)
        }
    }

    @Test
    fun givenAnAreaWithNoCacheThatFailsToFetch_whenObserved_thenError() = testOverview { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Error(WeatherError.Network))

        viewModel.uiState.test {
            assertEquals(OverviewUiState.Loading, awaitItem())
            assertEquals(OverviewUiState.Error(WeatherError.Network), awaitItem())
        }
    }

    @Test
    fun givenARefreshError_whenRefresh_thenShowMessageEventAndPriorDataRetained() = testOverview { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))
        weatherRepository.refreshResult = AppResult.Failure(WeatherError.Network)

        // refresh() reads the locations uiState's upstream has already observed, so subscribe first.
        viewModel.uiState.test {
            awaitItem()
            val success = awaitItem()
            assertIs<OverviewUiState.Success>(success)

            viewModel.events.test {
                viewModel.refresh()
                assertEquals(OverviewEvent.RefreshPartiallyFailed, awaitItem())
            }
            expectNoEvents()
        }
    }

    @Test
    fun givenARefresh_whenInFlight_thenIsRefreshingReflectsProgress() = testOverview { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))

        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.isRefreshing.test {
                assertEquals(false, awaitItem())
                viewModel.refresh()
                assertEquals(true, awaitItem())
                assertEquals(false, awaitItem())
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun givenUnitsPreferenceChanges_whenObserved_thenWeatherRepositoryObservesWithTheNewUnits() = testOverview { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))

        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            assertEquals(Units.METRIC, weatherRepository.lastObservedUnits)

            // The fake returns the same cached forecast regardless of units, so the resulting
            // uiState is value-equal and StateFlow conflates it away; assert the side effect
            // (which units observe() was actually called with) instead of awaiting a new item.
            unitsPreference.setUnits(Units.IMPERIAL)
            advanceUntilIdle()
            assertEquals(Units.IMPERIAL, weatherRepository.lastObservedUnits)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenSetUnitsCalled_thenUnitsPreferenceIsUpdated() = testOverview { viewModel ->
        viewModel.setUnits(Units.IMPERIAL)

        assertEquals(Units.IMPERIAL, unitsPreference.units.value)
    }

    @Test
    fun givenCardTapped_whenOnAreaClick_thenOpenDetailEmittedOnce() = testOverview { viewModel ->
        viewModel.events.test {
            viewModel.onAreaClick(chicago.id)
            assertEquals(OverviewEvent.OpenDetail(chicago.id), awaitItem())
        }
    }

    @Test
    fun givenASavedArea_whenRemoveArea_thenDisappearsFromSuccessAndShowMessageEmittedOnce() = testOverview { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))

        viewModel.events.test {
            viewModel.uiState.test {
                awaitItem()
                awaitItem()
                viewModel.removeArea(chicago.id)
                assertEquals(OverviewUiState.Empty, awaitItem())
            }
            assertEquals(OverviewEvent.AreaRemoved(chicago.name), awaitItem())
        }
    }

    @Test
    fun givenARemovedArea_whenUndoRemove_thenReappearsInSuccess() = testOverview { viewModel ->
        savedLocationRepository.add(chicago)
        weatherRepository.setObservation(chicago.id, ForecastObservation.Success(sampleForecast(chicago), stale = false))

        viewModel.uiState.test {
            awaitItem()
            awaitItem()
            viewModel.removeArea(chicago.id)
            assertEquals(OverviewUiState.Empty, awaitItem())

            viewModel.undoRemove()
            val restored = awaitItem()
            assertIs<OverviewUiState.Success>(restored)
            assertEquals(chicago.id, restored.areas.first().id)
        }
    }
}
