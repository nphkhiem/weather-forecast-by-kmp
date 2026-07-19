package com.example.my_weather_forecast.testutil

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Proves the JDBC in-memory driver opens on the JVM test target. No schema exists yet
 * (no .sq files until Phase 2/T2.1), so this only spins up the raw driver.
 */
class SqlDelightInMemoryDriverTest {

    private lateinit var driver: SqlDriver

    @AfterTest
    fun tearDown() {
        driver.close()
    }

    @Test
    fun givenInMemorySqliteDriver_whenOpened_thenDriverIsUsable() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

        assertNotNull(driver)
    }
}
