package com.example.my_weather_forecast.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.testutil.TestDispatcherProvider
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
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
    private val forecast = sampleForecast(location)

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
