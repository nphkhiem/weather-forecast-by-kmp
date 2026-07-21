package com.example.my_weather_forecast.presentation.overview

sealed interface OverviewEvent {
    data class OpenDetail(val locationId: Long) : OverviewEvent

    data object RefreshPartiallyFailed : OverviewEvent

    /** Undoable: the removed area can be restored via [OverviewViewModel.undoRemove]. */
    data class AreaRemoved(val name: String) : OverviewEvent
}
