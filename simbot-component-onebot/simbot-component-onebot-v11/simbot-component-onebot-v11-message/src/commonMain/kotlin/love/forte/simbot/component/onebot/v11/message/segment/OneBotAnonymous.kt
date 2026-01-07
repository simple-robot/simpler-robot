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
 * [匿名发消息](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E5%8C%BF%E5%90%8D%E5%8F%91%E6%B6%88%E6%81%AF-)
 */
@Serializable
@SerialName(OneBotAnonymous.TYPE)
public class OneBotAnonymous private constructor(
    override val data: Data
) : OneBotMessageSegment {
    /**
     * Data of [OneBotAnonymous].
     *
     * @property ignore 可选，表示无法匿名时是否继续发送
     */
    @Serializable
    public data class Data(val ignore: Boolean? = null)

    public companion object Factory {
        public const val TYPE: String = "anonymous"

        private val NULL = OneBotAnonymous(Data(null))
        private val TRUE = OneBotAnonymous(Data(true))
        private val FALSE = OneBotAnonymous(Data(false))

        /**
         * 构建一个 [OneBotAnonymous].
         * @param ignore 可选，表示无法匿名时是否继续发送
         */
        @JvmStatic
        @JvmOverloads
        public fun create(ignore: Boolean? = null): OneBotAnonymous =
            when (ignore) {
                null -> NULL
                true -> TRUE
                false -> FALSE
            }
    }


    override fun toString(): String {
        return "OneBotAnonymous(ignore=${data.ignore})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is OneBotAnonymous) return false

        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }
}
