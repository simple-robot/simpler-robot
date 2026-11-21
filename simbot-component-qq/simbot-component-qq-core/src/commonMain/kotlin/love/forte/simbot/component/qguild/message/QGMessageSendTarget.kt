/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.message

import love.forte.simbot.common.id.ID

/**
 * QQ 消息发送或回复的目标描述。
 *
 * @since 5.0
 */
public sealed class QGMessageSendTarget {
    /**
     * 目标的标识。对于 [Member]，这是成员 ID。
     */
    public abstract val id: ID

    /**
     * 文字子频道目标。
     * @property id 子频道 ID。
     */
    public class Channel(override val id: ID) : QGMessageSendTarget() {
        override fun equals(other: Any?): Boolean = other is Channel && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "Channel(id=$id)"
    }

    /**
     * 已创建的频道私信会话目标。
     * @property id 私信会话 ID。
     */
    public class Dms(override val id: ID) : QGMessageSendTarget() {
        override fun equals(other: Any?): Boolean = other is Dms && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "Dms(id=$id)"
    }

    /**
     * QQ 群目标。
     * @property id 群 openid。
     */
    public class Group(override val id: ID) : QGMessageSendTarget() {
        override fun equals(other: Any?): Boolean = other is Group && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "Group(id=$id)"
    }

    /**
     * QQ 用户私信目标。
     * @property id 用户 openid。
     */
    public class User(override val id: ID) : QGMessageSendTarget() {
        override fun equals(other: Any?): Boolean = other is User && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "User(id=$id)"
    }

    /**
     * 通过频道成员创建私信会话的目标。
     * @property guildId 成员所属的频道 ID。
     * @property memberId 频道成员 ID。
     */
    public class Member(public val guildId: ID, public val memberId: ID) : QGMessageSendTarget() {
        override val id: ID get() = memberId

        override fun equals(other: Any?): Boolean =
            other is Member && guildId == other.guildId && memberId == other.memberId

        override fun hashCode(): Int = 31 * guildId.hashCode() + memberId.hashCode()

        override fun toString(): String = "Member(guildId=$guildId, memberId=$memberId)"
    }

    companion {
        /**
         * 创建文字子频道目标。
         * @since 5.0
         */
        public fun channel(id: ID): Channel = Channel(id)

        /**
         * 创建频道私信会话目标，ID 为已创建的会话 ID。
         * @since 5.0
         */
        public fun dms(id: ID): Dms = Dms(id)

        /**
         * 创建 QQ 群目标。
         * @since 5.0
         */
        public fun group(id: ID): Group = Group(id)

        /**
         * 创建 QQ 用户私信目标。
         * @since 5.0
         */
        public fun user(id: ID): User = User(id)

        /**
         * 按频道和成员 ID 描述私信目标。
         * @since 5.0
         */
        public fun member(guildId: ID, memberId: ID): Member = Member(guildId, memberId)
    }
}
