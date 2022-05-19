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
 *
 */

package love.forte.simbot.event

import kotlinx.coroutines.Deferred
import love.forte.simbot.*
import love.forte.simbot.definition.IDContainer
import love.forte.simbot.utils.runWithInterruptible
import org.slf4j.Logger


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
 *
 * 事件监听器监听到实现并进行逻辑处理。此处不包含诸如过滤器等内容。
 * 事件监听器存在 [优先级][priority]，默认优先级为 [PriorityConstant.NORMAL].
 *
 *
 * 通常情况下，你可以使用 [MatchableEventListener] 来为监听函数额外提供 "匹配"（或者说"过滤"）函数 [match(...)][MatchableEventListener.match].
 *
 *
 * @see MatchableEventListener
 *
 * @author ForteScarlet
 */
public interface EventListener : java.util.EventListener, AttributeContainer, LoggerContainer, IDContainer,
    EventListenerFunction {
    
    /**
     * 监听器必须是唯一的. 通过 [id] 进行唯一性确认。
     */
    override val id: ID
    
    /**
     * 当前监听函数内部存在的日志对象。
     */
    override val logger: Logger
    
    /**
     * 优先级。对于一次事件处理流程，所有监听函数会根据此优先级进行顺序处理。
     * 整个流程下的所有监听函数中，[isAsync] == true 的监听函数会比普通函数有更高的优先级。
     */
    public val priority: Int get() = PriorityConstant.NORMAL
    
    /**
     * 是否需要异步执行。
     *
     * 异步执行的监听函数会被异步执行并立即返回一个 [Deferred], 并将其作为 [AsyncEventResult] 提供给当前的事件处理上下文中。
     *
     * 默认情况下，异步函数无法通过 [EventResult.isTruncated] 截断后续函数。
     *
     * 当 `isAsync == true` 时，当前监听函数在 [EventProcessor] 中被调度的实际顺序会高于 `isAsync == false` 的函数，
     * 也就是说当一次事件被推送的时候，会优先启动所有的异步监听函数。
     *
     * 理所当然的, 当监听函数被标记为 [isAsync] 时，其对应的所有 [监听函数拦截器][EventListenerInterceptor] 也应当相同的被异步化，并会拦截真正的监听函数结果。
     * 而 [监听事件拦截器][EventProcessingInterceptor] 不会受到影响。
     *
     */
    public val isAsync: Boolean
    
    /**
     * 判断当前监听函数是否对可以对指定的事件进行监听。
     *
     */
    public fun isTarget(eventType: Event.Key<*>): Boolean
    
    
    /**
     * 监听函数可以允许存在其独特的属性。
     */
    override fun <T : Any> getAttribute(attribute: Attribute<T>): T? = null
    
    
    /**
     * 监听函数的事件执行逻辑。
     *
     * 通过 [EventListenerProcessingContext] 处理事件，完成处理后返回 [处理结果][EventResult].
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
    override val id: ID
    override val logger: Logger
    override val isAsync: Boolean
    override fun isTarget(eventType: Event.Key<*>): Boolean
    
    @JvmSynthetic
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult =
        runWithInterruptible { invokeBlocking(context) }
    
    /**
     * 非挂起的执行事件监听逻辑。
     */
    override fun invokeBlocking(context: EventListenerProcessingContext): EventResult
}


// TODO
/**
 * 一个可以 [匹配][match] 的 [EventListener].
 *
 * [match] 允许在进行 [invoke] 之前优先检测参数 [EventListenerProcessingContext]
 * 是否允许被使用。这很类似于 "过滤器" 的概念。
 *
 *
 * 在 [MatchableEventListener] 中，[invoke] 相当于 先执行 [match],
 * 当得到 `true` 的时候去执行 [directInvoke]. 假如 [match] 得到了 `false`，
 * 那么 [invoke] 中真正的监听逻辑则不会被执行，而是返回 [EventResult.Invalid].
 *
 * ## 支持
 * 在 [EventListenerInterceptor] 中存在对 [MatchableEventListener] 的支持，
 * 也因此 [EventProcessor] 也应当在内部提供针对于 [MatchableEventListener] 的支持。
 *
 * 官方提供的针对 [EventListener] 的所有实现理论上都应是支持 [MatchableEventListener] 的。
 *
 */
public interface MatchableEventListener : EventListener {
    
    /**
     * 判断目标 [EventListenerProcessingContext] 是否符合当前监听函数的预期。
     *
     * @return 是否符合预期
     */
    public suspend fun match(context: EventListenerProcessingContext): Boolean
    
    
    /**
     * 直接执行目标逻辑。
     *
     * 通常应该在 [match] 匹配结果为 `true` 的时候被执行，否则可能会存在对于逻辑来讲预期外的异常。
     *
     * e.g.
     * ```kotlin
     * if (match(context)) {
     *    return directInvoke(context)
     * }
     * // or ...
     * ```
     *
     */
    public suspend fun directInvoke(context: EventListenerProcessingContext): EventResult
    
    
    /**
     * 执行监听函数。
     *
     * [invoke] 的行为相当于先通过 [match] 匹配，然后在匹配通过的时候执行 [directInvoke],
     * 不通过的时候直接返回 [EventResult.Invalid].
     *
     */
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult {
        if (match(context)) {
            return directInvoke(context)
        }
        return EventResult.Invalid
    }
    
}


/**
 * [MatchableEventListener] 的阻塞类型。
 *
 * 通常服务于不支持挂起行为的实现方。
 *
 * @see MatchableEventListener
 */
@Api4J
public interface BlockingMatchableEventListener : MatchableEventListener, BlockingEventListenerFunction {
    
    
    /**
     * 阻塞的执行 [MatchableEventListener.match]，判断目标 [context] 是否符合预期。
     *
     * @return 目标是否符合预期
     * @see MatchableEventListener.match
     */
    @Api4J
    public fun matchBlocking(context: EventListenerProcessingContext): Boolean
    
    /**
     * 阻塞的执行 [MatchableEventListener.directInvoke]。
     *
     * @see MatchableEventListener.directInvoke
     */
    @Api4J
    public fun directInvokeBlocking(context: EventListenerProcessingContext): EventResult
    
    
    @JvmSynthetic
    override suspend fun match(context: EventListenerProcessingContext): Boolean {
        return runWithInterruptible { matchBlocking(context) }
    }
    
    @JvmSynthetic
    override suspend fun directInvoke(context: EventListenerProcessingContext): EventResult {
        return runWithInterruptible { directInvokeBlocking(context) }
    }
    
    @JvmSynthetic
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult =
        runWithInterruptible { invokeBlocking(context) }
    
    /**
     * 非挂起的执行事件监听逻辑。
     */
    override fun invokeBlocking(context: EventListenerProcessingContext): EventResult {
        if (matchBlocking(context)) {
            return directInvokeBlocking(context)
        }
        
        return EventResult.Invalid
    }
}
