/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.definition

import love.forte.simbot.ID
import love.forte.simbot.JST
import love.forte.simbot.JSTP
import love.forte.simbot.Timestamp
import love.forte.simbot.utils.item.Items

/**
 * 一个频道服务器，或者说一个集会。
 *
 * 目前来看，大部分 guild 其本身是无法发送消息进行交流的。
 */
public interface Guild : Organization, GuildInfo {
    override val bot: GuildBot
    override val id: ID
    override val name: String
    override val icon: String
    override val description: String
    override val createTime: Timestamp
    override val ownerId: ID
    
    @JSTP
    override suspend fun owner(): GuildMember
    override val maximumMember: Int
    override val currentMember: Int
    
    
    /**
     * 得到当前频道服务器中的成员列表序列。
     */
    override val members: Items<GuildMember>
    
    
    /**
     * 根据ID查询指定的成员对象。
     */
    @JST(blockingBaseName = "getMember", blockingSuffix = "", asyncBaseName = "getMember")
    override suspend fun member(id: ID): GuildMember?
    
    /**
     * 频道服务器的子集为 [子频道][Channel] 序列。
     */
    public val channels: Items<Channel>
    
    /**
     * 尝试根据指定ID获取匹配的[子频道][Channel]。
     *
     * 未找到时得到null。
     */
    @JST(blockingBaseName = "getChannel", blockingSuffix = "", asyncBaseName = "getChannel")
    public suspend fun channel(id: ID): Channel?
    
    
    /**
     * 频道服务器的子集为 [子频道][Channel] 序列。
     *
     * @see channels
     */
    override val children: Items<Channel>
    
    /**
     * 尝试根据指定ID获取匹配的[子频道][Channel]。
     *
     * 未找到时得到null。
     *
     * @see channel
     */
    @JST(blockingBaseName = "getChild", blockingSuffix = "", asyncBaseName = "getChild")
    override suspend fun child(id: ID): Channel?
}


/**
 * 频道服务器信息。
 */
public interface GuildInfo : OrganizationInfo {
    
    // 下述内容均不保证能够获取
    
    /**
     * 当前服务器内频道最大承载量。
     * 如果无法获取，得到-1.
     */
    public val maximumChannel: Int
    
    /**
     * 当前服务器内已存在频道数量。
     * 如果无法获取，得到-1.
     */
    public val currentChannel: Int
    
    
}
