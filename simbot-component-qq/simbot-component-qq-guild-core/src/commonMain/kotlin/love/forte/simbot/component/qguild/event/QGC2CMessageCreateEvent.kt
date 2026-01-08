/*
 * Copyright (c) 2024-2025. ForteScarlet.
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

package love.forte.simbot.component.qguild.event

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.message.QGGroupAndC2CMessageContent
import love.forte.simbot.component.qguild.message.QGMessageReceipt
import love.forte.simbot.component.qguild.utils.toTimestamp
import love.forte.simbot.event.ContactMessageEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.qguild.event.C2CMessageCreate
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP


/**
 * 一个C2C单聊消息事件。
 *
 * @see C2CMessageCreate
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGC2CMessageCreateEvent : QGBaseMessageEvent<C2CMessageCreate>(), ContactMessageEvent {
    abstract override val bot: QGBot

    abstract override val id: ID

    @OptIn(ExperimentalQGApi::class)
    override val time: Timestamp
        get() = sourceEventEntity.data.timestamp.toTimestamp()

    override val authorId: ID
        get() = sourceEventEntity.data.author.userOpenid.ID

    abstract override val messageContent: QGGroupAndC2CMessageContent

    /**
     * 消息事件来源的 [QGFriend]
     */
    @STP
    abstract override suspend fun content(): QGFriend

    /**
     * 向此用户目标回复消息。
     */
    @ST
    abstract override suspend fun reply(text: String): QGMessageReceipt

    /**
     * 向此用户目标回复消息。
     */
    @ST
    abstract override suspend fun reply(message: Message): QGMessageReceipt

    /**
     * 向此用户目标回复消息。
     */
    @ST
    abstract override suspend fun reply(messageContent: MessageContent): QGMessageReceipt
}
