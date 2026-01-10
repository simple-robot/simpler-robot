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

import kotlinx.serialization.DeserializationStrategy
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.model.ChannelPermissions
import kotlin.jvm.JvmStatic

/**
 *
 * [获取指定子频道的权限](https://bot.q.qq.com/wiki/develop/api/openapi/channel_permissions/get_channel_permissions.html)
 *
 * 用于获取 子频道 `channel_id` 下用户 `user_id` 的权限。
 *
 * - 获取子频道用户权限。
 * - 要求操作人具有管理子频道的权限，如果是机器人，则需要将机器人设置为管理员。
 *
 * @author ForteScarlet
 */
public class GetChannelMemberPermissionsApi private constructor(
    channelId: String, memberId: String
) : GetQQGuildApi<ChannelPermissions>() {
    public companion object Factory : SimpleGetApiDescription(
        "/channels/{channel_id}/members/{user_id}/permissions"
    ) {
        /**
         * 构造 [GetChannelMemberPermissionsApi]
         */
        @JvmStatic
        public fun create(channelId: String, memberId: String): GetChannelMemberPermissionsApi =
            GetChannelMemberPermissionsApi(channelId, memberId)
    }

    override val path: Array<String> = arrayOf("channels", channelId, "members", memberId, "permissions")

    override val resultDeserializationStrategy: DeserializationStrategy<ChannelPermissions>
        get() = ChannelPermissions.serializer()
}
