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

import love.forte.simbot.PriorityConstant


/**
 * 标记一个函数为监听器/监听函数，并尝试自动检测其监听类型。
 *
 * 当标记在一个函数上的时候，应当提供一个事件类型的参数作为你需要监听的事件类型。eg：
 * ```kotlin
 *
 * @Listener
 * suspend fun ChannelMessageEvent.myListener() { ... }
 *
 * ```
 * 默认情况下，此函数的ID为其全限定名，你可以通过 [@Listener(id="...")][Listener.id] 指定一个ID。
 * 当不指定的时候默认为当前标记对象的全限定二进制名称。
 *
 *
 * @param id 监听函数ID。
 * @param priority 此事件的优先级。
 * @param async 此函数是否为异步函数。
 * 如果为 `object` 类型，则会直接获取实例，否则会尝试通过可用途径获取对应结果。
 *
 * @see Filter
 * @see Interceptor
 * @see love.forte.simbot.event.EventListener
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class Listener(
    val id: String = "",
    val priority: Int = PriorityConstant.NORMAL,
    val async: Boolean = false, // TODO 存在未知问题会导致此事件无法被触发
)
