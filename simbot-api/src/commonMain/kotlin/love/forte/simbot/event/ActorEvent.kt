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

import love.forte.simbot.definition.*
import love.forte.simbot.suspendrunner.STP


/**
 * 一个以某 [Actor] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ActorEvent : BotEvent, ContentEvent {
    /**
     * 被作为事件中心的 [Actor]。
     */
    override suspend fun content(): Actor
}

/**
 * 一个以某 [Contact] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ContactEvent : ActorEvent {
    /**
     * 被作为事件中心的 [Contact]。
     */
    override suspend fun content(): Contact
}

/**
 * 一个以某 [Organization] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface OrganizationEvent : ActorEvent {
    /**
     * 被作为事件中心的 [Organization]。
     */
    override suspend fun content(): Organization
}

/**
 * 一个以某 [ChatRoom] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChatRoomEvent : ActorEvent {
    /**
     * 被作为事件中心的 [ChatRoom]。
     */
    override suspend fun content(): ChatRoom
}

/**
 *
 * 一个以某 [ChatGroup] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChatGroupEvent : ChatRoomEvent, OrganizationEvent {
    /**
     * 被作为事件中心的 [ChatGroup]。
     */
    override suspend fun content(): ChatGroup
}

/**
 * 一个以某 [Guild] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface GuildEvent : OrganizationEvent {
    /**
     * 被作为事件中心的 [Guild]。
     */
    override suspend fun content(): Guild
}

/**
 * 一个以某 [Channel] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChannelEvent : ActorEvent {

    /**
     * 事件中的 [channel][content] 所属的 [Guild]。
     *
     */
    public suspend fun guild(): Guild

    /**
     * 被作为事件中心的 [Channel]。
     */
    override suspend fun content(): Channel
}

/**
 * 一个以某 [ChatChannel] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChatChannelEvent : ChannelEvent, ChatRoomEvent {
    /**
     * 被作为事件中心的 [ChatChannel]。
     */
    override suspend fun content(): ChatChannel
}

/**
 * 可以感知到 [Organization] 的事件类型。
 * 此类型由一些存在组织信息、但组织信息不是主要信息的事件类型实现。
 */
@STP
public interface OrganizationAwareEvent : BotEvent {
    /**
     * 事件中的 [Organization].
     */
    public suspend fun organization(): Organization
}

/**
 * 一个以某 [Member] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface MemberEvent : ActorEvent, OrganizationAwareEvent {
    /**
     * 事件中 [member][content] 所属的 [Organization]。
     *
     */
    override suspend fun organization(): Organization

    /**
     * 被作为事件中心的 [Member]。
     */
    override suspend fun content(): Member
}

/**
 * 一个以某 [ChatGroup] 中的 [Member] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface ChatGroupMemberEvent : MemberEvent {
    /**
     * 事件中 [member][content] 所属的 [ChatGroup]。
     */
    override suspend fun organization(): ChatGroup

    /**
     * 被作为事件中心的 [Member]。
     */
    override suspend fun content(): Member
}

/**
 * 一个以某 [Guild] 中的 [Member] 为中心的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface GuildMemberEvent : MemberEvent {
    /**
     * 事件中 [member][content] 所属的 [Guild]。
     */
    override suspend fun organization(): Guild

    /**
     * 被作为事件中心的 [Member]。
     */
    override suspend fun content(): Member
}

