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
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [戳一戳](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E6%88%B3%E4%B8%80%E6%88%B3)
 *
 */
@Serializable
@SerialName(OneBotPoke.TYPE)
public class OneBotPoke private constructor(override val data: Data) : OneBotMessageSegment {
    /**
     * @see Data.type
     */
    public val type: String
        get() = data.type

    /**
     * @see Data.id
     */
    public val id: ID
        get() = data.id.ID

    /**
     * @see Data.name
     */
    public val name: String?
        get() = data.name

    public companion object Factory {
        public const val TYPE: String = "poke"

        /**
         * 构建 [OneBotPoke].
         */
        @JvmStatic
        public fun create(data: Data): OneBotPoke =
            OneBotPoke(data)

        /**
         * 构建 [OneBotPoke].
         */
        @JvmStatic
        @JvmOverloads
        public fun create(type: String, id: ID, name: String? = null): OneBotPoke =
            create(Data(type, id.literal, name))
    }

    /**
     * [OneBotPoke] 的属性。
     */
    @Serializable
    public data class Data internal constructor(
        val type: String,
        val id: String,
        val name: String? = null
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneBotPoke) return false

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String {
        return "OneBotPoke(data=$data)"
    }
}
