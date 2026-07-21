package com.example.my_weather_forecast.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.my_weather_forecast.core.time.TimeProvider
import com.example.my_weather_forecast.data.local.WeatherDatabase
import com.example.my_weather_forecast.data.local.WeatherLocalDataSource
import com.example.my_weather_forecast.data.remote.WeatherRemoteDataSource
import com.example.my_weather_forecast.domain.model.ForecastObservation
import com.example.my_weather_forecast.domain.model.Location
import com.example.my_weather_forecast.domain.model.Units
import com.example.my_weather_forecast.domain.repository.CitySearchRepository
import com.example.my_weather_forecast.domain.repository.SavedLocationRepository
import com.example.my_weather_forecast.domain.repository.WeatherRepository
import com.example.my_weather_forecast.domain.usecase.AddLocationUseCase
import com.example.my_weather_forecast.domain.usecase.ObserveSavedLocationsUseCase
import com.example.my_weather_forecast.domain.usecase.RemoveLocationUseCase
import com.example.my_weather_forecast.presentation.detail.DetailViewModel
import com.example.my_weather_forecast.presentation.overview.OverviewViewModel
import com.example.my_weather_forecast.presentation.search.SearchViewModel
import com.example.my_weather_forecast.testutil.sampleForecast
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DataModuleKoinGraphTest {

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    private fun startTestKoin(): Koin {
        val inMemoryDriverModule = module {
            single<SqlDriver> { JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { WeatherDatabase.Schema.create(it) } }
        }
        return startKoin { modules(appModule, dataModule, presentationModule, inMemoryDriverModule) }.koin
    }

    @Test
    fun givenTheFullDataGraph_whenResolvingEveryBinding_thenNoneAreMissing() {
        val koin = startTestKoin()

        assertNotNull(koin.get<WeatherLocalDataSource>())
        assertNotNull(koin.get<WeatherRemoteDataSource>())
        assertNotNull(koin.get<SavedLocationRepository>())
        assertNotNull(koin.get<WeatherRepository>())
        assertNotNull(koin.get<CitySearchRepository>())
        assertNotNull(koin.get<AddLocationUseCase>())
        assertNotNull(koin.get<RemoveLocationUseCase>())
        assertNotNull(koin.get<ObserveSavedLocationsUseCase>())
        assertNotNull(koin.get<OverviewViewModel>())
        assertNotNull(koin.get<SearchViewModel>())
        assertNotNull(koin.get<DetailViewModel> { parametersOf(1L) })
    }

    @Test
    fun givenAResolvedWeatherRepository_whenObserved_thenReadsFromTheSameInMemoryDatabase() = runTest {
        val koin = startTestKoin()
        val location = Location(
            id = 1, name = "Chicago", country = "US", state = "IL", lat = 41.85, lon = -87.65, sortOrder = 0,
        )
        val fresh = sampleForecast(location, fetchedAtEpochMillis = koin.get<TimeProvider>().nowEpochMillis())
        koin.get<WeatherLocalDataSource>().upsert(location.id, fresh)

        val result = koin.get<WeatherRepository>().observe(location, Units.METRIC).first()

        assertEquals(ForecastObservation.Success(fresh, stale = false), result)
    }
}
