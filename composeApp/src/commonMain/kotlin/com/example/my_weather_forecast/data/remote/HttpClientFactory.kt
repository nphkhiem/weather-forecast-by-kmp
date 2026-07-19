package com.example.my_weather_forecast.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/** Shared plugin config, applied to both the production client and MockEngine-backed test clients. */
fun HttpClientConfig<*>.installWeatherClientPlugins() {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    install(Logging) {
        level = LogLevel.INFO
    }
}

object HttpClientFactory {
    // No explicit engine: androidMain/iosMain each declare exactly one Ktor engine dependency
    // (OkHttp / Darwin), which Ktor resolves automatically per platform.
    fun create(): HttpClient = HttpClient { installWeatherClientPlugins() }
}
