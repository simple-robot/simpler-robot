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

@file:Suppress("ConstructorParameterNaming")

package love.forte.simbot.component.kook.event.internal

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.ability.ReplyOption
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
import love.forte.simbot.component.kook.internal.*
import love.forte.simbot.component.kook.message.*
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.event.InteractionMessage
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

    override suspend fun reply(text: String): KookMessageReceipt =
        reply(InteractionMessage.valueOf(text), emptyList())

    /**
     * 保留本次回复选项并进入共享拦截链。
     */
    override suspend fun reply(text: String, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(text), options.toList())

    override suspend fun reply(message: Message): KookMessageReceipt =
        reply(InteractionMessage.valueOf(message), emptyList())

    /**
     * 保留本次回复选项并进入共享拦截链。
     */
    override suspend fun reply(message: Message, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(message), options.toList())

    override suspend fun reply(messageContent: MessageContent): KookMessageReceipt =
        reply(InteractionMessage.valueOf(messageContent), emptyList())

    /**
     * 回复消息。
     */
    override suspend fun reply(messageContent: MessageContent, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(messageContent), options.toList())

    /**
     * 回复保留事件来源，目标仅在拦截器主动查询时解析。
     */
    private suspend fun reply(message: InteractionMessage, options: List<ReplyOption>): KookMessageReceipt =
        bot.interceptMessageSend(
            KookMessageReplyContext.create(
                bot = bot,
                source = this,
                message = message,
                destination = KookMessageSendTarget.channel(_channel.source.id),
                options = options,
                target = { content() },
            ),
        ) { context ->
            _channel.sendChannelMessage(
                context,
                defaultQuote = sourceEvent.msgId,
                defaultTempTargetId = _author.source.id,
            )
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

    override suspend fun reply(text: String): KookMessageReceipt =
        reply(InteractionMessage.valueOf(text), emptyList())

    /**
     * 保留本次回复选项并进入共享拦截链。
     */
    override suspend fun reply(text: String, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(text), options.toList())

    override suspend fun reply(message: Message): KookMessageReceipt =
        reply(InteractionMessage.valueOf(message), emptyList())

    /**
     * 保留本次回复选项并进入共享拦截链。
     */
    override suspend fun reply(message: Message, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(message), options.toList())

    override suspend fun reply(messageContent: MessageContent): KookMessageReceipt =
        reply(InteractionMessage.valueOf(messageContent), emptyList())

    /**
     * 回复消息。
     */
    override suspend fun reply(messageContent: MessageContent, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(messageContent), options.toList())

    /**
     * 回复消息。
     */
    private suspend fun reply(message: InteractionMessage, options: List<ReplyOption>): KookMessageReceipt =
        bot.interceptMessageSend(
            KookMessageReplyContext.create(
                bot = bot,
                source = this,
                message = message,
                destination = KookMessageSendTarget.user(sourceEvent.authorId),
                options = options,
                target = { content() },
            ),
        ) {
            sendDirectMessage(bot, it)
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

    override suspend fun reply(text: String): KookMessageReceipt =
        reply(InteractionMessage.valueOf(text), emptyList())

    /**
     * 保留本次回复选项并进入共享拦截链。
     */
    override suspend fun reply(text: String, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(text), options.toList())

    override suspend fun reply(message: Message): KookMessageReceipt =
        reply(InteractionMessage.valueOf(message), emptyList())

    /**
     * 保留本次回复选项并进入共享拦截链。
     */
    override suspend fun reply(message: Message, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(message), options.toList())

    override suspend fun reply(messageContent: MessageContent): KookMessageReceipt =
        reply(InteractionMessage.valueOf(messageContent), emptyList())

    /**
     * 回复消息。
     */
    override suspend fun reply(messageContent: MessageContent, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(messageContent), options.toList())

    /**
     * 回复消息。
     */
    private suspend fun reply(message: InteractionMessage, options: List<ReplyOption>): KookMessageReceipt =
        bot.interceptMessageSend(
            KookMessageReplyContext.create(
                bot = bot,
                source = this,
                message = message,
                destination = KookMessageSendTarget.channel(_channel.source.id),
                options = options,
                target = { content() },
            ),
        ) { context ->
            _channel.sendChannelMessage(context, defaultQuote = sourceEvent.msgId)
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

    override suspend fun reply(text: String): KookMessageReceipt =
        reply(InteractionMessage.valueOf(text), emptyList())

    /**
     * 保留本次回复选项并进入共享拦截链。
     */
    override suspend fun reply(text: String, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(text), options.toList())

    override suspend fun reply(message: Message): KookMessageReceipt =
        reply(InteractionMessage.valueOf(message), emptyList())

    /**
     * 保留本次回复选项并进入共享拦截链。
     */
    override suspend fun reply(message: Message, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(message), options.toList())

    override suspend fun reply(messageContent: MessageContent): KookMessageReceipt =
        reply(InteractionMessage.valueOf(messageContent), emptyList())

    /**
     * 回复消息。
     */
    override suspend fun reply(messageContent: MessageContent, vararg options: ReplyOption): KookMessageReceipt =
        reply(InteractionMessage.valueOf(messageContent), options.toList())

    /**
     * 回复消息。
     */
    private suspend fun reply(message: InteractionMessage, options: List<ReplyOption>): KookMessageReceipt =
        bot.interceptMessageSend(
            KookMessageReplyContext.create(
                bot = bot,
                source = this,
                message = message,
                destination = KookMessageSendTarget.user(sourceEvent.authorId),
                options = options,
                target = { content() },
            ),
        ) {
            sendDirectMessage(bot, it)
        }

    override val messageContent: KookReceiveMessageContent = sourceEvent.toContent(true, bot)
}
