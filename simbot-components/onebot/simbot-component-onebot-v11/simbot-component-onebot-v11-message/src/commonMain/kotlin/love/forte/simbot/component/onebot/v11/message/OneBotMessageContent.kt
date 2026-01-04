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

package love.forte.simbot.component.onebot.v11.message

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.onebot.v11.message.segment.*
import love.forte.simbot.message.MessageContent
import love.forte.simbot.message.Messages
import love.forte.simbot.message.PlainText
import love.forte.simbot.suspendrunner.STP
import kotlin.jvm.JvmSynthetic


/**
 * 事件中接收到的消息内容。
 *
 * @author ForteScarlet
 */
public interface OneBotMessageContent : MessageContent {
    /**
     * 消息的ID
     */
    override val id: ID

    /**
     * 接收到的消息事件中的原始消息元素列表。
     */
    public val sourceSegments: List<OneBotMessageSegment>

    /**
     * 接收到的所有消息元素。
     * OneBot中，接收到的元素大概率都会是 [OneBotMessageElement]，
     * 其中大部分内容大概率都是 [OneBotMessageSegmentElement]。
     *
     * 由 [sourceSegments] 解析而来。
     *
     * @see OneBotMessageSegmentElement
     */
    override val messages: Messages

    /**
     * 寻找 [messages] 中第一个 [OneBotReply] 类型的元素，
     * 如果没有则得到 `null`。
     * 寻找的过程是即时的，不会发生挂起。
     *
     * @since 1.2.0
     * @see messages
     * @see OneBotReply
     */
    @STP
    override suspend fun reference(): OneBotReply? =
        messages
            .filterIsInstance<OneBotMessageElement>()
            .firstNotNullOfOrNull {
                it.oneBotSegmentOrNull<OneBotReply>()
            }

    /**
     * 根据 [消息引用][reference] 信息通过API查询对应引用的消息内容。
     *
     * @throws RuntimeException 任何可能在请求API过程中产生的异常
     *
     * @since 1.3.0
     */
    @STP
    override suspend fun referenceMessage(): OneBotMessageContent?

    /**
     * 消息中所有的 [文本消息][PlainText]
     * (或者说 [sourceSegments] 中所有的 [OneBotText])
     * 的合并结果。
     *
     * 如果没有任何文本消息类型的消息段，则会得到 `null`。
     */
    override val plainText: String?

    /**
     * 删除此消息。
     *
     * 支持的操作：
     * - [StandardDeleteOption.IGNORE_ON_FAILURE] 忽略产生的异常
     *
     * @throws Exception 任何请求API过程中可能会产生的异常，
     * 例如因权限不足或消息不存在得到的请求错误
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption)
}

// 实现在core模块里，因为要用到API
