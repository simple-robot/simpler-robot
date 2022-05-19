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

package love.forte.simbot.core.event

import love.forte.simbot.ID
import love.forte.simbot.event.*
import love.forte.simbot.event.Event.Key.Companion.isSub
import org.slf4j.Logger


/**
 *
 * 核心模块中所提供的最基础的 [EventListener] 实现。
 *
 * [SimpleListener] 实现 [EventListener] 所有的基础功能，并支持 [MatchableEventListener] 的特性。
 *
 * @author ForteScarlet
 */
public class SimpleListener(
    override val id: ID,
    override val logger: Logger,
    override val isAsync: Boolean,
    
    /**
     * 当前监听函数所需的事件目标类型集。当 [targets] 的元素为空时，视为监听全部。
     */
    private val targets: Set<Event.Key<*>>,
    
    /**
     * 过滤匹配函数。默认为始终放行。
     */
    internal val matcher: suspend EventListenerProcessingContext.() -> Boolean = { true },
    
    /**
     * 实际的监听函数。
     */
    internal val function: suspend EventListenerProcessingContext.() -> EventResult,
    
    ) : EventListener, MatchableEventListener {
    private val targetMatcher = targets.toTargetMatcher()
    override fun isTarget(eventType: Event.Key<*>): Boolean = targetMatcher(eventType)
    
    override suspend fun match(context: EventListenerProcessingContext): Boolean = context.matcher()
    
    override suspend fun directInvoke(context: EventListenerProcessingContext): EventResult = context.function()
    
    override fun toString(): String {
        return "SimpleListener(id=$id, isAsync=$isAsync, targets=$targets)"
    }
    
    /**
     * 提供一个新的 [id], 以及其他可选参数，并构建一个新的 [SimpleListener].
     */
    public fun copy(
        id: ID = this.id,
        logger: Logger = this.logger,
        isAsync: Boolean = this.isAsync,
        targets: Set<Event.Key<*>> = this.targets.toSet(),
        matcher: suspend EventListenerProcessingContext.() -> Boolean = this.matcher,
        function: suspend EventListenerProcessingContext.() -> EventResult = this.function
    ): SimpleListener {
        return SimpleListener(id, logger, isAsync, targets, matcher, function)
    }
    
    public companion object
}

private fun Set<Event.Key<*>>.toTargetMatcher(): ((Event.Key<*>) -> Boolean) {
    if (isEmpty()) {
        return { true }
    }
    if (size == 1) {
        val target = first()
        return { it isSub target }
    }
    
    val targets = toSet()
    return {
        it in targets || targets.any { t -> it isSub t }
    }
}