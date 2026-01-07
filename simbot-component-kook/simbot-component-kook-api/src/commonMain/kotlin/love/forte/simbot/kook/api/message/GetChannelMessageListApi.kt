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
import love.forte.simbot.kook.messages.ChannelMessageDetails
import love.forte.simbot.kook.util.appendIfNotNull
import love.forte.simbot.kook.util.parameters
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic


/**
 * [获取频道聊天消息列表](https://developer.kookapp.cn/doc/http/message#%E8%8E%B7%E5%8F%96%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF%E5%88%97%E8%A1%A8)
 *
 * > 此接口非标准分页，需要根据参考消息来查询相邻分页的消息
 *
 * @author ForteScarlet
 */
public class GetChannelMessageListApi private constructor(
    private val targetId: String,
    private val msgId: String? = null,
    private val pin: Boolean? = null,
    private val flag: String? = null,
    private val pageSize: Int? = null,
) : KookGetApi<ChannelMessageList>() {
    public companion object Factory {
        private val PATH = ApiPath.create("message", "list")

        /**
         * 构造一个 [获取频道聊天消息列表][GetChannelMessageListApi] 请求。
         *
         * @param targetId 频道 id
         * @param msgId 参考消息 id，不传则查询最新消息
         * @param pin 是否查询置顶消息。 置顶消息只支持查询最新的消息
         * @param flag 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         * @param pageSize 当前分页消息数量, 默认 50
         */
        @JvmStatic
        public fun create(
            targetId: String,
            msgId: String? = null,
            pin: Boolean? = null,
            flag: String? = null,
            pageSize: Int? = null,
        ): GetChannelMessageListApi = GetChannelMessageListApi(targetId, msgId, pin, flag, pageSize)

        /**
         * 构造一个 [获取频道聊天消息列表][GetChannelMessageListApi] 请求。
         *
         * @param targetId 频道 id
         * @param msgId 参考消息 id，不传则查询最新消息
         * @param pin 是否查询置顶消息。 置顶消息只支持查询最新的消息
         * @param flag 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         * @param pageSize 当前分页消息数量, 默认 50
         */
        @JvmStatic
        public fun create(
            targetId: String,
            msgId: String? = null,
            pin: Boolean? = null,
            flag: MessageQueryFlag? = null,
            pageSize: Int? = null,
        ): GetChannelMessageListApi = GetChannelMessageListApi(targetId, msgId, pin, flag?.value, pageSize)

        /**
         * 构造一个 [获取频道聊天消息列表][GetChannelMessageListApi] 请求。
         *
         * @param targetId 频道 id
         */
        @JvmStatic
        public fun create(targetId: String): GetChannelMessageListApi = GetChannelMessageListApi(targetId)

        /**
         * 构建一个 [获取频道聊天消息列表][GetChannelMessageListApi] 的构建器。
         *
         * @param targetId 频道 id
         */
        @JvmStatic
        public fun builder(targetId: String): Builder = Builder(targetId)

        /**
         * 构建一个 [获取频道聊天消息列表][GetChannelMessageListApi] 请求。
         */
        @JvmSynthetic
        public inline fun create(targetId: String, block: Builder.() -> Unit): GetChannelMessageListApi =
            builder(targetId).apply(block).build()
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<ChannelMessageList>
        get() = ChannelMessageList.serializer()

    override fun urlBuild(builder: URLBuilder) {
        builder.parameters {
            append("target_id", targetId)
            appendIfNotNull("msg_id", msgId)
            appendIfNotNull("pin", pin) { if (it) "1" else "0" }
            appendIfNotNull("flag", flag)
            appendIfNotNull("page_size", pageSize) { it.toString() }
        }
    }

    /**
     * [获取频道聊天消息列表][GetChannelMessageListApi] 的构建器。
     *
     * @property targetId 频道 id
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public class Builder internal constructor(public val targetId: String) {
        /**
         * 参考消息 id，不传则查询最新消息
         */
        public var msgId: String? = null

        /**
         * 是否查询置顶消息。 置顶消息只支持查询最新的消息
         */
        public var pin: Boolean? = null

        /**
         * 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         */
        public var flag: String? = null

        /**
         * 当前分页消息数量, 默认 50
         */
        public var pageSize: Int? = null

        /**
         * 参考消息 id，不传则查询最新消息
         */
        public fun msgId(msgId: String): Builder = apply { this.msgId = msgId }

        /**
         * 是否查询置顶消息。 置顶消息只支持查询最新的消息
         */
        public fun pin(pin: Boolean): Builder = apply { this.pin = pin }

        /**
         * 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         */
        public fun flag(flag: String): Builder = apply { this.flag = flag }

        /**
         * 查询模式，有三种模式可以选择。不传则默认查询最新的消息
         */
        public fun flag(flag: MessageQueryFlag): Builder = apply { this.flag = flag.value }

        /**
         * 当前分页消息数量, 默认 50
         */
        public fun pageSize(pageSize: Int): Builder = apply { this.pageSize = pageSize }

        /**
         * 构建一个 [获取频道聊天消息列表][GetChannelMessageListApi] 请求。
         */
        public fun build(): GetChannelMessageListApi = GetChannelMessageListApi(
            targetId = targetId,
            msgId = msgId,
            pin = pin,
            flag = flag,
            pageSize = pageSize,
        )
    }


}

/**
 * [GetChannelMessageListApi] 的响应体
 */
@Serializable
public data class ChannelMessageList @ApiResultType constructor(val items: List<ChannelMessageDetails> = emptyList())


