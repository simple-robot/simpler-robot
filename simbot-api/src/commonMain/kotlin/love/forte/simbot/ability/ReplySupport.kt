/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.ability

import love.forte.simbot.event.InternalInterceptionException
import love.forte.simbot.event.MessageEvent
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.suspendrunner.ST


/**
 * 对“消息回复”行为的支持。通常由部分事件类型实现，例如 [MessageEvent]。
 *
 * 与 [SendSupport] 相比，[ReplySupport] 在使用 [reply]
 * 的时候可能会使用或追加一些额外的内容，例如自动添加对指定消息的引用等。
 *
 * @author ForteScarlet
 */
@ST
public interface ReplySupport {

    /**
     * 发送一段纯文本消息。
     *
     * @return 消息发送成功后的回执
     * @throws InternalInterceptionException 在拦截事件处理过程中产生的异常，
     * 每一个具体的异常都会被收集在 [InternalInterceptionException.suppressedExceptions] 中。
     * @throws Exception 可能产生任何异常
     */
    public suspend fun reply(text: String): MessageReceipt

    /**
     * 发送一个消息 [Message]。
     *
     * @return 消息发送成功后的回执
     * @throws InternalInterceptionException 在拦截事件处理过程中产生的异常，
     * 每一个具体的异常都会被收集在 [InternalInterceptionException.suppressedExceptions] 中。
     * @throws Exception 可能产生任何异常
     */
    public suspend fun reply(message: Message): MessageReceipt

    /**
     * 使用 [MessageContent] 作为消息发送。
     * 不同的组件可能会根据 [MessageContent] 的具体类型做针对性的优化，
     * 并在不支持的情况下降级为使用 [MessageContent.messages]。
     *
     * @return 消息发送成功后的回执
     * @throws InternalInterceptionException 在拦截事件处理过程中产生的异常，
     * 每一个具体的异常都会被收集在 [InternalInterceptionException.suppressedExceptions] 中。
     * @throws Exception 可能产生任何异常
     */
    public suspend fun reply(messageContent: MessageContent): MessageReceipt

    /**
     * 发送一段纯文本消息。
     *
     * 带 [ReplyOption] 的重载函数由版本 5.0 后添加，为确保兼容性提供默认的 `reply(text)` 实现。
     * 实现方应当明确重写此函数。
     *
     * @param text 要发送的消息
     * @param options 额外提供用于消息回复的选项。如果 [options] 中存在预期内、但无法被满足的选项，则可能抛出异常。
     * 详见 [ReplyOption] 的说明。
     * @return 消息发送成功后的回执
     * @throws InternalInterceptionException 在拦截事件处理过程中产生的异常，
     * 每一个具体的异常都会被收集在 [InternalInterceptionException.suppressedExceptions] 中。
     * @throws Exception 可能产生任何异常，例如 [options] 中存在预期内、但无法被满足的选项。
     * @since 5.0
     */
    public suspend fun reply(text: String, vararg options: ReplyOption): MessageReceipt = reply(text)

    /**
     * 发送一个消息 [Message]。
     *
     * 带 [ReplyOption] 的重载函数由版本 5.0 后添加，为确保兼容性提供默认的 `reply(message)` 实现。
     * 实现方应当明确重写此函数。
     *
     * @param message 要发送的消息
     * @param options 额外提供用于消息回复的选项。如果 [options] 中存在预期内、但无法被满足的选项，则可能抛出异常。
     * 详见 [ReplyOption] 的说明。
     * @return 消息发送成功后的回执
     * @throws InternalInterceptionException 在拦截事件处理过程中产生的异常，
     * 每一个具体的异常都会被收集在 [InternalInterceptionException.suppressedExceptions] 中。
     * @throws Exception 可能产生任何异常，例如 [options] 中存在预期内、但无法被满足的选项。
     * @since 5.0
     */
    public suspend fun reply(message: Message, vararg options: ReplyOption): MessageReceipt = reply(message)

    /**
     * 使用 [MessageContent] 作为消息发送。
     *
     * [MessageContent] 通常来自消息事件，不同的组件可能会根据 [MessageContent] 的具体类型做针对性的优化，
     * 并在不支持的情况下降级为使用 [MessageContent.messages]。
     *
     * 带 [ReplyOption] 的重载函数由版本 5.0 后添加，为确保兼容性提供默认的 `reply(message)` 实现。
     * 实现方应当明确重写此函数。
     *
     * @param messageContent 要发送的消息。
     * @param options 额外提供用于消息回复的选项。如果 [options] 中存在预期内、但无法被满足的选项，则可能抛出异常。
     * 详见 [ReplyOption] 的说明。
     * @return 消息发送成功后的回执
     * @throws InternalInterceptionException 在拦截事件处理过程中产生的异常，
     * 每一个具体的异常都会被收集在 [InternalInterceptionException.suppressedExceptions] 中。
     * @throws Exception 可能产生任何异常，例如 [options] 中存在预期内、但无法被满足的选项。
     * @since 5.0
     */
    public suspend fun reply(messageContent: MessageContent, vararg options: ReplyOption): MessageReceipt =
        reply(messageContent)

}
