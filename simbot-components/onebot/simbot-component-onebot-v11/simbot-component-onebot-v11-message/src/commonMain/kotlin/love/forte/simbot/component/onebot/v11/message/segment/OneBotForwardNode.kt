/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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
import love.forte.simbot.component.onebot.common.annotations.ExperimentalOneBotAPI
import love.forte.simbot.component.onebot.v11.message.DefaultImageAdditionalParams
import love.forte.simbot.component.onebot.v11.message.resolveToOneBotSegmentList
import love.forte.simbot.message.Message
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * - [合并转发节点](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91%E8%8A%82%E7%82%B9-)
 * - [合并转发自定义节点](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#%E5%90%88%E5%B9%B6%E8%BD%AC%E5%8F%91%E8%87%AA%E5%AE%9A%E4%B9%89%E8%8A%82%E7%82%B9)
 * @author ForteScarlet
 */
@Serializable
@SerialName(OneBotForwardNode.TYPE)
public class OneBotForwardNode private constructor(
    override val data: Data
) : OneBotMessageSegment {

    override fun toString(): String {
        return "OneBotForwardNode(data=$data)"
    }

    public companion object Factory {
        public const val TYPE: String = "node"

        /**
         * Create [OneBotForwardNode].
         *
         */
        @JvmStatic
        public fun create(id: ID): OneBotForwardNode =
            OneBotForwardNode(Data(id))

        /**
         * Create a `custom` [OneBotForwardNode].
         *
         */
        @JvmStatic
        public fun create(
            userId: ID,
            nickname: String,
            content: List<OneBotMessageSegment>
        ): OneBotForwardNode =
            OneBotForwardNode(
                Data(
                    id = null,
                    userId = userId,
                    nickname = nickname,
                    content = content,
                )
            )

        /**
         * Create a `custom` [OneBotForwardNode].
         *
         * @since 1.9.0
         */
        @JvmStatic
        @JvmOverloads
        @ExperimentalOneBotAPI
        public fun create(
            userId: ID,
            nickname: String,
            content: Message,
            defaultImageAdditionalParams: DefaultImageAdditionalParams? = null
        ): OneBotForwardNode =
            create(
                userId = userId,
                nickname = nickname,
                content = content.resolveToOneBotSegmentList(defaultImageAdditionalParams)
            )
    }

    /**
     * 一个普通的节点或一个自定义节点.
     * 如果没有 [id]，则应当存在 [userId]、[nickname]、[content]。
     *
     * @author ForteScarlet
     */
    @Serializable
    public data class Data(
        val id: ID? = null,
        @SerialName("user_id")
        val userId: ID? = null,
        val nickname: String? = null,
        val content: List<OneBotMessageSegment>? = null,
    )
}
