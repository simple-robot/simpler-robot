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

package love.forte.simbot.event

import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Interceptor
import love.forte.simbot.PriorityConstant

/**
 * 与事件有关的拦截器。
 * @see EventProcessingInterceptor
 * @see EventListenerInterceptor
 */
public sealed interface EventInterceptor<C : EventInterceptor.Context<R>, R> : Interceptor<C, R> {
    /**
     * 拦截器应该存在一个唯一ID。
     * 对于过程拦截器和监听函数拦截器，它们可以分别独立。
     */
    public val id: ID

    /**
     * 优先级。
     */
    public val priority: Int get() = PriorityConstant.NORMAL

    public sealed interface Context<R> : Interceptor.Context<R> {
        public val eventContext: EventProcessingContext
    }
}


/**
 * 一个事件处理过程的拦截器. 是一个最外层的拦截器。
 *
 * @see BlockingEventProcessingInterceptor
 */
public interface EventProcessingInterceptor : EventInterceptor<EventProcessingInterceptor.Context, EventProcessingResult> {
    @JvmSynthetic
    override suspend fun intercept(context: Context): EventProcessingResult

    /**
     * [EventProcessingInterceptor] 的传递上下文。
     */
    public interface Context : EventInterceptor.Context<EventProcessingResult> {
        override val eventContext: EventProcessingContext
        @JvmSynthetic
        override suspend fun proceed(): EventProcessingResult
    }
}

/**
 * 为Java提供的阻塞调用的 [EventProcessingInterceptor] 接口方案。
 */
@Api4J
public interface BlockingEventProcessingInterceptor : EventProcessingInterceptor {
    public fun doIntercept(context: EventProcessingInterceptor.Context): EventProcessingResult

    @JvmSynthetic
    override suspend fun intercept(context: EventProcessingInterceptor.Context): EventProcessingResult = doIntercept(context)
}


/**
 * 事件监听函数拦截器.
 *
 *
 * @see BlockingEventListenerInterceptor
 */
public interface EventListenerInterceptor : EventInterceptor<EventListenerInterceptor.Context, EventResult> {
    @JvmSynthetic
    override suspend fun intercept(context: Context): EventResult

    /**
     * [EventListenerInterceptor] 的传递上下文。
     */
    public interface Context : EventInterceptor.Context<EventResult> {
        /**
         * 当前被拦截的监听函数实例。
         */
        override val eventContext: EventListenerProcessingContext
        public val listener: EventListener get() = eventContext.listener
        @JvmSynthetic
        override suspend fun proceed(): EventResult
    }
}

/**
 * 为Java提供的阻塞调用的 [EventListenerInterceptor] 接口方案。
 *
 */
@Api4J
public interface BlockingEventListenerInterceptor : EventListenerInterceptor {

    public fun doIntercept(context: EventListenerInterceptor.Context): EventResult

    @JvmSynthetic
    override suspend fun intercept(context: EventListenerInterceptor.Context): EventResult = doIntercept(context)
}

