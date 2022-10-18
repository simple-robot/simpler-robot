package love.forte.simbot

import kotlinx.datetime.Clock

/**
 * @see kotlinx.datetime.Instant
 */
public actual typealias Instant = kotlinx.datetime.Instant

/**
 * @see kotlinx.datetime.Instant.epochSeconds
 */
public actual val Instant.seconds: Long get() = this.epochSeconds

/**
 * @see kotlinx.datetime.Instant.nanosecondsOfSecond
 */
public actual val Instant.secondNanos: Int get() = this.nanosecondsOfSecond

/**
 * @see kotlinx.datetime.Instant.fromEpochSeconds
 */
public actual fun Instants.fromEpochSeconds(second: Long, nanos: Int): Instant = Instant.fromEpochSeconds(second, nanos)

/**
 * @see kotlinx.datetime.Instant.fromEpochMilliseconds
 */
public actual fun Instants.fromEpochMilliseconds(milliseconds: Long): Instant =
    Instant.fromEpochMilliseconds(milliseconds)

/**
 * @see kotlinx.datetime.Instant.DISTANT_PAST
 */
// https://github.com/Kotlin/kotlinx-datetime/issues/236
public actual val Instants.DEFAULT_NOT_SUPPORT: Instant get() = Instant.DISTANT_PAST

/**
 * @see Clock.System.now
 */
public actual fun Instants.now(): Instant = Clock.System.now()


internal actual fun nowTimestamp(): Timestamp {
    val instant = Clock.System.now()
    return Timestamp.bySecond(instant.epochSeconds, instant.nanosecondsOfSecond)
}