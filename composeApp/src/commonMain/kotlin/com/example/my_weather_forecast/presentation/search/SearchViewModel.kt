package com.example.my_weather_forecast.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.my_weather_forecast.core.result.AppResult
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.repository.CitySearchRepository
import com.example.my_weather_forecast.domain.usecase.AddLocationUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val citySearchRepository: CitySearchRepository,
    private val addLocationUseCase: AddLocationUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _events = MutableSharedFlow<SearchEvent>()
    val events: SharedFlow<SearchEvent> = _events.asSharedFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<SearchUiState> = _query
        .debounce(DEBOUNCE_MILLIS)
        .flatMapLatest { query -> search(query) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SearchUiState.Idle)

    private fun search(query: String): Flow<SearchUiState> =
        if (query.isBlank()) {
            flowOf(SearchUiState.Idle)
        } else {
            flow {
                emit(SearchUiState.Loading)
                when (val result = citySearchRepository.searchCity(query)) {
                    // The geocoding API can return multiple entries at the exact same
                    // coordinates (e.g. name-spelling variants of the same place); the results
                    // list is keyed by coordinates in the UI, so duplicates must be collapsed
                    // here rather than crashing on a repeated LazyColumn key.
                    is AppResult.Success -> emit(SearchUiState.Results(result.data.distinctBy { it.lat to it.lon }))
                    is AppResult.Failure -> emit(
                        if (result.error == WeatherError.NotFound) SearchUiState.Empty else SearchUiState.Error(result.error),
                    )
                }
            }
        }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun addLocation(location: Location) {
        viewModelScope.launch {
            when (val result = addLocationUseCase(location)) {
                is AppResult.Success -> _events.emit(SearchEvent.Added)
                is AppResult.Failure -> when (result.error) {
                    WeatherError.AtLimit -> _events.emit(SearchEvent.AtLimit)
                    WeatherError.AlreadySaved -> _events.emit(SearchEvent.AlreadySaved)
                    else -> _events.emit(SearchEvent.AddFailed)
                }
            }
        }
    }

    private companion object {
        const val DEBOUNCE_MILLIS = 300L
    }
}
