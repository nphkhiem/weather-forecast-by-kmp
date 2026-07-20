package com.example.my_weather_forecast.data.mapper

import com.example.my_weather_forecast.data.local.SavedLocation
import com.example.my_weather_forecast.domain.model.Location

fun SavedLocation.toDomain(): Location = Location(
    id = id,
    name = name,
    country = country,
    state = state,
    lat = lat,
    lon = lon,
    sortOrder = sortOrder.toInt(),
)
