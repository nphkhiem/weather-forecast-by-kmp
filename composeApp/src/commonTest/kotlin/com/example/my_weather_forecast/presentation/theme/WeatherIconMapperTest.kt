package com.example.my_weather_forecast.presentation.theme

import com.example.my_weather_forecast.domain.model.WeatherIcon
import kotlin.test.Test
import kotlin.test.assertEquals

class WeatherIconMapperTest {

    @Test
    fun givenEachWeatherIcon_whenMapped_thenEachResolvesToADistinctDrawable() {
        val resources = WeatherIcon.entries.map { it.toDrawableResource() }

        assertEquals(WeatherIcon.entries.size, resources.toSet().size)
    }

    @Test
    fun givenEachWeatherIcon_whenMappedToAReadableNameResource_thenEachIsDistinct() {
        val resources = WeatherIcon.entries.map { it.readableNameRes() }

        assertEquals(WeatherIcon.entries.size, resources.map { it.key }.toSet().size)
    }
}
