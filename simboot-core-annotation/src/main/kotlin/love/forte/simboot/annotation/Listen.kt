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

package love.forte.simboot.annotation

import love.forte.simbot.event.Event
import kotlin.reflect.KClass

/**
 * 标记于一个函数上，代表它们所表示的监听函数应当要监听的事件类型集。
 *
 * 需要在标记 [Listener] 的情况下使用 [Listen]. 当标记 [Listen] 后，
 * 不会再自动判定类型。
 *
 * 更推荐使用 [Listener] 配合监听类型参数的形式，[Listen] 的必要性已经不大了。
 * 未来可能会考虑移除 [Listen] 和 [Listens].
 *
 *
 * @param value 事件类型。指定的事件必须存在一个实现了 [Event.Key] 的伴生对象，否则此事件将会被视为不可监听并抛出异常。
 *
 * @see Listener
 * @see love.forte.simbot.event.Event
 */
@Retention(AnnotationRetention.RUNTIME)
@JvmRepeatable(Listens::class)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class Listen(val value: KClass<out Event>)


@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class Listens(vararg val value: Listen)
