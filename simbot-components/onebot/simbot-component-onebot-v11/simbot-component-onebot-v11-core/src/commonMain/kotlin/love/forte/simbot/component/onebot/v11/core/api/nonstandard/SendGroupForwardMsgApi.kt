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

package love.forte.simbot.component.onebot.v11.core.api.nonstandard

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.LongID
import love.forte.simbot.common.id.NumericalID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import love.forte.simbot.component.onebot.v11.core.api.OneBotApi
import love.forte.simbot.component.onebot.v11.core.api.OneBotApiResult
import love.forte.simbot.component.onebot.v11.message.segment.OneBotForwardNode
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import kotlin.jvm.JvmStatic

/**
 * [`send_group_forward_msg`-发送群合并转发消息 - 非标准接口](https://llonebot.apifox.cn/api-226189162)
 *
 * @since 1.9.0
 * @author Aliorpse
 */
@OneBotNonStandardApi
public class SendGroupForwardMsgApi private constructor(
    override val body: Any,
) : OneBotApi<SendGroupForwardMsgResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<SendGroupForwardMsgResult>
        get() = SendGroupForwardMsgResult.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<SendGroupForwardMsgResult>>
        get() = RES_SER

    public companion object Factory {
        private const val ACTION: String = "send_group_forward_msg"

        private val RES_SER = OneBotApiResult.Companion.serializer(SendGroupForwardMsgResult.serializer())

        /**
         * 构建一个 [SendGroupForwardMsgApi].
         *
         * @param groupId 群号
         * @param messages 要发送的内容
         */
        @JvmStatic
        public fun create(
            groupId: ID,
            messages: List<OneBotForwardNode>,
        ): SendGroupForwardMsgApi = SendGroupForwardMsgApi(
            Body(
                groupId.literal,
                messages
            )
        )
    }

    /**
     * @property groupId 群号
     * @property messages 要发送的内容
     */
    @Serializable
    internal data class Body(
        @SerialName("group_id")
        val groupId: String,
        /**
         * node 元素列表。使用 [OneBotMessageSegment] 为了确保序列化使用多态。
         */
        val messages: List<OneBotMessageSegment>,
    )
}

/**
 * [SendGroupForwardMsgApi] 的响应体。
 *
 * @property messageId 消息 ID
 * @property forwardId RES ID
 */
@Serializable
@OneBotNonStandardApi
public data class SendGroupForwardMsgResult @ApiResultConstructor constructor(
    @SerialName("message_id")
    private val longMessageId: LongID,
    @SerialName("forward_id")
    public val forwardId: ID
) {
    public val messageId: NumericalID
        get() = longMessageId
}
