/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.component.kook.message

import love.forte.simbot.ability.SendOption
import love.forte.simbot.component.kook.KookChatCapableChannel
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.component.kook.KookUserChat
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.message.Message

/**
 * Kook 消息发送上下文。
 *
 * [destination] 描述入口确定的目标，[target] 按需解析对应实体。
 * 复制消息或选项时保留调用来源、原始消息和目标。
 *
 * @since 5.0
 */
public class KookMessageSendContext private constructor(
    override val bot: KookBot,
    /**
     * 发起发送的对象。可能是频道 [KookChatCapableChannel]、成员 [KookMember]、
     * 私聊会话 [KookUserChat] 或消息 [Message]。
     */
    override val source: Any,
    originalMessage: InteractionMessage,
    message: InteractionMessage,
    /**
     * 目标的*描述*。一个密封类型，根据类型不同可能表示包含频道 ID [KookMessageSendTarget.Channel]、
     * 用户 ID [KookMessageSendTarget.User] 或私聊会话 code [KookMessageSendTarget.ChatCode]。
     */
    public override val destination: KookMessageSendTarget,
    override val options: List<SendOption>,
    private val resolveTarget: suspend () -> Any?,
) : MessageSendContext.Send(bot, originalMessage, message) {
    companion {
        /**
         * 创建一次发送操作，复制传入的选项列表。
         * @since 5.0
         */
        public fun create(
            bot: KookBot,
            source: Any,
            message: InteractionMessage,
            destination: KookMessageSendTarget,
            options: List<SendOption> = emptyList(),
            target: suspend () -> Any? = { null },
        ): KookMessageSendContext = KookMessageSendContext(
            bot = bot,
            source = source,
            originalMessage = message,
            message = message,
            destination = destination,
            options = options.toList(),
            resolveTarget = target
        )
    }

    /**
     * 调用创建上下文时提供的解析函数获取目标对象。
     * 组件内的频道、成员和私聊会话发送分别解析为 [KookChatCapableChannel]、[KookMember] 和 [KookUserChat]。
     * 消息扩展发送没有目标对象。频道原生请求 [KookChannelRequestMessage] 的目标与调用频道不一致时返回 null。
     * 每次调用都会执行解析函数，未提供时返回 null。
     */
    override suspend fun target(): Any? = resolveTarget()

    override fun withMessage(message: InteractionMessage): KookMessageSendContext =
        KookMessageSendContext(bot, source, originalMessage, message, destination, options, resolveTarget)

    override fun withOptions(options: Collection<SendOption>): KookMessageSendContext =
        KookMessageSendContext(bot, source, originalMessage, message, destination, options.toList(), resolveTarget)
}
