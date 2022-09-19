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

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.utils.item.Items


/**
 * 一个频道。
 *  @author ForteScarlet
 */
public interface Channel : ChatRoom, ChannelInfo {
    override val guildId: ID
    override val bot: GuildBot
    override val id: ID
    override val name: String
    override val icon: String
    override val description: String
    override val createTime: Timestamp
    override val ownerId: ID
    
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    override suspend fun owner(): GuildMember
    
    override val maximumMember: Int
    override val currentMember: Int
    
    
    /**
     * 得到这个频道对应的guild。
     */
    @JvmBlocking(asProperty = true, suffix = "")
    @JvmAsync(asProperty = true, suffix = "")
    public suspend fun guild(): Guild
    
    
    /**
     * 得到当前频道服务器中的成员列表。通常情况下，[Channel] 得到的成员列表与 [Guild] 得到的成员列表一致。
     */
    override val members: Items<GuildMember>
    
    /**
     * 根据ID查询指定成员。
     */
    @JvmBlocking(baseName = "getMember", suffix = "")
    @JvmAsync(baseName = "getMember", suffix = "")
    override suspend fun member(id: ID): GuildMember?
}


/**
 * 一个频道的信息。
 */
public interface ChannelInfo : OrganizationInfo {
    /**
     * 这个频道对应的guild的ID
     */
    public val guildId: ID
}