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

import love.forte.simbot.ability.SendOption
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.event.InteractionMessage
import love.forte.simbot.interception.MessageSendContext

/**
 * QQ Bot 按 ID 发送的上下文，复制消息或选项时保留具体类型与目标描述。
 *
 * @since 5.0
 */
public class QGMessageSendContext private constructor(
    override val source: QGBot,
    originalMessage: InteractionMessage,
    message: InteractionMessage,
    /**
     * 无需查询平台实体的发送目标，复制上下文后仍保留此描述。
     */
    public val destination: QGMessageSendTarget,
    override val options: List<SendOption>,
) : MessageSendContext.Send(source, originalMessage, message) {
    /**
     * 创建一次按 ID 发送的上下文，复制选项列表以隔离调用方后续修改。
     *
     * @param destination 发送目标的类型及 ID，不需要平台查询。
     */
    public constructor(
        bot: QGBot,
        message: InteractionMessage,
        destination: QGMessageSendTarget,
        options: List<SendOption> = emptyList(),
    ) : this(bot, message, message, destination, options.toList())

    /**
     * 返回 ID 目标描述，不发起平台查询。
     */
    override suspend fun target(): QGMessageSendTarget = destination

    /**
     * 保留原始消息、选项及 QQ 目标，使后续拦截器仍可使用组件上下文。
     */
    override fun withMessage(message: InteractionMessage): QGMessageSendContext =
        QGMessageSendContext(source, originalMessage, message, destination, options)

    /**
     * 复制传入的选项列表，保留消息与 QQ 目标。
     */
    override fun withOptions(options: Collection<SendOption>): QGMessageSendContext =
        QGMessageSendContext(source, originalMessage, message, destination, options.toList())
}
