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
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.model.*
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic


/**
 * [创建子频道](https://bot.q.qq.com/wiki/develop/api/openapi/channel/post_channels.html)
 *
 * 用于在 `guild_id` 指定的频道下创建一个子频道。
 *
 * - 要求操作人具有管理频道的权限，如果是机器人，则需要将机器人设置为管理员。
 * - 创建成功后，返回创建成功的子频道对象，同时会触发一个频道创建的事件通知。
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class CreateChannelApi private constructor(
    guildId: String,
    override val body: Body
) : PostQQGuildApi<SimpleChannel>() {
    public companion object Factory : SimplePostApiDescription(
        "/guilds/{guild_id}/channels"
    ) {
        /**
         * 构造 [CreateChannelApi]
         *
         */
        @JvmStatic
        public fun create(guildId: String, body: Body): CreateChannelApi = CreateChannelApi(guildId, body)

        /**
         * 构造 [CreateChannelApi]
         *
         */
        @JvmSynthetic
        public inline fun create(guildId: String, block: Body.Builder.() -> Unit): CreateChannelApi =
            create(guildId, Body.builder().also(block).build())
    }

    override val path: Array<String> = arrayOf("guilds", guildId, "channels")

    override val resultDeserializationStrategy: DeserializationStrategy<SimpleChannel>
        get() = SimpleChannel.serializer()

    override fun createBody(): Any? = null

    /**
     * [CreateChannelApi] 的请求体。
     */
    @Serializable
    public data class Body(
        /**
         * 子频道名称
         */
        val name: String,
        /**
         * 子频道类型 [ChannelType]
         */
        val type: ChannelType,
        /**
         * 子频道子类型 [ChannelSubType]
         */
        @SerialName("sub_type")
        val subType: ChannelSubType,
        /**
         * 子频道排序，必填；当子频道类型为 `子频道分组（ChannelType=4）` 时，必须大于等于 `2`
         */
        val position: Int,

        /**
         * 子频道所属分组ID
         */
        @SerialName("parent_id")
        val parentId: String,

        /**
         * 子频道私密类型 [PrivateType]
         */
        @SerialName("private_type")
        val privateType: PrivateType,

        /**
         * 子频道私密类型成员 ID
         */
        @SerialName("private_user_ids")
        val privateUserIds: List<String>,

        /**
         * 子频道发言权限 [SpeakPermission]
         */
        @SerialName("speak_permission")
        val speakPermission: SpeakPermission,

        /**
         * 应用类型子频道应用 AppID，仅应用子频道需要该字段
         */
        @SerialName("application_id")
        val applicationId: String? = null
    ) {
        /**
         * Builder for [Body].
         *
         * 其中除了 [applicationId] 以外的属性都是必选的。
         */
        public class Builder {
            /**
             * @see Body.name
             */
            public var name: String? = null

            /**
             * @see Body.type
             */
            public var type: ChannelType? = null

            /**
             * @see Body.subType
             */
            public var subType: ChannelSubType? = null

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
             * @see Body.privateUserIds
             */
            public var privateUserIds: List<String>? = null

            /**
             * @see Body.speakPermission
             */
            public var speakPermission: SpeakPermission? = null

            /**
             * @see Body.applicationId
             */
            public var applicationId: String? = null

            public fun name(value: String?): Builder = apply {
                this.name = value
            }

            public fun type(value: ChannelType?): Builder = apply {
                this.type = value
            }

            public fun subType(value: ChannelSubType?): Builder = apply {
                this.subType = value
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

            public fun privateUserIds(value: List<String>?): Builder = apply {
                this.privateUserIds = value
            }

            public fun speakPermission(value: SpeakPermission?): Builder = apply {
                this.speakPermission = value
            }

            public fun applicationId(value: String?): Builder = apply {
                this.applicationId = value
            }

            public fun build(): Body = Body(
                name = name ?: mismatchProp("name"),
                type = type ?: mismatchProp("type"),
                subType = subType ?: mismatchProp("subType"),
                position = position ?: mismatchProp("position"),
                parentId = parentId ?: mismatchProp("parentId"),
                privateType = privateType ?: mismatchProp("privateType"),
                privateUserIds = privateUserIds ?: mismatchProp("privateUserIds"),
                speakPermission = speakPermission ?: mismatchProp("speakPermission"),
                applicationId = applicationId,
            )
        }

        public companion object {
            /**
             * 创建 [Builder]
             */
            @JvmStatic
            public fun builder(): Builder = Builder()
        }
    }
}


private fun mismatchProp(name: String): Nothing {
    throw IllegalArgumentException("Required '$name' was null")
}
