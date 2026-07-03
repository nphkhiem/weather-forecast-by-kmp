package com.example.my_weather_forecast.core.time

import kotlinx.datetime.Clock

/**
 * Supplies the current wall-clock time. Injected so that TTL / cache-age logic is
 * deterministic in tests (provide a fake) instead of reading the system clock directly.
 * The contract is epoch milliseconds to match the `fetchedAtEpochMs` cache column.
 */
interface TimeProvider {
    /** Milliseconds since the Unix epoch (UTC). */
    fun nowEpochMillis(): Long
}

class SystemTimeProvider : TimeProvider {
    override fun nowEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
