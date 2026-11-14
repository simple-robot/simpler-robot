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

package love.forte.simbot.component.qguild.message

import love.forte.simbot.ability.ReplyOption
import love.forte.simbot.ability.ReplySupport
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.MessageSendContext

/**
 * QQ Bot 消息回复上下文。
 *
 * [destination] 描述入口确定的目标，[target] 按需解析对应实体。
 * 复制消息或选项时保留调用来源、原始消息和目标。
 *
 * @since 5.0
 */
public class QGMessageReplyContext private constructor(
    override val bot: QGBot,
    override val source: ReplySupport,
    originalMessage: InteractionMessage,
    message: InteractionMessage,
    public override val destination: QGMessageSendTarget,
    override val options: List<ReplyOption>,
    private val resolveTarget: suspend () -> Any?,
) : MessageSendContext.Reply(bot, originalMessage, message) {
    companion {
        /**
         * 创建一次回复操作，复制传入的选项列表。
         */
        public fun create(
            bot: QGBot,
            source: ReplySupport,
            message: InteractionMessage,
            destination: QGMessageSendTarget,
            options: List<ReplyOption> = emptyList(),
            target: suspend () -> Any? = { null },
        ): QGMessageReplyContext = QGMessageReplyContext(
            bot,
            source,
            message,
            message,
            destination,
            options.toList(),
            target
        )
    }

    override suspend fun target(): Any? = resolveTarget()

    override fun withMessage(message: InteractionMessage): QGMessageReplyContext =
        QGMessageReplyContext(bot, source, originalMessage, message, destination, options, resolveTarget)

    override fun withOptions(options: Collection<ReplyOption>): QGMessageReplyContext =
        QGMessageReplyContext(bot, source, originalMessage, message, destination, options.toList(), resolveTarget)
}
