package com.example.my_weather_forecast.testutil

import com.example.my_weather_forecast.domain.model.CurrentConditions
import com.example.my_weather_forecast.domain.model.DailyForecast
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.HourlyForecast
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.model.WeatherCondition
import com.example.my_weather_forecast.domain.model.WeatherIcon
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

fun sampleForecast(
    location: Location,
    fetchedAtEpochMillis: Long = 1704124800_000L,
    units: Units = Units.METRIC,
) = Forecast(
    location = location,
    current = CurrentConditions(
        temp = 21.0,
        feelsLike = 20.0,
        humidity = 55,
        windSpeed = 3.5,
        pop = 0.2,
        condition = WeatherCondition(owmCode = 803, group = "Clouds", description = "broken clouds", icon = WeatherIcon.CLOUDS, isDaytime = true),
    ),
    daily = listOf(
        DailyForecast(
            date = LocalDate(2024, 1, 1),
            tempMin = 15.0,
            tempMax = 24.0,
            humidity = 55,
            windSpeed = 3.5,
            pop = 0.2,
            condition = WeatherCondition(owmCode = 803, group = "Clouds", description = "broken clouds", icon = WeatherIcon.CLOUDS, isDaytime = true),
        ),
    ),
    hourly = listOf(
        HourlyForecast(time = Instant.fromEpochMilliseconds(fetchedAtEpochMillis), temp = 21.0, pop = 0.2, windSpeed = 3.5),
    ),
    units = units,
    fetchedAt = Instant.fromEpochMilliseconds(fetchedAtEpochMillis),
)
