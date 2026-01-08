/*
 * Copyright (c) 2024. ForteScarlet.
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
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.api.message.GroupAndC2CSendBody
import love.forte.simbot.qguild.model.MessageMedia


/**
 * 一个用于发送的 media 消息内容，内含 [fileInfo][MessageMedia.fileInfo] 信息。
 *
 * @author ForteScarlet
 */
@Serializable
public data class QGMedia(public val media: MessageMedia) : QGMessageElement

internal object MediaParser : SendingMessageParser {
    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.BuilderContext
    ) {
        // Channel not supported media
    }

    override suspend fun invoke(
        index: Int,
        element: Message.Element,
        messages: Messages?,
        builderContext: SendingMessageParser.GroupAndC2CBuilderContext
    ) {
        if (element is QGMedia) {
            val builder = builderContext.builderOrNew {
                isTextOrMedia(it.msgType) && it.media == null
            }.apply {
                msgType = GroupAndC2CSendBody.MSG_TYPE_MEDIA
            }

            builder.media = element.media
            if (builder.content.isEmpty()) {
                builder.content = " "
            }
        }
    }
}
