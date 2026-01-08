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

package love.forte.simbot.component.qguild.internal.message

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.message.MessageParsers
import love.forte.simbot.component.qguild.message.QGBaseMessageContent
import love.forte.simbot.component.qguild.message.QGGroupAndC2CMessageContent
import love.forte.simbot.component.qguild.message.QGMessageContent
import love.forte.simbot.message.Messages
import love.forte.simbot.qguild.api.message.DeleteMessageApi
import love.forte.simbot.qguild.api.message.GetMessageApi
import love.forte.simbot.qguild.model.Message

/**
 *
 * @author ForteScarlet
 */
internal class QGMessageContentImpl(
    private val bot: QGBot,
    override val sourceMessage: Message
) : QGMessageContent() {

    override val id: ID get() = sourceMessage.id.ID

    private val parseContext by lazy(LazyThreadSafetyMode.PUBLICATION) { MessageParsers.parse(sourceMessage) }

    override val messages: Messages get() = parseContext.messages

    override val plainText: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        parseContext.plainTextBuilder.toString()
    }

    override val sourceContent: String
        get() = sourceMessage.content

    @OptIn(ExperimentalQGApi::class)
    override suspend fun delete(vararg options: DeleteOption) {
        // TODO DeleteMessageApi.hidetip
        val api = DeleteMessageApi.create(sourceMessage.channelId, sourceMessage.id)

        kotlin.runCatching {
            bot.executeData(api)
        }.onFailure { e ->
            if (StandardDeleteOption.IGNORE_ON_FAILURE !in options) {
                throw e
            }
        }
    }

    @OptIn(ExperimentalQGApi::class)
    override suspend fun queryReferenceMessage(): QGBaseMessageContent? {
        val ref = reference() ?: return null
        val api = GetMessageApi.create(sourceMessage.channelId, ref.source.messageId)

        val msg = bot.executeData(api)

        return QGMessageContentImpl(bot, msg)
    }

    override fun toString(): String {
        return "QGReceiveMessageContentImpl(messageId=${sourceMessage.id}, sourceMessage=$sourceMessage)"
    }


    override fun equals(other: Any?): Boolean {
        if (other !is QGMessageContent) return false
        if (other === this) return true
        return sourceMessage.id == other.sourceMessage.id
    }

    override fun hashCode(): Int = sourceMessage.id.hashCode()
}

internal class QGGroupAndC2CMessageContentImpl(
    override val id: ID,
    override val sourceContent: String,
    override val attachments: List<Message.Attachment>,
) : QGGroupAndC2CMessageContent() {
    private val parseContext by lazy(LazyThreadSafetyMode.PUBLICATION) {
        MessageParsers.parse(
            sourceContent,
            attachments
        )
    }

    override val messages: Messages
        get() = parseContext.messages

    override val plainText: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        parseContext.plainTextBuilder.toString()
    }

    override fun toString(): String {
        return "QGGroupAndC2CMessageContentImpl(id=$id, sourceContent=$sourceContent)"
    }
}
