package love.forte.simbot.definition

import kotlinx.coroutines.flow.Flow
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Limiter
import love.forte.simbot.Timestamp
import love.forte.simbot.utils.runInBlocking
import java.util.stream.Stream



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


    //region members
    override suspend fun members(groupingId: ID?, limiter: Limiter): Flow<GuildMember>

    @Api4J
    override fun getMembers(groupingId: ID?, limiter: Limiter): Stream<out GuildMember>

    @Api4J
    override fun getMembers(groupingId: ID?): Stream<out GuildMember> = getMembers(groupingId, Limiter)

    @Api4J
    override fun getMembers(limiter: Limiter): Stream<out GuildMember> = getMembers(null, limiter)

    @Api4J
    override fun getMembers(): Stream<out GuildMember> = getMembers(null, Limiter)
    //endregion

    //region member
    override suspend fun member(id: ID): GuildMember?

    @Api4J
    override fun getMember(id: ID): GuildMember? = runInBlocking { member(id) }
    //endregion
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