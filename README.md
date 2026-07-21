# Weather Forecast

A Kotlin Multiplatform weather app for Android and iOS, built with Compose Multiplatform.
Search for a city, save up to 6 areas, and see current conditions, a 7-day outlook, and an
hourly rain forecast — with offline-first caching and a metric/imperial toggle.

## Features

- **Overview** — saved areas at a glance (current temp, high/low, rain chance), pull-to-refresh,
  swipe-to-delete with undo.
- **Search** — debounced city search backed by OpenWeatherMap's geocoding API, capped at 6 saved
  areas.
- **Detail** — current conditions, an hourly rain strip, and a 7-day forecast per area.
- **Offline-first caching** — SQLDelight is the single source of truth; a 30-minute TTL plus
  request coalescing keep the UI responsive and avoid redundant network calls. Stale cache is
  still shown (flagged) if a refresh fails.
- **Units** — switch between metric (°C, m/s) and imperial (°F, mph); switching invalidates the
  cache and refetches in the new units. Persisted across restarts.
- **Accessible** — all user-facing strings are externalized resources; every screen is
  TalkBack/VoiceOver-navigable with content descriptions on interactive and informational
  elements.

## Tech stack

- **UI**: Compose Multiplatform, Material 3
- **DI**: Koin
- **Networking**: Ktor (OkHttp on Android, Darwin on iOS)
- **Persistence**: SQLDelight (Android SQLite driver / iOS native driver)
- **Preferences**: multiplatform-settings (SharedPreferences on Android, NSUserDefaults on iOS)
- **Serialization**: kotlinx-serialization, kotlinx-datetime
- **Testing**: kotlin-test, Turbine, Ktor MockEngine, Compose UI testing (instrumented)

## Project layout

* `/composeApp` is the shared Kotlin Multiplatform module.
  - `commonMain` holds the domain, data, and presentation layers shared across both platforms.
  - `androidMain` / `iosMain` hold only the platform-specific bindings (SQL driver, HTTP engine,
    settings storage, Koin platform module).
  - `commonTest` / `androidUnitTest` hold unit tests (ViewModels, repositories, mappers).
    `androidInstrumentedTest` holds Compose UI tests that run on a device/emulator.
* `/iosApp` is the iOS app shell (SwiftUI entry point hosting the shared Compose UI).

## Architecture

Clean Architecture with one-way dependencies: `presentation` → `domain` ← `data`. The `domain`
layer (models, repository interfaces, use cases) has no framework imports and is the one part of
the codebase that doesn't know Android, iOS, Compose, or SQLDelight exist. See
[`docs/adr/0001-architecture-and-caching.md`](docs/adr/0001-architecture-and-caching.md) for the
reasoning behind this and the caching strategy.

## Building

- Android: `./gradlew :composeApp:assembleDebug`
- iOS: open `iosApp/iosApp.xcodeproj` in Xcode, or `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp`
- Tests: `./gradlew :composeApp:testDebugUnitTest :composeApp:iosSimulatorArm64Test` (unit),
  `./gradlew :composeApp:connectedDebugAndroidTest` (instrumented, needs a running emulator/device)

An OpenWeatherMap API key with a One Call by Call subscription is required; it's read via
BuildKonfig from `local.properties` (`OWM_API_KEY=...`), not committed to source control.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html).
