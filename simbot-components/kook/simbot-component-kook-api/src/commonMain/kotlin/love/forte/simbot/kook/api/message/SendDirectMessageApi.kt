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
 * [发送私信聊天消息](https://developer.kookapp.cn/doc/http/direct-message#%E5%8F%91%E9%80%81%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
public class SendDirectMessageApi private constructor(
    /**
     * 消息类型, 不传默认为 1, 代表文本类型。9 代表 kmarkdown 消息, 10 代表卡片消息。
     */
    private val type: Int? = null,
    /**
     * 目标用户 id，后端会自动创建会话。有此参数之后可不传chat_code参数
     */
    private val targetId: String? = null,
    /**
     * 私信会话 Code。chat_code与target_id必须传一个
     */
    private val chatCode: String? = null,
    private val content: String,
    private val quote: String? = null,
    private val nonce: String? = null,
) : KookPostApi<SendMessageResult>() {
    public companion object Factory {
        private val PATH = ApiPath.create("direct-message", "create")

        /**
         * 构造一个 [SendDirectMessageApi] 请求。
         *
         * @param targetId 目标用户 `id`，后端会自动创建会话
         * @param content 消息内容
         * @param type 消息类型, 不传默认为 `1`, 代表文本类型。`9` 代表 kmarkdown 消息, `10` 代表卡片消息。
         * @param quote 回复某条消息的 `msgId`
         * @param nonce 服务端不做处理, 原样返回
         */
        @JvmStatic
        public fun createByTargetId(
            targetId: String,
            content: String,
            type: Int? = null,
            quote: String? = null,
            nonce: String? = null,
        ): SendDirectMessageApi =
            SendDirectMessageApi(
                type = type,
                targetId = targetId,
                chatCode = null,
                content = content,
                quote = quote,
                nonce = nonce
            )

        /**
         * 构造一个 [SendDirectMessageApi] 请求。
         *
         * @param chatCode 私信会话 Code
         * @param content 消息内容
         * @param type 消息类型, 不传默认为 `1`, 代表文本类型。`9` 代表 kmarkdown 消息, `10` 代表卡片消息。
         * @param quote 回复某条消息的 `msgId`
         * @param nonce 服务端不做处理, 原样返回
         */
        @JvmStatic
        public fun createByChatCode(
            chatCode: String,
            content: String,
            type: Int? = null,
            quote: String? = null,
            nonce: String? = null,
        ): SendDirectMessageApi =
            SendDirectMessageApi(
                type = type,
                chatCode = chatCode,
                targetId = null,
                content = content,
                quote = quote,
                nonce = nonce
            )


        /**
         * 构建 [SendDirectMessageApi] 构建器。
         *
         * @param targetId 目标用户 id
         * @param content 消息内容
         */
        @JvmStatic
        @JvmOverloads
        public fun builderByTargetId(targetId: String, content: String? = null): Builder =
            Builder.byTargetId(targetId).also {
                it.content = content
            }

        /**
         * 构建 [SendDirectMessageApi] 构建器。
         *
         * @param chatCode 私信会话 Code
         * @param content 消息内容
         */
        @JvmStatic
        @JvmOverloads
        public fun builderByChatCode(chatCode: String, content: String? = null): Builder =
            Builder.byChatCode(chatCode).also {
                it.content = content
            }

        /**
         * 构建 [SendDirectMessageApi] 实例。
         */
        @JvmSynthetic
        public inline fun createByTargetId(targetId: String, block: Builder.() -> Unit): SendDirectMessageApi =
            builderByTargetId(targetId).apply(block).build()

        /**
         * 构建 [SendDirectMessageApi] 实例。
         */
        @JvmSynthetic
        public inline fun createByChatCode(chatCode: String, block: Builder.() -> Unit): SendDirectMessageApi =
            builderByChatCode(chatCode).apply(block).build()


    }

    override val resultDeserializationStrategy: DeserializationStrategy<SendMessageResult>
        get() = SendMessageResult.serializer()

    override val apiPath: ApiPath
        get() = PATH


    /**
     * [SendDirectMessageApi] 构建器。
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public class Builder private constructor(
        private var targetId: String? = null,
        private var chatCode: String? = null
    ) {
        public companion object {
            internal fun byTargetId(targetId: String) = Builder(targetId = targetId)
            internal fun byChatCode(chatCode: String) = Builder(chatCode = chatCode)
        }

        /**
         * 消息类型
         */
        public var type: Int? = null

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
         * 构建一个 [SendDirectMessageApi] 实例。
         */
        public fun build(): SendDirectMessageApi {
            require(chatCode != null || targetId != null) { "One of 'targetId' and 'chatCode' must not be null." }

            return SendDirectMessageApi(
                type = type,
                targetId = targetId,
                chatCode = chatCode,
                content = content ?: throw IllegalArgumentException("'content' must not be null"),
                quote = quote,
                nonce = nonce,
            )
        }
    }


    override fun createBody(): Any = Body(
        type = type,
        chatCode = chatCode,
        targetId = targetId,
        content = content,
        quote = quote,
        nonce = nonce,
    )

    @Serializable
    private data class Body(
        @SerialName("type") val type: Int? = null,
        @SerialName("target_id") val targetId: String? = null,
        @SerialName("chat_code") val chatCode: String? = null,
        @SerialName("content") val content: String,
        @SerialName("quote") val quote: String? = null,
        @SerialName("nonce") val nonce: String? = null,
    )

}



