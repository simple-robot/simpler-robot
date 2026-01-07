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

package love.forte.simbot.component.kook.role.internal

import love.forte.simbot.ability.DeleteFailureException
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption.Companion.standardAnalysis
import love.forte.simbot.ability.isIgnoreOnFailure
import love.forte.simbot.ability.isIgnoreOnNoSuchTarget
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.role.KookMemberRole
import love.forte.simbot.component.kook.util.requestResultBy
import love.forte.simbot.kook.api.ApiResultException
import love.forte.simbot.kook.api.role.RevokeGuildRoleApi
import love.forte.simbot.kook.objects.Role

/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotAPI::class)
internal class KookMemberRoleImpl(
    private val baseBot: KookBotImpl,
    override val member: KookMember,
    private val guildId: String,
    override val guildRole: KookGuildRoleImpl,
) : KookMemberRole {
    override val source: Role get() = guildRole.source

    override suspend fun delete(vararg options: DeleteOption) {
        val standardOptions = options.standardAnalysis()

        val api = RevokeGuildRoleApi.create(
            guildId = guildId,
            userId = member.source.id,
            roleId = source.roleId,
        )

        val deleted = api.requestResultBy(baseBot)

        if (deleted.isHttpSuccess && deleted.isSuccess) {
            return
        }

        if (deleted.httpStatusCode == 404) {
            if (standardOptions.isIgnoreOnNoSuchTarget) {
                return
            }

            throw NoSuchElementException("Role(id=${source.roleId}) in guild(id=$guildId)): ${deleted.message}")
        }


        if (standardOptions.isIgnoreOnFailure) {
            return
        }

        throw DeleteFailureException(
            "Delete Role(id=${source.roleId} in guild(id=$guildId)) failed: ${deleted.message}",
            ApiResultException(deleted, deleted.message)
        )
    }
}
