/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.annotation

import love.forte.simbot.event.Event
import kotlin.reflect.KClass

/**
 * 标记于一个函数上，代表它们所表示的监听函数应当要监听的事件类型集。
 *
 * 需要在标记 [Listener] 的情况下使用 [Listen]. 当标记 [Listen] 后，
 * 不会再自动判定类型。
 *
 * @property value 事件类型。指定的事件必须存在一个实现了 [Event.Key] 的伴生对象，否则此事件将会被视为不可监听并抛出异常。
 *
 * @see love.forte.simbot.event.Event
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@JvmRepeatable(Listens::class)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class Listen(val value: KClass<out Event>)


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class Listens(vararg val value: Listen)