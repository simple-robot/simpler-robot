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

package love.forte.simbot.event

import love.forte.simbot.Api4J
import love.forte.simbot.Attribute
import love.forte.simbot.AttributeContainer
import love.forte.simbot.utils.runWithInterruptible


/**
 * 一个监听函数。其代表对于一个 [监听器上下文][EventListenerProcessingContext] 的处理逻辑。
 *
 * @see EventListener
 * @see BlockingEventListenerFunction
 */
public fun interface EventListenerFunction : suspend (EventListenerProcessingContext) -> EventResult {
    
    /**
     * 执行监听处理流程。
     */
    @JvmSynthetic
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult
    
}


/**
 * 阻塞实现的 [EventListenerFunction], 更适合 Java开发者进行实现。
 *
 *
 * @see BlockingEventListener
 */
@Api4J
public fun interface BlockingEventListenerFunction : EventListenerFunction {
    @Api4J
    public fun invokeBlocking(context: EventListenerProcessingContext): EventResult
    
    @JvmSynthetic
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult =
        runWithInterruptible { invokeBlocking(context) }
}

/**
 *
 * 一个事件的事件监听器。
 * 事件监听器监听到实现并进行逻辑处理。此处不包含诸如过滤器等内容。
 *
 * @author ForteScarlet
 */
public interface EventListener : java.util.EventListener, AttributeContainer,
    EventListenerFunction {
    
    /**
     * 判断当前监听函数是否可以对指定的事件进行监听。
     *
     */
    public fun isTarget(eventType: Event.Key<*>): Boolean
    
    
    /**
     * 监听函数可以允许存在其独特的属性。
     */
    override fun <T : Any> getAttribute(attribute: Attribute<T>): T?
    
    
    /**
     * 判断目标 [EventListenerProcessingContext] 是否符合当前监听函数的预期。
     *
     * 与 [isTarget] 不同，[match] 中可能包含业务逻辑。但是与 [isTarget] 相同的是，
     * 它们都需要在 [invoke] 执行前进行判断。
     *
     * @return 是否符合预期
     */
    public suspend fun match(context: EventListenerProcessingContext): Boolean
    
    
    /**
     * 监听函数的事件执行逻辑。
     *
     * 通过 [EventListenerProcessingContext] 处理事件，完成处理后返回 [处理结果][EventResult].
     *
     * 在执行 [invoke] 之前，必须要首先通过 [isTarget] 来判断当前监听函数是否允许此类型的事件，然后通过 [match] 匹配。
     *
     * e.g.
     * ```kotlin
     * if (isTarget(context.event.key) && match(context)) {
     *     // do invoke
     *     invoke(context)
     * }
     * ```
     *
     * 否则可能会引发预期外的行为或错误。
     *
     */
    @JvmSynthetic
    override suspend operator fun invoke(context: EventListenerProcessingContext): EventResult
}


/**
 * 一个事件的事件监听器。
 *
 * 提供非挂起的抽象执行函数 [invokeBlocking] 来便于 Java 等不支持挂起函数的语言以阻塞代码实现.
 *
 * @see EventListener
 */
@Api4J
public interface BlockingEventListener : EventListener, BlockingEventListenerFunction {
    override fun isTarget(eventType: Event.Key<*>): Boolean
    
    @JvmSynthetic
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult =
        runWithInterruptible { invokeBlocking(context) }
    
    /**
     * 非挂起的执行事件监听逻辑。
     */
    override fun invokeBlocking(context: EventListenerProcessingContext): EventResult
}
