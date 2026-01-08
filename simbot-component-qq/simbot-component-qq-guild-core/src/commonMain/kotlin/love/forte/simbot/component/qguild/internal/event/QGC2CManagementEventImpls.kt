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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.event.QGC2CMsgReceiveEvent
import love.forte.simbot.component.qguild.event.QGC2CMsgRejectEvent
import love.forte.simbot.component.qguild.event.QGFriendAddEvent
import love.forte.simbot.component.qguild.event.QGFriendDelEvent
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.friend.idFriend
import love.forte.simbot.qguild.event.C2CManagementData


internal class QGFriendAddEventImpl(
    private val _id: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: C2CManagementData,
) : QGFriendAddEvent() {
    override val id: ID
        get() = _id?.ID ?: sourceEventEntity.computeId()

    override suspend fun content(): QGFriend {
        return idFriend(
            bot = bot,
            id = sourceEventEntity.openid.ID,
            eventId = _id,
            seq = null,
        )
    }
}

internal class QGFriendDelEventImpl(
    private val _id: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: C2CManagementData,
) : QGFriendDelEvent() {
    override val id: ID
        get() = _id?.ID ?: sourceEventEntity.computeId()

    override suspend fun content(): QGFriend {
        return idFriend(
            bot = bot,
            id = sourceEventEntity.openid.ID,
            eventId = null,
            seq = null,
        )
    }
}

internal class QGC2CMsgRejectEventImpl(
    private val _id: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: C2CManagementData,
) : QGC2CMsgRejectEvent() {
    override val id: ID
        get() = _id?.ID ?: sourceEventEntity.computeId()

    override suspend fun content(): QGFriend {
        return idFriend(
            bot = bot,
            id = sourceEventEntity.openid.ID,
            eventId = null,
            seq = null,
        )
    }
}

internal class QGC2CMsgReceiveEventImpl(
    private val _id: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: C2CManagementData,
) : QGC2CMsgReceiveEvent() {
    override val id: ID
        get() = _id?.ID ?: sourceEventEntity.computeId()

    override suspend fun content(): QGFriend {
        return idFriend(
            bot = bot,
            id = sourceEventEntity.openid.ID,
            eventId = null,
            seq = null,
        )
    }
}
