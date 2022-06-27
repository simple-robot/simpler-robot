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

import kotlinx.coroutines.Deferred
import love.forte.simbot.*
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


private val temporaryLogger = LoggerFactory.getLogger("love.forte.simbot.event.EventListener")

/**
 *
 * 一个事件的事件监听器。
 *
 * 事件监听器监听到实现并进行逻辑处理。此处不包含诸如过滤器等内容。
 * 事件监听器存在 [优先级][priority]，默认优先级为 [PriorityConstant.NORMAL].
 *
 *
 * @author ForteScarlet
 */
public interface EventListener : java.util.EventListener, AttributeContainer, LoggerContainer,
    EventListenerFunction {
    
    /**
     * 监听器必须是唯一的. 通过 [id] 进行唯一性确认。
     */
    public val id: String
    
    /**
     * 当前监听函数内部存在的日志对象。
     */
    @Deprecated("Will be remove", level = DeprecationLevel.ERROR)
    override val logger: Logger get() = temporaryLogger
    
    /**
     * 优先级。对于一次事件处理流程，所有监听函数会根据此优先级进行顺序处理。
     * 整个流程下的所有监听函数中，[isAsync] == true 的监听函数会比普通函数有更高的优先级。
     */
    public val priority: Int get() = PriorityConstant.NORMAL
    
    /**
     * 是否需要异步执行。
     *
     * 对于一个 [EventListener] 来说，[isAsync] 仅代表一个“标记”，不会影响 [invoke] 的实际执行效果。
     * [isAsync] 的表现形式应当由持有当前监听函数的 [EventProcessor] 来做决定。
     *
     *
     * 通常情况下来讲，异步执行的监听函数会被异步执行并立即返回一个 [Deferred], 并将其作为 [AsyncEventResult] 提供给当前的事件处理上下文中。
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
    override val id: String
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
