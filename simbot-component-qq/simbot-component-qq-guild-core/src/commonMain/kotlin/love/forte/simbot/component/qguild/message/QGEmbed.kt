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
import love.forte.simbot.event.MessageEvent
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.message.EmbedBuilder
import love.forte.simbot.qguild.model.Message
import kotlin.jvm.JvmStatic


/**
 *
 * embed 消息，一种特殊的 ark。
 *
 * [QGEmbed] 是针对 [Message.Embed] 类型的消息元素实现，在发送时会被作为一个 **独立的** [MessageSendApi] (的 `embed` 属性) 发送。
 *
 * 更多参考 [文档](https://bot.q.qq.com/wiki/develop/api/openapi/message/template/embed_message.html)
 *
 * Note: [QGEmbed] 似乎不能与 [MessageSendApi.Body.messageReference] 配合使用，
 * 也就是尽可能不要在 [MessageEvent.reply] 中使用 [QGEmbed], 否则会导致此消息不可见。
 *
 * @see Message.Embed
 * @see MessageSendApi.Body.embed
 *
 * @property embed 对应的 [Message.Embed] 内容。
 *
 * @author ForteScarlet
 */
@SerialName("qg.embed")
@Serializable
@ConsistentCopyVisibility
public data class QGEmbed internal constructor(public val embed: Message.Embed) : QGMessageElement {

    public companion object {
        /**
         * 将提供的 [Message.Embed] 包装为 [QGEmbed]。
         */
        @JvmStatic
        public fun byEmbed(embed: Message.Embed): QGEmbed = QGEmbed(embed)
    }
}

/**
 * 使用 [EmbedBuilder] 构建并得到 [QGEmbed]。
 *
 * @see QGEmbed
 */
public inline fun buildQGEmbed(block: EmbedBuilder.() -> Unit): QGEmbed =
    QGEmbed.byEmbed(EmbedBuilder().also(block).build())


internal object EmbedParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: love.forte.simbot.message.Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        if (element is QGEmbed) {
            val embed = element.embed
            // 必须是一个全空的 builder。
            val builder = builderContext.builderOrNew { builder -> builder.isEmpty }
            builder.embed = embed
            // 下一次必须是另外新建的builder
            builderContext.nextMustBeNew()
        }
    }

    // TODO Embed暂不不支持 GroupAndC2C

}
