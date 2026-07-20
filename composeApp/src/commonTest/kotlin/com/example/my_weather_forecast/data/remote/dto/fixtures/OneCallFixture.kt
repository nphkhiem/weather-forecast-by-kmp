package com.example.my_weather_forecast.data.remote.dto.fixtures

/**
 * A real One Call 3.0 response shape (America/Chicago, UTC-6, captured 2024-01-01) trimmed to
 * the fields this app reads, with a couple of untouched fields (sunrise, pressure, uvi) left in
 * to prove unknown-key tolerance. `current` deliberately has no `pop` field, matching the real API.
 */
val ONE_CALL_SAMPLE_JSON = """
{
  "lat": 41.85,
  "lon": -87.65,
  "timezone": "America/Chicago",
  "timezone_offset": -21600,
  "current": {
    "dt": 1704124800,
    "sunrise": 1704112200,
    "pressure": 1012,
    "uvi": 1.2,
    "temp": 282.55,
    "feels_like": 281.87,
    "humidity": 72,
    "wind_speed": 3.6,
    "weather": [
      { "id": 803, "main": "Clouds", "description": "broken clouds", "icon": "04d" }
    ]
  },
  "hourly": [
    {
      "dt": 1704124800,
      "temp": 283.15,
      "feels_like": 282.00,
      "humidity": 70,
      "wind_speed": 4.1,
      "pop": 0.35,
      "weather": [
        { "id": 500, "main": "Rain", "description": "light rain", "icon": "10d" }
      ]
    },
    {
      "dt": 1704128400,
      "temp": 284.0,
      "feels_like": 283.0,
      "humidity": 68,
      "wind_speed": 4.3,
      "pop": 0.4,
      "weather": [
        { "id": 501, "main": "Rain", "description": "moderate rain", "icon": "10d" }
      ]
    },
    {
      "dt": 1704132000,
      "temp": 285.0,
      "feels_like": 284.0,
      "humidity": 65,
      "wind_speed": 4.5,
      "pop": 0.45,
      "weather": [
        { "id": 500, "main": "Rain", "description": "light rain", "icon": "10d" }
      ]
    }
  ],
  "daily": [
    {
      "dt": 1704132000,
      "sunrise": 1704112200,
      "temp": { "day": 280.0, "min": 275.15, "max": 283.15, "night": 276.0, "eve": 279.0, "morn": 275.5 },
      "humidity": 80,
      "wind_speed": 5.2,
      "pop": 0.6,
      "weather": [
        { "id": 500, "main": "Rain", "description": "light rain", "icon": "10d" }
      ]
    },
    {
      "dt": 1704218400,
      "temp": { "min": 276.0, "max": 284.0 },
      "humidity": 78,
      "wind_speed": 5.0,
      "pop": 0.5,
      "weather": [
        { "id": 501, "main": "Rain", "description": "moderate rain", "icon": "10d" }
      ]
    },
    {
      "dt": 1704304800,
      "temp": { "min": 270.0, "max": 280.0 },
      "humidity": 60,
      "wind_speed": 3.5,
      "pop": 0.1,
      "weather": [
        { "id": 800, "main": "Clear", "description": "clear sky", "icon": "01d" }
      ]
    },
    {
      "dt": 1704391200,
      "temp": { "min": 268.0, "max": 278.0 },
      "humidity": 55,
      "wind_speed": 3.0,
      "pop": 0.0,
      "weather": [
        { "id": 801, "main": "Clouds", "description": "few clouds", "icon": "02d" }
      ]
    },
    {
      "dt": 1704477600,
      "temp": { "min": 265.0, "max": 275.0 },
      "humidity": 50,
      "wind_speed": 2.8,
      "pop": 0.05,
      "weather": [
        { "id": 803, "main": "Clouds", "description": "broken clouds", "icon": "04d" }
      ]
    },
    {
      "dt": 1704564000,
      "temp": { "min": 263.0, "max": 273.0 },
      "humidity": 48,
      "wind_speed": 2.5,
      "pop": 0.2,
      "weather": [
        { "id": 600, "main": "Snow", "description": "light snow", "icon": "13d" }
      ]
    },
    {
      "dt": 1704650400,
      "temp": { "min": 260.0, "max": 270.0 },
      "humidity": 45,
      "wind_speed": 2.2,
      "pop": 0.15,
      "weather": [
        { "id": 701, "main": "Mist", "description": "mist", "icon": "50d" }
      ]
    },
    {
      "dt": 1704736800,
      "temp": { "min": 258.0, "max": 268.0 },
      "humidity": 42,
      "wind_speed": 2.0,
      "pop": 0.1,
      "weather": [
        { "id": 804, "main": "Clouds", "description": "overcast clouds", "icon": "04d" }
      ]
    }
  ]
}
""".trimIndent()
