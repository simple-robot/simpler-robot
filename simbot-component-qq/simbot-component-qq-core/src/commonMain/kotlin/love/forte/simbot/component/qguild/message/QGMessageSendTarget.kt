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
import love.forte.simbot.qguild.stdlib.MessageDestination

/**
 * QQ 消息发送或回复的目标描述。
 *
 * @property destination 区分子频道、频道私聊会话、群和用户。
 * @property id 对应目标的频道 ID、私聊会话 ID 或 openid。
 */
public class QGMessageSendTarget private constructor(
    public val destination: MessageDestination,
    public val id: ID,
    /**
     * 按频道成员发送私信时所属的频道 ID，此时 [id] 为成员 ID。
     */
    public val guildId: ID? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QGMessageSendTarget) return false

        if (destination != other.destination) return false
        if (id != other.id) return false
        if (guildId != other.guildId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = destination.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + guildId.hashCode()
        return result
    }

    override fun toString(): String {
        return "QGMessageSendTarget(destination=$destination, id=$id, guildId=$guildId)"
    }

    companion {
        /**
         * 创建一个消息发送目标。
         */
        public fun create(destination: MessageDestination, id: ID): QGMessageSendTarget =
            QGMessageSendTarget(destination, id)

        /**
         * 按频道和成员 ID 描述私信目标。
         */
        public fun member(guildId: ID, memberId: ID): QGMessageSendTarget =
            QGMessageSendTarget(MessageDestination.DMS, memberId, guildId)
    }
}
