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
import love.forte.simbot.message.MessageReference
import kotlin.jvm.JvmStatic


/**
 * [回复](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E5%9B%9E%E5%A4%8D)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(OneBotReply.TYPE)
public class OneBotReply private constructor(
    override val data: Data
) : OneBotMessageSegment, MessageReference {

    /**
     * The [Data.id].
     *
     * @see Data.id
     */
    override val id: ID
        get() = data.id

    public companion object Factory {
        public const val TYPE: String = "reply"

        /**
         * Create [OneBotReply].
         * @param id 回复时引用的消息 ID
         */
        @JvmStatic
        public fun create(id: ID): OneBotReply =
            OneBotReply(Data(id))
    }

    /**
     * The Data of [OneBotReply].
     *
     * @property id 回复时引用的消息 ID
     */
    @Serializable
    public data class Data(val id: ID)
}
