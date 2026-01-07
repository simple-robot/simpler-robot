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

package love.forte.simbot.component.onebot.v11.message.segment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import love.forte.simbot.message.Face
import kotlin.jvm.JvmStatic

/**
 * [QQ 表情](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#qq-%E8%A1%A8%E6%83%85)
 *
 * 可用于发送，与直接使用 [Face] 无区别。
 * 接收时不会使用，而是始终解析为 [Face]。
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(OneBotFace.TYPE)
public class OneBotFace private constructor(override val data: Data) :
    OneBotMessageSegment {
    public val id: ID
        get() = data.id

    public companion object Factory {
        public const val TYPE: String = "face"

        @JvmStatic
        public fun create(id: ID): OneBotFace =
            OneBotFace(Data(id))
    }

    @Serializable
    public data class Data(val id: ID)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneBotFace) return false

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String {
        return "OneBotFace(id=$id)"
    }
}
