/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.message.PlainText
import love.forte.simbot.message.Text
import love.forte.simbot.qguild.message.ContentTextDecoder
import love.forte.simbot.qguild.message.ContentTextEncoder


/**
 *
 * 直接作用在 [Message.content][love.forte.simbot.qguild.model.Message.content] 上
 * 不经转义处理的消息。
 *
 * 当收到消息事件时，
 * 如果消息中不包含任何其他特殊信息（主要是提及信息，例如提及所有人、提及用户、提及频道等）
 * 便会将 `content` 直接转化为此类而不是 [Text]。
 *
 * 当发送消息使用 [QGContentText] 时消息内容不会被尝试转义为无[内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)的消息文本，而是直接追加 [content] 中的内容。
 *
 * 举个例子，如果使用 [Text] 发送消息，则消息内容会被尝试转为无[内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)的文本：
 * ```kotlin
 * Text { "用户: <@user_id>" }
 * ```
 * 在发送的时候会被转成：
 * ```Text
 * 用户: &lt;@user_id&gt;
 * ```
 *
 * 而使用 [QGContentText] 则不会被转义，它会直接原样输出。
 *
 * 解码、转码器参考 [ContentTextDecoder] 、[ContentTextEncoder]，
 * 或使用 [decode]、[encode]。
 *
 *
 * @author ForteScarlet
 */
@SerialName("qg.contentText")
@Serializable
public data class QGContentText(public val content: String) : PlainText, QGMessageElement {
    /**
     * 同 [content]。
     */
    override val text: String
        get() = content

    /**
     * 将 [content] 视为未转义的源文本，并使用 [ContentTextEncoder]
     * 得到一个所有特殊字符都被转为转义后文本的内容。
     *
     * @return 转义后的结果
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun encode(): String = ContentTextEncoder.encode(content)

    /**
     * 将 [content] 视为已被转义的转义结果，并使用 [ContentTextDecoder]
     * 得到一个将转义文本解码为未转义字符的结果
     *
     * @return 解码后的结果
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun decode(): String = ContentTextDecoder.decode(content)
}
