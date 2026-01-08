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

package love.forte.simbot.component.qguild.role

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.suspendrunner.ST

/**
 * 一个被赋予在某个成员身上的[角色][QGRole]。
 *
 * @see QGRole
 */
@ExperimentalQGApi
public interface QGMemberRole : QGRole, DeleteSupport {
    /**
     * 得到当前角色所属的成员ID
     */
    public val memberId: ID

    /**
     * 得到此角色对应在频道中的角色信息。
     */
    public val guildRole: QGGuildRole

    /**
     * 为当前成员移除此角色。即撤除对应成员的当前角色权限。
     *
     * @throws QQGuildApiException API请求错误，例如无权限
     */
    @ST
    override suspend fun delete(vararg options: DeleteOption)

    /**
     * 为当前成员移除此角色。即撤除对应成员的当前角色权限。
     *
     * [channelId] 会在进行删除时为API填充 `channel.id` 属性，
     * 用以支持 要删除的身份组 `ID` 是 `5-子频道管理员` 的情况。
     * 更多请参考 [QQ频道机器人官方文档](https://bot.q.qq.com/wiki/develop/api/openapi/guild/delete_guild_member_role.html)
     *
     * @throws QQGuildApiException API请求错误，例如无权限
     */
    @ST
    public suspend fun delete(channelId: ID?, vararg options: DeleteOption)
}
