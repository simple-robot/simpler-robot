/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:Suppress("DEPRECATION")

package love.forte.simboot.listener

import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
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
@Deprecated("TODO")
public interface ListenerAnnotationProcessor {

    /**
     * 优先级。
     */
    public val priority: Int get() = PriorityConstant.NORMAL

    /**
     * @return 如果返回true，则会继续此处理器后续的处理器，返回false将会终止处理。
     *
     */
    public fun process(context: Unit): Boolean // TODO delete

}


/**
 * 监听函数注册后置处理器。当 [ListenerAnnotationProcessor] 进行监听函数处理并且通过其参数 [EventListenerRegistrar] 进行注册时，
 * 对这个被注册的监听函数进行后置处理.
 *
 * TODO
 */
@Deprecated("TODO")
public interface ListenerAnnotationPostRegisteredProcessor {


    /**
     * 当一个注解监听器被解析并注册后，执行此后置处理器对其进行处理，并得到最终结果，
     * 或者返回 null 抛弃本次注册行为。
     */
    public fun process(listenerData: ListenerData, listener: EventListener): EventListener?
}


/**
 * [love.forte.simboot.annotation.Listen] 对应的数据类。
 * @see love.forte.simboot.annotation.Listen
 */
@Deprecated("TODO")
public data class ListenData(
    val value: KClass<out Event>
)

/**
 * [love.forte.simboot.annotation.Listens] 对应的数据类。
 * @see love.forte.simboot.annotation.Listens
 */
@Deprecated("TODO")
public data class ListensData(
    val value: List<ListenData>
)


/**
 * [love.forte.simboot.annotation.Listener] 对应的数据类。
 * @see love.forte.simboot.annotation.Listener
 */
@Deprecated("TODO")
public data class ListenerData(
    val id: String, // empty able
    val priority: Int,
    val async: Boolean,
    val listens: ListensData?,
)
