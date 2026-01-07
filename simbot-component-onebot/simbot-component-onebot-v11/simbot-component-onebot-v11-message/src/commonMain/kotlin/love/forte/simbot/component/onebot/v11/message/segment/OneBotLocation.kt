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
 * [位置](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E4%BD%8D%E7%BD%AE)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(OneBotLocation.TYPE)
public class OneBotLocation private constructor(
    override val data: Data
) : OneBotMessageSegment {

    /**
     * @see Data.lat
     */
    public val lat: String
        get() = data.lat

    /**
     * @see Data.lon
     */
    public val lon: String
        get() = data.lon

    public companion object Factory {
        public const val TYPE: String = "location"

        /**
         * Create [OneBotLocation].
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            lat: String,
            lon: String,
            title: String? = null,
            content: String? = null
        ): OneBotLocation =
            OneBotLocation(Data(lat, lon, title, content))
    }

    /**
     * Data of [OneBotLocation].
     *
     * @property lat 纬度
     * @property lon 经度
     * @property title 发送时可选，标题
     * @property content 发送时可选，内容描述
     */
    @Serializable
    public data class Data(
        val lat: String,
        val lon: String,
        val title: String? = null,
        val content: String? = null,
    )
}
