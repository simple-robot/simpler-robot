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
import love.forte.simbot.component.qguild.channel.QGChannel
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.event.*
import love.forte.simbot.qguild.event.EventChannel
import love.forte.simbot.suspendrunner.STP
import love.forte.simbot.qguild.event.EventChannel as QGSourceEventChannel

/**
 *
 * 子频道相关的事件。
 *
 * [QGChannelEvent] 主要用于为子类型提供一个基础类型，其本身没有过多的事件类型约束。
 *
 * 子频道的增减视为频道服务器变更事件 [OrganizationChangeEvent],
 * 而子频道本身的修改事件则视为子频道变更事件 [ChannelEvent] + [ChangeEvent]。
 *
 * @see QGChannelCreateEvent
 * @see QGChannelUpdateEvent
 * @see QGChannelDeleteEvent
 * @see QGChannelCategoryCreateEvent
 * @see QGChannelCategoryUpdateEvent
 * @see QGChannelCategoryDeleteEvent
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
@STP
public sealed class QGChannelEvent : QGBotEvent<QGSourceEventChannel>() {
    /**
     * 标准库中子频道相关事件得到的事件本体。
     */
    abstract override val sourceEventEntity: EventChannel

    /**
     * 操作者ID
     */
    public val operatorId: ID get() = sourceEventEntity.opUserId.ID
}


/**
 * 子频道被创建事件。是一个频道服务器变更事件。
 */
@STP
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGChannelCreateEvent : QGChannelEvent(), OrganizationChangeEvent {
    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 发生变更的频道服务器。
     */
    abstract override suspend fun content(): QGGuild

    /**
     * 新增的频道。
     */
    public abstract suspend fun channel(): QGChannel
}

/**
 * 子频道信息变更事件。
 * 事件通知即已完成变更。
 */
@STP
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGChannelUpdateEvent : QGChannelEvent(), ChangeEvent, ChannelEvent {
    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 发生变更的频道。
     */
    abstract override suspend fun content(): QGTextChannel

    /**
     * 发生的所在频道服务器
     */
    abstract override suspend fun source(): QGGuild
}

/**
 * 子频道被删除
 *
 * 当收到此事件时，[content] 已经被删除。
 */
@OptIn(FuzzyEventTypeImplementation::class)
@STP
public abstract class QGChannelDeleteEvent : QGChannelEvent(), OrganizationChangeEvent {
    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 发生变更的频道服务器。
     */
    abstract override suspend fun content(): QGGuild

    /**
     * 被删除的频道。
     */
    public abstract suspend fun channel(): QGChannel
}
