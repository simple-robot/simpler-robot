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


private val TEMPORARY_LOGGER = LoggerFactory.getLogger("love.forte.simbot.event.EventListener")
private const val TEMPORARY_ID = ""
private const val TEMPORARY_IS_ASYNC = false
private const val TEMPORARY_IS_PRIORITY = PriorityConstant.NORMAL

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
     * 监听函数本身不需要所谓的'唯一标识'.
     */
    @Deprecated("No longer needed", level = DeprecationLevel.ERROR)
    public val id: String get() = TEMPORARY_ID
    
    /**
     * 监听函数本身不需要所谓的'日志'.
     */
    @Deprecated("No longer needed", level = DeprecationLevel.ERROR)
    override val logger: Logger get() = TEMPORARY_LOGGER
    
    /**
     * 监听函数本身不持有'优先级'.
     */
    @Deprecated("No longer needed", level = DeprecationLevel.ERROR)
    public val priority: Int get() = TEMPORARY_IS_PRIORITY
    
    /**
     *
     * 监听函数本身不需要所谓的'异步标识'.
     *
     */
    @Deprecated("No longer needed", level = DeprecationLevel.ERROR)
    public val isAsync: Boolean get() = TEMPORARY_IS_ASYNC
    
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
