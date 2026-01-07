/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.event.internal

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.kook.KookChatChannel
import love.forte.simbot.component.kook.KookGuild
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.component.kook.KookUserChat
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.event.KookBotSelfChannelMessageEvent
import love.forte.simbot.component.kook.event.KookBotSelfMessageEvent
import love.forte.simbot.component.kook.event.KookChannelMessageEvent
import love.forte.simbot.component.kook.event.KookContactMessageEvent
import love.forte.simbot.component.kook.internal.KookGuildImpl
import love.forte.simbot.component.kook.internal.KookMemberImpl
import love.forte.simbot.component.kook.internal.KookUserChatImpl
import love.forte.simbot.component.kook.internal.send
import love.forte.simbot.component.kook.message.KookMessageReceipt
import love.forte.simbot.component.kook.message.KookReceiveMessageContent
import love.forte.simbot.component.kook.message.sendToChannel
import love.forte.simbot.component.kook.message.toContent
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.kook.api.userchat.CreateUserChatApi
import love.forte.simbot.kook.event.TextExtra
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.kook.event.Event as KEvent


internal class KookChannelMessageEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: KEvent<TextExtra>,
    private val _author: KookMemberImpl,
    private val _channel: KookChatChannel,
    private val _guild: KookGuildImpl,
    override val sourceEventRaw: String
) : KookChannelMessageEvent() {
    override val authorId: ID
        get() = sourceEvent.authorId.ID

    override suspend fun author(): KookMember = _author
    override suspend fun content(): KookChatChannel = _channel
    override suspend fun source(): KookGuild = _guild

    override suspend fun reply(message: Message): KookMessageReceipt {
        return message.sendToChannel(
            bot,
            targetId = _channel.source.id,
            quote = sourceEvent.msgId,
            tempTargetId = null,
            defaultTempTargetId = _author.source.id
        )
            ?: throw IllegalArgumentException("Valid messages must not be empty.")
    }

    override suspend fun reply(text: String): KookMessageReceipt {
        return _channel.send(text = text, quote = sourceEvent.msgId)
    }

    override suspend fun reply(messageContent: MessageContent): KookMessageReceipt {
        return _channel.send(messageContent, sourceEvent.msgId)
    }

    override val messageContent: KookReceiveMessageContent = sourceEvent.toContent(false, bot)
}


internal class KookContactMessageEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: KEvent<TextExtra>,
    override val sourceEventRaw: String
) : KookContactMessageEvent() {
    override val authorId: ID
        get() = sourceEvent.authorId.ID

    private val userChatViewInitLock = Mutex()

    private var userChatView: KookUserChatImpl? = null

    private suspend fun userChatView(): KookUserChatImpl {
        val view = CreateUserChatApi.create(sourceEvent.authorId).requestDataBy(bot)
        return KookUserChatImpl(bot, view)
    }

    override suspend fun content(): KookUserChat = userChatView ?: userChatViewInitLock.withLock {
        userChatView ?: userChatView().also { userChatView = it }
    }

    override suspend fun reply(message: Message): KookMessageReceipt {
        return content().send(message)
    }

    override suspend fun reply(text: String): KookMessageReceipt {
        return content().send(text)
    }

    override suspend fun reply(messageContent: MessageContent): KookMessageReceipt {
        return content().send(messageContent)
    }

    override val messageContent: KookReceiveMessageContent = sourceEvent.toContent(true, bot)
}


internal class KookBotSelfChannelMessageEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: KEvent<TextExtra>,
    private val _channel: KookChatChannel,
    private val _member: KookMemberImpl,
    private val _guild: KookGuildImpl,
    override val sourceEventRaw: String
) : KookBotSelfChannelMessageEvent() {
    override val authorId: ID
        get() = sourceEvent.authorId.ID

    override suspend fun content(): KookChatChannel = _channel
    override suspend fun author(): KookMember = _member
    override suspend fun source(): KookGuild = _guild

    override suspend fun reply(message: Message): KookMessageReceipt {
        return _channel.send(message, sourceEvent.msgId)
    }

    override suspend fun reply(text: String): KookMessageReceipt {
        return _channel.send(text, sourceEvent.msgId)
    }

    override suspend fun reply(messageContent: MessageContent): KookMessageReceipt {
        return _channel.send(messageContent, sourceEvent.msgId)
    }

    override val messageContent: KookReceiveMessageContent = sourceEvent.toContent(false, bot)

}


internal class KookBotSelfMessageEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: KEvent<TextExtra>,
    override val sourceEventRaw: String
) : KookBotSelfMessageEvent() {
    override val authorId: ID
        get() = sourceEvent.authorId.ID

    private val userChatViewInitLock = Mutex()
    private var userChatView: KookUserChatImpl? = null

//    private val userChatView = lazyValue {
//        val view = CreateUserChatApi.create(sourceEvent.authorId).requestDataBy(bot)
//        KookUserChatImpl(bot, view)
//    }

    private suspend fun userChatView(): KookUserChatImpl {
        val view = CreateUserChatApi.create(sourceEvent.authorId).requestDataBy(bot)
        return KookUserChatImpl(bot, view)
    }

    override suspend fun content(): KookUserChat = userChatView ?: userChatViewInitLock.withLock {
        userChatView ?: userChatView().also { userChatView = it }
    }

    override suspend fun reply(message: Message): KookMessageReceipt {
        return content().send(message)
    }

    override suspend fun reply(text: String): KookMessageReceipt {
        return content().send(text)
    }

    override suspend fun reply(messageContent: MessageContent): KookMessageReceipt {
        return content().send(messageContent)
    }

    override val messageContent: KookReceiveMessageContent = sourceEvent.toContent(true, bot)
}
