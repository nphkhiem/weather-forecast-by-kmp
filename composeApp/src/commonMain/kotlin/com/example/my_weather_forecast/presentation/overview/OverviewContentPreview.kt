package com.example.my_weather_forecast.presentation.overview

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

private fun previewArea(id: Long, name: String, stale: Boolean = false) = AreaSummary(
    id = id,
    name = name,
    currentTemp = 21.0,
    icon = WeatherIcon.entries[(id % WeatherIcon.entries.size).toInt()],
    todayHigh = 24.0,
    todayLow = 15.0,
    rainChance = 0.3,
    stale = stale,
)

private val oneArea = listOf(previewArea(1, "Chicago"))

private val sixAreas = listOf(
    previewArea(1, "Chicago"),
    previewArea(2, "London", stale = true),
    previewArea(3, "Tokyo"),
    previewArea(4, "Sydney"),
    previewArea(5, "Cairo"),
    previewArea(6, "Reykjavik"),
)

@Composable
private fun OverviewContentPreview(uiState: OverviewUiState, darkTheme: Boolean) {
    WeatherForecastTheme(darkTheme = darkTheme) {
        Surface {
            OverviewContent(uiState = uiState, onAreaClick = {}, onRemove = {})
        }
    }
}

@Preview
@Composable
private fun OverviewLoadingLightPreview() = OverviewContentPreview(OverviewUiState.Loading, darkTheme = false)

@Preview
@Composable
private fun OverviewLoadingDarkPreview() = OverviewContentPreview(OverviewUiState.Loading, darkTheme = true)

@Preview
@Composable
private fun OverviewEmptyLightPreview() = OverviewContentPreview(OverviewUiState.Empty, darkTheme = false)

@Preview
@Composable
private fun OverviewEmptyDarkPreview() = OverviewContentPreview(OverviewUiState.Empty, darkTheme = true)

@Preview
@Composable
private fun OverviewErrorLightPreview() = OverviewContentPreview(OverviewUiState.Error(WeatherError.Network), darkTheme = false)

@Preview
@Composable
private fun OverviewErrorDarkPreview() = OverviewContentPreview(OverviewUiState.Error(WeatherError.Network), darkTheme = true)

@Preview
@Composable
private fun OverviewSuccessOneAreaLightPreview() = OverviewContentPreview(OverviewUiState.Success(oneArea), darkTheme = false)

@Preview
@Composable
private fun OverviewSuccessOneAreaDarkPreview() = OverviewContentPreview(OverviewUiState.Success(oneArea), darkTheme = true)

@Preview
@Composable
private fun OverviewSuccessSixAreasLightPreview() = OverviewContentPreview(OverviewUiState.Success(sixAreas), darkTheme = false)

@Preview
@Composable
private fun OverviewSuccessSixAreasDarkPreview() = OverviewContentPreview(OverviewUiState.Success(sixAreas), darkTheme = true)
