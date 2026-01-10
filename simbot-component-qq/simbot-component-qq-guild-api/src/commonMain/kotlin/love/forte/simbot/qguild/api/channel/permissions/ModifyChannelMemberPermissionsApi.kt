/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.qguild.api.channel.permissions

import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PutQQGuildApi
import love.forte.simbot.qguild.api.QQGuildApiWithoutResult
import love.forte.simbot.qguild.api.SimplePutApiDescription
import love.forte.simbot.qguild.model.Permissions
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * [修改子频道权限](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/put_channel_permissions.html)
 *
 * 用于修改子频道 `channel_id` 下用户 `user_id` 的权限。
 *
 * - 要求操作人具有 `管理子频道` 的权限，如果是机器人，则需要将机器人设置为管理员。
 * - 参数包括 `add` 和 `remove` 两个字段，分别表示授予的权限以及删除的权限。
 * 要授予用户权限即把 `add` 对应位置 1，删除用户权限即把 `remove` 对应位置 1。当两个字段同一位都为 1，表现为删除权限。
 * - 本接口不支持修改 `可管理子频道` 权限。
 *
 * @author ForteScarlet
 */
public class ModifyChannelMemberPermissionsApi private constructor(
    channelId: String, memberId: String,
    private val _body: Body
) : QQGuildApiWithoutResult, PutQQGuildApi<Unit>() {
    public companion object Factory : SimplePutApiDescription(
        "/channels/{channel_id}/members/{user_id}/permissions"
    ) {
        /**
         * 构造 [ModifyChannelMemberPermissionsApi].
         * @param add 需要追加的权限的位图
         * @param remove 需要移除的权限的位图
         */
        @JvmStatic
        @JvmName("create") // support value class for Java
        public fun create(
            channelId: String,
            memberId: String,
            add: Permissions? = null,
            remove: Permissions? = null,
        ): ModifyChannelMemberPermissionsApi = ModifyChannelMemberPermissionsApi(channelId, memberId, Body(add, remove))

        /**
         * 构造 [ModifyChannelMemberPermissionsApi].
         * @param add 需要追加的权限的位图
         */
        @JvmStatic
        @JvmName("createForAdd") // support value class for Java
        public fun createForAdd(
            channelId: String, memberId: String, add: Permissions
        ): ModifyChannelMemberPermissionsApi = create(channelId, memberId, add = add)

        /**
         * 构造 [ModifyChannelMemberPermissionsApi].
         * @param remove 需需要移除的权限的位图
         */
        @JvmStatic
        @JvmName("createForRemove") // support value class for Java
        public fun createForRemove(
            channelId: String, memberId: String, remove: Permissions
        ): ModifyChannelMemberPermissionsApi = create(channelId, memberId, remove = remove)
    }


    override val path: Array<String> = arrayOf("channels", channelId, "members", memberId, "permissions")

    override fun createBody(): Any = _body

    @Serializable
    private data class Body(
        val add: Permissions?,
        val remove: Permissions?
    ) {
        init {
            require(add != null || remove != null) {
                "At least one of the parameters should not be null"
            }
        }
    }
}
