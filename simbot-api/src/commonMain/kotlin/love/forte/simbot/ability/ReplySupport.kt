/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import love.forte.simbot.event.MessageEvent
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt


/**
 * 对“消息回复”行为的支持。通常由部分事件类型实现，例如 [MessageEvent]。
 *
 * 与 [SendSupport] 相比，[ReplySupport] 在使用 [reply]
 * 的时候可能会使用或追加一些额外的内容，例如自动添加对指定消息的引用等。
 *
 * @author ForteScarlet
 */
public interface ReplySupport {

    /**
     * 发送一段纯文本消息。
     *
     * @return 消息发送成功后的回执
     * @throws Exception 可能产生任何异常
     */
    public suspend fun reply(text: String): MessageReceipt

    /**
     * 发送一个消息 [Message]。
     *
     * @return 消息发送成功后的回执
     * @throws Exception 可能产生任何异常
     */
    public suspend fun reply(message: Message): MessageReceipt

    /**
     * 使用 [MessageContent] 作为消息发送。
     * 不同的组件可能会根据 [MessageContent] 的具体类型做针对性的优化，
     * 并在不支持的情况下降级为使用 [MessageContent.messages]。
     *
     * @return 消息发送成功后的回执
     * @throws Exception 可能产生任何异常
     */
    public suspend fun reply(messageContent: MessageContent): MessageReceipt

}
