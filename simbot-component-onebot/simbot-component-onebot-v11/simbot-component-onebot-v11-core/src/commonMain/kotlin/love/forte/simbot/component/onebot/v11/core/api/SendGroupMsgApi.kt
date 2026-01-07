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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.common.id.ID
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * [`send_group_msg`-发送群消息](https://github.com/botuniverse/onebot-11/blob/master/api/public.md#send_group_msg-发送群消息)
 *
 * @author ForteScarlet
 */
public class SendGroupMsgApi private constructor(
    override val body: Any,
) : OneBotApi<SendMsgResult> {
    override val action: String
        get() = ACTION

    override val resultDeserializer: DeserializationStrategy<SendMsgResult>
        get() = SendMsgResult.serializer()

    override val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<SendMsgResult>>
        get() = RES_SER

    public companion object Factory {
        private const val ACTION: String = "send_group_msg"

        private val RES_SER: KSerializer<OneBotApiResult<SendMsgResult>> =
            OneBotApiResult.serializer(SendMsgResult.serializer())

        /**
         * 构建一个 [SendGroupMsgApi].
         *
         * @param groupId 群号
         * @param message 要发送的内容
         * @param autoEscape 消息内容是否作为纯文本发送（即不解析 CQ 码），只在 `message` 字段是字符串时有效
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            groupId: ID,
            message: OneBotMessageOutgoing,
            autoEscape: Boolean? = null,
        ): SendGroupMsgApi = SendGroupMsgApi(Body(groupId, message, autoEscape))
    }

    /**
     * @property groupId 群号
     * @property message 要发送的内容
     * @property autoEscape 消息内容是否作为纯文本发送（即不解析 CQ 码），只在 `message` 字段是字符串时有效
     */
    @Serializable
    internal data class Body(
        @SerialName("group_id")
        internal val groupId: ID,
        internal val message: OneBotMessageOutgoing,
        @SerialName("auto_escape")
        internal val autoEscape: Boolean? = null,
    )
}

// /**
//  * [SendGroupMsgApi] 的响应体。
//  *
//  * @property messageId 消息 ID
//  */
// @Serializable
// public data class SendGroupMsgResult @ApiResultConstructor constructor(
//     @SerialName("message_id")
//     public val messageId: IntID,
// )
