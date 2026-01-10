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

package love.forte.simbot.kook.api.role

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.KookPostApi
import love.forte.simbot.kook.objects.Permissions
import love.forte.simbot.kook.objects.Role
import love.forte.simbot.kook.objects.SimpleRole
import love.forte.simbot.kook.util.BooleanToIntSerializer
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic


/**
 * [更新服务器角色](https://developer.kaiheila.cn/doc/http/guild-role#更新服务器角色)
 *
 * @author ForteScarlet
 */
public class UpdateGuildRoleApi private constructor(
    guildId: String,
    roleId: Long,
    name: String? = null,
    color: Int? = null,
    isHoist: Boolean? = null,
    isMentionable: Boolean? = null,
    permissions: Permissions? = null,
) : KookPostApi<Role>() {
    public companion object Factory {
        private val PATH = ApiPath.create("guild-role", "update")

        /**
         * 构建一个 [更新服务器角色][UpdateGuildRoleApi] 请求.
         *
         * @param guildId 服务器id
         * @param roleId 角色id
         *
         * @see builder
         */
        @JvmStatic
        public fun create(guildId: String, roleId: Long): UpdateGuildRoleApi =
            UpdateGuildRoleApi(guildId, roleId)

        /**
         * 构建一个 [更新服务器角色][UpdateGuildRoleApi] 请求.
         *
         * @param guildId 服务器id
         * @param roleId 角色id
         */
        @JvmStatic
        public fun builder(guildId: String, roleId: Long): Builder = Builder(guildId, roleId)


        /**
         * 构建一个 [更新服务器角色][UpdateGuildRoleApi] 请求.
         *
         * @param guildId 服务器id
         * @param roleId 角色id
         *
         * @see builder
         */
        @JvmSynthetic
        public inline fun build(guildId: String, roleId: Long, block: Builder.() -> Unit): UpdateGuildRoleApi =
            builder(guildId, roleId).also(block).build()
    }

    override val apiPath: ApiPath
        get() = PATH

    override val resultDeserializationStrategy: DeserializationStrategy<SimpleRole>
        get() = SimpleRole.serializer()

    /**
     * [UpdateGuildRoleApi] 的构建器。
     *
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public class Builder internal constructor(public val guildId: String, public val roleId: Long) {
        /**
         * 角色名称
         */
        public var name: String? = null

        /**
         *  颜色
         */
        public var color: Int? = null

        /**
         * 只能为 0 或者 1，是否把该角色的用户在用户列表排到前面。
         *
         */
        public var isHoist: Boolean? = null

        /**
         * 只能为 0 或者 1，该角色是否可以被提及
         */
        public var isMentionable: Boolean? = null

        /**
         * 权限,参见权限说明
         */
        public var permissions: Permissions? = null

        /**
         * 设置 [name] 值并返回 [Builder] 自身实例。
         */
        public fun name(name: String): Builder = apply { this.name = name }

        /**
         * 设置 [color] 值并返回 [Builder] 自身实例。
         */
        public fun color(color: Int): Builder = apply { this.color = color }

        /**
         * 设置 [isHoist] 值并返回 [Builder] 自身实例。
         */
        public fun isHoist(isHoist: Boolean): Builder = apply { this.isHoist = isHoist }

        /**
         * 设置 [isMentionable] 值并返回 [Builder] 自身实例。
         */
        public fun isMentionable(isMentionable: Boolean): Builder = apply { this.isMentionable = isMentionable }

        /**
         * 设置 [permissions] 值并返回 [Builder] 自身实例。
         */
        @JvmName("permissions")
        public fun permissions(permissions: Permissions): Builder = apply { this.permissions = permissions }


        /**
         * 构建 [UpdateGuildRoleApi] 实例。
         *
         */
        public fun build(): UpdateGuildRoleApi = UpdateGuildRoleApi(
            guildId,
            roleId,
            name,
            color,
            isHoist,
            isMentionable,
            permissions
        )
    }

    override val body: Any = Body(guildId, roleId, name, color, isHoist, isMentionable, permissions)

    @Serializable
    private data class Body(
        @SerialName("guild_id") val guildId: String,
        @SerialName("role_id") val roleId: Long,
        val name: String? = null,
        val color: Int? = null,
        @Serializable(with = BooleanToIntSerializer::class)
        val hoist: Boolean? = null,
        @Serializable(with = BooleanToIntSerializer::class)
        val mentionable: Boolean? = null,
        val permissions: Permissions? = null,
    )
}
