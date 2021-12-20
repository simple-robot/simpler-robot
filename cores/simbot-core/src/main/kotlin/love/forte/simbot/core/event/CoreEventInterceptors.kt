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

@file:JvmName("CoreInterceptUtil")
@file:JvmMultifileClass

package love.forte.simbot.core.event

import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Interceptor
import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.*
import java.util.*

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class CoreFunctionalProcessingInterceptorDSL

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class CoreFunctionalListenerInterceptorDSL

/**
 * 核心提供的事件拦截器实现, 基于函数提供外部事件逻辑。
 *
 * @see CoreFunctionalEventProcessingInterceptor
 *
 */
public sealed class CoreFunctionalEventInterceptor<C : EventInterceptor.Context<R>, R> : Interceptor<C, R> {
    public abstract val interceptFunction: suspend (C) -> R
    override suspend fun intercept(context: C): R = interceptFunction(context)
}


public class CoreFunctionalEventProcessingInterceptor(
    override val id: ID,
    override val priority: Int = PriorityConstant.NORMAL,
    override val interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult
) : EventProcessingInterceptor,
    CoreFunctionalEventInterceptor<EventProcessingInterceptor.Context, EventProcessingResult>()


public class CoreFunctionalEventListenerInterceptor(
    override val id: ID,
    override val priority: Int = PriorityConstant.NORMAL,
    override val interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult
) : EventListenerInterceptor, CoreFunctionalEventInterceptor<EventListenerInterceptor.Context, EventResult>()


/**
 * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventProcessingInterceptor].
 */
@JvmSynthetic
@CoreFunctionalProcessingInterceptorDSL
public fun coreProcessingInterceptor(
    id: ID,
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult
): EventProcessingInterceptor =
    CoreFunctionalEventProcessingInterceptor(id = id, priority = priority, interceptFunction = interceptFunction)

@JvmSynthetic
@CoreFunctionalProcessingInterceptorDSL
public fun coreProcessingInterceptor(
    id: String = UUID.randomUUID().toString(),
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: suspend (EventProcessingInterceptor.Context) -> EventProcessingResult
): EventProcessingInterceptor = coreProcessingInterceptor(id.ID, priority, interceptFunction)


/**
 * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventListenerInterceptor].
 */
@JvmSynthetic
@CoreFunctionalListenerInterceptorDSL
public fun coreListenerInterceptor(
    id: ID,
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult
): EventListenerInterceptor =
    CoreFunctionalEventListenerInterceptor(id = id, priority = priority, interceptFunction = interceptFunction)


/**
 * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventListenerInterceptor].
 */
@JvmSynthetic
@CoreFunctionalListenerInterceptorDSL
public fun coreListenerInterceptor(
    id: String = UUID.randomUUID().toString(),
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: suspend (EventListenerInterceptor.Context) -> EventResult
): EventListenerInterceptor =
    coreListenerInterceptor(id.ID, priority, interceptFunction)


////// 4j

/**
 * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventProcessingInterceptor].
 */
@Api4J
@JvmOverloads
@JvmName("coreProcessingInterceptor")
public fun processingInterceptor4J(
    id: ID,
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: java.util.function.Function<EventProcessingInterceptor.Context, EventProcessingResult>
): EventProcessingInterceptor =
    coreProcessingInterceptor(id = id, priority = priority) { interceptFunction.apply(it) }

@Api4J
@JvmOverloads
@JvmName("coreProcessingInterceptor")
public fun processingInterceptor4J(
    id: String = UUID.randomUUID().toString(),
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: java.util.function.Function<EventProcessingInterceptor.Context, EventProcessingResult>
): EventProcessingInterceptor = coreProcessingInterceptor(id.ID, priority) { interceptFunction.apply(it) }


/**
 * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventListenerInterceptor].
 */
@Api4J
@JvmOverloads
@JvmName("coreListenerInterceptor")
public fun listenerInterceptor4J(
    id: ID,
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: java.util.function.Function<EventListenerInterceptor.Context, EventResult>
): EventListenerInterceptor =
    coreListenerInterceptor(id = id, priority = priority) { interceptFunction.apply(it) }


/**
 * 提供一个 [id], [优先级][priority] 和 [拦截函数][interceptFunction],
 * 得到一个流程拦截器 [EventListenerInterceptor].
 */
@Api4J
@JvmOverloads
@JvmName("coreListenerInterceptor")
public fun listenerInterceptor4J(
    id: String = UUID.randomUUID().toString(),
    priority: Int = PriorityConstant.NORMAL,
    interceptFunction: java.util.function.Function<EventListenerInterceptor.Context, EventResult>
): EventListenerInterceptor =
    coreListenerInterceptor(id.ID, priority) { interceptFunction.apply(it) }

