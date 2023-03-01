/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot

import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.installAllEventProviders
import love.forte.simbot.utils.currentClassLoader
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
    @JvmSynthetic
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
    @JvmSynthetic
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
    @JvmSynthetic
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
    @JvmSynthetic
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


/**
 * 尝试加载所有的 [ComponentAutoRegistrarFactory] 和 [ComponentAutoRegistrarFactory]
 * 并注册到 [ApplicationBuilder] 中。
 *
 * 相当于同时使用 [installAllComponents] 和 [installAllEventProviders]：
 * ```kotlin
 * simbotApplication(Foo) {
 *    installAllComponents(classLoader)
 *    installAllEventProviders(classLoader)
 * }
 * ```
 */
public fun ApplicationBuilder<*>.installAll(classLoader: ClassLoader = this.currentClassLoader) {
    installAllComponents(classLoader)
    installAllEventProviders(classLoader)
}
