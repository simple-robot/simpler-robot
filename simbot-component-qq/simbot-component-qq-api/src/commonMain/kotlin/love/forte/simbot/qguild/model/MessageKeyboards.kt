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
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.common.QQ
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
 * @property content 按钮内容。
 * @author ForteScarlet
 */
@Serializable
public class MessageKeyboards internal constructor(public val content: Content) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageKeyboards) return false
        return content == other.content
    }

    override fun hashCode(): Int = content.hashCode()

    override fun toString(): String = "MessageKeyboards(content=$content)"

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("content"))
    public operator fun component1(): Content = content

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(content: Content = this.content): MessageKeyboards = MessageKeyboards(content)
    //endregion

    /**
     * 按钮内容，包含若干 [行][rows]。
     *
     * @property rows 行内容。
     */
    @Serializable
    public class Content internal constructor(public val rows: List<ContentRow>) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Content) return false
            return rows == other.rows
        }

        override fun hashCode(): Int = rows.hashCode()

        override fun toString(): String = "Content(rows=$rows)"

        //region data-class 兼容
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("rows"))
        public operator fun component1(): List<ContentRow> = rows

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(rows: List<ContentRow> = this.rows): Content = Content(rows)
        //endregion
    }

    /**
     * 每行的内容，包含若干 [按钮][buttons]。
     *
     * @property buttons 按钮列表。
     */
    @Serializable
    public class ContentRow internal constructor(public val buttons: List<MessageKeyboardButton>) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ContentRow) return false
            return buttons == other.buttons
        }

        override fun hashCode(): Int = buttons.hashCode()

        override fun toString(): String = "ContentRow(buttons=$buttons)"

        //region data-class 兼容
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("buttons"))
        public operator fun component1(): List<MessageKeyboardButton> = buttons

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            buttons: List<MessageKeyboardButton> = this.buttons,
        ): ContentRow = ContentRow(buttons)
        //endregion
    }

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
            return QQ.DefaultJson.decodeFromString(serializer(), jsonString)
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
