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

package love.forte.simboot.annotation

import love.forte.simbot.PriorityConstant
import love.forte.simbot.event.Event
import kotlin.reflect.KClass

internal abstract class NoSupportMarkEvent : Event

/**
 * 标记于一个函数上，代表它们所表示的监听函数应当要监听的事件类型集。
 *
 * 与 [Listener] 同时存在时，通过 [Listen] 来判定监听类型而不是自动判断。
 *
 * @see love.forte.simbot.event.Event
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@JvmRepeatable(Listens::class)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
public annotation class Listen(
    /**
     * 事件类型。指定的事件必须存在一个实现了 [Event.Key] 的伴生对象，
     * 否则此事件将会被视为不可监听并抛出异常。
     *
     */
    val value: KClass<out Event> = NoSupportMarkEvent::class,

    /**
     * 此事件的优先级。
     *
     */
    val priority: Int = PriorityConstant.NORMAL
)


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@MustBeDocumented
public annotation class Listens(vararg val value: Listen)