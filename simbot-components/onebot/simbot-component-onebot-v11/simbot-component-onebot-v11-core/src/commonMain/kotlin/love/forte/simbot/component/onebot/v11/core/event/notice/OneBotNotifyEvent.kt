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

package love.forte.simbot.component.onebot.v11.core.event.notice

import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.core.actor.OneBotGroup
import love.forte.simbot.component.onebot.v11.core.actor.OneBotMember
import love.forte.simbot.component.onebot.v11.event.notice.RawNotifyEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.MemberEvent
import love.forte.simbot.suspendrunner.STP


/**
 * 群成员荣誉变更事件、红包人气王事件或戳一戳事件。
 *
 * @see RawNotifyEvent
 * @see OneBotHonorEvent
 * @see OneBotLuckyKingEvent
 * @see OneBotPokeEvent
 * @author ForteScarlet
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotNotifyEvent : OneBotNoticeEvent {
    override val sourceEvent: RawNotifyEvent

    /**
     * 荣誉类型.
     *
     * @see RawNotifyEvent.honorType
     */
    public val honorType: String?
        get() = sourceEvent.honorType

    /**
     * 群号。如果是代表私聊相关的事件（例如私聊戳一戳）则为 `null`。
     */
    public val groupId: LongID?
        get() = sourceEvent.groupId

    /**
     * 成员ID
     */
    public val userId: LongID
        get() = sourceEvent.userId

}

/**
 * 群成员荣誉变更事件、红包人气王事件或戳一戳事件。
 *
 * @see OneBotNotifyEvent
 *
 * @since 1.4.0
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotGroupNotifyEvent : OneBotNotifyEvent, MemberEvent {
    /**
     * 事件发送的群号
     */
    override val groupId: LongID

    /**
     * 群
     *
     * @throws Exception
     */
    @STP
    override suspend fun source(): OneBotGroup

    /**
     * 群成员
     *
     * @throws Exception
     */
    @STP
    override suspend fun content(): OneBotMember
}

/**
 * 群成员荣誉变更事件
 */
public interface OneBotHonorEvent : OneBotGroupNotifyEvent {
    /**
     * 荣誉类型.
     *
     * @see RawNotifyEvent.honorType
     */
    override val honorType: String
        get() = sourceEvent.honorType!!
}

/**
 * 群红包运气王事件
 */
public interface OneBotLuckyKingEvent : OneBotGroupNotifyEvent {
    /**
     * 人气王用户ID
     */
    public val targetId: LongID
        get() = sourceEvent.targetId!!
}

/**
 * 戳一戳事件
 *
 * @see OneBotMemberPokeEvent
 * @see OneBotBotSelfPokeEvent
 */
public interface OneBotPokeEvent : OneBotNotifyEvent {
    /**
     * 被戳的用户ID
     */
    public val targetId: LongID
        get() = sourceEvent.targetId!!
}

/**
 * 群里Bot以外的群成员被戳一戳事件
 */
public interface OneBotMemberPokeEvent : OneBotPokeEvent, OneBotGroupNotifyEvent

/**
 * 群里Bot被戳一戳事件，即 [targetId] == [selfId]。
 */
public interface OneBotBotSelfPokeEvent : OneBotPokeEvent, OneBotGroupNotifyEvent

/**
 * 私聊里的戳一戳事件，[groupId] == `null`。
 *
 * @since 1.4.0
 */
public interface OneBotPrivatePokeEvent : OneBotPokeEvent
