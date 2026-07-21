package com.example.my_weather_forecast.presentation.detail

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.my_weather_forecast.core.result.WeatherError
import com.example.my_weather_forecast.domain.model.CurrentConditions
import com.example.my_weather_forecast.domain.model.DailyForecast
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.HourlyForecast
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.model.WeatherCondition
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.presentation.theme.WeatherForecastTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview

private val chicago = Location(id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0)

private val clearSky = WeatherCondition(owmCode = 800, group = "Clear", description = "clear sky", icon = WeatherIcon.CLEAR)

private val previewForecast = Forecast(
    location = chicago,
    current = CurrentConditions(temp = 22.0, feelsLike = 21.0, humidity = 60, windSpeed = 4.5, pop = 0.2, condition = clearSky),
    daily = (0..6).map { offset ->
        DailyForecast(
            date = LocalDate(2024, 1, 1 + offset),
            tempMin = 15.0 + offset,
            tempMax = 24.0 + offset,
            humidity = 55 + offset,
            windSpeed = 3.0 + offset,
            pop = 0.1 * offset,
            condition = clearSky,
        )
    },
    hourly = (0..11).map { hour ->
        HourlyForecast(
            time = Instant.fromEpochMilliseconds(1704124800_000L + hour * 3_600_000L),
            temp = 20.0 + hour,
            pop = 0.05 * hour,
            windSpeed = 3.0,
        )
    },
    units = Units.METRIC,
    fetchedAt = Instant.fromEpochMilliseconds(1704124800_000L),
)

@Composable
private fun DetailContentPreview(uiState: DetailUiState, darkTheme: Boolean) {
    WeatherForecastTheme(darkTheme = darkTheme) {
        Surface {
            DetailContent(uiState = uiState)
        }
    }
}

@Preview
@Composable
private fun DetailLoadingLightPreview() = DetailContentPreview(DetailUiState.Loading, darkTheme = false)

@Preview
@Composable
private fun DetailLoadingDarkPreview() = DetailContentPreview(DetailUiState.Loading, darkTheme = true)

@Preview
@Composable
private fun DetailErrorLightPreview() = DetailContentPreview(DetailUiState.Error(WeatherError.Network), darkTheme = false)

@Preview
@Composable
private fun DetailErrorDarkPreview() = DetailContentPreview(DetailUiState.Error(WeatherError.Network), darkTheme = true)

@Preview
@Composable
private fun DetailSuccessFreshLightPreview() = DetailContentPreview(
    DetailUiState.Success(previewForecast, stale = false, lastUpdated = previewForecast.fetchedAt),
    darkTheme = false,
)

@Preview
@Composable
private fun DetailSuccessFreshDarkPreview() = DetailContentPreview(
    DetailUiState.Success(previewForecast, stale = false, lastUpdated = previewForecast.fetchedAt),
    darkTheme = true,
)

@Preview
@Composable
private fun DetailSuccessStaleLightPreview() = DetailContentPreview(
    DetailUiState.Success(previewForecast, stale = true, lastUpdated = previewForecast.fetchedAt),
    darkTheme = false,
)

@Preview
@Composable
private fun DetailSuccessStaleDarkPreview() = DetailContentPreview(
    DetailUiState.Success(previewForecast, stale = true, lastUpdated = previewForecast.fetchedAt),
    darkTheme = true,
)
