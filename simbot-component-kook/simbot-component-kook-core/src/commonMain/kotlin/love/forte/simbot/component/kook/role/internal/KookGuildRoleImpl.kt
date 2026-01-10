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
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.internal.KookGuildImpl
import love.forte.simbot.component.kook.role.KookGuildRole
import love.forte.simbot.component.kook.role.KookGuildRoleUpdater
import love.forte.simbot.component.kook.role.KookMemberRole
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.component.kook.util.requestResultBy
import love.forte.simbot.kook.api.ApiResultException
import love.forte.simbot.kook.api.role.DeleteGuildRoleApi
import love.forte.simbot.kook.api.role.GrantGuildRoleApi
import love.forte.simbot.kook.api.role.UpdateGuildRoleApi
import love.forte.simbot.kook.objects.Permissions
import love.forte.simbot.kook.objects.Role
import kotlin.concurrent.Volatile

/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotAPI::class)
internal class KookGuildRoleImpl(
    private val baseBot: KookBotImpl,
    override val guild: KookGuildImpl,
    source: Role
) : KookGuildRole {
    @Volatile
    override var source: Role = source
        private set

    override fun toString(): String = "KookGuildRole(id=${source.roleId}, name=${source.name}, guildId=${guild.name})"

    /**
     * 赋予某用户角色权限
     */
    override suspend fun grantTo(memberId: ID): KookMemberRole {
        // 寻找此成员
        val guildId = guild.id
        val member = baseBot.internalMember(guild.source.id, memberId.literal)
            ?: throw NoSuchElementException("member(id=$memberId) in guild(id=$guildId)")

        return grantTo(member)
    }

    override suspend fun grantTo(member: KookMember): KookMemberRole {
        val guildId = guild.source.id

        GrantGuildRoleApi.create(
            guildId,
            member.source.id,
            source.roleId
        ).requestDataBy(baseBot)

        return KookMemberRoleImpl(baseBot, member, guildId, this)
    }

    /**
     * 删除当前频道服务器中的角色
     */
    override suspend fun delete(vararg options: DeleteOption) {
        val standardOptions = options.standardAnalysis()

        val api = DeleteGuildRoleApi.create(guildId = guild.source.id, roleId = source.roleId)
        val deleted = api.requestResultBy(baseBot)

        if (deleted.isHttpSuccess && deleted.isSuccess) {
            return
        }

        // 妹找着啊
        if (deleted.httpStatusCode == 404) {
            if (standardOptions.isIgnoreOnNoSuchTarget) {
                return
            }
            throw NoSuchElementException(
                "Role(id=${source.roleId}) in guild(id=${guild.source.id}): ${deleted.message}"
            )
        }

        if (standardOptions.isIgnoreOnFailure) {
            return
        }

        throw DeleteFailureException(
            "Delete Role(id=${source.roleId}) failed: ${deleted.message}",
            ApiResultException(deleted, deleted.message)
        )
    }

    override fun updater(): KookGuildRoleUpdater = KookGuildRoleUpdaterImpl(baseBot, guild.source.id, this)

    internal fun updateRole(role: Role) {
        source = role
    }
}


@OptIn(ExperimentalSimbotAPI::class)
private class KookGuildRoleUpdaterImpl(
    private val baseBot: KookBotImpl, private val guildId: String, private val role: KookGuildRoleImpl
) : KookGuildRoleUpdater {
    override var name: String? = null
    override var color: Int? = null
    override var isHoist: Boolean? = null
    override var isMentionable: Boolean? = null
    override var permissions: Permissions? = null

    override suspend fun update() {
        fun ifAllNull(vararg values: Any?): Boolean {
            return values.all { it == null }
        }
        if (ifAllNull(name, color, isHoist, isMentionable, permissions)) {
            // all null
            return
        }

        val updater = this

        val roleId = role.source.roleId
        val updated = UpdateGuildRoleApi.build(guildId, roleId) {
            name = updater.name
            color = updater.color
            isHoist = updater.isHoist
            isMentionable = updater.isMentionable
            permissions = updater.permissions
        }.requestDataBy(baseBot)

        role.updateRole(updated)
    }
}
