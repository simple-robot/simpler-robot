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

import love.forte.simbot.ability.DeleteFailureException
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.definition.Member
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.member.GetGuildRoleMemberListApi
import love.forte.simbot.suspendrunner.ST
import kotlin.js.JsName

/**
 * 一个[角色][QGRole]在某个频道服务器中的表现类型。
 *
 * @see QGRole
 */
@ExperimentalQGApi
public interface QGGuildRole : QGRole, DeleteSupport {

    /**
     * 将当前角色赋予指定用户ID的用户。
     *
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @ST
    public suspend fun grantTo(memberId: ID): QGMemberRole

    /**
     * 将当前角色赋予指定用户。
     *
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @ST
    public suspend fun grantTo(member: QGMember): QGMemberRole

    /**
     * 将当前角色赋予指定用户。
     *
     * @throws ClassCastException 如果 [member] 的类型不是 [QGMember]
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @ST
    public suspend fun grantTo(member: Member): QGMemberRole =
        grantTo(
            member as? QGMember
                ?: throw ClassCastException("QGGuildRole.grantTo only support member of type QGMember, but ${member::class}"),
        )


    /**
     * 将当前角色赋予指定用户ID的用户。
     *
     * @param channelId 如果身份组 `ID` 是 `5-子频道管理员`，需要增加 `channel.id` 来指定具体是哪个子频道
     *
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @ST
    public suspend fun grantTo(memberId: ID, channelId: ID): QGMemberRole


    /**
     * 将当前角色赋予指定用户。
     *
     * @param channelId 如果身份组 `ID` 是 `5-子频道管理员`，需要增加 `channel.id` 来指定具体是哪个子频道
     *
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @ST
    public suspend fun grantTo(member: QGMember, channelId: ID): QGMemberRole

    /**
     * 将当前角色赋予指定用户。[member] 的实际类型必须为 [QGMember]
     *
     * @param channelId 如果身份组 `ID` 是 `5-子频道管理员`，需要增加 `channel.id` 来指定具体是哪个子频道
     *
     * @throws ClassCastException 如果 [member] 的类型不是 [QGMember]
     * @throws QQGuildApiException API异常，例如没有权限或用户不存在等
     */
    @ST
    public suspend fun grantTo(member: Member, channelId: ID): QGMemberRole =
        grantTo(
            member as? QGMember
                ?: throw ClassCastException("QGGuildRole.grantTo only support member of type QGMember, but ${member::class}"),
            channelId
        )

    /**
     * 删除频道下对应的角色（身份组）。
     *
     * @throws NoSuchElementException 没有供删除的目标
     * @throws DeleteFailureException 删除失败，通常来自 [QQGuildApiException] (cause 中)
     * @throws Exception 其他非API或参数校验而产生的异常
     */
    @ST
    override suspend fun delete(vararg options: DeleteOption)

    /**
     * 得到当前频道身份组下所有的频道成员。
     *
     * @see members
     */
    public val members: Collectable<QGMember>
        get() = members(GetGuildRoleMemberListApi.MAX_LIMIT)

    /**
     * 得到当前频道身份组下所有的频道成员。
     *
     * @param batch 内部自动分页查询时每一次查询的批次数量。
     * 默认为 [GetGuildRoleMemberListApi.MAX_LIMIT]
     * (通过 [members] 属性获取即为此默认值。)
     *
     * @throws IllegalArgumentException [batch] 小于等于 `0`
     * @see members
     */
    @JsName("getMembers")
    public fun members(batch: Int): Collectable<QGMember>
}
