package com.example.my_weather_forecast.presentation.detail

import com.example.my_weather_forecast.domain.model.Units
import myweatherforecast.composeapp.generated.resources.Res
import myweatherforecast.composeapp.generated.resources.unit_mph
import myweatherforecast.composeapp.generated.resources.unit_mps
import org.jetbrains.compose.resources.StringResource

internal fun Units.windSpeedUnitLabelRes(): StringResource = when (this) {
    Units.METRIC -> Res.string.unit_mps
    Units.IMPERIAL -> Res.string.unit_mph
}
