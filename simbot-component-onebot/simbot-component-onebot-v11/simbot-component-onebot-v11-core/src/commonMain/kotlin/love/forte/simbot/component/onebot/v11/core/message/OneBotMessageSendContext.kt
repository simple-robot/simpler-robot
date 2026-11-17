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

import love.forte.simbot.ability.SendOption
import love.forte.simbot.ability.SendSupport
import love.forte.simbot.component.onebot.v11.core.actor.OneBotFriend
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.MessageSendContext

/**
 * OneBot 消息发送上下文。
 *
 * [destination] 描述入口确定的目标，[target] 按需执行目标解析。
 * 复制消息或选项时保留调用来源、原始消息和目标。
 *
 * @since 5.0
 */
public class OneBotMessageSendContext private constructor(
    override val bot: OneBotBot,
    /**
     * 发起发送的对象。通常为目标群或用户对象，如 [OneBotGroup]、[OneBotFriend] 或 [OneBotMember]。
     */
    override val source: SendSupport,
    originalMessage: InteractionMessage,
    message: InteractionMessage,
    /**
     * 创建时确定的目标类型和 ID，见 [OneBotMessageSendTarget]。不会查询平台 API、复制上下文时保持不变。
     */
    public override val destination: OneBotMessageSendTarget,
    override val options: List<SendOption>,
    private val resolveTarget: suspend () -> Any?,
) : MessageSendContext.Send(bot, originalMessage, message) {
    companion {
        /**
         * 创建一次发送操作，复制传入的选项列表。
         * @since 5.0
         */
        public fun create(
            bot: OneBotBot,
            source: SendSupport,
            message: InteractionMessage,
            destination: OneBotMessageSendTarget,
            options: List<SendOption> = emptyList(),
            target: suspend () -> Any? = { null },
        ): OneBotMessageSendContext = OneBotMessageSendContext(
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
     * 组件内发送入口返回 [source]。公开工厂允许解析函数返回任意值，默认返回 null。
     */
    override suspend fun target(): Any? = resolveTarget()

    override fun withMessage(message: InteractionMessage): OneBotMessageSendContext =
        OneBotMessageSendContext(bot, source, originalMessage, message, destination, options, resolveTarget)

    override fun withOptions(options: Collection<SendOption>): OneBotMessageSendContext =
        OneBotMessageSendContext(bot, source, originalMessage, message, destination, options.toList(), resolveTarget)
}
