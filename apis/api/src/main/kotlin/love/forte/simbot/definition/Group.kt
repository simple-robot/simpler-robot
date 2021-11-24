/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
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
import love.forte.simbot.Api4J
import love.forte.simbot.Grouping
import love.forte.simbot.ID


/**
 * 一个群。
 * @author ForteScarlet
 */
public interface Group : Organization {

    /**
     * 得到服务器信息.
     */
    override val info: GroupInfo

    /**
     * 一般来讲，群不存在子集。
     */
    override suspend fun children(grouping: Grouping): Flow<Organization> {
        return emptyFlow()
    }
}

/**
 * 群组信息。
 */
public interface GroupInfo : OrganizationInfo



/**
 * 一个频道服务器，或者说一个集会。
 */
public interface Guild : Organization {

    /**
     * 获取频道服务器信息
     */
    override suspend fun info(): GuildInfo
    override val info: GuildInfo get() = runBlocking { info() }


    /**
     * 一个 Guild 的子集应当是一些频道.
     */
    override suspend fun children(grouping: Grouping): Flow<Channel>

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
public interface Channel : Organization {

    /**
     * 频道信息。
     */
    override suspend fun info(): ChannelInfo
    override val info: ChannelInfo get() = runBlocking { info() }

    /**
     * 一般来讲，频道不存在子集。
     */
    override suspend fun children(grouping: Grouping): Flow<Organization> {
        return emptyFlow()
    }

}

/**
 * 一个频道的信息。
 */
public interface ChannelInfo : OrganizationInfo {
    /**
     * 这个频道对应的guild的ID
     */
    public val guildId: ID

    /**
     * 得到这个频道对应的guild。
     */
    public suspend fun guild(): Guild
    @Api4J
    public val guild: Guild get() = runBlocking { guild() }



}