package com.example.my_weather_forecast.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.repository.WeatherRepository
import com.example.my_weather_forecast.domain.usecase.AddLocationUseCase
import com.example.my_weather_forecast.domain.usecase.ObserveSavedLocationsUseCase
import com.example.my_weather_forecast.domain.usecase.RemoveLocationUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OverviewViewModel(
    observeSavedLocationsUseCase: ObserveSavedLocationsUseCase,
    private val removeLocationUseCase: RemoveLocationUseCase,
    private val addLocationUseCase: AddLocationUseCase,
    private val weatherRepository: WeatherRepository,
) : ViewModel() {

    // Fixed until T6.1 adds a real units preference.
    private val units = Units.METRIC

    private val _events = MutableSharedFlow<OverviewEvent>()
    val events: SharedFlow<OverviewEvent> = _events.asSharedFlow()

    private var latestLocations: List<Location> = emptyList()
    private var lastRemovedLocation: Location? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<OverviewUiState> = observeSavedLocationsUseCase()
        .onEach { latestLocations = it }
        .flatMapLatest { locations -> observeAreas(locations) }
        // Zero timeout: a positive one schedules a delay() on viewModelScope's Main dispatcher,
        // which crashes real ViewModel unit tests on JVM (no Robolectric) even under setMain().
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), OverviewUiState.Loading)

    private fun observeAreas(locations: List<Location>) =
        if (locations.isEmpty()) {
            flowOf(OverviewUiState.Empty)
        } else {
            combine(locations.map { weatherRepository.observe(it, units) }) { observations ->
                toUiState(observations.toList())
            }
        }

    private fun toUiState(observations: List<ForecastObservation>): OverviewUiState {
        if (observations.any { it is ForecastObservation.Loading }) return OverviewUiState.Loading

        val successes = observations.filterIsInstance<ForecastObservation.Success>()
        if (successes.isEmpty()) {
            val firstError = observations.filterIsInstance<ForecastObservation.Error>().first()
            return OverviewUiState.Error(firstError.error)
        }

        return OverviewUiState.Success(successes.map { it.toAreaSummary() })
    }

    private fun ForecastObservation.Success.toAreaSummary(): AreaSummary = forecast.toAreaSummary(stale)

    private fun Forecast.toAreaSummary(stale: Boolean): AreaSummary = AreaSummary(
        id = location.id,
        name = location.name,
        currentTemp = current.temp,
        icon = current.condition.icon,
        todayHigh = daily.first().tempMax,
        todayLow = daily.first().tempMin,
        rainChance = daily.first().pop,
        stale = stale,
    )

    fun onAreaClick(id: Long) {
        viewModelScope.launch {
            _events.emit(OverviewEvent.OpenDetail(id))
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val anyFailed = coroutineScope {
                latestLocations
                    .map { location -> async { weatherRepository.refresh(location, units) } }
                    .map { it.await() }
                    .any { it is AppResult.Failure }
            }
            if (anyFailed) {
                _events.emit(OverviewEvent.ShowMessage("Some areas couldn't be refreshed"))
            }
        }
    }

    fun removeArea(id: Long) {
        viewModelScope.launch {
            val removed = latestLocations.find { it.id == id } ?: return@launch
            removeLocationUseCase(id)
            lastRemovedLocation = removed
            _events.emit(OverviewEvent.ShowMessage("${removed.name} removed"))
        }
    }

    fun undoRemove() {
        viewModelScope.launch {
            val removed = lastRemovedLocation ?: return@launch
            lastRemovedLocation = null
            addLocationUseCase(removed)
        }
    }
}
