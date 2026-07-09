/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.qguild.model

import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.qguild.model.MessageKeyboards.Companion.builder
import kotlin.jvm.JvmStatic

public typealias MessageKeyboardButton = MessageKeyboard

/**
 * [消息交互=>消息按钮](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/trans/msg-btn.html)
 *
 * Java 中可以通过 [builder] 构建实例；Kotlin 中还可以通过 DSL API 构建实例：
 * ```kotlin
 * val keyboards = MessageKeyboards {
 *     // this: MessageKeyboardsBuilder
 * }
 * ```
 *
 * @since 4.4.0
 * @see MessageKeyboard
 * @see MessageKeyboardsBuilder
 *
 * @author ForteScarlet
 */
@Serializable
public data class MessageKeyboards internal constructor(val content: Content) {
    /**
     * 按钮内容，包含若干 [行][rows]。
     */
    @Serializable
    public data class Content internal constructor(public val rows: List<ContentRow>)

    /**
     * 每行的内容，包含若干 [按钮][buttons]。
     */
    @Serializable
    public data class ContentRow internal constructor(public val buttons: List<MessageKeyboardButton>)

    public companion object {
        /**
         * 创建一个只有单行的 [消息按钮][MessageKeyboards]。
         */
        @JvmStatic
        public fun create(singleRowButtons: Collection<MessageKeyboardButton>): MessageKeyboards =
            MessageKeyboards(Content(listOf(ContentRow(singleRowButtons.toList()))))

        /**
         * 创建一个只有一个 [消息按钮][MessageKeyboards] 的 keyboard 对象。
         */
        @JvmStatic
        public fun create(singleButton: MessageKeyboardButton): MessageKeyboards =
            MessageKeyboards(Content(listOf(ContentRow(listOf(singleButton)))))

        /**
         * 将一个 JSON 字符串解析为 [MessageKeyboards] 对象。
         */
        @JvmStatic
        public fun parse(jsonString: String): MessageKeyboards {
            return QQGuild.DefaultJson.decodeFromString(serializer(), jsonString)
        }

        /**
         * 获取一个 Builder。
         */
        @JvmStatic
        public fun builder(): MessageKeyboardsBuilder {
            return MessageKeyboardsBuilder()
        }
    }
}
