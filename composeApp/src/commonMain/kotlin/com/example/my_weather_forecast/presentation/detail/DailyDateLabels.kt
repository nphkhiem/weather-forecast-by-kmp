package com.example.my_weather_forecast.presentation.detail

import kotlinx.datetime.LocalDate

/**
 * Formats each date as dd/MM, except the single date (if any) where the year visibly changes
 * from the previous entry in the list: that one gets dd/MM/yyyy so a year-crossing forecast
 * window doesn't leave which year "02/01" belongs to ambiguous.
 */
internal fun List<LocalDate>.dailyDateLabels(): List<String> =
    mapIndexed { index, date ->
        val crossesYearBoundary = index > 0 && date.year != this[index - 1].year
        if (crossesYearBoundary) date.toLabelWithYear() else date.toLabel()
    }

private fun LocalDate.toLabel(): String = "${dayOfMonth.paddedTwoDigits()}/${monthNumber.paddedTwoDigits()}"

private fun LocalDate.toLabelWithYear(): String = "${toLabel()}/$year"

private fun Int.paddedTwoDigits(): String = toString().padStart(2, '0')
