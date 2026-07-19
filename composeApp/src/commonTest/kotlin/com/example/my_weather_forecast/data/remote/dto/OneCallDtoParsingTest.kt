package com.example.my_weather_forecast.data.remote.dto

import com.example.my_weather_forecast.data.remote.dto.fixtures.ONE_CALL_SAMPLE_JSON
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OneCallDtoParsingTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun givenARealOneCallJson_whenParsed_thenDailyHasEightPopulatedEntries() {
        val response = json.decodeFromString(OneCallResponseDto.serializer(), ONE_CALL_SAMPLE_JSON)

        assertEquals(8, response.daily.size)
        response.daily.forEach { day ->
            assertTrue(day.pop in 0.0..1.0)
            assertTrue(day.humidity in 0..100)
            assertTrue(day.windSpeed >= 0.0)
            assertTrue(day.temp.min <= day.temp.max)
            assertTrue(day.weather.isNotEmpty())
        }
    }

    @Test
    fun givenARealOneCallJson_whenParsed_thenCurrentAndHourlyFieldsArePopulated() {
        val response = json.decodeFromString(OneCallResponseDto.serializer(), ONE_CALL_SAMPLE_JSON)

        assertEquals(282.55, response.current.temp)
        assertEquals(72, response.current.humidity)
        assertEquals(803, response.current.weather.first().id)
        assertEquals(3, response.hourly.size)
        assertEquals(0.35, response.hourly.first().pop)
    }
}
