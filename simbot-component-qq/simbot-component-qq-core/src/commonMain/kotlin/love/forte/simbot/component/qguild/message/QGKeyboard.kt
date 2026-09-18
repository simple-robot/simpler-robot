/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

import kotlinx.serialization.Serializable
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.model.MessageKeyboard
import love.forte.simbot.qguild.model.MessageKeyboards
import kotlin.jvm.JvmStatic

/**
 *
 * markdown 消息内含有的按钮信息
 *
 * @since 4.2.0
 * @see QGKeyboards
 *
 * @author ForteScarlet
 */
@Serializable
@Deprecated(
    message = "请使用 QGKeyboards，QGKeyboard 和原本的 MessageKeyboard 类型的结构不够完整。",
    replaceWith = ReplaceWith("QGKeyboards", imports = ["love.forte.simbot.component.qguild.message.QGKeyboards"]),
    level = DeprecationLevel.ERROR
)
@Suppress("DEPRECATION", "DEPRECATION_ERROR")
public data class QGKeyboard internal constructor(public val keyboard: MessageKeyboard) : QGMessageElement {
    public companion object {
        /**
         * 使用 [keyboard] 直接包装构建。
         */
        @JvmStatic
        public fun create(keyboard: MessageKeyboard): QGKeyboard = QGKeyboard(keyboard)

        /**
         * 使用 [id][MessageKeyboard.create] 构建。
         *
         * @see MessageKeyboard.create
         */
        @JvmStatic
        public fun createById(id: String): QGKeyboard = create(MessageKeyboard.create(id))
    }
}

@Suppress("DEPRECATION", "DEPRECATION_ERROR")
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
                it.keyboards == null
            }
            builder.keyboards = MessageKeyboards.create(keyboard)
        }
    }
}
