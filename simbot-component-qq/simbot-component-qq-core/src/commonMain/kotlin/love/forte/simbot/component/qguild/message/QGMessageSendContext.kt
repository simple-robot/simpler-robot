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
 * QQ Bot 消息发送上下文。
 *
 * [destination] 描述入口确定的目标，[target] 按需解析对应实体。
 * 复制消息或选项时保留调用来源、原始消息和目标。
 *
 * @since 5.0
 */
public class QGMessageSendContext private constructor(
    override val bot: QGBot,
    /**
     * 创建上下文时传入的发送来源。组件通过 [QGBot] 按 ID 发送时为 Bot，通过实体发送时为该实体。
     */
    override val source: Any,
    originalMessage: InteractionMessage,
    message: InteractionMessage,
    /**
     * 创建上下文时传入的目标描述 [QGMessageSendTarget]，包含子频道、频道私聊、群或用户的类型和标识。
     * 组件发送构建时根据发送目标确定其值，复制消息或选项时保持不变。
     */
    public override val destination: QGMessageSendTarget,
    override val options: List<SendOption>,
    private val resolveTarget: suspend () -> Any?,
) : MessageSendContext.Send(bot, originalMessage, message) {
    companion {
        /**
         * 创建一次发送操作，复制传入的选项列表。
         * @since 5.0
         */
        public fun create(
            bot: QGBot,
            source: Any,
            message: InteractionMessage,
            destination: QGMessageSendTarget,
            options: List<SendOption> = emptyList(),
            target: suspend () -> Any? = { null },
        ): QGMessageSendContext = QGMessageSendContext(
            bot,
            source,
            message,
            message,
            destination,
            options.toList(),
            target
        )
    }

    /**
     * 每次调用都会执行创建上下文时提供的目标解析函数。
     * 组件通过实体发送时返回该实体，通过 [QGBot] 按 ID 发送时返回 null。
     * 通过公开工厂创建时解析函数可以返回任意值，默认返回 null。
     */
    override suspend fun target(): Any? = resolveTarget()

    override fun withMessage(message: InteractionMessage): QGMessageSendContext =
        QGMessageSendContext(bot, source, originalMessage, message, destination, options, resolveTarget)

    override fun withOptions(options: Collection<SendOption>): QGMessageSendContext =
        QGMessageSendContext(bot, source, originalMessage, message, destination, options.toList(), resolveTarget)
}
