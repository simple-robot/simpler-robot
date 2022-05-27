package love.forte.simbot.definition

import kotlinx.coroutines.flow.Flow
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Limiter
import love.forte.simbot.Timestamp
import love.forte.simbot.utils.runInBlocking
import java.util.stream.Stream

/**
 * 一个频道服务器，或者说一个集会。
 *
 * 目前来看，大部分 guild 其本身是无法发送消息进行交流的。
 */
public interface Guild : Organization, GuildInfo {
    override val bot: GuildMemberBot
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
    override fun getMember(id: ID): GuildMember? = runInBlocking { member(id) }
    //endregion

    //region children
    /**
     * 一个 Guild 的子集应当是一些频道.
     */
    override suspend fun children(groupingId: ID?, limiter: Limiter): Flow<Channel>

    override suspend fun children(groupingId: ID?): Flow<Channel> = children(groupingId, Limiter)

    @Api4J
    override fun getChildren(groupingId: ID?, limiter: Limiter): Stream<out Channel>

    @Api4J
    override fun getChildren(groupingId: ID?): Stream<out Channel> = getChildren(groupingId, Limiter)

    @Api4J
    override fun getChildren(): Stream<out Channel> = getChildren(null, Limiter)
    //endregion
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
