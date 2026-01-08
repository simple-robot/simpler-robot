/*
 * Copyright (c) 2022-2024. ForteScarlet.
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
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.api.message.MessageSendApi
import kotlin.jvm.JvmOverloads

/**
 *
 * QQ频道机器人为公域时可能需要指定一个回复消息的目标，通过拼接 [QGReplyTo] 到当前消息列表中来提供一个回复消息的目标信息。
 *
 * [QGReplyTo] 是一个仅用于发送的消息，不会在 [QGMessageContent.messages] 中得到。
 *
 * [QGReplyTo] 在消息链中被解析时，如果存在，会**覆盖**当前所有已经被解析存在的消息体的 [msgId][MessageSendApi.Body.msgId]，
 * 但是无法影响到尚未被构建的未知 builder。
 *
 * 更多相关说明参考 [MessageParsers.parse]。
 *
 * @author ForteScarlet
 */
@SerialName("qg.replyTo")
@Serializable
@QGSendOnly
public data class QGReplyTo
@JvmOverloads constructor(val id: ID, val seq: Int? = null) :
    QGMessageElement

internal object ReplyToParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        if (element is QGReplyTo) {
            builderContext.builders.forEach {
                it.msgId = element.id.literal
            }
        }
    }

    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        if (element is QGReplyTo) {
            builderContext.builders.forEach {
                it.msgId = element.id.literal
                element.seq?.also { seq -> it.msgSeq = seq }
            }
        }
    }
}
