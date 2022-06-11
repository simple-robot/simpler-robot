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
 */

@file:JvmName("ContinuousSessionScopeContextUtil")
package love.forte.simbot.event

import kotlinx.coroutines.withTimeout
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.message.MessageContent
import love.forte.simbot.randomID


/**
 * 进入到 [ContinuousSessionContext] 上下文中。
 *
 * 主要作用为可以更方便的使用 [ContinuousSessionContext.next] 、[ContinuousSessionContext.nextMessage] 等需要事件上下文环境的情况。
 *
 * e.g.
 * ```kotlin
 * suspend fun EventProcessingContext.barListener(event: BarEvent, session: ContinuousSessionContext) {
 *    session { // this: session
 *       next(...)
 *    }
 * }
 *
 * // ---------------------------------------
 *
 * suspend fun FooMessageEvent.fooListener(session: ContinuousSessionContext) {
 *    session { // this: session
 *        nextMessages(...)
 *    }
 * }
 * ```
 *
 * 这种行为类似于 [run] 或 [apply]。 你也可以使用它们来代替当前函数。
 *
 * 如果你的函数中接收者为 [EventProcessingContext], 那么你可以尝试使用 [inSession].
 *
 *
 * @receiver [ContinuousSessionContext] 实例
 * @return [block] 函数的最终返回值
 */
@ExperimentalSimbotApi
public inline operator fun <T> ContinuousSessionContext.invoke(block: ContinuousSessionContext.() -> T): T = block()



/**
 * 通过 [EventProcessingContext] 获取并进入 [ContinuousSessionContext] 的作用域中, 可以在 [EventProcessingContext] 作为receiver 时搭配使用：
 *
 * ```kotlin
 * suspend fun EventProcessingContext.onEvent(event: FooMessageEvent) {
 *     inSession { // this: ContinuousSessionContext
 *         event.reply("Hello~")
 *         val nextMessage = event.nextMessage(FooMessageEvent)
 *         // ...
 *     }
 * }
 * ```
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
    val session = this[EventProcessingContext.Scope.ContinuousSession] ?: throw NullPointerException("Cannot get ContinuousSessionContext from current EventProcessingContext [$this].")
    return session.block()
}




/**
 * 挂起并等待下一个符合 [类型][k] 与 [条件][matcher] 的 [事件][Event] 对象。
 *
 * 更多说明请参考 [ContinuousSessionContext.waitingForNext].
 *
 * @see ContinuousSessionContext.waitingForNext
 */
@ExperimentalSimbotApi
public suspend fun <E : Event>  ContinuousSessionContext.waitingForNext(k: Event.Key<E>, matcher: EventMatcher<E> = EventMatcher): E {
    return waitingForNext(key = k, matcher = matcher)
}


/**
 * 挂起并等待下一个符合条件的 [消息事件][MessageEvent] 中的消息体。
 *
 * 更多请参考 [ContinuousSessionContext.waitingForNextMessage].
 *
 * @see ContinuousSessionContext.waitingForNextMessage
 */
@ExperimentalSimbotApi
public suspend fun <E : MessageEvent> ContinuousSessionContext.waitingForNextMessage(
    key: Event.Key<E>,
    matcher: EventMatcher<E> = EventMatcher,
): MessageContent {
    return waitingForNext(randomID(), key, matcher).messageContent
}

