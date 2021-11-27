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

package love.forte.simbot.annotation

import love.forte.simbot.event.Event
import kotlin.reflect.KClass

/**
 * 标记于一个函数上或一个类上，代表它们所表示的监听函数应当要监听的事件类型集。
 *
 * @see love.forte.simbot.event.Event
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@JvmRepeatable(Listens::class)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class Listen(
    /**
     * 事件类型。指定的事件必须存在一个实现了 [Event.Key] 的伴生对象，否则此事件将会被视为不可监听。
     *
     * 优先使用 [eventId].
     */
    val value: KClass<out Event> = Event::class,

    /**
     * 如果对应事件类型不是通过伴生对象实现的，则通过此属性指定对应事件的名称。
     *
     * 如果为空则会使用 [value] 来通过半生对象得到一个事件类型。
     */
    val eventId: String = ""
)




@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class Listens(vararg val value: Listen)