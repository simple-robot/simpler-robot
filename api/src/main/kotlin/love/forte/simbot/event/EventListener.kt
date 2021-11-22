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

import love.forte.simbot.ID
import kotlin.reflect.KClass


/**
 *
 * 一个监听事件的事件监听器。
 *
 * 事件监听器监听到实现并进行逻辑处理。此处不包含诸如过滤器等内容。
 *
 * 事件监听器存在 [优先级][priority]，默认优先级为 [Int.MAX_VALUE].
 *
 * @author ForteScarlet
 */
public interface EventListener : java.util.EventListener {

    /**
     * 监听器必须是唯一的. 通过 [id] 进行唯一性确认。
     */
    public val id: ID

    /**
     * 优先级。
     */
    public val priority: Int get() = Int.MAX_VALUE

    /**
     * 判断当前监听函数是否对可以对指定的事件进行监听。
     *
     * TODO eventType 是否用 [KClass] ? 或者最好用全限定名或者其他方式？
     *
     */
    public fun isTarget(eventType: KClass<out Event>): Boolean


    /**
     * 监听函数的事件执行逻辑。
     *
     * 通过 [EventProcessingContext] 处理事件，完成处理后返回 [处理结果][EventResult].
     *
     */
    public suspend operator fun invoke(context: EventProcessingContext): EventResult


}
