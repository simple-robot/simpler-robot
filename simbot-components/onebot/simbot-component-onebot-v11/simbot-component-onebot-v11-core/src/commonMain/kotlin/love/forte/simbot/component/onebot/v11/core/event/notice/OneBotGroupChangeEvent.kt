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
import love.forte.simbot.component.onebot.v11.event.notice.RawGroupDecreaseEvent
import love.forte.simbot.component.onebot.v11.event.notice.RawGroupIncreaseEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.event.MemberDecreaseEvent
import love.forte.simbot.event.MemberIncreaseEvent
import love.forte.simbot.event.MemberIncreaseOrDecreaseEvent
import love.forte.simbot.suspendrunner.STP

/**
 * 群成员增加或减少事件
 *
 * @see OneBotGroupMemberIncreaseEvent
 * @see OneBotGroupMemberDecreaseEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotGroupChangeEvent : OneBotNoticeEvent, MemberIncreaseOrDecreaseEvent {
    /**
     * 群号
     */
    public val groupId: LongID

    /**
     * 增加或离去的群成员ID
     */
    public val userId: LongID

    /**
     * 群
     * @throws Exception
     */
    @STP
    override suspend fun content(): OneBotGroup

    /**
     * 增加或离去的成员。
     * 如果无法获取则可能得到 `null`，
     * 例如成员已经离去而无法获取。
     * @throws Exception
     */
    @STP
    override suspend fun member(): OneBotMember?
}

/**
 * 群成员增加事件
 *
 * @see RawGroupIncreaseEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotGroupMemberIncreaseEvent : OneBotGroupChangeEvent, MemberIncreaseEvent {
    override val sourceEvent: RawGroupIncreaseEvent

    /**
     * 群号
     */
    override val groupId: LongID
        get() = sourceEvent.groupId

    /**
     * 增加群成员ID
     */
    override val userId: LongID
        get() = sourceEvent.userId

    /**
     * 群
     * @throws Exception
     */
    @STP
    override suspend fun content(): OneBotGroup

    /**
     * 增加的成员
     * @throws Exception
     */
    @STP
    override suspend fun member(): OneBotMember
}

/**
 * 群成员减少事件
 *
 * @see RawGroupDecreaseEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public interface OneBotGroupMemberDecreaseEvent : OneBotGroupChangeEvent, MemberDecreaseEvent {
    override val sourceEvent: RawGroupDecreaseEvent

    /**
     * 群号
     */
    override val groupId: LongID
        get() = sourceEvent.groupId

    /**
     * 离去的群成员ID
     */
    override val userId: LongID
        get() = sourceEvent.userId

    /**
     * 群
     * @throws Exception
     */
    @STP
    override suspend fun content(): OneBotGroup

    /**
     * 离去的成员。已经离去无法获取，
     * 始终得到 null
     */
    @STP
    override suspend fun member(): OneBotMember? = null
}
