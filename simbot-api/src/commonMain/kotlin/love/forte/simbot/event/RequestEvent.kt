/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.event

import love.forte.simbot.ability.AcceptSupport
import love.forte.simbot.ability.RejectSupport
import love.forte.simbot.bot.Bot
import love.forte.simbot.common.id.ID
import love.forte.simbot.definition.ChatGroup
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.Organization
import love.forte.simbot.definition.User
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP


/**
 * [Bot] 收到的某种与请求/申请有关的事件。
 *
 * @author ForteScarlet
 */
public interface RequestEvent : BotEvent, RejectSupport, AcceptSupport {
    /**
     * 伴随请求的附加消息。
     *
     * 在没有、不支持的情况下可能为 `null`。
     */
    public val message: String?

    /**
     * 此申请的主、被动类型。
     * 如果无法界定，则给一个默认值，并提供相关的说明。
     */
    public val type: Type

    /**
     * [RequestEvent] 的主、被动类型。
     *
     */
    public enum class Type {
        /**
         * 主动的。例如是主动发起的申请。
         */
        PROACTIVE,

        /**
         * 被动的。例如是被邀请的。
         */
        PASSIVE
    }

    /**
     * 拒绝此请求。
     *
     * @throws Exception 任何可能产生的错误。
     */
    @ST
    override suspend fun reject()

    /**
     * 接受此请求。
     *
     * @throws Exception 任何可能产生的错误。
     */
    @ST
    override suspend fun accept()
}


/**
 * [Bot] 收到的某种与 [Organization] 相关的请求/申请有关的事件。
 *
 * 通常情况下，如果 [Bot] 想收到某种与 [Organization] 相关的请求事件，
 * 需要在此组织内拥有一定的权限。
 *
 * @author ForteScarlet
 */
public interface OrganizationRequestEvent : RequestEvent, OrganizationEvent

/**
 * 某个用户想要加入目标 [Organization] 的请求事件。
 *
 * @author ForteScarlet
 */
public interface OrganizationJoinRequestEvent : OrganizationEvent {
    /**
     * 申请者的 ID。
     */
    public val requesterId: ID

    /**
     * 尝试获取申请者的一些基础信息。
     * 如果无法获取或不支持，则可能得到 `null`。
     */
    @STP
    public suspend fun requester(): User?
}


/**
 * 某用户申请加入 [ChatGroup] 的事件。
 *
 * @author ForteScarlet
 *
 */
@STP
public interface ChatGroupJoinRequestEvent : OrganizationJoinRequestEvent, ChatGroupEvent {
    /**
     * 被申请加入的 [ChatGroup]。
     */
    override suspend fun content(): ChatGroup
}

/**
 * 某用户申请加入 [Guild] 的事件。
 *
 * @author ForteScarlet
 *
 */
@STP
public interface GuildJoinRequestEvent : OrganizationJoinRequestEvent, GuildEvent {
    /**
     * 被申请加入的 [Guild]。
     */
    override suspend fun content(): Guild
}
