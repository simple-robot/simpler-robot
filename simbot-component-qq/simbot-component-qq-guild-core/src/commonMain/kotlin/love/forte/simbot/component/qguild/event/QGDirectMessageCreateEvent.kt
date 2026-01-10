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

package love.forte.simbot.component.qguild.event

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.dms.QGDmsContact
import love.forte.simbot.component.qguild.utils.toTimestamp
import love.forte.simbot.event.ContactMessageEvent
import love.forte.simbot.qguild.event.DirectMessageCreate
import love.forte.simbot.qguild.model.Message
import love.forte.simbot.suspendrunner.STP


/**
 * 频道单聊事件，源自 [DirectMessageCreate]
 *
 * @see DirectMessageCreate
 * @author ForteScarlet
 */
public abstract class QGDirectMessageCreateEvent : QGMessageEvent(), ContactMessageEvent {
    abstract override val sourceEventEntity: Message

    @OptIn(ExperimentalQGApi::class)
    override val time: Timestamp
        get() = sourceEventEntity.timestamp.toTimestamp()

    override val authorId: ID
        get() = sourceEventEntity.author.id.ID

    /**
     * 发送消息的频道私聊用户目标
     */
    @STP
    abstract override suspend fun content(): QGDmsContact


}
