/*
 *     Copyright (c) 2025. ForteScarlet.
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

package love.forte.simbot.event

/**
 * 通过 [SubclassOptInRequired] 标记某个事件类型为一个 “模糊的事件类型”。
 * 这类事件通常拥有更明确的子类型，因此也更推荐实现这些更具体的类型，
 * 而它们的存在主要是为了提供一个更为通用的事件类型或者仅仅提供一些额外能力（例如 `XxxAware` 之类的事件），
 * 以便于在事件监听或类型判断上发挥作用。
 *
 * 举个例子，[MessageEvent] 代表了一个 Bot 收到消息的事件，用户可以通过监听它来得知所有的与消息相关的事件。
 *
 * ```kotlin
 * eventDispatcher.process<MessageEvent> { event ->
 *     ...
 * }
 * ```
 *
 * 但是在组件进行具体实现的时候，[MessageEvent] 所代表的含义就比较模糊：它即可能代表群消息，也可能代表私聊消息。
 * 因此应该根据 [ChatGroupMessageEvent] 或 [ContactMessageEvent] 这类末端更具体的类型进行实现或扩展，
 * 而不是直接实现 [MessageEvent]。
 *
 * ```kotlin
 * // 推荐实现更具体的事件类型
 * class MyMessageEventImpl : ChatGroupMessageEvent {
 *     ...
 * }
 * ```
 *
 * 如果你清楚你的事件类型是一个模糊的事件类型，那么你可以使用这个注解来标记它。
 * 此注解主要用于警告此问题的存在，以便于开发者更好地进行事件类型的设计，但不会对代码进行任何限制。
 *
 * @since 4.11.0
 *
 * @author ForteScarlet
 */
@RequiresOptIn(
    message = "This is a fuzzy event type. It is recommended to implement a more specific event type.",
    level = RequiresOptIn.Level.WARNING
)
@Target(AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS)
public annotation class FuzzyEventTypeImplementation
