package love.forte.simbot.definition

import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.runInBlocking


/**
 * 一个频道。
 *  @author ForteScarlet
 */
public interface Channel : ChatRoom, ChannelInfo {
    override val guildId: ID
    override val bot: GuildMemberBot
    override val id: ID
    override val name: String
    override val icon: String
    override val description: String
    override val createTime: Timestamp
    override val ownerId: ID
    
    @JvmSynthetic
    override suspend fun owner(): Member
    
    @Api4J
    override val owner: GuildMember
    override val maximumMember: Int
    override val currentMember: Int
    
    
    /**
     * 得到这个频道对应的guild。
     */
    public suspend fun guild(): Guild
    
    @Api4J
    public val guild: Guild
        get() = runInBlocking { guild() }
    
    
    /**
     * 得到当前频道服务器中的成员列表。通常情况下，[Channel] 得到的成员列表与 [Guild] 得到的成员列表一致。
     */
    override val members: Items<GuildMember>
    
    /**
     * 根据ID查询指定成员。
     */
    @JvmSynthetic
    override suspend fun member(id: ID): GuildMember?
    
    /**
     * 根据ID查询指定成员。
     */
    @Api4J
    override fun getMember(id: ID): GuildMember? = runInBlocking { member(id) }
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