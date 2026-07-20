package com.example.my_weather_forecast.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.my_weather_forecast.data.local.ForecastCache
import com.example.my_weather_forecast.data.local.ForecastPayloadAdapter
import com.example.my_weather_forecast.data.local.SqlDelightSavedLocationLocalDataSource
import com.example.my_weather_forecast.data.local.SqlDelightWeatherLocalDataSource
import com.example.my_weather_forecast.data.local.WeatherDatabase
import com.example.my_weather_forecast.data.local.WeatherLocalDataSource
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.testutil.TestDispatcherProvider
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SavedLocationRepositoryImplTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var repository: SavedLocationRepositoryImpl
    private lateinit var weatherLocalDataSource: WeatherLocalDataSource

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        WeatherDatabase.Schema.create(driver)
        val database = WeatherDatabase(driver, ForecastCache.Adapter(ForecastPayloadAdapter))
        val dispatcherProvider = TestDispatcherProvider()
        val savedLocationLocalDataSource = SqlDelightSavedLocationLocalDataSource(database.savedLocationQueries, dispatcherProvider)
        weatherLocalDataSource = SqlDelightWeatherLocalDataSource(database.forecastCacheQueries, dispatcherProvider)
        repository = SavedLocationRepositoryImpl(savedLocationLocalDataSource, weatherLocalDataSource)
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun givenASavedLocationWithACachedForecast_whenRemove_thenGoneFromFlowAndForecastCachePurged() = runTest {
        val location = Location(
            id = 0, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
        )
        repository.add(location)
        val insertedId = repository.observeAll().first().first().id
        weatherLocalDataSource.upsert(insertedId, sampleForecast(location.copy(id = insertedId)))

        repository.remove(insertedId)

        assertTrue(repository.observeAll().first().isEmpty())
        assertNull(weatherLocalDataSource.observe(insertedId).first())
    }
}
