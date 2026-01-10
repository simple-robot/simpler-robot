/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.qguild.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmInline

/**
 * [Permissions](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/model.html#permissions)
 *
 * 权限是QQ频道管理频道成员的一种方式，管理员可以对不同的人、不同的子频道设置特定的权限。用户的权限包括**个人权限**和**身份组权限**两部分，最终生效是取两种权限的并集。
 *
 * _注意：不能设置ID为1的身份组权限。逻辑上未获得任何身份组权限的普通用户被归到"普通用户"身份组（ID=1）。_
 *
 * 权限使用位图表示，传递时序列化为十进制数值字符串。如权限值为 `0x6FFF`，会被序列化为十进制 `"28671"`。
 *
 * ### 序列化
 * [Permissions] 的序列化器会将其序列化为**字符串**格式。
 *
 */
@Serializable(PermissionsSerializer::class)
@JvmInline
public value class Permissions(public val value: Long) {

    /**
     * 与另外一个权限合并
     */
    public operator fun plus(other: Permissions): Permissions = Permissions(value or other.value)

    /**
     * 与另外一个权限合并
     */
    public operator fun plus(other: Long): Permissions = Permissions(value or other)

    /**
     * 判断是否包含 [permission] 权限。
     */
    public operator fun contains(permission: Permissions): Boolean = contains(permission.value)

    /**
     * 判断是否包含 [permission] 权限。
     */
    public operator fun contains(permission: Long): Boolean = (value and permission) != 0L

    public companion object {
        /**
         * 可查看子频道
         *
         * 支持 `指定成员` 可见类型，支持 `身份组` 可见类型
         */
        public const val CHANNEL_VIEWABLE: Long = 1 shl 0

        /**
         * 可管理子频道
         *
         * 创建者、管理员、子频道管理员都具有此权限
         */
        public const val CHANNEL_MANAGEABLE: Long = 1 shl 1

        /**
         * 可发言子频道
         *
         * 支持 `指定成员` 发言类型，支持 身份组` 发言类型
         */
        public const val CHANNEL_SPEAKABLE: Long = 1 shl 2

        /**
         * 可直播子频道
         *
         * 支持 `指定成员` 发起直播，支持 `身份组` 发起直播；仅可在直播子频道中设置
         */
        public const val CHANNEL_LIVEABLE: Long = 1 shl 3

    }


    /**
     * @suppress 直接使用 [Permissions.contains] .
     */
    @Deprecated(
        "use contains", ReplaceWith(
            "contains(CHANNEL_VIEWABLE)",
            "love.forte.simbot.qguild.model.Permissions.Companion.CHANNEL_VIEWABLE"
        )
    )
    public val isChannelViewable: Boolean get() = contains(CHANNEL_VIEWABLE)

    /**
     * @suppress 直接使用 [Permissions.contains] .
     */
    @Deprecated(
        "use contains", ReplaceWith(
            "contains(CHANNEL_MANAGEABLE)",
            "love.forte.simbot.qguild.model.Permissions.Companion.CHANNEL_MANAGEABLE"
        )
    )
    public val isChannelManageable: Boolean get() = contains(CHANNEL_MANAGEABLE)

    /**
     * @suppress 没什么道理的操作
     */
    @Deprecated("Nothing makes sense", ReplaceWith("value != 0L"))
    public val isAdmin: Boolean get() = value != 0L

}


internal object PermissionsSerializer : KSerializer<Permissions> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Permissions", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Permissions = Permissions(decoder.decodeString().toLong())

    override fun serialize(encoder: Encoder, value: Permissions) {
        encoder.encodeString(value.value.toString())
    }
}
