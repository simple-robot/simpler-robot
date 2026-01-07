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

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.api.KookGetApi
import love.forte.simbot.kook.messages.DirectMessageDetails
import love.forte.simbot.kook.util.appendIfNotNull
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic


/**
 * [获取私信聊天消息列表](https://developer.kookapp.cn/doc/http/direct-message#%E8%8E%B7%E5%8F%96%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF%E5%88%97%E8%A1%A8)
 *
 * @author ForteScarlet
 */
public class GetDirectMessageListApi private constructor(
    /**
     * - `chat_code`: 私信会话 Code。chat_code与target_id必须传一个
     * - `target_id`: 目标用户 id，后端会自动创建会话。有此参数之后可不传chat_code参数
     */
    private val targetParamName: String,
    private val targetParamValue: String,
    /**
     * 参考消息 id，不传则查询最新消息
     */
    private val msgId: String? = null,
    /**
     * 查询模式，有三种模式可以选择。不传则默认查询最新的消息。
     *
     * flag: 查询模式，有三种模式可以选择。不传则默认查询最新的消息。
     * - `before`: 查询参考消息之前的消息，不包括参考消息
     * - `around`: 查询以参考消息为中心，前后一定数量的消息
     * - `after`: 查询参考消息之后的消息，不包括参考消息
     *
     * @see MessageQueryFlag
     */
    private val flag: String? = null,
    /**
     * 目标页数
     */
    private val page: Int? = null,
    /**
     * 当前分页消息数量, 默认 50
     */
    private val pageSize: Int? = null,
) : KookGetApi<DirectMessageList>() {
    public companion object Factory {
        private val PATH = ApiPath.create("direct-message", "list")
        private const val TARGET_ID_PARAM_NAME = "target_id"
        private const val CHAT_CODE_PARAM_NAME = "chat_code"

        /**
         * 构造一个 [GetDirectMessageListApi] 请求。
         *
         * @param targetId 目标用户 id，后端会自动创建会话
         * @param msgId 参考消息 id，不传则查询最新消息
         * @param flag 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         * @param page 目标页数
         * @param pageSize 当前分页消息数量, 默认 50
         */
        @JvmStatic
        public fun createByTargetId(
            targetId: String,
            msgId: String? = null,
            flag: String? = null,
            page: Int? = null,
            pageSize: Int? = null,
        ): GetDirectMessageListApi =
            GetDirectMessageListApi(TARGET_ID_PARAM_NAME, targetId, msgId, flag, page, pageSize)

        /**
         * 构造一个 [GetDirectMessageListApi] 请求。
         *
         * @param targetId 目标用户 id，后端会自动创建会话
         * @param msgId 参考消息 id，不传则查询最新消息
         * @param flag 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         * @param page 目标页数
         * @param pageSize 当前分页消息数量, 默认 50
         */
        @JvmStatic
        public fun createByTargetId(
            targetId: String,
            msgId: String? = null,
            flag: MessageQueryFlag? = null,
            page: Int? = null,
            pageSize: Int? = null,
        ): GetDirectMessageListApi = createByTargetId(targetId, msgId, flag?.value, page, pageSize)

        /**
         * 构造一个 [GetDirectMessageListApi] 请求。
         *
         * @param chatCode 私信会话 Code
         * @param msgId 参考消息 id，不传则查询最新消息
         * @param flag 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         * @param page 目标页数
         * @param pageSize 当前分页消息数量, 默认 50
         */
        @JvmStatic
        public fun createByChatCode(
            chatCode: String,
            msgId: String? = null,
            flag: String? = null,
            page: Int? = null,
            pageSize: Int? = null,
        ): GetDirectMessageListApi =
            GetDirectMessageListApi(CHAT_CODE_PARAM_NAME, chatCode, msgId, flag, page, pageSize)

        /**
         * 构造一个 [GetDirectMessageListApi] 请求。
         *
         * @param chatCode 私信会话 Code
         * @param msgId 参考消息 id，不传则查询最新消息
         * @param flag 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         * @param page 目标页数
         * @param pageSize 当前分页消息数量, 默认 50
         */
        @JvmStatic
        public fun createByChatCode(
            chatCode: String,
            msgId: String? = null,
            flag: MessageQueryFlag? = null,
            page: Int? = null,
            pageSize: Int? = null,
        ): GetDirectMessageListApi = createByChatCode(chatCode, msgId, flag?.value, page, pageSize)

        /**
         * 构造一个 [GetDirectMessageListApi] 请求。
         *
         * @param targetId 目标用户 id，后端会自动创建会话
         */
        @JvmStatic
        public fun createByTargetId(targetId: String): GetDirectMessageListApi =
            GetDirectMessageListApi(TARGET_ID_PARAM_NAME, targetId)

        /**
         * 构造一个 [GetDirectMessageListApi] 请求。
         *
         * @param chatCode 私信会话 Code
         */
        @JvmStatic
        public fun createByChatCode(chatCode: String): GetDirectMessageListApi =
            GetDirectMessageListApi(CHAT_CODE_PARAM_NAME, chatCode)

        /**
         * 构造一个 [Builder]。
         *
         * @param targetId 目标用户 id，后端会自动创建会话
         */
        @JvmStatic
        public fun builderByTargetId(targetId: String): Builder = Builder.byTargetId(targetId)

        /**
         * 构造一个 [Builder]。
         *
         * @param chatCode 私信会话 Code
         */
        @JvmStatic
        public fun builderByChatCode(chatCode: String): Builder = Builder.byChatCode(chatCode)


        /**
         * 构建一个 [GetDirectMessageListApi] 请求。
         */
        @JvmSynthetic
        public inline fun createByTargetId(targetId: String, block: Builder.() -> Unit): GetDirectMessageListApi =
            builderByTargetId(targetId).apply(block).build()

        /**
         * 构建一个 [GetDirectMessageListApi] 请求。
         */
        @JvmSynthetic
        public inline fun createByChatCode(chatCode: String, block: Builder.() -> Unit): GetDirectMessageListApi =
            builderByChatCode(chatCode).apply(block).build()
    }

    override val resultDeserializationStrategy: DeserializationStrategy<DirectMessageList>
        get() = DirectMessageList.serializer()

    override val apiPath: ApiPath
        get() = PATH

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append(targetParamName, targetParamValue)
            appendIfNotNull("msg_id", msgId)
            appendIfNotNull("flag", flag)
            appendIfNotNull("page", page) { it.toString() }
            appendIfNotNull("page_size", pageSize) { it.toString() }
        }
    }


    /**
     * [GetDirectMessageListApi] 的构建器。
     *
     * @see GetDirectMessageListApi.builderByTargetId
     * @see GetDirectMessageListApi.builderByChatCode
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public class Builder private constructor(
        private val targetParamName: String,
        private val targetParamValue: String,
    ) {
        internal companion object {
            fun byTargetId(targetId: String): Builder = Builder(TARGET_ID_PARAM_NAME, targetId)
            fun byChatCode(chatCode: String): Builder = Builder(CHAT_CODE_PARAM_NAME, chatCode)
        }


        /**
         * 参考消息 id，不传则查询最新消息
         */
        public var msgId: String? = null

        /**
         * 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         */
        public var flag: String? = null

        /**
         * 目标页数
         */
        public var page: Int? = null

        /**
         * 当前分页消息数量, 默认 50
         */
        public var pageSize: Int? = null

        /**
         * 参考消息 id，不传则查询最新消息
         */
        public fun msgId(msgId: String): Builder = apply { this.msgId = msgId }

        /**
         * 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         */
        public fun flag(flag: String): Builder = apply { this.flag = flag }

        /**
         * 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         */
        public fun flag(flag: MessageQueryFlag): Builder = apply { this.flag = flag.value }

        /**
         * 目标页数
         */
        public fun page(page: Int): Builder = apply { this.page = page }

        /**
         * 当前分页消息数量, 默认 50
         */
        public fun pageSize(pageSize: Int): Builder = apply { this.pageSize = pageSize }

        /**
         * 构建一个 [GetDirectMessageListApi] 请求。
         */
        public fun build(): GetDirectMessageListApi = GetDirectMessageListApi(
            targetParamName = targetParamName,
            targetParamValue = targetParamValue,
            msgId = msgId,
            flag = flag,
            page = page,
            pageSize = pageSize,
        )
    }
}


/**
 * [GetDirectMessageListApi] 的响应体
 */
@Serializable
public data class DirectMessageList @ApiResultType constructor(val items: List<DirectMessageDetails> = emptyList())
