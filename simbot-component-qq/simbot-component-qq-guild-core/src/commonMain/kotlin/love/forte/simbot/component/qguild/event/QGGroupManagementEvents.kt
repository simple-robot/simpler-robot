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
import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.group.QGGroupMember
import love.forte.simbot.component.qguild.utils.toTimestamp
import love.forte.simbot.event.ChatGroupEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.qguild.event.GroupRobotManagementData
import love.forte.simbot.suspendrunner.STP

/**
 * 群聊模块中的管理相关事件。
 *
 * @see GroupRobotManagementData
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public abstract class QGGroupManagementEvent :
    QGBotEvent<GroupRobotManagementData>(),
    ChatGroupEvent {
    abstract override val id: ID

    @OptIn(ExperimentalQGApi::class)
    override val time: Timestamp
        get() = sourceEventEntity.timestamp.toTimestamp()

    /**
     * 事件相关的群
     */
    @STP
    abstract override suspend fun content(): QGGroup

    /**
     * 事件行为的操作者
     */
    @STP
    public abstract suspend fun operator(): QGGroupMember

    protected fun GroupRobotManagementData.computeId(): ID = buildString(
        groupOpenid.length
                + opMemberOpenid.length
                + timestamp.length
                + 2
    ) {
        append(groupOpenid)
        append('-')
        append(opMemberOpenid)
        append('-')
        append(timestamp)
    }.ID
}

/**
 * 机器人加入群聊
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGGroupAddRobotEvent : QGGroupManagementEvent()

/**
 * 机器人退出群聊
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGGroupDelRobotEvent : QGGroupManagementEvent()

/**
 * 群聊拒绝机器人主动消息
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGGroupMsgRejectEvent : QGGroupManagementEvent()

/**
 * 群聊接受机器人主动消息
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGGroupMsgReceiveEvent : QGGroupManagementEvent()
