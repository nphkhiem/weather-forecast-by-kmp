package com.example.my_weather_forecast.presentation.overview

sealed interface OverviewEvent {
    data class OpenDetail(val locationId: Long) : OverviewEvent

    /** [undoable] controls whether the snackbar shows an Undo action. */
    data class ShowMessage(val message: String, val undoable: Boolean = false) : OverviewEvent
}
