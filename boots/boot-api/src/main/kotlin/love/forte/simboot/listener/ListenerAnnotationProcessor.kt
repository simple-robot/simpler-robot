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

package love.forte.simboot.listener

import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListenerRegistrar
import kotlin.reflect.KClass

/**
 *
 * [love.forte.simboot.annotation.Listener] 注解所标记的监听函数处理器。
 *
 * 处理器可以存在多个，所有处理器将会按照 [priority] 进行顺序处理解析。
 *
 * [ListenerAnnotationProcessor] 所注册的监听函数通常情况喜下不需要关心对过滤器的处理，
 * 对于一个监听函数，在函数本体解析完成后进行监听函数过滤器的解析。
 *
 * @see love.forte.simboot.annotation.Listener
 *
 * @author ForteScarlet
 */
public interface ListenerAnnotationProcessor {

    /**
     * 优先级。
     */
    public val priority: Int get() = PriorityConstant.NORMAL

    /**
     * 提供 [ListensData], 进行处理并向 [listenerRegistrar] 中注册监听函数。
     *
     * @return 如果返回true，则会继续此处理器后续的处理器，返回false将会终止处理。
     *
     */
    public fun process(listenerData: ListenerData, listenerRegistrar: EventListenerRegistrar): Boolean

}


/**
 * [love.forte.simboot.annotation.Listen] 对应的数据类。
 * @see love.forte.simboot.annotation.Listen
 */
public data class ListenData(
    val value: KClass<out Event>
)

/**
 * [love.forte.simboot.annotation.Listens] 对应的数据类。
 * @see love.forte.simboot.annotation.Listens
 */
public data class ListensData(
    val value: List<ListenData>
)


/**
 * [love.forte.simboot.annotation.Listener] 对应的数据类。
 * @see love.forte.simboot.annotation.Listener
 */
public data class ListenerData(
    val id: String,
    val priority: Int,
    val listens: ListensData?
)