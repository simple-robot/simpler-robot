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

package love.forte.simbot.component.qguild.guild

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.component.qguild.channel.QGCategoryChannel
import love.forte.simbot.component.qguild.channel.QGChannel
import love.forte.simbot.component.qguild.channel.QGForumChannel
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.role.QGGuildRole
import love.forte.simbot.component.qguild.role.QGRole
import love.forte.simbot.component.qguild.role.QGRoleCreator
import love.forte.simbot.component.qguild.utils.toTimestamp
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.apipermission.ApiPermissions
import love.forte.simbot.qguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.qguild.api.member.GetGuildMemberListApi
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP
import love.forte.simbot.definition.Guild as SimbotGuild
import love.forte.simbot.qguild.model.Guild as QGSourceGuild

/**
 * 一个QQ频道服务器.
 *
 * @author ForteScarlet
 */
public interface QGGuild : SimbotGuild, CoroutineScope, QGObjectiveContainer<QGSourceGuild> {
    /**
     * 原始的频道信息实例
     */
    override val source: QGSourceGuild

    /**
     * 频道ID
     */
    override val id: ID
        get() = source.id.ID

    /**
     * 频道名称
     */
    override val name: String
        get() = source.name

    /**
     * 频道主ID
     */
    override val ownerId: ID
        get() = source.ownerId.ID

    /**
     * bot加入此频道的时间。
     */
    @OptIn(ExperimentalQGApi::class)
    public val joinTime: Timestamp
        get() = source.joinedAt.toTimestamp()

    /**
     * 频道描述
     */
    public val description: String
        get() = source.description

    /**
     * 频道图标。
     */
    public val icon: String
        get() = source.icon

    /**
     * 当前成员数量
     */
    public val memberCount: Int
        get() = source.memberCount

    /**
     * 最大成员数量
     */
    public val maxMembers: Int
        get() = source.maxMembers

    /**
     * 查询并获取当前bot在频道服务器中拥有的API权限集。
     */
    @STP
    public suspend fun permissions(): ApiPermissions

    //region channels
    /**
     * 得到此频道服务器下的所有子频道。
     *
     * _可以通过 [permissions] 手动检查是否存在 [GetGuildChannelListApi] 的权限。_
     *
     * 注意: [channels] 的结果集中会出现**所有类型**的子频道。
     *
     * 如果希望只获取可用于发送消息的文字频道类型，参考使用 [chatChannels]。
     *
     * @see channel
     * @see categories
     * @see chatChannels
     * @see forums
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    override val channels: Collectable<QGChannel>

    /**
     * 获取指定ID的子频道。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    @ST(blockingBaseName = "getChannel", blockingSuffix = "", asyncBaseName = "getChannel", reserveBaseName = "getChannel")
    override suspend fun channel(id: ID): QGChannel?

    /**
     * 得到当前频道服务器下的所有分类类型 [ChannelType.CATEGORY] 的频道。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    public val categories: Collectable<QGCategoryChannel>

    /**
     * 获取指定ID的子频道分类。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     * @throws IllegalStateException 当目标子频道的类型不属于 [分组类型][ChannelType.CATEGORY] 时
     *
     */
    @ST(blockingBaseName = "getCategory", blockingSuffix = "", asyncBaseName = "getCategory", reserveBaseName = "getCategory")
    public suspend fun category(id: ID): QGCategoryChannel?

    /**
     * 获取所有类型为 [ChannelType.TEXT] 的文字频道集。
     */
    override val chatChannels: Collectable<QGTextChannel>

    /**
     * 获取指定ID的文字频道。
     */
    @ST(blockingBaseName = "getChatChannel", blockingSuffix = "", asyncBaseName = "getChatChannel", reserveBaseName = "getChatChannel")
    override suspend fun chatChannel(id: ID): QGTextChannel?

    /**
     * 得到当前 guild 中的所有 **帖子类型** [ChannelType.FORUM] 的子频道实例。
     *
     * 得到的数据集是 [QGGuild.channels] 的子集。
     *
     * @see QGForumChannel
     *
     */
    public val forums: Collectable<QGForumChannel>

    /**
     * 根据ID寻找匹配的 **帖子类型** [ChannelType.FORUM] 的子频道
     *
     * @throws QQGuildApiException API请求过程中出现的异常
     * @throws IllegalStateException 当目标子频道类型不是 [ChannelType.FORUM] 时
     */
    @ST(blockingBaseName = "getForum", blockingSuffix = "", asyncBaseName = "getForum", reserveBaseName = "getForum")
    public suspend fun forum(id: ID): QGForumChannel?
    //endregion

    //region members
    /**
     * 获取指定成员的信息。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    @ST(blockingBaseName = "getMember", blockingSuffix = "", asyncBaseName = "getMember", reserveBaseName = "getMember")
    override suspend fun member(id: ID): QGMember?

    /**
     * 频道所有成员列表。
     *
     * _可以通过 [permissions] 手动检查是否存在 [GetGuildMemberListApi] 的权限。
     * 通常情况下此权限仅限私域类型的BOT。_
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    override val members: Collectable<QGMember>
        get() = members(batch = GetGuildMemberListApi.MAX_LIMIT)

    /**
     * 频道所有成员列表。
     *
     * _可以通过 [permissions] 手动检查是否存在 [GetGuildMemberListApi] 的权限。
     * 通常情况下此权限仅限私域类型的BOT。_
     *
     * @param batch 内部分页时每批次的查询数量。
     * 默认为 [GetGuildMemberListApi.MAX_LIMIT]
     *
     * @throws IllegalArgumentException 如果 [batch] <= 0
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    public fun members(batch: Int): Collectable<QGMember>

    /**
     * 将 bot 的信息作为 [QGMember] 查询
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    override suspend fun botAsMember(): QGMember
    //endregion

    /**
     * 当前频道中的角色。
     *
     * @throws QQGuildApiException 请求失败，例如没有权限
     */
    @ExperimentalQGApi
    override val roles: Collectable<QGGuildRole>

    /**
     * 得到一个角色构造器。
     */
    @ExperimentalQGApi
    public fun roleCreator(): QGRoleCreator
}

/**
 * 构造一个 [QGRole]。
 *
 * @see QGRoleCreator
 */
@ExperimentalQGApi
public suspend inline fun QGGuild.createRole(block: QGRoleCreator.() -> Unit): QGGuildRole =
    roleCreator().also(block).create()
