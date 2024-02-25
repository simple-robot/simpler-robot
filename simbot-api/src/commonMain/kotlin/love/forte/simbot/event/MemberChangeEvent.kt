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
import love.forte.simbot.suspendrunner.STP


/**
 * 当 [Member] 发生了某种变化时的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface MemberChangeEvent : ChangeEvent, MemberEvent {
    /**
     * 发送了变化的 [Member]。
     *
     */
    override suspend fun content(): Member
}

/**
 * 当 [Guild] 的 [Member] 发生了某种变化时的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface GuildMemberChangeEvent : ChangeEvent, GuildMemberEvent {
    /**
     * 变化成员所在 [Guild]
     */
    override suspend fun source(): Guild

        /**
     * 发送了变化的 [Member]。
     *
     */
    override suspend fun content(): Member
}

/**
 * 当 [ChatGroup] 的 [Member] 发生了某种变化时的事件。
 *
 * @author ForteScarlet
 */
@STP
public interface GroupMemberChangeEvent : ChangeEvent, ChatGroupMemberEvent {
    /**
     * 变化成员所在 [ChatGroup]
     */
    override suspend fun source(): ChatGroup

        /**
     * 发送了变化的 [Member]。
     *
     */
    override suspend fun content(): Member
}
