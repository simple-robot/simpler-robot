/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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
    private val idValue: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: C2CManagementData,
) : QGFriendAddEvent() {
    override val id: ID
        get() = idValue?.ID ?: sourceEventEntity.computeId()

    override suspend fun content(): QGFriend {
        return idFriend(
            bot = bot,
            id = sourceEventEntity.openid.ID,
            eventId = idValue,
            seq = null,
        )
    }
}

internal class QGFriendDelEventImpl(
    private val idValue: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: C2CManagementData,
) : QGFriendDelEvent() {
    override val id: ID
        get() = idValue?.ID ?: sourceEventEntity.computeId()

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
    private val idValue: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: C2CManagementData,
) : QGC2CMsgRejectEvent() {
    override val id: ID
        get() = idValue?.ID ?: sourceEventEntity.computeId()

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
    private val idValue: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: C2CManagementData,
) : QGC2CMsgReceiveEvent() {
    override val id: ID
        get() = idValue?.ID ?: sourceEventEntity.computeId()

    override suspend fun content(): QGFriend {
        return idFriend(
            bot = bot,
            id = sourceEventEntity.openid.ID,
            eventId = null,
            seq = null,
        )
    }
}
