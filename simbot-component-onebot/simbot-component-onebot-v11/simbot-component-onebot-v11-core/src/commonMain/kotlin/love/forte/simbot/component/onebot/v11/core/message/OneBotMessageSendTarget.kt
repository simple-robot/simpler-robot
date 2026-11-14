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
public class OneBotMessageSendTarget private constructor(public val type: Type, public val id: ID) {
    companion {
        /**
         * 根据目标类型和 ID 创建目标描述。
         * @since 5.0
         */
        public fun create(type: Type, id: ID): OneBotMessageSendTarget = OneBotMessageSendTarget(type, id)
    }

    override fun equals(other: Any?): Boolean =
        this === other || other is OneBotMessageSendTarget && type == other.type && id == other.id

    override fun hashCode(): Int = 31 * type.hashCode() + id.hashCode()

    override fun toString(): String = "OneBotMessageSendTarget(type=$type, id=$id)"

    /**
     * 群聊或私聊。
     * @since 5.0
     */
    public enum class Type { GROUP, PRIVATE }
}
