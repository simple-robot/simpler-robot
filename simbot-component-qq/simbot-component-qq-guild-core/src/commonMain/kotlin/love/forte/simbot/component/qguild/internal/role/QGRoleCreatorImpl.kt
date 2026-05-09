/*
 *     Copyright (c) 2023-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.component.qguild.internal.role

import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl
import love.forte.simbot.component.qguild.role.QGGuildRole
import love.forte.simbot.component.qguild.role.QGRoleCreator
import love.forte.simbot.qguild.api.role.CreateGuildRoleApi
import love.forte.simbot.qguild.api.role.ModifyGuildRoleApi
import love.forte.simbot.qguild.stdlib.requestDataBy


/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalQGApi::class)
internal class QGRoleCreatorImpl(private val guild: QGGuildImpl) : QGRoleCreator {
    override var name: String? = null
    override var color: Int? = null
    override var isHoist: Boolean? = null

    override suspend fun create(): QGGuildRole {
        require(name != null || color != null || isHoist != null) {
            "No Parameters are set"
        }

        val hoist = isHoist?.let { if (it) 1 else 0 }
        val created = CreateGuildRoleApi.create(guild.id.literal, name, color, hoist)
            .requestDataBy(guild.bot.source)

        val role = created.role
            ?: ModifyGuildRoleApi.create(guild.id.literal, created.roleId, name, color, hoist)
                .requestDataBy(guild.bot.source).role

//            ?: GetGuildRoleListApi.create(guild.id.literal).requestBy(guild.baseBot)
//                .roles.find { it.id == created.roleId }!!


        return role.toGuildRole(
            bot = guild.bot,
            guildId = guild.id,
            sourceGuild = guild.bot.checkIfTransmitCacheable(guild)
        )
    }
}
