/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

@file:JvmName("MessagesBuilders")
@file:JvmMultifileClass

package love.forte.simbot.message

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


/**
 * Builds a list of Messages using the provided container and block.
 *
 * @param container The mutable list that contains the message elements. Default is an empty list.
 * @param block The lambda expression where the MessagesBuilder functions are called to populate the message elements.
 * @return The built Messages object.
 */
@MessagesBuilderDsl
public inline fun buildMessages(
    container: MutableList<Message.Element> = mutableListOf(),
    block: MessagesBuilder.() -> Unit
): Messages = MessagesBuilder.create(container).apply(block).build()


/**
 * 使用 `+=` operator 添加 [element]。
 *
 * @since 4.4.0
 * @see MessagesAddable.add
 */
@MessagesBuilderDsl
public operator fun MessagesAddable<*>.plusAssign(element: Message.Element) {
    add(element)
}

/**
 * 使用 `+=` operator 添加 [text]。
 *
 * @since 4.4.0
 *
 * @see MessagesAddable.add
 */
@MessagesBuilderDsl
public operator fun MessagesAddable<*>.plusAssign(text: String) {
    add(text)
}

/**
 * 使用 `+=` operator 添加 [messages]。
 *
 * @since 4.4.0
 * @see MessagesAddable.addAll
 */
@MessagesBuilderDsl
public operator fun MessagesAddable<*>.plusAssign(messages: Iterable<Message.Element>) {
    addAll(messages)
}

/**
 * 使用 `+=` operator 添加 [element]。
 *
 * @suppress deprecated
 * @see MessagesBuilder.add
 */
@Deprecated(
    "使用receiver类型为MessagesAddable的重载",
    ReplaceWith("plusAssign(messages)")
)
@MessagesBuilderDsl
@JvmName("plusAssign") // binary compatible
public fun MessagesBuilder.plusAssign0(element: Message.Element) {
    plusAssign(element)
}

/**
 * 使用 `+=` operator 添加 [text]。
 *
 * @suppress deprecated
 * @see MessagesBuilder.add
 */
@Deprecated(
    "使用receiver类型为MessagesAddable的重载",
    ReplaceWith("plusAssign(messages)")
)
@MessagesBuilderDsl
@JvmName("plusAssign") // binary compatible
public fun MessagesBuilder.plusAssign0(text: String) {
    plusAssign(text)
}

/**
 * 使用 `+=` operator 添加 [messages]。
 *
 * @suppress deprecated
 * @see MessagesBuilder.addAll
 */
@Deprecated(
    "使用receiver类型为MessagesAddable的重载",
    ReplaceWith("plusAssign(messages)")
)
@MessagesBuilderDsl
@JvmName("plusAssign") // binary compatible
public fun MessagesBuilder.plusAssign0(messages: Iterable<Message.Element>) {
    plusAssign(messages)
}
