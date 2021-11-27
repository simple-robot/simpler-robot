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
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.Bot
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.action.MuteAction


/**
 * 一个组织下的成员,
 */
public interface Member : User, MemberInfo, MuteAction {

    override val id: ID
    override val bot: Bot

    /**
     * 在客观条件允许的情况下，对其进行禁言。
     *
     * 此行为不会捕获异常。
     *
     */
    override suspend fun mute(): Boolean

    @JvmSynthetic
    public suspend fun roles(): Flow<Role>
    @Api4J
    public val roles: List<Role> get() = runBlocking { roles().toList() }

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