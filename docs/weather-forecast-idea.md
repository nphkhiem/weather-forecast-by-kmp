# Weather Forecast (KMP) — Refined Idea

> Output of `android-idea-refine`. Input to the spec + plan in
> `docs/weather-forecast-plan.md`. Scope: Android + iOS via Kotlin Multiplatform,
> shared Compose Multiplatform UI.

## Problem Statement

**HMW:** How might we let a user watch the weather for the handful of places
they care about (home, family, a trip) — today plus the next 7 days, with rain,
wind, and humidity — in a clean shared-UI app that stays fast and nearly free of
API calls?

One sentence: *see today + 7-day forecasts for up to 6 saved cities, on Android
and iOS, from a single shared codebase, backed by an aggressive cache.*

## Repository Starting Point (analyzed)

- Fresh **Compose Multiplatform** wizard scaffold. Single `:composeApp` module with
  `commonMain` / `androidMain` / `iosMain`; the Android app entry point lives in
  `androidMain`. **UI is shared** (Compose), not per-platform SwiftUI.
- Toolchain: Kotlin 2.2.0, AGP 8.7.3, Compose MP 1.8.2, minSdk 24 / target 35.
- Already wired in the version catalog: **Ktor 3.2.1**, **Koin 4.1.0**,
  Navigation-Compose, Lifecycle-ViewModel (KMP), Coroutines.
- Not yet present: kotlinx-serialization + Ktor content-negotiation/logging,
  kotlinx-datetime, any persistence layer, and a real test stack.
- Cruft / blockers to clear first: `App.kt` has a syntax error (`Text(text = )` with
  no argument — **the project does not compile as-is**); `Greeting.kt` is leftover
  template; `data/WeatherRemoteDataSource.kt` is an empty interface; the only test is
  a placeholder `assertEquals(3, 1 + 2)`.

**Implication:** this is a from-scaffold build, and the stack the wizard chose
(Ktor + Koin + Compose MP + shared Viewmodel) is already the KMP-idiomatic set, so
the refinement is about *shape and sequencing*, not re-platforming.

## Locked Decisions (from clarifying questions)

| Area | Decision | Consequence |
|---|---|---|
| Weather API | **OpenWeatherMap One Call 3.0** | One request per area returns current + 48h hourly + **8-day daily (today + 7)**; free tier **1,000 calls/day** (card on file, charged only beyond free). Geocoding via the separate free Geocoding API. |
| Caching | **SQLDelight** | Typed SQL + reactive `Flow` queries + robust per-area TTL; single source of truth for the UI. |
| Location input | **City search only** | Geocoding-backed search; **no location permission** needed — removes the biggest production adoption risk. |
| Multi-area UI | **Overview list → detail** | Compact card per area (all 6 visible at a glance); tap opens full 7-day + hourly. iOS-Weather pattern; scales cleanly. |

## Divergent Exploration (lenses)

Kept the ones that changed the design; discarded the rest deliberately (see *Not Doing*).

- **Platform-native:** home-screen widget + a daily "rain likely today" notification.
  *Verdict:* high value but a separate surface with its own background-work and
  per-platform cost (Glance on Android, WidgetKit on iOS — not shareable). **Defer.**
- **Offline-first:** treat the network as optional; the DB is the source of truth and
  the UI always renders the last good forecast with a "last updated" stamp.
  *Verdict:* **adopt as the core pattern** — it directly serves the "don't hammer the
  API" goal and gives free offline support.
- **Constraint removal (calls are free):** if calls were unlimited we'd refresh on
  every open. Because they are not, TTL + coalescing is a *feature*, not a nicety.
  *Verdict:* makes caching a first-class, tested component, not an afterthought.
- **Simplification (10× fewer screens):** could the whole app be one screen?
  *Verdict:* the overview card list *is* the 1-screen version; detail is the only
  drill-down. Three screens total (Overview, Search, Detail). Hold the line there.
- **Inversion (proactive push):** app tells you when rain is coming instead of you
  opening it. *Verdict:* this is the widget/notification idea — **defer** to keep MVP
  reactive and permission-free.
- **10× scale:** 10M users — the client architecture is unaffected (per-device cache,
  per-device key). The real limit is the *account's* 1,000/day key budget, which is a
  per-user client concern the TTL already bounds.

## Converged Directions

**Direction A — "Cached glanceable dashboard" (RECOMMENDED).**
Shared Compose UI, three screens, SQLDelight as single source of truth, One Call 3.0
per area, strict TTL + request coalescing, city-search onboarding, no permissions, no
background work. Everything the requirements ask for and nothing that risks the
call budget or adoption. This is the MVP.

**Direction B — "Proactive weather companion."**
A on top of widgets + a scheduled "rain today" notification. Adds Glance/WidgetKit
(non-shared, per-platform), background scheduling, and notification permission. High
delight, materially more surface area and platform-specific code. **Phase 2 product.**

**Direction C — "Comparison board."**
Optimize for viewing all 6 areas *simultaneously* (side-by-side metrics, sortable by
"driest tomorrow", etc.). Interesting for trip planning but denser UX and more layout
work; the recommended overview already shows all areas at a glance. **Optional later.**

## KMP / Android Platform Constraints

- **minSdk 24**, compile/target 35. No API-level blockers: Ktor, SQLDelight,
  kotlinx-datetime, Compose MP all support 24.
- **Shared UI** in `commonMain` via Compose MP → iOS gets the same screens for free;
  no SwiftUI screen work in the MVP.
- **`Dispatchers.IO` does not exist in `commonMain`** → inject a `DispatcherProvider`
  with `expect`/`actual` IO dispatcher.
- **Secrets:** the API key must not be committed. Inject from `local.properties` /
  Gradle into `commonMain` (BuildKonfig, or an `expect val` reading `BuildConfig` on
  Android and `Info.plist` on iOS).
- **Permissions:** **none** for the MVP (city search, not GPS). No `INTERNET` change
  needed on Android (already declared); iOS needs no ATS exception for HTTPS.
- **Background work:** **none** in the MVP. Refresh happens only in the foreground,
  bounded by TTL — which is what keeps us far under 1,000 calls/day.
- **Testing reality:** MockK/Hilt/Robolectric/JUnit5 from the generic Android stack are
  JVM-only. Shared code is tested in `commonTest` with **kotlin-test + fakes + Turbine +
  kotlinx-coroutines-test + Ktor MockEngine + SQLDelight in-memory driver**. (Detailed
  in the plan.)

## Call-Budget Sanity Check (the core requirement)

Worst case, 6 areas, foreground refresh gated by a 30-minute TTL:
`6 areas × (60/30) refreshes/hr × ~16 waking hrs ≈ 192 calls/day` — and realistically
far fewer because refresh only fires when a stale area is actually observed. Even a
15-minute TTL stays under ~600/day. **Comfortably inside the 1,000/day free tier**,
with manual pull-to-refresh as the only bypass. Geocoding calls happen only while the
user is actively searching to add a city.

## Key Assumptions to Validate

- [ ] **One Call 3.0 is enabled on the user's key.** It is a distinct subscription from
  the classic free endpoints; a key that only has `/data/2.5/*` will 401 on
  `/data/3.0/onecall`. *Validate first thing in Phase 1 with one real call.*
- [ ] `daily` really returns 8 entries (today + 7) with `pop`, `humidity`, `wind_speed`,
  `temp.{min,max}`, `weather[]`. *Validate by capturing a real response fixture.*
- [ ] A 30-minute TTL feels fresh enough to users while holding the budget.
  *Validate by making TTL a single named constant and reviewing after dogfooding.*
- [ ] Shared Compose UI renders acceptably on iOS (fonts, safe areas, scroll feel).
  *Validate on an iOS simulator at the end of the first UI phase, not at the end.*
- [ ] SQLDelight native driver + Ktor Darwin engine behave under the new K/N memory
  model. *Validate with an iOS unit test in the persistence phase.*

## MVP Scope

Three shared screens plus the data spine:

1. **Overview** — LazyColumn of area cards (name, current temp + icon, today hi/lo,
   rain %). Empty state → "Add a city". Pull-to-refresh. FAB → Search.
2. **Search / Add** — debounced city search via Geocoding; tap a result to save;
   enforce the **max 6** rule with a clear message at the limit.
3. **Detail** — current conditions header; 7-day list (day, icon, hi/lo, rain %, wind,
   humidity); an hourly rain-probability strip; "last updated" + stale indicator.

Data spine: DTOs → domain models, remote data source (One Call + Geocoding), SQLDelight
cache, and a `WeatherRepository` that is the single source of truth (observe cache →
TTL check → refresh → re-emit), all under Clean-Architecture layers and covered by
given/when/then tests.

## Not Doing (and why)

- **Home-screen widgets / live notifications** — per-platform (Glance + WidgetKit), not
  shareable; a separate product phase. Adds background work + notification permission.
- **GPS / current-location** — avoids a runtime permission users frequently deny; city
  search covers the need. Easy to add later as an optional entry.
- **Background/periodic sync (WorkManager / BGTaskScheduler)** — not needed for the MVP
  and it eats call budget + battery. Foreground TTL refresh is enough.
- **Maps, radar tiles, air quality, alerts** — out of the stated feature set; `alerts`
  and `minutely` are excluded from the One Call request to shrink payloads.
- **Multi-module split (`:core:*`, `:feature:*`)** — package-based Clean layering inside
  `:composeApp` is simpler and correct at this size; extract modules only if the app
  grows. Documented as a later option.
- **Account/sync across devices** — no backend; the cache and saved cities are local.

## Open Questions

- [ ] **Units:** default to metric (°C, m/s) with a user toggle to imperial, or infer
  from device locale? (Plan assumes: metric default + a settings toggle in Phase 6.)
- [ ] **TTL value:** 30 min proposed — acceptable, or do you want a shorter/longer
  default?
- [ ] **Reorder areas:** is drag-to-reorder in scope for the MVP, or is add/remove
  enough? (Plan assumes add/remove for MVP, reorder as a small later slice.)
- [ ] **Weather icons:** bundle a local vector icon set mapped from OWM condition codes
  (offline, no image dep), or load OWM's PNG icons via Coil3? (Plan assumes bundled
  local icons.)

---
*Sources for API facts:* OpenWeatherMap
[One Call API 3.0](https://openweathermap.org/api/one-call-3),
[pricing](https://openweathermap.org/price).
