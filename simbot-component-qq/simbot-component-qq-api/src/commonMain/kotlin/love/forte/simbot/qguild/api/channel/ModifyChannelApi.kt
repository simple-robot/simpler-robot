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
import love.forte.simbot.qguild.api.PatchQQGuildApi
import love.forte.simbot.qguild.api.SimplePatchApiDescription
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.common.PrivateDomainOnly
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
     * @property name 子频道名
     * @property position 排序
     * @property parentId 分组 id
     * @property privateType 子频道私密类型 [PrivateType]
     * @property speakPermission 子频道发言权限 [SpeakPermission]
     */
    @Serializable
    public class Body @ApiModelConstructor public constructor(
        public val name: String? = null,
        public val position: Int? = null,
        @SerialName("parent_id") public val parentId: String? = null,
        @SerialName("private_type") public val privateType: PrivateType? = null,
        @SerialName("speak_permission") public val speakPermission: SpeakPermission? = null
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Body) return false
            if (name != other.name) return false
            if (position != other.position) return false
            if (parentId != other.parentId) return false
            if (privateType != other.privateType) return false
            if (speakPermission != other.speakPermission) return false
            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + position.hashCode()
            result = 31 * result + parentId.hashCode()
            result = 31 * result + privateType.hashCode()
            result = 31 * result + speakPermission.hashCode()
            return result
        }

        override fun toString(): String =
            "Body(" +
                "name=$name, " +
                "position=$position, " +
                "parentId=$parentId, " +
                "privateType=$privateType, " +
                "speakPermission=$speakPermission)"

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("name"))
        public operator fun component1(): String? = name

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("position"))
        public operator fun component2(): Int? = position

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("parentId"))
        public operator fun component3(): String? = parentId

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("privateType"))
        public operator fun component4(): PrivateType? = privateType

        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("speakPermission"))
        public operator fun component5(): SpeakPermission? = speakPermission

        @Suppress("DeprecatedCallableAddReplaceWith")
        @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
        public fun copy(
            name: String? = this.name,
            position: Int? = this.position,
            parentId: String? = this.parentId,
            privateType: PrivateType? = this.privateType,
            speakPermission: SpeakPermission? = this.speakPermission,
        ): Body = Body(name, position, parentId, privateType, speakPermission)

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
             * 创建一个 [Body]。
             *
             * @since 5.0
             */
            @JvmStatic
            public fun of(
                name: String? = null,
                position: Int? = null,
                parentId: String? = null,
                privateType: PrivateType? = null,
                speakPermission: SpeakPermission? = null,
            ): Body = Body(name, position, parentId, privateType, speakPermission)

            /**
             * 创建一个 [Builder].
             */
            @JvmStatic
            public fun builder(): Builder = Builder()
        }

    }
}
