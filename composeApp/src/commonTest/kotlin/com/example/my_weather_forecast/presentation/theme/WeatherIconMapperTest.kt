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
}
