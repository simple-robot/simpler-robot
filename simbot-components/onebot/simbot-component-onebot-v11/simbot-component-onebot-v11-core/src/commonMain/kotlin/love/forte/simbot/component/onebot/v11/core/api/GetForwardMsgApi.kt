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

package love.forte.simbot.component.onebot.v11.core.api

import kotlinx.serialization.*
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.onebot.common.annotations.ApiResultConstructor
import love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI
import love.forte.simbot.component.onebot.v11.message.segment.OneBotForwardNode
import kotlin.jvm.JvmStatic

/**
 * [`get_forward_msg`-获取合并转发消息](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_forward_msg-获取合并转发消息)
 *
 * @author ForteScarlet
 */
public class GetForwardMsgApi private constructor(
    override val body: Any,
) : OneBotApi<GetForwardMsgResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<GetForwardMsgResult>
        get() = GetForwardMsgResult.serializer()

    override val apiResultDeserializer:
        DeserializationStrategy<OneBotApiResult<GetForwardMsgResult>>
        get() = RES_SER

    public companion object Factory {
        private const val ACTION: String = "get_forward_msg"

        private val RES_SER: KSerializer<OneBotApiResult<GetForwardMsgResult>> =
            OneBotApiResult.serializer(GetForwardMsgResult.serializer())

        /**
         * 构建一个 [GetForwardMsgApi].
         *
         * @param id 合并转发 ID
         */
        @JvmStatic
        public fun create(id: ID): GetForwardMsgApi = GetForwardMsgApi(Body(id))
    }

    /**
     * @property id 合并转发 ID
     */
    @Serializable
    internal data class Body(
        internal val id: ID,
    )
}

/**
 * [GetForwardMsgApi] 的响应体。
 *
 * @property message 消息内容，使用 [消息的数组格式](https://github.com/botuniverse/onebot-11/blob/master/message/array.md) 表示，
 * 数组中的消息段全部为 [`node` 消息段](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md#合并转发自定义节点)
 */
@Serializable
public class GetForwardMsgResult @ApiResultConstructor constructor(
    // 部分协议实现中会将此字段命名为 `messages` 而不是
    // https://github.com/botuniverse/onebot-11/blob/master/api/public.md#get_forward_msg-获取合并转发消息
    // 中约定的 `message`，此处提供两个属性做兼容。
    // 不直接使用 @JsonNames 是因为它是实验性特性，
    // 且需要由 Json.useAlternativeNames 配置启用而不是取决于此实体类型，
    // 而且似乎启用会一定程度的影响性能。

    /**
     * @suppress internal
     */
    @SerialName("message")
    @InternalOneBotAPI
    internal val messageInternal: List<OneBotForwardNode>? = null,
    /**
     * @suppress internal
     */
    @SerialName("messages")
    @InternalOneBotAPI
    internal val messagesInternal: List<OneBotForwardNode>? = null,
) {
    @Transient
    public val message: List<OneBotForwardNode> =
        messageInternal ?: messagesInternal ?: emptyList()

    public operator fun component1(): List<OneBotForwardNode> = message

    override fun toString(): String =
        "GetForwardMsgResult(message=$message)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GetForwardMsgResult) return false

        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }
}
