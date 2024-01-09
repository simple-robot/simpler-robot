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

package love.forte.simbot.definition

import love.forte.simbot.ability.SendSupport
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.suspendrunner.ST


/**
 *
 * 一个聊天室。
 *
 * 一个聊天室是一个可以向其中发送消息的行为主体。
 * 向聊天室发送的消息可能会被多个 [组织成员][Member] 收到。
 *
 * [ChatRoom] 通常与 [Organization] 配合实现，
 * 例如最常见的概念：[群聊][ChatGroup] 和 [频道服务器][Guild] 中的 [频道][Channel] 的相关子类型 [聊天频道][ChatChannel]。
 *
 * @see ChatGroup
 * @see ChatChannel
 *
 * @author ForteScarlet
 */
public interface ChatRoom : Actor, SendSupport {
    /**
     * 此聊天室的名称。
     */
    public val name: String

}


/**
 * 一个用于聊天的群聊。
 *
 * 群聊本身承载了聚集[组织成员][Member]的职责和作为聊天室向其他成员发送消息的职责。
 *
 */
public interface ChatGroup : ChatRoom, Organization {
    /**
     * 群聊的ID
     */
    override val id: ID

    /**
     * 群聊的名称
     */
    override val name: String
}

/**
 * 一个频道服务器。
 *
 * [Guild] 是对一组 [组织成员][Member] 和一组 [频道][Channel] 的统一。
 *
 */
public interface Guild : Organization {
    /**
     * 频道服务器的ID。
     */
    override val id: ID

    /**
     * 频道服务器的名称
     */
    override val name: String

    /**
     * 根据ID获取一个指定的频道。
     * 如果找不到则会得到 `null`。
     */
    @ST(blockingBaseName = "getChannel", blockingSuffix = "", asyncBaseName = "getChannel")
    public suspend fun channel(id: ID): Channel?

    /**
     * 获取此频道服务器内的所有频道集合。
     */
    public val channels: Collectable<Channel>

    /**
     * 根据ID获取一个指定的聊天频道。
     * 如果找不到则会得到 `null`。
     */
    @ST(blockingBaseName = "getChatChannel", blockingSuffix = "", asyncBaseName = "getChatChannel")
    public suspend fun chatChannel(id: ID): ChatChannel?

    /**
     * 获取此频道服务器内的所有聊天频道集合。
     *
     * [chatChannels] 的结果通常来讲是 [channels] 的子集。
     */
    public val chatChannels: Collectable<ChatChannel>

}

/**
 * 一个频道。
 *
 * [Channel] 是 [频道服务器][Guild] 中所有频道中的某一个频道。
 * 频道的类型可能有很多，其中就包含了允许发送消息的 [聊天频道][ChatChannel]。
 *
 */
public interface Channel : Actor {
    /**
     * 频道的ID
     */
    override val id: ID

    /**
     * 频道的名称
     */
    public val name: String

    /**
     * 此频道所属分类。
     *
     * 如果当前频道没有所属分类、无法获取或不支持分类这一概念，则可能得到 `null` 。
     */
    public val category: Category?
}

/**
 * 一个聊天频道。
 *
 * [ChatChannel] 是一个实现了 [ChatRoom] 的 [Channel]，
 * 代表这个频道可以用来向其他人发送消息。
 *
 */
public interface ChatChannel : Channel, ChatRoom
