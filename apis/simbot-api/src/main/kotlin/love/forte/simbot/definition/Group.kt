/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.definition

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import love.forte.simbot.*
import java.util.stream.Stream


/**
 * 群组信息。
 */
public interface GroupInfo : OrganizationInfo

/**
 * 一个群。
 * @author ForteScarlet
 */
public interface Group : ChatRoom, GroupInfo {

    override val bot: Bot
    override val id: ID
    override val name: String
    override val icon: String
    override val description: String
    override val createTime: Timestamp
    override val ownerId: ID
    override suspend fun owner(): Member
    @Api4J override val owner: Member
    override val maximumMember: Int
    override val currentMember: Int

    /**
     * 一般来讲，群不存在子集。
     */
    override suspend fun children(groupingId: ID?, limiter: Limiter): Flow<Organization> {
        return emptyFlow()
    }

    @Api4J
    override fun getChildren(groupingId: ID?, limiter: Limiter): Stream<Organization> = Stream.empty()
}




/**
 * 一个频道服务器，或者说一个集会。
 *
 * 目前来看，大部分 guild 其本身是无法发送消息进行交流的。
 */
public interface Guild : Organization, GuildInfo {
    override val bot: Bot
    override val id: ID
    override val name: String
    override val icon: String
    override val description: String
    override val createTime: Timestamp
    override val ownerId: ID
    override suspend fun owner(): Member
    @Api4J override val owner: Member
    override val maximumMember: Int
    override val currentMember: Int


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

/**
 * 一个频道。
 *  @author ForteScarlet
 */
public interface Channel : ChatRoom, ChannelInfo {
    override val guildId: ID
    override val bot: Bot
    override val id: ID
    override val name: String
    override val icon: String
    override val description: String
    override val createTime: Timestamp
    override val ownerId: ID
    override suspend fun owner(): Member
    @Api4J override val owner: Member
    override val maximumMember: Int
    override val currentMember: Int


    /**
     * 得到这个频道对应的guild。
     */
    public suspend fun guild(): Guild
    @Api4J
    public val guild: Guild get() = runBlocking { guild() }
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