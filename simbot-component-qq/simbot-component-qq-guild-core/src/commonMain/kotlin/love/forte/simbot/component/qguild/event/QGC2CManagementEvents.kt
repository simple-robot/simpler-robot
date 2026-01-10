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
import love.forte.simbot.component.qguild.friend.QGFriend
import love.forte.simbot.component.qguild.utils.toTimestamp
import love.forte.simbot.event.ContactEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.qguild.event.C2CManagementData
import love.forte.simbot.suspendrunner.STP

/**
 * 用户模块-用户管理相关事件
 *
 * @see C2CManagementData
 * @author ForteScarlet
 */
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public abstract class QGC2CManagementEvent : QGBotEvent<C2CManagementData>(), ContactEvent {
    abstract override val id: ID

    @OptIn(ExperimentalQGApi::class)
    override val time: Timestamp
        get() = sourceEventEntity.timestamp.toTimestamp()

    /**
     * 事件相关的C2C单聊用户目标
     */
    @STP
    abstract override suspend fun content(): QGFriend

    protected fun C2CManagementData.computeId(): ID = buildString(
        openid.length + timestamp.length + 1
    ) {
        append(openid)
        append('-')
        append(timestamp)
    }.ID
}


/**
 * 用户添加机器人
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGFriendAddEvent : QGC2CManagementEvent()

/**
 * 用户删除机器人
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGFriendDelEvent : QGC2CManagementEvent()

/**
 * 拒绝机器人主动消息
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGC2CMsgRejectEvent : QGC2CManagementEvent()

/**
 * 允许机器人主动消息
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGC2CMsgReceiveEvent : QGC2CManagementEvent()
