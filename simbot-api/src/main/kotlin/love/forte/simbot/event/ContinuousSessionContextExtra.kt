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

@file:JvmName("ContinuousSessionScopeContextUtil")

package love.forte.simbot.event

import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.message.MessageContent
import love.forte.simbot.utils.randomIdStr


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
 *
 * @receiver [ContinuousSessionContext] 实例
 * @return [block] 函数的最终返回值
 */
@ExperimentalSimbotApi
public inline operator fun <T> ContinuousSessionContext.invoke(block: ContinuousSessionContext.() -> T): T = block()


/**
 * 挂起并等待下一个符合 [类型][k] 与 [条件][matcher] 的 [事件][Event] 对象。
 *
 * 更多说明请参考 [ContinuousSessionContext.waitingForNext].
 *
 * @see ContinuousSessionContext.waitingForNext
 */
@ExperimentalSimbotApi
public suspend fun <E : Event> ContinuousSessionContext.waitingForNext(
    k: Event.Key<E>,
    matcher: ContinuousSessionEventMatcher<E> = ContinuousSessionEventMatcher,
): E {
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
    matcher: ContinuousSessionEventMatcher<E> = ContinuousSessionEventMatcher,
): MessageContent {
    return waitingForNext(randomIdStr(), key, matcher).messageContent
}

