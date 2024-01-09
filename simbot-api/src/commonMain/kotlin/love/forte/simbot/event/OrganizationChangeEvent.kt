/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.event

import love.forte.simbot.definition.ChatGroup
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.Member
import love.forte.simbot.definition.Organization
import love.forte.simbot.suspendrunner.STP

/**
 * 某 [Organization] 产生了某种变化的事件，例如发生了 [成员变动][MemberIncreaseOrDecreaseEvent]。
 */
@STP
public interface OrganizationChangeEvent : ChangeEvent, OrganizationEvent {
    /**
     * 已经发生了变化的 [Organization]。
     */
    override suspend fun content(): Organization
}

/**
 * 某组织成员增加或减少的事件。
 *
 * @see MemberIncreaseEvent
 * @see MemberDecreaseEvent
 *
 * @author ForteScarlet
 */
@STP
public interface MemberIncreaseOrDecreaseEvent : OrganizationChangeEvent {
    /**
     * 增加或减少成员的 [Organization]。
     */
    override suspend fun content(): Organization

    /**
     * 增加进来的[新成员][MemberIncreaseEvent.member]，或[已经离开的成员][MemberDecreaseEvent.member]。
     * 如不支持获取，则可能得到 `null` 。
     *
     * @see MemberIncreaseEvent.member
     * @see MemberDecreaseEvent.member
     */
    public suspend fun member(): Member?
}

/**
 * 某组织成员增加事件。
 *
 * @see ChatGroupMemberIncreaseEvent
 * @see GuildMemberIncreaseEvent
 *
 * @author ForteScarlet
 */
@STP
public interface MemberIncreaseEvent : MemberIncreaseOrDecreaseEvent {
    /**
     * 增加成员的 [Organization]。
     */
    override suspend fun content(): Organization

    /**
     * 增加进来的新成员。
     * 假如不支持获取，则可能得到 `null` 。
     */
    override suspend fun member(): Member?
}

/**
 * 某组织成员减少事件。
 *
 * @see ChatGroupMemberDecreaseEvent
 * @see GuildMemberDecreaseEvent
 *
 * @author ForteScarlet
 */
@STP
public interface MemberDecreaseEvent : MemberIncreaseOrDecreaseEvent {
    /**
     * 减少成员的 [Organization]。
     */
    override suspend fun content(): Organization

    /**
     * 已经离开的成员。
     * 假如不支持获取，则可能得到 `null` 。
     *
     * 已经离开的成员信息通常都是一种缓存信息。
     * 在能获取的情况下，很大概率其已经无法在对应组织内获取到，
     * 也无法对其发送消息。
     */
    override suspend fun member(): Member?
}

/**
 * 某 [ChatGroup] 成员变动事件。
 *
 * @see ChatGroupMemberIncreaseEvent
 * @see ChatGroupMemberDecreaseEvent
 */
@STP
public interface ChatGroupMemberIncreaseOrDecreaseEvent : MemberIncreaseOrDecreaseEvent, ChatGroupEvent {
    /**
     * 发送成员变动的 [ChatGroup]。
     */
    override suspend fun content(): ChatGroup
}

/**
 * 某 [ChatGroup] 成员增加事件。
 * @author ForteScarlet
 */
@STP
public interface ChatGroupMemberIncreaseEvent : MemberIncreaseEvent, ChatGroupMemberIncreaseOrDecreaseEvent {
    override suspend fun content(): ChatGroup
}

/**
 * 某 [ChatGroup] 成员减少事件。
 * @author ForteScarlet
 */
@STP
public interface ChatGroupMemberDecreaseEvent : MemberDecreaseEvent, ChatGroupMemberIncreaseOrDecreaseEvent {
    override suspend fun content(): ChatGroup
}

/**
 * 某 [Guild] 成员变动事件。
 *
 * @see GuildMemberIncreaseEvent
 * @see GuildMemberDecreaseEvent
 */
@STP
public interface GuildMemberIncreaseOrDecreaseEvent : MemberIncreaseOrDecreaseEvent, GuildEvent {
    /**
     * 发送成员变动的 [Guild]。
     */
    override suspend fun content(): Guild
}

/**
 * 某 [Guild] 成员增加事件。
 * @author ForteScarlet
 */
@STP
public interface GuildMemberIncreaseEvent : MemberIncreaseEvent, GuildMemberIncreaseOrDecreaseEvent {
    override suspend fun content(): Guild
}

/**
 * 某 [Guild] 成员减少事件。
 * @author ForteScarlet
 */
@STP
public interface GuildMemberDecreaseEvent : MemberDecreaseEvent, GuildMemberIncreaseOrDecreaseEvent {
    override suspend fun content(): Guild
}
