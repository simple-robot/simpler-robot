/*
 * Copyright (c) 2022-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
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
