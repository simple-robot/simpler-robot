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

import love.forte.simbot.Api4J
import love.forte.simbot.BlockingFilter
import love.forte.simbot.Filter
import love.forte.simbot.PriorityConstant

/**
 * 事件过滤器。
 *
 * 事件过滤器一般与监听函数绑定，为简化监听函数相似的过滤条件服务。
 *
 * [EventFilter] 是 [EventListener] 的一种辅助特性，并非独立的机制存在。
 *
 * 通常情况下，一个 [EventListener] 内可能有隐式的多个filter，并在filter流程任意节点出现false的时候得到一个默认返回值.
 * 对于此接口的直接运用，常见的为在匹配失败的时候直接返回一个 [无效响应][EventResult.Invalid]。
 *
 * 假若在过滤匹配失败后，你希望返回一个其他的自定义结果，可以重写 [defaultResult] 进行自定义实现。
 * 默认情况下，[defaultResult] 返回 [EventResult] 的无效化实例 [EventResult.Invalid].
 *
 * 过滤器存在 [优先级][priority], 默认情况下的优先级为 [PriorityConstant.NORMAL].
 *
 * @author ForteScarlet
 */
public interface EventFilter : Filter<EventListenerProcessingContext> {

    /**
     * 优先级。
     */
    public val priority: Int get() = PriorityConstant.NORMAL

    /**
     * 过滤器的检测函数。通过 [EventProcessingContext] 来验证是否需要处理当前事件。
     */
    @JvmSynthetic
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun test(context: EventListenerProcessingContext): Boolean

    /**
     * 如果过滤器匹配失败，可以通过此函数得到一个默认的返回值。
     * 默认情况下返回 [EventResult.Invalid].
     */
    @JvmSynthetic
    public suspend fun defaultResult(context: EventProcessingContext): EventResult = EventResult.Invalid
}


/**
 * 事件过滤器。
 * 为不支持挂起函数（例如Java）的场景而提供的非挂起阻塞过滤器。
 *
 * @see EventFilter
 */
@Api4J
public interface BlockingEventFilter : EventFilter, BlockingFilter<EventListenerProcessingContext> {

    /**
     * 过滤器的检测函数。通过 [EventProcessingContext] 来验证是否需要处理当前事件。
     */
    @Api4J
    override fun testBlocking(): Boolean

    /**
     * 如果过滤器匹配失败，可以通过此函数得到一个默认的返回值。
     * 默认情况下返回 [EventResult.Invalid].
     */
    @Api4J
    public fun defaultResultBlocking(context: EventProcessingContext): EventResult = EventResult.Invalid




    @JvmSynthetic
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun test(context: EventListenerProcessingContext): Boolean= testBlocking()
    @JvmSynthetic
    override suspend fun defaultResult(context: EventProcessingContext): EventResult = defaultResultBlocking(context)




}