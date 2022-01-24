/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.definition

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import love.forte.simbot.Api4J
import love.forte.simbot.Bot
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.action.MuteSupport
import love.forte.simbot.utils.runInBlocking
import java.util.stream.Stream
import kotlin.time.Duration


/**
 * 一个组织下的成员.
 *
 * @see GuildMember
 * @see GroupMember
 */
public interface Member : User, MemberInfo, MuteSupport {

    override val id: ID
    override val bot: Bot

    /**
     * 这个成员所属的组织。一般来讲，一个 [Member] 实例不会同时存在于 [Group] 和 [Channel].
     */
    @JvmSynthetic
    public suspend fun organization(): Organization

    @Api4J
    public val organization: Organization
        get() = runInBlocking { organization() }

    /**
     * 在客观条件允许的情况下，对其进行禁言。
     * 此行为不会捕获异常。
     *
     */
    @JvmSynthetic
    override suspend fun mute(duration: Duration): Boolean

    /**
     * 当前群成员在其所属组织内所扮演/拥有的角色。
     */
    @JvmSynthetic
    public suspend fun roles(): Flow<Role>

    /**
     * 当前群成员在其所属组织内所扮演/拥有的角色。
     */
    @Api4J
    public val roles: Stream<out Role>


    /**
     * 判断当前成员是否拥有"管理者"的权限。
     *
     * @see Role.isAdmin
     */
    public suspend fun isAdmin(): Boolean = roles().firstOrNull { r -> r.isAdmin() } != null

    /**
     * 判断当前成员是否拥有"拥有者"的权限。
     *
     * @see Role.isOwner
     */
    public suspend fun isOwner(): Boolean = roles().firstOrNull { r -> r.isOwner() } != null

    /**
     * 判断当前成员是否拥有"管理者"的权限。
     *
     * @see Role.isAdmin
     */
    @Api4J
    public val isAdmin: Boolean
        get() = roles.anyMatch { r -> r.isAdmin }

    /**
     * 判断当前成员是否拥有"拥有者"的权限。
     *
     * @see Role.isOwner
     */
    @Api4J
    public val isOwner: Boolean
        get() = roles.anyMatch { r -> r.isOwner }

}


/**
 * 一个频道服务器下的成员。
 */
public interface GuildMember : Member {
    /**
     * 这个成员所属的频道服务器。
     */
    @JvmSynthetic
    public suspend fun guild(): Guild

    @Api4J
    public val guild: Guild
        get() = runInBlocking { guild() }


    @JvmSynthetic
    override suspend fun organization(): Guild = guild()

    @Api4J
    override val organization: Guild
        get() = guild
}


public interface GroupMember : Member {
    /**
     * 这个成员所属的群。
     */
    @JvmSynthetic
    public suspend fun group(): Group

    @Api4J
    public val group: Group
        get() = runInBlocking { group() }


    @JvmSynthetic
    override suspend fun organization(): Group = group()

    @Api4J
    override val organization: Group
        get() = group
}


/**
 * 一个成员信息。
 */
public interface MemberInfo : UserInfo {
    override val id: ID
    override val username: String
    override val avatar: String

    /**
     * 此成员在当前组织下的昵称。
     */
    public val nickname: String

    /**
     * 此成员加入当前组织的时间。
     *
     * _不被支持的可能性很大。_
     *
     */
    public val joinTime: Timestamp

}