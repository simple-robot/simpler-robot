/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.qguild.api.channel

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.api.PatchQQGuildApi
import love.forte.simbot.qguild.api.SimplePatchApiDescription
import love.forte.simbot.qguild.model.PrivateType
import love.forte.simbot.qguild.model.SimpleChannel
import love.forte.simbot.qguild.model.SpeakPermission
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic


/**
 * [修改子频道](https://bot.q.qq.com/wiki/develop/api/openapi/channel/patch_channel.html)
 *
 * 用于修改 `channel_id` 指定的子频道的信息。
 *
 * - 要求操作人具有 `管理子频道` 的权限，如果是机器人，则需要将机器人设置为管理员。
 * - 修改成功后，会触发子频道更新事件。
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class ModifyChannelApi private constructor(
    channelId: String, override val body: Body
) : PatchQQGuildApi<SimpleChannel>() {
    public companion object Factory : SimplePatchApiDescription(
        "/channels/{channel_id}"
    ) {

        /**
         * 构造 [ModifyChannelApi].
         *
         */
        @JvmStatic
        public fun create(channelId: String, body: Body): ModifyChannelApi =
            ModifyChannelApi(channelId, body)

        /**
         * 使用 [Body.Builder] 构造 [Body] 并将其作为参数构造 [ModifyChannelApi].
         */
        @JvmSynthetic
        public inline fun create(channelId: String, block: Body.Builder.() -> Unit): ModifyChannelApi =
            create(channelId, Body.builder().also(block).build())
    }

    override val path: Array<String> = arrayOf("channels", channelId)

    override fun createBody(): Any? = null

    override val resultDeserializationStrategy: DeserializationStrategy<SimpleChannel>
        get() = SimpleChannel.serializer()

    /**
     * 用于 [ModifyChannelApi] 的请求体。
     *
     * 需要修改哪个字段，就传递哪个字段即可。
     *
     */
    @Serializable
    public data class Body(
        /**
         * 子频道名
         */
        val name: String? = null,
        /**
         * 排序
         */
        val position: Int? = null,
        /**
         * 分组 id
         */
        @SerialName("parent_id") val parentId: String? = null,
        /**
         * 子频道私密类型 [PrivateType]
         */
        @SerialName("private_type") val privateType: PrivateType? = null,
        /**
         * 子频道发言权限 [SpeakPermission]
         */
        @SerialName("speak_permission") val speakPermission: SpeakPermission? = null
    ) {
        /**
         * [Builder] for [Body]
         */
        @Suppress("MemberVisibilityCanBePrivate")
        public class Builder {
            /**
             * @see Body.name
             */
            public var name: String? = null

            /**
             * @see Body.position
             */
            public var position: Int? = null

            /**
             * @see Body.parentId
             */
            public var parentId: String? = null

            /**
             * @see Body.privateType
             */
            public var privateType: PrivateType? = null

            /**
             * @see Body.speakPermission
             */
            public var speakPermission: SpeakPermission? = null

            public fun name(value: String?): Builder = apply {
                this.name = value
            }

            public fun position(value: Int?): Builder = apply {
                this.position = value
            }

            public fun parentId(value: String?): Builder = apply {
                this.parentId = value
            }

            public fun privateType(value: PrivateType?): Builder = apply {
                this.privateType = value
            }

            public fun speakPermission(value: SpeakPermission?): Builder = apply {
                this.speakPermission = value
            }

            public fun build(): Body = Body(
                name = name,
                position = position,
                parentId = parentId,
                privateType = privateType,
                speakPermission = speakPermission,
            )

        }

        public companion object {
            /**
             * 创建一个 [Builder].
             */
            @JvmStatic
            public fun builder(): Builder = Builder()
        }

    }
}
