package com.example.my_weather_forecast.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.testutil.TestDispatcherProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SavedLocationLocalDataSourceTest {

    private lateinit var driver: JdbcSqliteDriver
    private lateinit var dataSource: SavedLocationLocalDataSource

    @BeforeTest
    fun setUp() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        WeatherDatabase.Schema.create(driver)
        val database = WeatherDatabase(driver, ForecastCache.Adapter(ForecastPayloadAdapter))
        dataSource = SqlDelightSavedLocationLocalDataSource(database.savedLocationQueries, TestDispatcherProvider())
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun givenAnEmptyDb_whenInsertThenObserve_thenFlowEmitsTheLocation() = runTest {
        val location = Location(
            id = 0, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
        )

        dataSource.insert(location)
        val result = dataSource.observeAll().first()

        assertEquals(1, result.size)
        assertEquals("Chicago", result.first().name)
        assertEquals("US", result.first().country)
        assertEquals("IL", result.first().state)
        assertEquals(41.85, result.first().lat)
        assertEquals(-87.65, result.first().lon)
    }

    @Test
    fun givenALocation_whenDeleted_thenFlowNoLongerEmitsIt() = runTest {
        dataSource.insert(
            Location(id = 0, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0),
        )
        val insertedId = dataSource.observeAll().first().first().id

        dataSource.deleteById(insertedId)

        assertEquals(emptyList(), dataSource.observeAll().first())
    }
}
