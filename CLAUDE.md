# CLAUDE.md

Kotlin Multiplatform weather-forecast app (Android + iOS) with **shared Compose
Multiplatform UI**. Single `:composeApp` module; package root
`com.example.my_weather_forecast`.

Plan and spec: `docs/weather-forecast-plan.md`. Idea/scope: `docs/weather-forecast-idea.md`.
Implement **one task from the plan at a time**, keeping the build green.

## Commands
- Build Android: `./gradlew :composeApp:assembleDebug`
- Unit tests (shared logic, JVM): `./gradlew :composeApp:testDebugUnitTest`
- iOS compile: `./gradlew :composeApp:compileKotlinIosSimulatorArm64`
- iOS tests: `./gradlew :composeApp:iosSimulatorArm64Test`
- Run the iOS app: open `iosApp/iosApp.xcodeproj` in Xcode
- Run each task's tests on **both** JVM and iOS before marking it done.

## Architecture
- Clean Architecture, package-based inside `:composeApp`:
  - `domain/` — models, repository interfaces, use cases. **No** framework imports.
  - `data/` — `remote/` (Ktor + DTOs), `local/` (SQLDelight), `mapper/`, `repository/` impls.
  - `presentation/` — `theme/` + per-feature `ViewModel` + `Screen` + `Content`.
  - `core/` (dispatcher, time, result), `di/` (Koin modules).
- MVVM. Dependencies point inward: presentation → domain ← data.
- DI: **Koin** in `commonMain`. Async: Coroutines + Flow. Inject a `DispatcherProvider`
  (`Dispatchers.IO` does not exist in `commonMain`).

## Stack
Ktor (HTTP) · kotlinx-serialization (JSON) · SQLDelight (cache) · kotlinx-datetime ·
Navigation-Compose · Compose Multiplatform / Material3. Keep `commonMain` free of
Android-only libraries.

## Conventions
- `StateFlow<UiState>` for screen state; `SharedFlow` for one-shot events (nav, snackbar).
- `UiState` = `sealed interface` with `Loading` / `Success` / `Error` (+ `Empty` where it
  applies); use `data object` for stateless cases.
- Split `XxxScreen` (Koin VM, `collectAsStateWithLifecycle`) from `XxxContent`
  (stateless, `@Preview` in light + dark). Every composable takes `modifier: Modifier = Modifier`.
- Hoist state out of composables. Use Material3 tokens only — no hardcoded colors/sizes.
- Three model families stay separate: DTO (`data/remote`) ↔ SQLDelight row (`data/local`)
  ↔ domain model. Map in `data/mapper`; never leak DTOs into presentation.

## Testing (KMP — read this)
- Tests live in `commonTest`: **kotlin-test + hand-written fakes + Turbine +
  kotlinx-coroutines-test + Ktor `MockEngine` + SQLDelight in-memory driver**.
- Do **not** use MockK / Hilt / Robolectric / JUnit5 / MockWebServer in shared code —
  they are JVM-only.
- TDD: RED → GREEN → REFACTOR. Name tests given/when/then, e.g.
  `` `given stale cache and offline, when observed, then emits cached forecast`() ``.

## Caching & API
- **OpenWeatherMap One Call 3.0**; up to 6 saved areas; request with
  `exclude=minutely,alerts` and a `units` param.
- SQLDelight is the single source of truth — the UI observes DB Flows and **never** calls
  the network directly.
- TTL (default 30 min) + request coalescing; serve stale data when offline; pull-to-refresh
  bypasses TTL. Stay under 1,000 calls/day.
- API key comes from `local.properties` (`owm.apiKey`) via BuildKonfig — **never commit the key**.

## Hygiene
Keep the build green after every change. No `TODO`/`FIXME`, no dead code, no debug logging,
imports minimal. One logical change per commit.
