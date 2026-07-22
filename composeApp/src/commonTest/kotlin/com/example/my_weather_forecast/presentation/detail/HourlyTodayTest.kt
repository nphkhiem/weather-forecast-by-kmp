package com.example.my_weather_forecast.presentation.detail

import com.example.my_weather_forecast.domain.model.HourlyForecast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

private val Zone = TimeZone.UTC

class HourlyTodayTest {

    @Test
    fun givenHoursEntirelyWithinToday_whenFilteredToToday_thenNoneAreDropped() {
        val today = LocalDate(2026, 7, 22)
        val hours = listOf(
            hourAt(2026, 7, 22, 10),
            hourAt(2026, 7, 22, 14),
            hourAt(2026, 7, 22, 23),
        )

        val result = hours.todayOnly(today, Zone)

        assertEquals(hours, result)
    }

    @Test
    fun givenHoursSpanningIntoTomorrow_whenFilteredToToday_thenOnlyTodaysHoursRemain() {
        val today = LocalDate(2026, 7, 22)
        val todayEvening = hourAt(2026, 7, 22, 22)
        val tomorrowMorning = hourAt(2026, 7, 23, 1)
        val hours = listOf(todayEvening, tomorrowMorning)

        val result = hours.todayOnly(today, Zone)

        assertEquals(listOf(todayEvening), result)
    }

    @Test
    fun givenEveryHourIsAfterToday_whenFilteredToToday_thenResultIsEmpty() {
        val today = LocalDate(2026, 7, 22)
        val hours = listOf(hourAt(2026, 7, 23, 0), hourAt(2026, 7, 24, 0))

        val result = hours.todayOnly(today, Zone)

        assertEquals(emptyList(), result)
    }

    private fun hourAt(year: Int, month: Int, day: Int, hour: Int): HourlyForecast = HourlyForecast(
        time = LocalDateTime(year, month, day, hour, 0).toInstant(Zone),
        temp = 20.0,
        pop = 0.0,
        windSpeed = 1.0,
    )
}
