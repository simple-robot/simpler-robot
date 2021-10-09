package love.forte.simbot

import love.forte.simbot.exception.SimbotIllegalArgumentException
import love.forte.simbot.exception.SimbotIllegalStateException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalContracts::class)
@Suppress("NOTHING_TO_INLINE")
public object Simbot {
    /**
     * Throws an [SimbotIllegalStateException] if the [value] is false.
     * Like [kotlin.check].
     *
     * @see kotlin.check
     */
    public inline fun check(value: Boolean) {
        contract {
            returns() implies value
        }
        check(value) { "Check failed." }
    }

    /**
     * Throws an [SimbotIllegalStateException] with the result of calling [lazyMessage] if the [value] is false.
     * Like [kotlin.check].
     *
     * @see kotlin.check
     */
    public inline fun check(value: Boolean, lazyMessage: () -> Any) {
        contract {
            returns() implies value
        }
        if (!value) {
            val message = lazyMessage()
            throw SimbotIllegalStateException(message.toString())
        }
    }


    /**
     * Throws an [SimbotIllegalArgumentException] if the [value] is false.
     *
     * Like [kotlin.require].
     * @see kotlin.require
     */
    public inline fun require(value: Boolean) {
        contract {
            returns() implies value
        }
        require(value) { "Failed requirement." }
    }

    /**
     * Throws an [SimbotIllegalArgumentException] with the result of calling [lazyMessage] if the [value] is false.
     * Like [kotlin.require].
     * @see kotlin.require
     */
    public inline fun require(value: Boolean, lazyMessage: () -> Any) {
        contract {
            returns() implies value
        }
        if (!value) {
            val message = lazyMessage()
            throw SimbotIllegalArgumentException(message.toString())
        }
    }
}