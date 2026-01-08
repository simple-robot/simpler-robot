/*
 * Copyright (c) 2022-2025. ForteScarlet.
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

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.event.*
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.EventMember
import love.forte.simbot.suspendrunner.STP

/**
 * QQ频道[成员相关的事件][EventIntents.GuildMembers]。
 *
 * [QGMemberEvent] 是一种父事件类型，本身不存在过多类型约束。
 *
 * - [新增频道成员][QGMemberAddEvent]
 * - [频道成员信息更新][QGMemberUpdateEvent]
 * - [频道成员离开/移除][QGMemberRemoveEvent]
 *
 * @see QGMemberAddEvent
 * @see QGMemberUpdateEvent
 * @see QGMemberRemoveEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public sealed class QGMemberEvent : QGEvent<EventMember>() {
    /**
     * 事件接收到的原始的用户信息。
     */
    abstract override val sourceEventEntity: EventMember
}

/**
 * 频道成员增加事件。
 *
 */
@STP
public abstract class QGMemberAddEvent : QGMemberEvent(), GuildMemberIncreaseEvent {
    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 操作者ID。
     */
    public open val operatorId: ID
        get() = sourceEventEntity.opUserId.ID

    /**
     * 操作者。无权限获取、找不到（例如获取时已经离群）等情况会得到null。
     */
    public abstract suspend fun operator(): QGMember?

    /**
     * 新增的频道成员。
     */
    abstract override suspend fun member(): QGMember

    /**
     * 增加了频道成员的频道
     */
    abstract override suspend fun content(): QGGuild
}

/**
 * 频道成员信息更新事件。
 */
@STP
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGMemberUpdateEvent : QGMemberEvent(), MemberChangeEvent, OrganizationSourceEvent {
    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 操作者ID。
     */
    public open val operatorId: ID
        get() = sourceEventEntity.opUserId.ID

    /**
     * 操作者。无权限获取、找不到（例如获取时已经离群）等情况会得到null。
     */
    public abstract suspend fun operator(): QGMember?

    /**
     * 发生变更的成员。
     */
    abstract override suspend fun content(): QGMember

    /**
     * 发生变更的成员的所属频道。
     */
    abstract override suspend fun source(): QGGuild

    /**
     * 同 [source]
     */
    public suspend fun guild(): QGGuild = source()
}

/**
 * 频道成员离开/被移除事件。事件触发时已经被移除。
 */
@STP
public abstract class QGMemberRemoveEvent : QGMemberEvent(), GuildMemberDecreaseEvent {
    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 操作者ID。
     */
    public open val operatorId: ID
        get() = sourceEventEntity.opUserId.ID

    /**
     * 操作者。无权限获取、找不到（例如获取时已经离群）等情况会得到null。
     */
    public abstract suspend fun operator(): QGMember?

    /**
     * 离去的成员。
     */
    abstract override suspend fun member(): QGMember

    /**
     * 成员所处的频道
     */
    abstract override suspend fun content(): QGGuild
}


