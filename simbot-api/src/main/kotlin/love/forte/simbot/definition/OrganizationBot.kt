/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.definition

import love.forte.simbot.JST
import love.forte.simbot.bot.Bot


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
    @JST(blockingBaseName = "toMember", blockingSuffix = "", asyncBaseName = "toMember")
    public suspend fun asMember(): Member
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
    @JST(blockingBaseName = "toMember", blockingSuffix = "", asyncBaseName = "toMember")
    override suspend fun asMember(): GroupMember
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
    @JST(blockingBaseName = "toMember", blockingSuffix = "", asyncBaseName = "toMember")
    override suspend fun asMember(): GuildMember
}
