/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.kook.api.message

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.KookPostApi
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic

/**
 * [发送频道聊天消息](https://developer.kookapp.cn/doc/http/message#%E5%8F%91%E9%80%81%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * > **接口说明**
 * > 注意：强烈建议过滤掉机器人发送的消息，再进行回应。
 * > 否则会很容易形成两个机器人循环自言自语导致发送量过大，进而导致机器人被封禁。如果确实需要机器人联动的情况，慎重进行处理，防止形成循环。
 * > 若发送的消息为诸如图片一类的资源，消息内容必须由机器人创建，否则会提示: "找不到资源"。
 *
 * @author ForteScarlet
 */
public class SendChannelMessageApi private constructor(
    private val type: Int? = null,
    private val targetId: String,
    private val content: String,
    private val quote: String?,
    private val nonce: String?,
    private val tempTargetId: String?,
    /**
     * 模板消息id, 如果使用了，content会作为模板消息的input。
     * 参见[模板消息](https://developer.kookapp.cn/doc/http/template)
     * @since 4.2.0
     */
    private val templateId: String?
) : KookPostApi<SendMessageResult>() {
    public companion object Factory {
        private val PATH = ApiPath.create("message", "create")

        /**
         * 构造 [发送频道聊天消息][SendChannelMessageApi] 实例。
         *
         * @param type 消息类型, 不传默认为 1, 代表文本类型。 `9` 代表 kmarkdown 消息, `10` 代表卡片消息。
         * @param targetId 目标频道 id
         * @param content 消息内容
         * @param quote 回复某条消息的 msgId
         * @param nonce nonce, 服务端不做处理, 原样返回
         * @param tempTargetId 用户 id,如果传了，代表该消息是临时消息，该消息不会存数据库，但是会在频道内只给该用户推送临时消息。用于在频道内针对用户的操作进行单独的回应通知等。
         */
        @JvmStatic
        public fun create(
            type: Int? = null,
            targetId: String,
            content: String,
            quote: String? = null,
            nonce: String? = null,
            tempTargetId: String? = null,
        ): SendChannelMessageApi = create(
            type = type,
            targetId = targetId,
            content = content,
            quote = quote,
            nonce = nonce,
            tempTargetId = tempTargetId,
            templateId = null
        )

        /**
         * 构造 [发送频道聊天消息][SendChannelMessageApi] 实例。
         *
         * @since 4.2.0
         *
         * @param type 消息类型, 不传默认为 1, 代表文本类型。 `9` 代表 kmarkdown 消息, `10` 代表卡片消息。
         * @param targetId 目标频道 id
         * @param content 消息内容
         * @param quote 回复某条消息的 msgId
         * @param nonce nonce, 服务端不做处理, 原样返回
         * @param tempTargetId 用户 id,如果传了，代表该消息是临时消息，该消息不会存数据库，但是会在频道内只给该用户推送临时消息。
         * 用于在频道内针对用户的操作进行单独的回应通知等。
         * @param templateId 模板 id, 如果使用了，content会作为模板消息的input。
         */
        @JvmStatic
        public fun create(
            type: Int? = null,
            targetId: String,
            content: String,
            quote: String? = null,
            nonce: String? = null,
            tempTargetId: String? = null,
            templateId: String? = null,
        ): SendChannelMessageApi = SendChannelMessageApi(
            type = type,
            targetId = targetId,
            content = content,
            quote = quote,
            nonce = nonce,
            tempTargetId = tempTargetId,
            templateId = templateId
        )

        /**
         * 构造 [发送频道聊天消息][SendChannelMessageApi] 实例。
         * @param type 消息类型, 不传默认为 1, 代表文本类型。 `9` 代表 kmarkdown 消息, `10` 代表卡片消息。
         * @param targetId 目标频道 id
         * @param content 消息内容
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            type: Int? = null,
            targetId: String,
            content: String,
        ): SendChannelMessageApi = SendChannelMessageApi(
            type = type,
            targetId = targetId,
            content = content,
            quote = null,
            nonce = null,
            tempTargetId = null,
            templateId = null
        )

        /**
         * 构建 [发送频道聊天消息][SendChannelMessageApi] 构建器。
         *
         * @param targetId 目标频道 id
         * @param content 消息内容
         */
        @JvmStatic
        public fun builder(targetId: String, content: String): Builder = Builder().also {
            it.targetId = targetId
            it.content = content
        }

        /**
         * 构建 [发送频道聊天消息][SendChannelMessageApi] 构建器。
         */
        @JvmStatic
        public fun builder(): Builder = Builder()

        /**
         * 构建 [发送频道聊天消息][SendChannelMessageApi] 实例。
         */
        @JvmSynthetic
        public inline fun create(block: Builder.() -> Unit): SendChannelMessageApi = builder().apply(block).build()
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<SendMessageResult>
        get() = SendMessageResult.serializer()

    /**
     * [发送频道聊天消息][SendChannelMessageApi] 构建器。
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public class Builder @PublishedApi internal constructor() {
        /**
         * 消息类型
         */
        public var type: Int? = null

        /**
         * 目标频道 id
         */
        public var targetId: String? = null

        /**
         * 消息内容
         */
        public var content: String? = null

        /**
         * 回复某条消息的 msgId
         */
        public var quote: String? = null

        /**
         * nonce, 服务端不做处理, 原样返回
         */
        public var nonce: String? = null

        /**
         * 用户 id,如果传了，代表该消息是临时消息，该消息不会存数据库，但是会在频道内只给该用户推送临时消息。
         * 用于在频道内针对用户的操作进行单独的回应通知等。
         */
        public var tempTargetId: String? = null

        /**
         * 模板消息id, 如果使用了，content会作为模板消息的input。
         * 参见[模板消息](https://developer.kookapp.cn/doc/http/template)
         * @since 4.2.0
         */
        public var templateId: String? = null

        /**
         * 消息类型
         */
        public fun type(type: Int?): Builder = apply { this.type = type }

        /**
         * 消息类型
         */
        public fun type(type: SendMessageType?): Builder = apply { this.type = type?.value }

        /**
         * 目标频道 id
         */
        public fun targetId(targetId: String): Builder = apply { this.targetId = targetId }

        /**
         * 消息内容
         */
        public fun content(content: String): Builder = apply { this.content = content }

        /**
         * 回复某条消息的 msgId
         */
        public fun quote(quote: String?): Builder = apply { this.quote = quote }

        /**
         * nonce, 服务端不做处理, 原样返回
         */
        public fun nonce(nonce: String?): Builder = apply { this.nonce = nonce }

        /**
         * 用户 id,如果传了，代表该消息是临时消息，该消息不会存数据库，但是会在频道内只给该用户推送临时消息。
         * 用于在频道内针对用户的操作进行单独的回应通知等。
         */
        public fun tempTargetId(tempTargetId: String?): Builder = apply { this.tempTargetId = tempTargetId }

        /**
         * 模板消息ID。
         * @see templateId
         */
        public fun templateId(templateId: String?): Builder = apply { this.templateId = templateId }

        /**
         * 构建一个 [发送频道聊天消息][SendChannelMessageApi] 实例。
         */
        public fun build(): SendChannelMessageApi = SendChannelMessageApi(
            type = type,
            targetId = targetId ?: throw IllegalArgumentException("'targetId' must not be null"),
            content = content ?: throw IllegalArgumentException("'content' must not be null"),
            quote = quote,
            nonce = nonce,
            tempTargetId = tempTargetId,
            templateId = templateId,
        )
    }

    override fun createBody(): Any = Body(
        type = type,
        targetId = targetId,
        content = content,
        quote = quote,
        nonce = nonce,
        tempTargetId = tempTargetId,
        templateId = templateId,
    )

    @Serializable
    private data class Body(
        @SerialName("type") val type: Int? = null,
        @SerialName("target_id") val targetId: String,
        @SerialName("content") val content: String,
        @SerialName("quote") val quote: String? = null,
        @SerialName("nonce") val nonce: String? = null,
        @SerialName("temp_target_id") val tempTargetId: String? = null,
        @SerialName("template_id") val templateId: String? = null,
    )

}


