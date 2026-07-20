package com.example.my_weather_forecast.presentation.theme

import com.example.my_weather_forecast.domain.model.WeatherIcon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherIconMapperTest {

    @Test
    fun givenEachWeatherIcon_whenMapped_thenEachResolvesToADistinctDrawable() {
        val resources = WeatherIcon.entries.map { it.toDrawableResource() }

        assertEquals(WeatherIcon.entries.size, resources.toSet().size)
    }

    @Test
    fun givenEachWeatherIcon_whenMappedToAReadableName_thenEachIsNonBlankAndDistinct() {
        val names = WeatherIcon.entries.map { it.readableName() }

        assertEquals(WeatherIcon.entries.size, names.toSet().size)
        assertTrue(names.all { it.isNotBlank() })
    }
}
