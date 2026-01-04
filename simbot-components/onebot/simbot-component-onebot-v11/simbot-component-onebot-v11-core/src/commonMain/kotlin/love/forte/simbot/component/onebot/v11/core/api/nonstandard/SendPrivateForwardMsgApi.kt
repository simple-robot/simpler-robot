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
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.onebot.v11.core.api.OneBotApi
import love.forte.simbot.component.onebot.v11.core.api.OneBotApiResult
import love.forte.simbot.component.onebot.v11.core.api.SendMsgResult
import love.forte.simbot.component.onebot.v11.message.segment.OneBotForwardNode
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment
import kotlin.jvm.JvmStatic

/**
 * [`send_private_forward_msg`-发送私聊合并转发消息 - 非标准接口](https://llonebot.apifox.cn/api-226189040)
 *
 * @since 1.9.0
 * @author Aliorpse
 */
@OneBotNonStandardApi
public class SendPrivateForwardMsgApi private constructor(
    override val body: Any
) : OneBotApi<SendMsgResult> {

    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<SendMsgResult>
        get() = SendMsgResult.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<SendMsgResult>>
        get() = RES_SER

    public companion object Factory {
        private const val ACTION = "send_private_forward_msg"
        private val RES_SER = OneBotApiResult.Companion.serializer(SendMsgResult.serializer())

        /**
         * 构建一个 [SendPrivateForwardMsgApi].
         * @param userId 对方 QQ 号
         * @param messages 要发送的内容
         */
        @JvmStatic
        public fun create(
            userId: ID,
            messages: List<OneBotForwardNode>,
        ): SendPrivateForwardMsgApi = SendPrivateForwardMsgApi(
            Body(
                userId.literal,
                messages
            )
        )
    }

    /**
     * @property userId 对方 QQ 号
     * @property messages 要发送的内容
     */
    @Serializable
    internal data class Body(
        @SerialName("user_id")
        val userId: String,
        /**
         * node 元素列表。使用 [OneBotMessageSegment] 为了确保序列化使用多态。
         */
        val messages: List<OneBotMessageSegment>,
    )
}
