/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot

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