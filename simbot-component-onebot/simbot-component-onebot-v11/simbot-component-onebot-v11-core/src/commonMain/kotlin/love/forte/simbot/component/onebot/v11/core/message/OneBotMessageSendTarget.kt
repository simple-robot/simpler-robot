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

package love.forte.simbot.component.onebot.v11.core.message

import love.forte.simbot.common.id.ID

/**
 * OneBot 消息的目标类型和 ID，读取时不查询平台。
 *
 * @since 5.0
 */
public sealed interface OneBotMessageSendTarget {
    /**
     * 目标会话的 ID。
     */
    public val id: ID

    /**
     * 群聊目标。
     */
    public class Group(override val id: ID) : OneBotMessageSendTarget {
        override fun equals(other: Any?): Boolean = this === other || other is Group && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "Group(id=$id)"
    }

    /**
     * 私聊目标。
     */
    public class Private(override val id: ID) : OneBotMessageSendTarget {
        override fun equals(other: Any?): Boolean = this === other || other is Private && id == other.id

        override fun hashCode(): Int = id.hashCode()

        override fun toString(): String = "Private(id=$id)"
    }

    public companion object {
        /**
         * 创建群聊目标。
         */
        public fun group(id: ID): Group = Group(id)

        /**
         * 创建私聊目标。
         */
        public fun privateTarget(id: ID): Private = Private(id)
    }
}
