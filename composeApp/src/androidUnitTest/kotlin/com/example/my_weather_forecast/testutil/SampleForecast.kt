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

fun sampleForecast(location: Location) = Forecast(
    location = location,
    current = CurrentConditions(
        temp = 282.55,
        feelsLike = 281.87,
        humidity = 72,
        windSpeed = 3.6,
        pop = 0.35,
        condition = WeatherCondition(owmCode = 803, group = "Clouds", description = "broken clouds", icon = WeatherIcon.CLOUDS),
    ),
    daily = listOf(
        DailyForecast(
            date = LocalDate(2024, 1, 1),
            tempMin = 275.15,
            tempMax = 283.15,
            humidity = 80,
            windSpeed = 5.2,
            pop = 0.6,
            condition = WeatherCondition(owmCode = 500, group = "Rain", description = "light rain", icon = WeatherIcon.RAIN),
        ),
    ),
    hourly = listOf(HourlyForecast(time = Instant.fromEpochSeconds(1704124800), temp = 283.15, pop = 0.35, windSpeed = 4.1)),
    units = Units.METRIC,
    fetchedAt = Instant.fromEpochSeconds(1704124800),
)
