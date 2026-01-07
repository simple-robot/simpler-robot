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
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [链接分享](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E9%93%BE%E6%8E%A5%E5%88%86%E4%BA%AB)
 */
@Serializable
@SerialName(OneBotShare.TYPE)
public class OneBotShare private constructor(
    override val data: Data
) : OneBotMessageSegment {

    /**
     * Data of [OneBotShare].
     */
    @Serializable
    public data class Data(
        val url: String,
        val title: String,
        val content: String? = null,
        val image: String? = null
    )

    public companion object Factory {
        public const val TYPE: String = "share"

        /**
         * 构建一个 [OneBotShare].
         */
        @JvmStatic
        public fun create(data: Data): OneBotShare =
            OneBotShare(data)

        /**
         * 构建一个 [OneBotShare].
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            url: String,
            title: String,
            content: String? = null,
            image: String? = null
        ): OneBotShare = create(Data(url, title, content, image))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneBotShare) return false

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    override fun toString(): String {
        return "OneBotShare(data=$data)"
    }

}
