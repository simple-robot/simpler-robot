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
import love.forte.simbot.Bot


/**
 * 一个 [Bot] 在一个 [组织][Organization] 中所扮演的对象。
 *
 * [OrganizationBot] 实现 [Bot], 并对外提供转化为 [Member] 的能力, 以允许将自身转化为在目标组织中的成员。
 *
 * ```kotlin
 * // Kt
 * val member: Member = bot.asMember() // suspend
 * ```
 *
 * ```java
 * // Java
 * Member member = bot.toMember(); // blocking
 * ```
 *
 * @author ForteScarlet
 */
public interface OrganizationBot : Bot {
    
    /**
     * 将当前bot转化为此组织中的成员。
     */
    @JvmSynthetic
    public suspend fun asMember(): Member
    
    /**
     * 将当前bot转化为此组织中的成员。
     */
    @Api4J
    public fun toMember(): Member
    
}


/**
 * 一个 [Bot] 在一个 [群][Group] 中所扮演的对象。
 *
 * [GroupBot] 实现 [OrganizationBot], 并对外提供转化为 [Member] 的能力, 以允许将自身转化为在目标群中的成员。
 *
 * @see OrganizationBot
 * @see GroupMember
 * @author ForteScarlet
 */
public interface GroupBot : OrganizationBot {
    /**
     * 将当前bot转化为此组织中的成员。
     */
    @JvmSynthetic
    override suspend fun asMember(): GroupMember
    
    /**
     * 将当前bot转化为此组织中的成员。
     */
    @Api4J
    override fun toMember(): GroupMember
}


/**
 * 一个 [Bot] 在一个 [频道服务器][Guild] 中所扮演的对象。
 *
 * [GuildBot] 实现 [OrganizationBot], 并对外提供转化为 [Member] 的能力, 以允许将自身转化为在目标频道服务器中的成员。
 *
 * @see OrganizationBot
 * @see GuildMember
 * @author ForteScarlet
 */
public interface GuildBot : OrganizationBot {
    /**
     * 将当前bot转化为此组织中的成员。
     */
    @JvmSynthetic
    override suspend fun asMember(): GuildMember
    
    /**
     * 将当前bot转化为此组织中的成员。
     */
    @Api4J
    override fun toMember(): GuildMember
}