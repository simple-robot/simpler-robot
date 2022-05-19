/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simbot.event

import love.forte.simbot.Api4J
import love.forte.simbot.Interceptor
import love.forte.simbot.PriorityConstant
import love.forte.simbot.utils.runWithInterruptible

/**
 * 与事件有关的拦截器。拦截器是一个包裹在目标前后的一道 “关卡”,  通过拦截器可以自由定义对目标逻辑前后以及异常的处理。
 *
 * 比较常见与相似的概念比如动态代理在逻辑上是类似的。
 *
 *
 *
 * @see EventProcessingInterceptor
 * @see EventListenerInterceptor
 */
public interface EventInterceptor<C : EventInterceptor.Context<R>, R> : Interceptor<C, R> {
    /**
     * 优先级。
     */
    public val priority: Int get() = PriorityConstant.NORMAL
    
    
    /**
     * 事件拦截器所拦截的目标内容。
     */
    public interface Context<R> : Interceptor.Context<R> {
        
        /**
         * 事件处理流程的context。
         */
        public val eventContext: EventProcessingContext
    }
}


/**
 * 事件处理过程的拦截器。[EventProcessingInterceptor] 是针对 **整个** 事件流程的整体拦截，他不关系处理流程中的某个单独的监听函数，
 * 而是对所有被触发的事件流程的整体进行一次拦截。
 *
 * @see BlockingEventProcessingInterceptor
 */
public interface EventProcessingInterceptor :
    EventInterceptor<EventProcessingInterceptor.Context, EventProcessingResult> {
    @JvmSynthetic
    override suspend fun intercept(context: Context): EventProcessingResult
    
    /**
     * [EventProcessingInterceptor] 的传递上下文。
     */
    public interface Context : EventInterceptor.Context<EventProcessingResult> {
        override val eventContext: EventProcessingContext
    }
}

/**
 * 为Java提供的阻塞调用的 [EventProcessingInterceptor] 接口方案。
 *
 * @see EventProcessingInterceptor
 */
@Api4J
public interface BlockingEventProcessingInterceptor : EventProcessingInterceptor {
    /**
     * 阻塞的执行拦截逻辑。
     */
    public fun doIntercept(context: EventProcessingInterceptor.Context): EventProcessingResult
    
    @JvmSynthetic
    override suspend fun intercept(context: EventProcessingInterceptor.Context): EventProcessingResult =
        runWithInterruptible { doIntercept(context) }
}


/**
 * 事件监听函数拦截器。与 [EventProcessingInterceptor] 不同，[EventListenerInterceptor] 则针对一次事件处理流程中的 **每一个** [监听函数][EventListener] 进行独立拦截。
 *
 * 事件监听器不建议对 [EventListenerProcessingContext.textContent] 进行操作，尤其是在(boot下)同时使用了 [love.forte.simboot.listener.EventListenerTextContentProcessor] 的情况下。
 *
 * 对于不支持挂起函数的实现方提供了 [BlockingEventListenerInterceptor]，以阻塞的 [BlockingEventListenerInterceptor.doIntercept] 来代替 [intercept].
 *
 * @see BlockingEventListenerInterceptor
 * @see love.forte.simboot.listener.EventListenerTextContentProcessor
 */
public interface EventListenerInterceptor : EventInterceptor<EventListenerInterceptor.Context, EventResult> {
    /**
     * 监听函数拦截器的拦截点.
     *
     * @see Point
     */
    public val point: Point get() = Point.DEFAULT
    
    @JvmSynthetic
    override suspend fun intercept(context: Context): EventResult
    
    /**
     * [EventListenerInterceptor] 的context。
     */
    public interface Context : EventInterceptor.Context<EventResult> {
        /**
         * 当前被拦截的监听函数实例。
         */
        override val eventContext: EventListenerProcessingContext
        
        
        /**
         * 被拦截的监听函数本身。
         */
        public val listener: EventListener get() = eventContext.listener
    }
    
    
    /**
     * [EventListenerInterceptor] 针对于监听函数执行的拦截点。
     *
     * [Point] 中所有的模式的具体行为的最终解释权都归 [EventProcessor] 的具体实现所有。
     */
    public enum class Point {
        /**
         * 默认拦截点。通常情况下来讲就是监听函数被执行前的位置。
         */
        DEFAULT,
        
        /**
         * 在 [MatchableEventListener.match] 匹配成功后拦截。
         *
         * 如果拦截目标是一个 [MatchableEventListener] 类型的监听函数，
         * 那么当前监听函数会在 [MatchableEventListener.match] 匹配通过后才被执行。
         *
         * 假如拦截器的拦截目标不属于 [MatchableEventListener] 类型，那么 [AFTER_MATCH]
         * 的预期行为将与 [DEFAULT] 一致，即直接拦截监听函数的执行。
         *
         *
         */
        AFTER_MATCH
    }
    
    
}

/**
 * 为Java提供的阻塞调用的 [EventListenerInterceptor] 接口方案。
 *
 * @see EventListenerInterceptor
 */
@Api4J
public interface BlockingEventListenerInterceptor : EventListenerInterceptor {
    
    public fun doIntercept(context: EventListenerInterceptor.Context): EventResult
    
    @JvmSynthetic
    override suspend fun intercept(context: EventListenerInterceptor.Context): EventResult = doIntercept(context)
}

