package com.example.my_weather_forecast.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.my_weather_forecast.domain.model.CurrentConditions
import com.example.my_weather_forecast.domain.model.DailyForecast
import com.example.my_weather_forecast.domain.model.Forecast
import com.example.my_weather_forecast.domain.model.HourlyForecast
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.model.WeatherCondition
import com.example.my_weather_forecast.domain.model.WeatherIcon
import com.example.my_weather_forecast.testutil.TestDispatcherProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class WeatherLocalDataSourceTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var dataSource: WeatherLocalDataSource

    private val location = Location(
        id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
    )
    private val forecast = Forecast(
        location = location,
        current = CurrentConditions(
            temp = 282.55,
            feelsLike = 281.87,
            humidity = 72,
            windSpeed = 3.6,
            pop = 0.35,
            condition = WeatherCondition(
                owmCode = 803, group = "Clouds", description = "broken clouds", icon = WeatherIcon.CLOUDS,
            ),
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
        hourly = listOf(
            HourlyForecast(time = Instant.fromEpochSeconds(1704124800), temp = 283.15, pop = 0.35, windSpeed = 4.1),
        ),
        units = Units.METRIC,
        fetchedAt = Instant.fromEpochSeconds(1704124800),
    )

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        WeatherDatabase.Schema.create(driver)
        val database = WeatherDatabase(driver, ForecastCache.Adapter(ForecastPayloadAdapter))
        dataSource = SqlDelightWeatherLocalDataSource(database.forecastCacheQueries, TestDispatcherProvider())
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun givenAForecastCacheRow_whenObserved_thenPayloadRoundTripsToDomainForecast() = runTest {
        dataSource.upsert(location.id, forecast)

        val result = dataSource.observe(location.id).first()

        assertEquals(forecast, result)
    }

    @Test
    fun givenNoCachedForecast_whenObserved_thenEmitsNull() = runTest {
        val result = dataSource.observe(999L).first()

        assertNull(result)
    }

    @Test
    fun givenACachedForecast_whenDeleted_thenObserveEmitsNull() = runTest {
        dataSource.upsert(location.id, forecast)

        dataSource.deleteByLocationId(location.id)

        assertNull(dataSource.observe(location.id).first())
    }
}
