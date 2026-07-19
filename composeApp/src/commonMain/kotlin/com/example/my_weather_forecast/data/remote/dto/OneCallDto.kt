package com.example.my_weather_forecast.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OneCallResponseDto(
    val lat: Double,
    val lon: Double,
    @SerialName("timezone_offset") val timezoneOffsetSeconds: Int,
    val current: CurrentDto,
    val hourly: List<HourlyDto>,
    val daily: List<DailyDto>,
)

@Serializable
data class CurrentDto(
    val dt: Long,
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val humidity: Int,
    @SerialName("wind_speed") val windSpeed: Double,
    val weather: List<WeatherConditionDto>,
)

@Serializable
data class HourlyDto(
    val dt: Long,
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val humidity: Int,
    @SerialName("wind_speed") val windSpeed: Double,
    val pop: Double,
    val weather: List<WeatherConditionDto>,
)

@Serializable
data class DailyDto(
    val dt: Long,
    val temp: DailyTempDto,
    val humidity: Int,
    @SerialName("wind_speed") val windSpeed: Double,
    val pop: Double,
    val weather: List<WeatherConditionDto>,
)

@Serializable
data class DailyTempDto(
    val min: Double,
    val max: Double,
)

@Serializable
data class WeatherConditionDto(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
)
