package com.example.my_weather_forecast.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_weather_forecast.core.preference.UnitsPreference
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.repository.SavedLocationRepository
import com.example.my_weather_forecast.domain.repository.WeatherRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    private val unitsPreference: UnitsPreference,
) : ViewModel() {

    private var latestLocation: Location? = null

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DetailUiState> = combine(
        savedLocationRepository.observeAll()
            .map { locations -> locations.find { it.id == locationId } }
            .onEach { latestLocation = it },
        unitsPreference.units,
    ) { location, units -> location to units }
        .flatMapLatest { (location, units) -> observeForecast(location, units) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DetailUiState.Loading)

    private fun observeForecast(location: Location?, units: Units) =
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
            _isRefreshing.value = true
            try {
                weatherRepository.refresh(location, unitsPreference.units.value)
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}
