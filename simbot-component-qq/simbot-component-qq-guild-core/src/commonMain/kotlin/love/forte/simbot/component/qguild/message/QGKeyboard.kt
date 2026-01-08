/*
 * Copyright (c) 2025. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.message

import kotlinx.serialization.Serializable
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.model.MessageKeyboard
import kotlin.jvm.JvmStatic

/**
 *
 * markdown 消息内含有的按钮信息
 *
 * @since 4.2.0
 *
 * @author ForteScarlet
 */
@Serializable
@ConsistentCopyVisibility
public data class QGKeyboard internal constructor(
    public val keyboard: MessageKeyboard
) : QGMessageElement {
    public companion object {
        /**
         * 使用 [keyboard] 直接包装构建。
         */
        @JvmStatic
        public fun create(keyboard: MessageKeyboard): QGKeyboard =
            QGKeyboard(keyboard)

        /**
         * 使用 [id][MessageKeyboard.create] 构建。
         *
         * @see MessageKeyboard.create
         */
        @JvmStatic
        public fun createById(id: String): QGKeyboard =
            create(MessageKeyboard.create(id))
    }
}

internal object KeyboardParser : SendingMessageParser {
    internal val logger = LoggerFactory.getLogger("love.forte.simbot.component.qguild.message.KeyboardParser")

    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        // 频道相关的消息发送不支持 keyboard
        if (element is QGKeyboard) {
            logger.warn("Keyboard message is not yet supported for sending to channel.")
        }
    }

    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        if (element is QGKeyboard) {
            val keyboard = element.keyboard
            val builder = builderContext.builderOrNew {
                it.keyboard == null
            }
            builder.keyboard = keyboard
        }
    }
}