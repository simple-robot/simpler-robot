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