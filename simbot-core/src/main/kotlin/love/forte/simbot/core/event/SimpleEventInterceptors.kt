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

@file:JvmName("SimpleInterceptUtil")
@file:JvmMultifileClass

package love.forte.simbot.core.event

import love.forte.simbot.Api4J
import love.forte.simbot.Interceptor
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.*
import java.util.function.Function

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class SimpleFunctionalProcessingInterceptorDSL

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class SimpleFunctionalListenerInterceptorDSL

/**
 * 核心提供的事件拦截器实现, 基于函数提供外部事件逻辑。
 *
 * @see SimpleFunctionalEventProcessingInterceptor
 *
 */
public sealed class SimpleFunctionalEventInterceptor<C : EventInterceptor.Context<R>, R> : Interceptor<C, R> {
    public abstract val interceptFunction: suspend (C) -> R
    override suspend fun intercept(context: C): R = interceptFunction(context)
}


public class SimpleFunctionalEventProcessingInterceptor(
    override val priority: Int = PriorityConstant.NORMAL,
    override val interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult,
) : EventProcessingInterceptor,
    SimpleFunctionalEventInterceptor<EventProcessingInterceptor.Context, EventProcessingResult>()


public class SimpleFunctionalEventListenerInterceptor(
    override val point: EventListenerInterceptor.Point,
    override val priority: Int = PriorityConstant.NORMAL,
    override val interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult,
) : EventListenerInterceptor, SimpleFunctionalEventInterceptor<EventListenerInterceptor.Context, EventResult>()


/**
 * 提供一个 [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventProcessingInterceptor].
 */
@JvmSynthetic
@SimpleFunctionalProcessingInterceptorDSL
public fun simpleProcessingInterceptor(
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult,
): EventProcessingInterceptor =
    SimpleFunctionalEventProcessingInterceptor(priority = priority, interceptFunction = interceptFunction)

/**
 * 提供一个 [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventListenerInterceptor].
 */
@JvmSynthetic
@SimpleFunctionalListenerInterceptorDSL
public fun simpleListenerInterceptor(
    point: EventListenerInterceptor.Point = EventListenerInterceptor.Point.DEFAULT,
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult,
): EventListenerInterceptor =
    SimpleFunctionalEventListenerInterceptor(point = point, priority = priority, interceptFunction = interceptFunction)

////// 4j

/**
 * 提供一个 [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventProcessingInterceptor].
 */
@Api4J
@JvmOverloads
@JvmName("simpleProcessingInterceptor")
public fun processingInterceptor4J(
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: Function<EventProcessingInterceptor.Context, EventProcessingResult>,
): EventProcessingInterceptor =
    simpleProcessingInterceptor(priority = priority) { interceptFunction.apply(it) }


/**
 * 提供一个 [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventListenerInterceptor].
 */
@Api4J
@JvmOverloads
@JvmName("simpleListenerInterceptor")
public fun listenerInterceptor4J(
    point: EventListenerInterceptor.Point = EventListenerInterceptor.Point.DEFAULT,
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: Function<EventListenerInterceptor.Context, EventResult>,
): EventListenerInterceptor =
    simpleListenerInterceptor(point, priority) { interceptFunction.apply(it) }

