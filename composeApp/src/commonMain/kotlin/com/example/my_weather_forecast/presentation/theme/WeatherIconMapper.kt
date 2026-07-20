package com.example.my_weather_forecast.presentation.theme

import com.example.my_weather_forecast.domain.model.WeatherIcon
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.ic_weather_atmosphere
import myweatherforecast.composeapp.generated.resources.ic_weather_clear
import myweatherforecast.composeapp.generated.resources.ic_weather_clouds
import myweatherforecast.composeapp.generated.resources.ic_weather_drizzle
import myweatherforecast.composeapp.generated.resources.ic_weather_rain
import myweatherforecast.composeapp.generated.resources.ic_weather_snow
import myweatherforecast.composeapp.generated.resources.ic_weather_thunderstorm
import myweatherforecast.composeapp.generated.resources.ic_weather_unknown
import org.jetbrains.compose.resources.DrawableResource

fun WeatherIcon.toDrawableResource(): DrawableResource = when (this) {
    WeatherIcon.THUNDERSTORM -> Res.drawable.ic_weather_thunderstorm
    WeatherIcon.DRIZZLE -> Res.drawable.ic_weather_drizzle
    WeatherIcon.RAIN -> Res.drawable.ic_weather_rain
    WeatherIcon.SNOW -> Res.drawable.ic_weather_snow
    WeatherIcon.ATMOSPHERE -> Res.drawable.ic_weather_atmosphere
    WeatherIcon.CLEAR -> Res.drawable.ic_weather_clear
    WeatherIcon.CLOUDS -> Res.drawable.ic_weather_clouds
    WeatherIcon.UNKNOWN -> Res.drawable.ic_weather_unknown
}
