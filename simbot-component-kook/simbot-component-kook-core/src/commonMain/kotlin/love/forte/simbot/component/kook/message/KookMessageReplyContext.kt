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

import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.component.kook.KookChatChannel
import love.forte.simbot.component.kook.KookUserChat
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.component.kook.event.KookMessageEvent
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.MessageSendContext
import love.forte.simbot.suspendrunner.STP

/**
 * Kook 消息回复上下文。
 *
 * [destination] 描述入口确定的目标，[target] 按需解析对应实体。
 * 复制消息或选项时保留调用来源、原始消息和目标。
 *
 * @since 5.0
 */
public class KookMessageReplyContext private constructor(
    override val bot: KookBot,
    /**
     * 发起回复的对象。通常为一个消息事件 [KookMessageEvent]。
     */
    override val source: ReplySupport,
    originalMessage: InteractionMessage,
    message: InteractionMessage,
    /**
     * 可以确定的目标*描述*。一个密封类型，根据类型不同可能表示包含频道 ID [KookMessageSendTarget.Channel]、
     * 用户 ID [KookMessageSendTarget.User] 或私聊会话 code [KookMessageSendTarget.ChatCode]。
     */
    public override val destination: KookMessageSendTarget,
    override val options: List<ReplyOption>,
    private val resolveTarget: suspend () -> Any?,
) : MessageSendContext.Reply(bot, originalMessage, message) {
    companion {
        /**
         * 创建一次回复操作，复制传入的选项列表。
         * @since 5.0
         */
        public fun create(
            bot: KookBot,
            source: ReplySupport,
            message: InteractionMessage,
            destination: KookMessageSendTarget,
            options: List<ReplyOption> = emptyList(),
            target: suspend () -> Any? = { null },
        ): KookMessageReplyContext = KookMessageReplyContext(
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
     * 组件内的频道消息回复解析为频道 [KookChatChannel]，私聊消息回复解析为私聊会话 [KookUserChat]。
     * 解析私聊会话可能请求平台 API。
     * **每次**调用都会执行解析函数，未提供时返回 null。
     */
    @STP
    override suspend fun target(): Any? = resolveTarget()

    override fun withMessage(message: InteractionMessage): KookMessageReplyContext =
        KookMessageReplyContext(bot, source, originalMessage, message, destination, options, resolveTarget)

    override fun withOptions(options: Collection<ReplyOption>): KookMessageReplyContext =
        KookMessageReplyContext(bot, source, originalMessage, message, destination, options.toList(), resolveTarget)
}
