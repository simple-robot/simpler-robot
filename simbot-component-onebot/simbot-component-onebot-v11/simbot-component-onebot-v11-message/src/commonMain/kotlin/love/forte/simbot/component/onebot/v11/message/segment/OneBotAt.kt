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
import love.forte.simbot.common.id.literal
import love.forte.simbot.message.At
import love.forte.simbot.message.AtAll
import kotlin.jvm.JvmStatic

/**
 * [@某人](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E6%9F%90%E4%BA%BA)
 *
 * 可用于发送，与直接使用 [At] 和 [AtAll] 没什么区别。
 * 不会被用于接收，接收时会直接解析为对应的 [At] 或 [AtAll]。
 *
 */
@Serializable
@SerialName(OneBotAt.TYPE)
public class OneBotAt private constructor(
    override val data: Data,
) : OneBotMessageSegment {
    /**
     * 判断是否为 at 全体
     */
    public val isAll: Boolean
        get() = data.qq == ALL_QQ

    public companion object Factory {
        public const val TYPE: String = "at"
        public const val ALL_QQ: String = "all"

        /**
         * 构建一个 [OneBotAt]。
         */
        @JvmStatic
        public fun create(target: String): OneBotAt =
            OneBotAt(Data(target))

        /**
         * 构建一个 [OneBotAt]。
         */
        @JvmStatic
        public fun create(target: ID): OneBotAt =
            create(target.literal)

        /**
         * 构建一个代表 at 全体的 [OneBotAt]。
         */
        @JvmStatic
        public fun createAtAll(): OneBotAt =
            OneBotAt(Data(ALL_QQ))
    }

    @Serializable
    public data class Data internal constructor(val qq: String)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneBotAt) return false

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String {
        return "OneBotAt(data=$data)"
    }
}
