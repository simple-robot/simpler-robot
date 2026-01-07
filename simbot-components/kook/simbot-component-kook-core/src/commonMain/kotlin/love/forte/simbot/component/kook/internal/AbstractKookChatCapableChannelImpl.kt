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

package love.forte.simbot.component.kook.internal

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.KookCategory
import love.forte.simbot.component.kook.KookChannelUpdater
import love.forte.simbot.component.kook.KookChatCapableChannel
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.message.KookChannelMessageDetailsContent
import love.forte.simbot.component.kook.message.KookMessageCreatedReceipt.Companion.asReceipt
import love.forte.simbot.component.kook.message.KookMessageReceipt
import love.forte.simbot.component.kook.message.KookReceiveMessageContent
import love.forte.simbot.component.kook.message.sendToChannel
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.kook.api.message.SendChannelMessageApi
import love.forte.simbot.kook.messages.MessageType
import love.forte.simbot.kook.objects.Channel
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author ForteScarlet
 */
internal abstract class AbstractKookChatCapableChannelImpl(
    override val bot: KookBotImpl,
    override val source: Channel,
) : KookChatCapableChannel {
    override val coroutineContext: CoroutineContext
        get() = bot.subContext

    override suspend fun send(request: SendChannelMessageApi): KookMessageReceipt {
        return request.requestDataBy(bot).asReceipt(false, bot)
    }

    override suspend fun send(message: Message, quote: ID?, tempTargetId: ID?): KookMessageReceipt {
        return send(message, quote?.literal, tempTargetId?.literal)
    }

    override suspend fun send(message: MessageContent, quote: ID?, tempTargetId: ID?): KookMessageReceipt {
        return send(message, quote?.literal, tempTargetId?.literal)
    }

    override val category: KookCategory?
        get() = source.category(bot)

    override suspend fun delete(vararg options: DeleteOption) {
        bot.deleteChannel(source.id, options)
    }

    override fun updater(): KookChannelUpdater = KookChannelUpdaterImpl(this, bot)
}

internal suspend fun KookChatCapableChannel.send(
    message: Message,
    quote: String? = null,
    tempTargetId: String? = null
): KookMessageReceipt {
    return message.sendToChannel(
        bot,
        targetId = source.id,
        quote = quote,
        tempTargetId = tempTargetId,
        defaultTempTargetId = null
    ) ?: throw IllegalArgumentException("Valid messages must not be empty.")
}

internal suspend fun KookChatCapableChannel.send(
    message: MessageContent,
    quote: String? = null,
    tempTargetId: String? = null
): KookMessageReceipt {
    return when (message) {
        is KookReceiveMessageContent -> {
            val source = message.source
            SendChannelMessageApi.create(
                type = source.type?.value,
                targetId = this.source.id,
                content = source.content,
                quote = quote,
                nonce = null,
                tempTargetId = tempTargetId,
            ).requestDataBy(bot).asReceipt(false, bot)
        }

        is KookChannelMessageDetailsContent -> {
            val details = message.details
            SendChannelMessageApi.create(
                type = details.type,
                targetId = this.source.id,
                content = details.content,
                quote = quote ?: details.quote?.id,
                nonce = null,
                tempTargetId = tempTargetId,
            ).requestDataBy(bot).asReceipt(false, bot)
        }

        else -> {
            send(message.messages)
        }
    }
}

internal suspend fun KookChatCapableChannel.send(
    text: String,
    quote: String? = null,
    tempTargetId: String? = null
): KookMessageReceipt {
    return send(
        MessageType.TEXT.type,
        text,
        quote,
        null,
        tempTargetId
    )
}

/**
 * 根据 [SendChannelMessageApi] api 构建并发送消息。
 */
private suspend fun KookChatCapableChannel.send(
    type: Int,
    content: String,
    quote: String? = null,
    nonce: String? = null,
    tempTargetId: String? = null,
): KookMessageReceipt {
    val request = SendChannelMessageApi.create(type, source.id, content, quote, nonce, tempTargetId)
    return send(request)
}
