/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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
 */

package love.forte.simbot.core.event

import kotlinx.coroutines.withTimeout
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.core.scope.SimpleScope
import love.forte.simbot.event.ContinuousSessionContext
import love.forte.simbot.event.EventProcessingContext


/**
 * 通过 [EventProcessingContext] 获取并进入 [ContinuousSessionContext] 的作用域中, 可以在存在 [EventProcessingContext] 时搭配使用：
 *
 * ```kotlin
 * suspend fun EventProcessingContext.onEvent(event: FooMessageEvent) {
 *     inSession { // this: ContinuousSessionContext
 *         event.reply("喵~")
 *         val nextMessage = event.nextMessage(FooMessageEvent)
 *         // ...
 *     }
 * }
 * ```
 *
 * 或
 *
 * ```kotlin
 * suspend fun onEvent(context: EventProcessingContext, event: FooMessageEvent) {
 *     context.inSession { // this: ContinuousSessionContext
 *         event.reply("喵~")
 *         val nextMessage = event.nextMessage(FooMessageEvent)
 *         // ...
 *     }
 * }
 * ```
 *
 *
 * ## 超时
 * 如果想要控制整个作用域下的整体超时时间，可以直接通过 [withTimeout] 来包裹作用域：
 * ```kotlin
 * withTimeout(...) {
 *    inSession { // this: ContinuousSessionContext
 *       // ...
 *    }
 * }
 * ```
 *
 * ## 值传递
 * [inSession] 可以向外传递返回值：
 * ```
 * val value: Int = inSession {
 *     // ...
 *     114
 * }
 * ```
 *
 *
 * @throws NullPointerException 当 [EventProcessingContext] 中无法获取 [ContinuousSessionContext] 时。
 *
 */
@ExperimentalSimbotApi
public inline fun <R> EventProcessingContext.inSession(block: ContinuousSessionContext.() -> R): R {
    val session = this[SimpleScope.ContinuousSession]
        ?: throw NullPointerException("Cannot get ContinuousSessionContext from current EventProcessingContext [$this].")
    return session.block()
}