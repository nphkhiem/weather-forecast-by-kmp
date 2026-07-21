# ADR 0001: Clean Architecture layering and offline-first caching

## Status

Accepted.

## Context

The app is a Kotlin Multiplatform (Android + iOS) client for a third-party weather API
(OpenWeatherMap), sharing all business logic and most UI via Compose Multiplatform. Two
cross-cutting concerns needed a decision early on, since they shape almost every other file in
the project:

1. How to structure the codebase so platform frameworks (Android, iOS, Compose, SQLDelight,
   Ktor) don't leak into business logic, and so that logic is unit-testable without an
   emulator/simulator.
2. How to make the app feel fast and work offline, given a rate-limited third-party API and a
   mobile network that isn't always available.

## Decision

**Layering.** The codebase is split into `domain`, `data`, and `presentation`, with dependencies
flowing one way: `presentation` depends on `domain`; `data` depends on `domain`; `domain` depends
on nothing framework-specific. `domain` holds plain Kotlin models, repository *interfaces*, and
use cases. `data` provides the repository *implementations* (Ktor for network, SQLDelight for
persistence). `presentation` holds ViewModels and Compose UI. This is enforced by convention (no
build-module boundary), checked by code review, not the compiler — a deliberate trade-off for a
single-module app where a full Gradle multi-module split wasn't worth the ceremony at this size.

**Caching.** SQLDelight is the single source of truth for forecast data — the UI only ever reads
from the local database via `WeatherLocalDataSource.observe()`, which is a `Flow` that emits on
every cache write. `WeatherRepositoryImpl` sits in front of it and:

- Serves cached data immediately if it exists, flagging it `stale` once older than a 30-minute
  TTL.
- Triggers a background refetch when data is missing or stale, coalescing concurrent refresh
  requests for the same area behind a single in-flight `Deferred` so a rotation-triggered
  re-subscribe (or multiple screens observing the same area) doesn't multiply network calls.
- On refetch failure, keeps serving the stale cache with the error attached, rather than
  replacing a real (if stale) forecast with an error screen.
- Uses `units` as part of the cache identity: a cached forecast fetched in metric is treated as a
  cache miss when imperial is requested, so switching units in the UI naturally triggers a
  refetch in the new units without any explicit invalidation code.

## Consequences

- ViewModels and repositories are fully unit-testable on the JVM (and as common tests on iOS) with
  fakes, no Android framework or network access required.
- The "cache is the source of truth" model means the repository never returns raw network
  responses directly to the UI — a network fetch's only job is to update the cache, and the UI's
  only job is to observe it. This keeps the stale/loading/error state machine in one place
  (`WeatherRepositoryImpl`) instead of duplicated per screen.
- The trade-off: without module boundaries, nothing stops a future contributor from importing
  `androidx.compose` into `domain/` by mistake. This is an accepted risk at the current project
  size; a multi-module split is the natural next step if the codebase grows enough to justify it.
