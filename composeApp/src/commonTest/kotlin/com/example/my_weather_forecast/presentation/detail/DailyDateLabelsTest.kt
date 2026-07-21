package com.example.my_weather_forecast.presentation.detail

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DailyDateLabelsTest {

    @Test
    fun givenAWeekEntirelyWithinOneYear_whenLabeled_thenEveryDateIsPlainDdMm() {
        val dates = (21..27).map { LocalDate(2026, 7, it) }

        val labels = dates.dailyDateLabels()

        assertEquals(listOf("21/07", "22/07", "23/07", "24/07", "25/07", "26/07", "27/07"), labels)
    }

    @Test
    fun givenAWeekCrossingIntoANewYear_whenLabeled_thenOnlyJanFirstGetsTheYear() {
        val dates = listOf(
            LocalDate(2025, 12, 29),
            LocalDate(2025, 12, 30),
            LocalDate(2025, 12, 31),
            LocalDate(2026, 1, 1),
            LocalDate(2026, 1, 2),
            LocalDate(2026, 1, 3),
            LocalDate(2026, 1, 4),
        )

        val labels = dates.dailyDateLabels()

        assertEquals(
            listOf("29/12", "30/12", "31/12", "01/01/2026", "02/01", "03/01", "04/01"),
            labels,
        )
    }

    @Test
    fun givenAWeekStartingOnJanFirstWithNoVisibleDecemberThirtyFirst_whenLabeled_thenJanFirstStaysPlain() {
        val dates = (1..7).map { LocalDate(2026, 1, it) }

        val labels = dates.dailyDateLabels()

        assertEquals(listOf("01/01", "02/01", "03/01", "04/01", "05/01", "06/01", "07/01"), labels)
    }
}
