package com.example.my_weather_forecast.di

import com.example.my_weather_forecast.data.local.ForecastCache
import com.example.my_weather_forecast.data.local.ForecastPayloadAdapter
import com.example.my_weather_forecast.data.local.SavedLocationLocalDataSource
import com.example.my_weather_forecast.data.local.SqlDelightSavedLocationLocalDataSource
import com.example.my_weather_forecast.data.local.SqlDelightWeatherLocalDataSource
import com.example.my_weather_forecast.data.local.WeatherDatabase
import com.example.my_weather_forecast.data.local.WeatherLocalDataSource
import com.example.my_weather_forecast.data.remote.HttpClientFactory
import com.example.my_weather_forecast.data.remote.KtorWeatherRemoteDataSource
import com.example.my_weather_forecast.data.remote.WeatherRemoteDataSource
import com.example.my_weather_forecast.data.repository.CitySearchRepositoryImpl
import com.example.my_weather_forecast.data.repository.SavedLocationRepositoryImpl
import com.example.my_weather_forecast.data.repository.WeatherRepositoryImpl
import com.example.my_weather_forecast.domain.repository.CitySearchRepository
import com.example.my_weather_forecast.domain.repository.SavedLocationRepository
import com.example.my_weather_forecast.domain.repository.WeatherRepository
import com.example.my_weather_forecast.domain.usecase.AddLocationUseCase
import com.example.my_weather_forecast.domain.usecase.ObserveSavedLocationsUseCase
import com.example.my_weather_forecast.domain.usecase.RemoveLocationUseCase
import io.ktor.client.HttpClient
import org.koin.dsl.module

val dataModule = module {
    single { WeatherDatabase(get(), ForecastCache.Adapter(ForecastPayloadAdapter)) }
    single<SavedLocationLocalDataSource> { SqlDelightSavedLocationLocalDataSource(get<WeatherDatabase>().savedLocationQueries, get()) }
    single<WeatherLocalDataSource> { SqlDelightWeatherLocalDataSource(get<WeatherDatabase>().forecastCacheQueries, get()) }

    single { HttpClientFactory.create() }
    single<WeatherRemoteDataSource> { KtorWeatherRemoteDataSource(get<HttpClient>(), get<String>(OWM_API_KEY_QUALIFIER), get()) }

    single<SavedLocationRepository> { SavedLocationRepositoryImpl(get(), get()) }
    single<WeatherRepository> { WeatherRepositoryImpl(get(), get(), get(), get()) }
    single<CitySearchRepository> { CitySearchRepositoryImpl(get()) }

    factory { AddLocationUseCase(get()) }
    factory { RemoveLocationUseCase(get()) }
    factory { ObserveSavedLocationsUseCase(get()) }
}
