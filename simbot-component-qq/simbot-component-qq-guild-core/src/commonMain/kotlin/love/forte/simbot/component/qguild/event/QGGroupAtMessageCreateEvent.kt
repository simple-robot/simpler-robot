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
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.group.QGGroupMember
import love.forte.simbot.component.qguild.message.QGGroupAndC2CMessageContent
import love.forte.simbot.component.qguild.utils.toTimestamp
import love.forte.simbot.event.ChatGroupMessageEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.MessageReceipt
import love.forte.simbot.qguild.event.GroupAtMessageCreate
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP


/**
 * 群消息事件。需要bot被at后触发。
 *
 * @see GroupAtMessageCreate
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGGroupAtMessageCreateEvent : QGBaseMessageEvent<GroupAtMessageCreate>(), ChatGroupMessageEvent {
    abstract override val bot: QGBot

    /**
     * 事件ID
     */
    abstract override val id: ID

    /**
     * 事件时间
     */
    @OptIn(ExperimentalQGApi::class)
    override val time: Timestamp
        get() = sourceEventEntity.data.timestamp.toTimestamp()

    /**
     * 消息发送者的ID
     */
    override val authorId: ID
        get() = sourceEventEntity.data.author.memberOpenid.ID

    abstract override val messageContent: QGGroupAndC2CMessageContent

    @STP
    abstract override suspend fun author(): QGGroupMember

    /**
     * 此消息事件所处的QQ群。
     * 使用此事件内的 [content.send][QGGroup.send] 发送消息时，
     * 会自动附加回复用的 `msgId`, 不消耗主动消息次数，
     * 但是可能会被自动添加 `@目标` 效果（这是QQ平台决定的）。
     */
    @STP
    abstract override suspend fun content(): QGGroup

    /**
     * 基于当前消息源回复此消息。
     * 会尝试添加引用回复效果。
     */
    @ST
    abstract override suspend fun reply(text: String): MessageReceipt

    /**
     * 基于当前消息源回复此消息。
     * 会尝试添加引用回复效果。
     */
    @ST
    abstract override suspend fun reply(message: Message): MessageReceipt

    /**
     * 基于当前消息源回复此消息。
     * 会尝试添加引用回复效果。
     */
    @ST
    abstract override suspend fun reply(messageContent: MessageContent): MessageReceipt
}
