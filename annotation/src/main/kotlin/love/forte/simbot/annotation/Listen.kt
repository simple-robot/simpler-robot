/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
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
 * @see love.forte.simbot.event.Event
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@JvmRepeatable(Listens::class)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class Listen(
    /**
     * 事件类型。指定的事件必须存在一个实现了 [Event.Key] 的伴生对象，否则此事件将会被视为不可监听。
     */
    val eventKey: KClass<out Event> = Event::class
)




@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class Listens(val value: Array<Listen>)