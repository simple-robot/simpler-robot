/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.definition

import love.forte.simbot.Api4J
import love.forte.simbot.ID
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
    override suspend fun owner(): Member
    
    @Api4J
    override val owner: Member
    override val maximumMember: Int
    override val currentMember: Int
    
    
    /**
     * 得到当前频道服务器中的成员列表序列。
     */
    override val members: Items<GuildMember>
    
    
    /**
     * 根据ID查询指定的成员对象。
     */
    @JvmSynthetic
    override suspend fun member(id: ID): GuildMember?
    
    /**
     * 根据ID查询指定的成员对象。
     */
    @Api4J
    override fun getMember(id: ID): GuildMember?
    
    /**
     * 频道服务器的子集为 [子频道][Channel] 序列。
     */
    public val channels: Items<Channel>
    
    /**
     * 尝试根据指定ID获取匹配的[子频道][Channel]。
     *
     * 未找到时得到null。
     */
    @JvmSynthetic
    public suspend fun channel(id: ID): Channel?
    
    /**
     * 尝试根据指定ID阻塞的获取匹配的[子频道][Channel]。
     *
     * 未找到时得到null。
     */
    @Api4J
    public fun getChannel(id: ID): Channel?
    
    
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
    @JvmSynthetic
    override suspend fun child(id: ID): Channel?
    
    /**
     * 尝试根据指定ID阻塞的获取匹配的[子频道][Channel]。
     *
     * 未找到时得到null。
     *
     * @see getChannel
     */
    @Api4J
    override fun getChild(id: ID): Channel?
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
