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
import love.forte.simbot.component.qguild.event.QGGroupAddRobotEvent
import love.forte.simbot.component.qguild.event.QGGroupDelRobotEvent
import love.forte.simbot.component.qguild.event.QGGroupMsgReceiveEvent
import love.forte.simbot.component.qguild.event.QGGroupMsgRejectEvent
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.group.QGGroupMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.group.QGGroupMemberImpl
import love.forte.simbot.component.qguild.internal.group.idGroup
import love.forte.simbot.qguild.event.GroupRobotManagementData

internal class QGGroupAddRobotEventImpl(
    private val _id: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: GroupRobotManagementData,
) : QGGroupAddRobotEvent() {
    override val id: ID
        get() = _id?.ID ?: sourceEventEntity.computeId()

    override suspend fun content(): QGGroup {
        return idGroup(
            bot = bot,
            id = sourceEventEntity.groupOpenid.ID,
            isFake = false
        )
    }

    override suspend fun operator(): QGGroupMember {
        return QGGroupMemberImpl(bot, sourceEventEntity.opMemberOpenid.ID)
    }
}

internal class QGGroupDelRobotEventImpl(
    private val _id: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: GroupRobotManagementData,
) : QGGroupDelRobotEvent() {
    override val id: ID
        get() = _id?.ID ?: sourceEventEntity.computeId()

    override suspend fun content(): QGGroup {
        return idGroup(
            bot = bot,
            id = sourceEventEntity.groupOpenid.ID,
            isFake = false
        )
    }

    override suspend fun operator(): QGGroupMember {
        return QGGroupMemberImpl(bot, sourceEventEntity.opMemberOpenid.ID)
    }
}

internal class QGGroupMsgRejectEventImpl(
    private val _id: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: GroupRobotManagementData,
) : QGGroupMsgRejectEvent() {
    override val id: ID
        get() = _id?.ID ?: sourceEventEntity.computeId()

    override suspend fun content(): QGGroup {
        return idGroup(
            bot = bot,
            id = sourceEventEntity.groupOpenid.ID,
            isFake = false
        )
    }

    override suspend fun operator(): QGGroupMember {
        return QGGroupMemberImpl(bot, sourceEventEntity.opMemberOpenid.ID)
    }
}

internal class QGGroupMsgReceiveEventImpl(
    private val _id: String?,
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: GroupRobotManagementData,
) : QGGroupMsgReceiveEvent() {
    override val id: ID
        get() = _id?.ID ?: sourceEventEntity.computeId()

    override suspend fun content(): QGGroup {
        return idGroup(
            bot = bot,
            id = sourceEventEntity.groupOpenid.ID,
            isFake = false
        )
    }

    override suspend fun operator(): QGGroupMember {
        return QGGroupMemberImpl(bot, sourceEventEntity.opMemberOpenid.ID)
    }
}
