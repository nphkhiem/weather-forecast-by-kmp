package com.example.my_weather_forecast.data.local

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull

/** Proves the native SQLite driver opens the real generated schema on the iOS test target. */
class NativeSqlDelightDriverTest {

    private lateinit var driver: NativeSqliteDriver

    @BeforeTest
    fun setUp() {
        driver = NativeSqliteDriver(WeatherDatabase.Schema, ":memory:")
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun givenTheGeneratedSchema_whenNativeDriverOpens_thenDatabaseIsUsable() {
        val database = WeatherDatabase(driver, ForecastCache.Adapter(ForecastPayloadAdapter))

        assertNotNull(database)
    }
}
