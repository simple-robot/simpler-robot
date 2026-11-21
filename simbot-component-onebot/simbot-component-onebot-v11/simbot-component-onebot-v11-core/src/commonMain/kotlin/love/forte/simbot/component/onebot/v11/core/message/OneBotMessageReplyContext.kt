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

package love.forte.simbot.component.onebot.v11.core.message

import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.component.onebot.v11.core.actor.OneBotFriend
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.core.event.message.OneBotMessageEvent
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.MessageSendContext

/**
 * OneBot 消息回复上下文。
 *
 * [destination] 描述最初可确定的目标，[target] 按需执行目标解析。
 * 复制消息或选项时保留调用来源、原始消息和目标。
 *
 * @since 5.0
 */
public class OneBotMessageReplyContext private constructor(
    override val bot: OneBotBot,
    /**
     * 发起回复的对象。通常为消息事件 [OneBotMessageEvent]。
     */
    override val source: ReplySupport,
    originalMessage: InteractionMessage,
    message: InteractionMessage,
    /**
     * 创建时确定的目标类型和 ID，见 [OneBotMessageSendTarget]。读取不会查询平台，复制上下文时保持不变。
     * 组件内群消息回复使用群 ID，私聊消息回复使用用户 ID。
     */
    public override val destination: OneBotMessageSendTarget,
    override val options: List<ReplyOption>,
    private val resolveTarget: suspend () -> Any?,
) : MessageSendContext.Reply(bot, originalMessage, message) {
    companion {
        /**
         * 创建一次回复操作，复制传入的选项列表。
         * @since 5.0
         */
        public fun create(
            bot: OneBotBot,
            source: ReplySupport,
            message: InteractionMessage,
            destination: OneBotMessageSendTarget,
            options: List<ReplyOption> = emptyList(),
            target: suspend () -> Any? = { null },
        ): OneBotMessageReplyContext = OneBotMessageReplyContext(
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
     * 每次调用都执行创建上下文时提供的目标解析函数，解析可能挂起或抛出异常。
     * 组件内群消息回复解析为 [OneBotGroup]，好友消息解析为 [OneBotFriend]，群临时消息解析为 [OneBotMember]。
     * 未知类型的私聊消息返回 null。公开工厂允许解析函数返回任意值，默认返回 null。
     */
    override suspend fun target(): Any? = resolveTarget()

    override fun withMessage(message: InteractionMessage): OneBotMessageReplyContext =
        OneBotMessageReplyContext(bot, source, originalMessage, message, destination, options, resolveTarget)

    override fun withOptions(options: Collection<ReplyOption>): OneBotMessageReplyContext =
        OneBotMessageReplyContext(bot, source, originalMessage, message, destination, options.toList(), resolveTarget)
}
