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

import love.forte.simbot.Filter

/**
 * 事件过滤器。
 *
 * 事件过滤器一般与监听函数绑定，为简化监听函数相似的过滤条件服务。
 *
 * [EventFilter] 是 [EventListener] 的一种辅助特性，并非独立的机制存在。
 *
 * 通常情况下，一个 [EventListener] 内可能有隐式的多个filter，并在filter流程任意节点出现false的时候得到一个默认返回值.
 * 对于此接口的直接运用，常见的为在匹配失败的时候直接返回一个 [无效相应][EventResult.Invalid]。
 *
 * 假若在过滤匹配失败后，你希望返回一个其他的自定义结果，可以重写 [defaultResult] 进行自定义实现。
 * 默认情况下，[defaultResult] 返回 [EventResult] 的无效化实例 [EventResult.Invalid].
 *
 * 过滤器存在 [优先级][priority], 默认情况下的优先级为 [Int.MAX_VALUE].
 *
 * @author ForteScarlet
 */
public interface EventFilter : Filter<EventProcessingContext> {
    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun test(context: EventProcessingContext): Boolean
    public suspend fun defaultResult(context: EventProcessingContext): EventResult = EventResult
    public val priority: Int get() = Int.MAX_VALUE
}
