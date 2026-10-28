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
import love.forte.simbot.qguild.model.MessageKeyboardButton
import love.forte.simbot.qguild.model.MessageKeyboards
import love.forte.simbot.qguild.model.MessageKeyboardsBuilder
import kotlin.jvm.JvmStatic

/**
 * markdown 消息内含有的多按钮信息。
 *
 * Kotlin 可以使用 DSL API 快速构建 [QGKeyboards] 对象实例：
 * ```kotlin
 * QGKeyboards {
 *     content {
 *         row {
 *             button { ... }
 *         }
 *     }
 * }
 * ```
 *
 * @since 4.4.0
 * @see MessageKeyboards
 *
 * @author ForteScarlet
 */
@Serializable
public data class QGKeyboards internal constructor(public val keyboards: MessageKeyboards) : QGMessageElement {
    public companion object {
        /**
         * 使用 [keyboards] 直接包装构建。
         *
         * @see QGKeyboards
         * @see MessageKeyboards.builder
         */
        @JvmStatic
        public fun create(keyboards: MessageKeyboards): QGKeyboards = QGKeyboards(keyboards)

        /**
         * 使用 [singleButton] 直接包装构建一个单 button。
         */
        @JvmStatic
        public fun create(singleButton: MessageKeyboardButton): QGKeyboards =
            QGKeyboards(MessageKeyboards.create(singleButton))

        /**
         * 使用 [singleRowButtons] 直接包装构建一组单行 buttons。
         */
        @JvmStatic
        public fun create(singleRowButtons: Collection<MessageKeyboardButton>): QGKeyboards =
            QGKeyboards(MessageKeyboards.create(singleRowButtons))

        /**
         * 将一个 JSON 字符串解析为 [QGKeyboards] 对象。
         */
        @JvmStatic
        public fun parse(jsonString: String): QGKeyboards {
            return QGKeyboards(MessageKeyboards.parse(jsonString))
        }
    }
}

/**
 * 构建 [QGKeyboards]。
 *
 * @since 4.4.0
 */
public inline fun QGKeyboards(builder: MessageKeyboardsBuilder.() -> Unit): QGKeyboards {
    return QGKeyboards.create(MessageKeyboards { builder() })
}

internal object KeyboardsParser : SendingMessageParser {
    internal val logger = LoggerFactory.getLogger("love.forte.simbot.component.qguild.message.KeyboardsParser")

    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        // 频道相关的消息发送不支持 keyboards
        if (element is QGKeyboards) {
            logger.warn("Keyboards message is not yet supported for sending to channel.")
        }
    }

    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        if (element is QGKeyboards) {
            val keyboards = element.keyboards
            val builder = builderContext.builderOrNew {
                it.keyboards == null
            }
            builder.keyboards = keyboards
        }
    }
}
