/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("SimpleListeners")
@file:JvmMultifileClass

package love.forte.simbot.core.event

import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventResult

/**
 * 提供一个匹配函数，将当前监听函数根据新的匹配函数转化为一个新的 [EventListener].
 *
 * e.g.
 * ```kotlin
 * listener.withMatcher { // this: EventListenerProcessingContext
 *    // do something...
 *
 *    true // or false?
 * }
 * ```
 *
 */
@JvmSynthetic
public fun EventListener.withMatcher(matcher: suspend EventListenerProcessingContext.() -> Boolean = { true }): EventListener {
    if (this is SimpleListener) {
        val old = this.matcher
        return copy(matcher = {
            old() && matcher()
        })
    }
    
    return DelegatedEventListener(this, matcher)
}


// region proxy and delegate
/**
 * 提供一层包装来代理目标监听函数。
 */
public fun <E : EventListener> E.proxy(
    handleMatch: suspend EventListenerProcessingContext.(E) -> Boolean = { it.match(this) },
    handleInvoke: suspend EventListenerProcessingContext.(E) -> EventResult,
): EventListener {
    return EventListenerProxy(this, handleMatch, handleInvoke)
}


private class EventListenerProxy<E : EventListener>(
    val listener: E,
    val handleMatch: suspend EventListenerProcessingContext.(E) -> Boolean,
    val handleInvoke: suspend EventListenerProcessingContext.(E) -> EventResult,
) : EventListener by listener {
    
    override suspend fun match(context: EventListenerProcessingContext): Boolean {
        return context.run { handleMatch(this@EventListenerProxy.listener) }
    }
    
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
        return context.run { handleInvoke(this@EventListenerProxy.listener) }
    }
    
    
    override fun toString(): String {
        return "ProxiedEventListener($listener)"
    }
}


private class DelegatedEventListener(
    val delegate: EventListener,
    val matcher: suspend EventListenerProcessingContext.() -> Boolean,
) : EventListener by delegate {
    
    override suspend fun match(context: EventListenerProcessingContext): Boolean {
        return delegate.match(context) && matcher(context)
    }
    
    
    override fun toString(): String {
        return "DelegatedListener($delegate)"
    }
}
// endregion
