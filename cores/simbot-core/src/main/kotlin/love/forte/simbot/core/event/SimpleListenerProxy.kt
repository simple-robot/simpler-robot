/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

@file:JvmName("CoreListeners") @file:JvmMultifileClass

package love.forte.simbot.core.event

import love.forte.simbot.ID
import love.forte.simbot.event.*
import org.slf4j.Logger

/**
 * 提供一个匹配函数，将当前监听函数转化为一个 [MatchableEventListener].
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
public fun EventListener.withMatcher(matcher: suspend EventListenerProcessingContext.() -> Boolean = { true }): MatchableEventListener {
    if (this is SimpleListener) {
        val old = this.matcher
        return copy(matcher = {
            old(this) && matcher()
        })
    }
    
    if (this is DelegatedMatchableEventListener) {
        val old = this.matcher
        return DelegatedMatchableEventListener(this) {
            old() && matcher()
        }
    }
    
    return DelegatedMatchableEventListener(this, matcher)
}


// region proxy and delegate
/**
 * 提供一层包装来代理目标监听函数。
 *
 * 如果 [E] 为 [MatchableEventListener], 则返回值也同样为 [MatchableEventListener] 类型。
 * 但是如果不是则返回值类型为普通的 [EventListener] 代理实现。
 */
public fun <E : EventListener> E.proxy(delegate: suspend EventListenerProcessingContext.(E) -> EventResult): EventListener {
    if (this is MatchableEventListener) return MatchableEventListenerProxy(this, delegate)
    
    return EventListenerProxy(this, delegate)
}


/**
 * 提供一层包装来代理目标监听函数。
 */
public fun <E : MatchableEventListener> E.proxy(delegate: suspend EventListenerProcessingContext.(E) -> EventResult): MatchableEventListener {
    return MatchableEventListenerProxy(this, delegate)
}


private class EventListenerProxy<E : EventListener>(
    val listener: E,
    val handle: suspend EventListenerProcessingContext.(E) -> EventResult,
) : EventListener by listener {
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
        return context.handle(listener)
    }
    
    
    override fun toString(): String {
        return "ProxiedEventListener($listener)"
    }
}


private class MatchableEventListenerProxy<E : MatchableEventListener>(
    val listener: E,
    val handle: suspend EventListenerProcessingContext.(E) -> EventResult,
) : MatchableEventListener by listener {
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
        return context.handle(listener)
    }
    
    
    override fun toString(): String {
        return "ProxiedMatchableEventListener($listener)"
    }
}


private class DelegatedMatchableEventListener(
    val delegate: EventListener,
    val matcher: suspend EventListenerProcessingContext.() -> Boolean,
) : MatchableEventListener {
    override val id: ID
        get() = delegate.id
    override val logger: Logger
        get() = delegate.logger
    override val isAsync: Boolean
        get() = delegate.isAsync
    
    override fun isTarget(eventType: Event.Key<*>): Boolean = delegate.isTarget(eventType)
    
    private val matcher0: suspend (EventListenerProcessingContext) -> Boolean =
        if (delegate is MatchableEventListener) {
            { context -> delegate.match(context) && context.matcher() }
        } else {
            { context -> context.matcher() }
        }
    
    override suspend fun match(context: EventListenerProcessingContext): Boolean {
        return matcher0(context)
    }
    
    private val invoker0: suspend (EventListenerProcessingContext) -> EventResult =
        if (delegate is MatchableEventListener) {
            { context -> delegate.directInvoke(context) }
        } else {
            { context -> delegate.invoke(context) }
        }
    
    override suspend fun directInvoke(context: EventListenerProcessingContext): EventResult {
        return invoker0(context)
    }
    
    override fun toString(): String {
        return "DelegatedListener($delegate)"
    }
}
// endregion