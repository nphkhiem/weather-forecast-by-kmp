package com.example.my_weather_forecast.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.repository.SavedLocationRepository
import com.example.my_weather_forecast.domain.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel(
    private val locationId: Long,
    savedLocationRepository: SavedLocationRepository,
    private val weatherRepository: WeatherRepository,
) : ViewModel() {

    // Fixed until T6.1 adds a real units preference.
    private val units = Units.METRIC

    private var latestLocation: Location? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DetailUiState> = savedLocationRepository.observeAll()
        .map { locations -> locations.find { it.id == locationId } }
        .onEach { latestLocation = it }
        .flatMapLatest { location -> observeForecast(location) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DetailUiState.Loading)

    private fun observeForecast(location: Location?) =
        if (location == null) {
            flowOf(DetailUiState.Error(WeatherError.NotFound))
        } else {
            weatherRepository.observe(location, units).map { it.toUiState() }
        }

    private fun ForecastObservation.toUiState(): DetailUiState = when (this) {
        ForecastObservation.Loading -> DetailUiState.Loading
        is ForecastObservation.Success -> DetailUiState.Success(forecast, stale, forecast.fetchedAt)
        is ForecastObservation.Error -> DetailUiState.Error(error)
    }

    fun refresh() {
        val location = latestLocation ?: return
        viewModelScope.launch {
            weatherRepository.refresh(location, units)
        }
    }
}
