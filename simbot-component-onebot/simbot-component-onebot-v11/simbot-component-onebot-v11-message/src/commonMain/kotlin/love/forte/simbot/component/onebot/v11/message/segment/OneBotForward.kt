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
import kotlin.jvm.JvmStatic


/**
 * [合并转发](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91-)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(OneBotForward.TYPE)
public class OneBotForward private constructor(
    override val data: Data
) : OneBotMessageSegment {

    /**
     * Get the [Data.id].
     */
    public val id: ID
        get() = data.id

    public companion object Factory {
        public const val TYPE: String = "forward"

        /**
         * Create [OneBotForward].
         *
         * @param id 合并转发 ID, see [Data.id]
         */
        @JvmStatic
        public fun create(id: ID): OneBotForward =
            OneBotForward(Data(id))
    }

    /**
     * Data of [OneBotForward]
     *
     * @property id 合并转发 ID，需通过
     * [get_forward_msg API](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_forward_msg-%E8%8E%B7%E5%8F%96%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91%E6%B6%88%E6%81%AF)
     * 获取具体内容
     */
    @Serializable
    public data class Data(val id: ID)
}
