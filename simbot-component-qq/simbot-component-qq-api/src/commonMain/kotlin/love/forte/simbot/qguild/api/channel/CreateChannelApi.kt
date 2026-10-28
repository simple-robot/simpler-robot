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

package love.forte.simbot.qguild.api.channel

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.common.PrivateDomainOnly
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
     *
     * @property name 子频道名称
     * @property type 子频道类型 [ChannelType]
     * @property subType 子频道子类型 [ChannelSubType]
     * @property position 子频道排序，必填；当子频道类型为 `子频道分组（ChannelType=4）` 时，必须大于等于 `2`
     * @property parentId 子频道所属分组 ID
     * @property privateType 子频道私密类型 [PrivateType]
     * @property privateUserIds 子频道私密类型成员 ID
     * @property speakPermission 子频道发言权限 [SpeakPermission]
     * @property applicationId 应用类型子频道应用 AppID，仅应用子频道需要该字段
     */
    @Serializable
    public class Body @ApiModelConstructor public constructor(
        public val name: String,
        public val type: ChannelType,
        @SerialName("sub_type")
        public val subType: ChannelSubType,
        public val position: Int,
        @SerialName("parent_id")
        public val parentId: String,
        @SerialName("private_type")
        public val privateType: PrivateType,
        @SerialName("private_user_ids")
        public val privateUserIds: List<String>,
        @SerialName("speak_permission")
        public val speakPermission: SpeakPermission,
        @SerialName("application_id")
        public val applicationId: String? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Body) return false
            if (name != other.name) return false
            if (type != other.type) return false
            if (subType != other.subType) return false
            if (position != other.position) return false
            if (parentId != other.parentId) return false
            if (privateType != other.privateType) return false
            if (privateUserIds != other.privateUserIds) return false
            if (speakPermission != other.speakPermission) return false
            if (applicationId != other.applicationId) return false
            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + type.hashCode()
            result = 31 * result + subType.hashCode()
            result = 31 * result + position
            result = 31 * result + parentId.hashCode()
            result = 31 * result + privateType.hashCode()
            result = 31 * result + privateUserIds.hashCode()
            result = 31 * result + speakPermission.hashCode()
            result = 31 * result + applicationId.hashCode()
            return result
        }

        override fun toString(): String =
            "Body(" +
                "name=$name, " +
                "type=$type, " +
                "subType=$subType, " +
                "position=$position, " +
                "parentId=$parentId, " +
                "privateType=$privateType, " +
                "privateUserIds=$privateUserIds, " +
                "speakPermission=$speakPermission, " +
                "applicationId=$applicationId)"

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("name"))
        public operator fun component1(): String = name

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("type"))
        public operator fun component2(): ChannelType = type

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("subType"))
        public operator fun component3(): ChannelSubType = subType

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("position"))
        public operator fun component4(): Int = position

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("parentId"))
        public operator fun component5(): String = parentId

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("privateType"))
        public operator fun component6(): PrivateType = privateType

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("privateUserIds"))
        public operator fun component7(): List<String> = privateUserIds

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("speakPermission"))
        public operator fun component8(): SpeakPermission = speakPermission

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("applicationId"))
        public operator fun component9(): String? = applicationId

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            name: String = this.name,
            type: ChannelType = this.type,
            subType: ChannelSubType = this.subType,
            position: Int = this.position,
            parentId: String = this.parentId,
            privateType: PrivateType = this.privateType,
            privateUserIds: List<String> = this.privateUserIds,
            speakPermission: SpeakPermission = this.speakPermission,
            applicationId: String? = this.applicationId,
        ): Body = Body(
            name = name,
            type = type,
            subType = subType,
            position = position,
            parentId = parentId,
            privateType = privateType,
            privateUserIds = privateUserIds,
            speakPermission = speakPermission,
            applicationId = applicationId,
        )

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
             * 创建一个 [Body]。
             *
             * @since 5.0
             */
            @JvmStatic
            public fun of(
                name: String,
                type: ChannelType,
                subType: ChannelSubType,
                position: Int,
                parentId: String,
                privateType: PrivateType,
                privateUserIds: List<String>,
                speakPermission: SpeakPermission,
                applicationId: String? = null,
            ): Body = Body(
                name = name,
                type = type,
                subType = subType,
                position = position,
                parentId = parentId,
                privateType = privateType,
                privateUserIds = privateUserIds,
                speakPermission = speakPermission,
                applicationId = applicationId,
            )

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
