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
 * - [推荐好友](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E6%8E%A8%E8%8D%90%E5%A5%BD%E5%8F%8B)
 * - [推荐群](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E6%8E%A8%E8%8D%90%E7%BE%A4)
 * @author ForteScarlet
 */
@Serializable
@SerialName(OneBotContact.TYPE)
public class OneBotContact private constructor(
    override val data: Data
) : OneBotMessageSegment {

    /**
     * @see Data.type
     */
    public val contactType: String
        get() = data.type

    /**
     * @see Data.id
     */
    public val contactId: ID
        get() = data.id

    public companion object Factory {
        public const val TYPE: String = "contact"

        /**
         * Create [OneBotContact].
         */
        @JvmStatic
        public fun create(type: String, id: ID): OneBotContact =
            OneBotContact(Data(type, id))
    }

    /**
     * The data of [OneBotContact].
     *
     * @property type 可能的值:`qq` (推荐好友) / `group` (推荐群)
     * @property id 被推荐人的 QQ 号 / 被推荐的群号
     */
    @Serializable
    public data class Data(
        val type: String,
        val id: ID,
    )
}
