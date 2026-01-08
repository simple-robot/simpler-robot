/*
 * Copyright (c) 2022-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api.message.direct

import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.api.message.MessageSendApi
import love.forte.simbot.qguild.api.message.toRealBody
import love.forte.simbot.qguild.model.Message
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * [发送私信](https://bot.q.qq.com/wiki/develop/api/openapi/dms/post_dms_messages.html)
 * ## 接口
 * `POST /dms/{guild_id}/messages`
 *
 * ## 功能描述
 * 用于发送私信消息，前提是已经创建了私信会话。
 *
 * * 私信的 `guild_id` 在创建私信会话时以及私信消息事件中获取。
 * * 私信场景下，每个机器人每天可以对一个用户发 `2` 条主动消息。
 * * 私信场景下，每个机器人每天累计可以发 `200` 条主动消息。
 * * 私信场景下，被动消息没有条数限制。
 *
 * ## 参数
 * 和 [发送消息][MessageSendApi] 参数一致。
 *
 * ## 返回
 * 和 [发送消息][MessageSendApi] 返回一致。
 *
 *
 * @see MessageSendApi
 *
 * @author ForteScarlet
 */
public class DmsSendApi private constructor(
    guildId: String,
    private val _body: MessageSendApi.Body, // TencentMessageForSending || MultiPartFormDataContent
) : PostQQGuildApi<Message>() {
    public companion object Factory : SimplePostApiDescription(
        "/channels/{channel_id}/messages"
    ) {

        /**
         * 构造 [CreateDmsApi]
         */
        @JvmStatic
        public fun create(guildId: String, body: MessageSendApi.Body): DmsSendApi = DmsSendApi(guildId, body)
    }

    override fun createBody(): Any = _body.toRealBody(MessageSendApi.defaultJson)

    override val path: Array<String> = arrayOf("dms", guildId, "messages")

    override val resultDeserializationStrategy: DeserializationStrategy<Message>
        get() = Message.serializer()

    override val headers: Headers
        get() = if (body is MultiPartFormDataContent) MessageSendApi.FormDataHeader else Headers.Empty
}

/**
 * 构造 [CreateDmsApi]
 *
 * ```kotlin
 * val api = DmsSendApi.create(guildId) {
 *    // body builder
 * }
 * ```
 */
@JvmSynthetic
public inline fun DmsSendApi.Factory.create(
    guildId: String,
    builder: MessageSendApi.Body.Builder.() -> Unit
): DmsSendApi =
    create(guildId, MessageSendApi.Body.invoke(builder))
