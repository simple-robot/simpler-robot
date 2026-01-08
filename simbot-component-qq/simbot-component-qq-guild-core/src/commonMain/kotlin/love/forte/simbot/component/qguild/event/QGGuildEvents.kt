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
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.event.ChangeEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.GuildEvent
import love.forte.simbot.event.OrganizationChangeEvent
import love.forte.simbot.qguild.event.EventGuild
import love.forte.simbot.suspendrunner.STP

/**
 *
 * 频道变更事件相关。
 *
 * [QGGuildEvent] 主要为子类型提供基本类型，但 [QGGuildEvent] 本身没有过多事件类型约束。
 *
 * [频道新增事件][QGGuildCreateEvent] 表示bot进入到了一个新的频道中。
 *
 * [频道更新事件][QGGuildUpdateEvent] 表示某个频道的信息发生了更新。
 *
 * [频道移除事件][QGGuildDeleteEvent] 表示bot离开了某个频道。
 * 此事件与前两者有较大差别：它不实现 [GuildEvent] —— 毕竟频道已经离开（不存在）了。
 *
 * @see QGGuildCreateEvent
 * @see QGGuildUpdateEvent
 * @see QGGuildDeleteEvent
 *
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public sealed class QGGuildEvent : QGBotEvent<EventGuild>() {
    /**
     * 事件操作者的ID
     */
    public val operatorId: ID get() = sourceEventEntity.opUserId.ID
}

/**
 * 频道创建事件。
 *
 * 触发时机：
 * - 机器人被加入到某个频道服务器的时候
 */
@STP
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGGuildCreateEvent : QGGuildEvent(), GuildEvent, ChangeEvent {
    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 创建的guild。
     */
    abstract override suspend fun content(): QGGuild
}

/**
 * 频道更新事件。
 *
 * 触发时机：
 * - 频道信息变更
 */
@STP
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGGuildUpdateEvent : QGGuildEvent(), GuildEvent, OrganizationChangeEvent {
    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 被更新的 [QGGuild]。
     */
    abstract override suspend fun content(): QGGuild
}

/**
 * 频道删除事件。
 *
 * [QGGuildDeleteEvent] 不实现 [GuildEvent]，因为事件触发时已经离开频道，不存在可稳定获取的 [QGGuild] 实例。
 * 但是此事件额外提供属性 [guild], 代表获取一个**可能存在**的不稳定 [QGGuild]. 详细内容参考 [guild] 说明。
 *
 *
 * 触发时机：
 * - 频道被解散
 * - 机器人被移除
 */
@STP
public abstract class QGGuildDeleteEvent : QGGuildEvent() {
    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 被删除的guild。
     *
     * 获取一个**可能存在**的不稳定 [QGGuild].
     * 之所以称其为**不稳定**，因为这个对象**可能**来自于内部缓存中已经被移除的对象。
     * 当此对象能够被获取时，也已经无法使用大部分API（发送消息等）
     *
     * 如果希望得到较为可靠的**频道信息**，使用 [sourceEventEntity]。
     */
    @FragileSimbotAPI
    public abstract val guild: QGGuild?
}
