/*
 * Copyright (c) 2023-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal.role

import io.ktor.util.internal.*
import kotlinx.coroutines.flow.map
import love.forte.simbot.ability.DeleteFailureException
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption.Companion.standardAnalysis
import love.forte.simbot.ability.StandardDeleteOption.IGNORE_ON_FAILURE
import love.forte.simbot.ability.StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET
import love.forte.simbot.ability.isIgnoreOnFailure
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl
import love.forte.simbot.component.qguild.internal.guild.QGMemberImpl
import love.forte.simbot.component.qguild.role.QGGuildRole
import love.forte.simbot.component.qguild.role.QGRoleUpdater
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.member.GetGuildRoleMemberListApi
import love.forte.simbot.qguild.api.member.createFlow
import love.forte.simbot.qguild.api.role.AddMemberRoleApi
import love.forte.simbot.qguild.api.role.DeleteGuildRoleApi
import love.forte.simbot.qguild.isNotFound
import love.forte.simbot.qguild.model.Role
import love.forte.simbot.qguild.stdlib.requestDataBy
import kotlin.concurrent.Volatile


/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalQGApi::class)
internal class QGGuildRoleImpl(
    bot: QGBotImpl,
    override val guildId: ID,
    @Volatile override var source: Role,
    private val sourceGuild: QGGuildImpl? = null
) : BaseQGRole(bot), QGGuildRole {
    override fun updater(): QGRoleUpdater = QGRoleUpdaterImpl(this)

    override suspend fun grantTo(memberId: ID): QGMemberRoleImpl = grantTo0(memberId, null)

    override suspend fun grantTo(member: QGMember): QGMemberRoleImpl = grantTo0(member, null)

    override suspend fun grantTo(memberId: ID, channelId: ID): QGMemberRoleImpl = grantTo0(memberId, channelId.literal)

    override suspend fun grantTo(member: QGMember, channelId: ID): QGMemberRoleImpl =
        grantTo0(member, channelId.literal)

    private suspend fun grantTo0(memberId: ID, channelId: String?): QGMemberRoleImpl {
        AddMemberRoleApi.create(guildId.literal, memberId.literal, id.literal, channelId).requestDataBy(bot.source)
        return QGMemberRoleImpl(bot, this, memberId)
    }

    private suspend fun grantTo0(member: QGMember, channelId: String?): QGMemberRoleImpl {
        AddMemberRoleApi.create(guildId.literal, member.id.literal, id.literal, channelId).requestDataBy(bot.source)
        return QGMemberRoleImpl(bot, this, member.id)
    }

    override suspend fun delete(vararg options: DeleteOption) {
        val stdOpts = options.standardAnalysis()
        kotlin.runCatching {
            DeleteGuildRoleApi.create(guildId.literal, source.id).requestDataBy(bot.source)
        }.onFailure { e ->
            if (e is QQGuildApiException) {
                if (e.isNotFound) {
                    if (IGNORE_ON_NO_SUCH_TARGET !in stdOpts) {
                        throw NoSuchElementException().also { it.initCauseBridge(e) }
                    }
                    return
                }

                if (IGNORE_ON_FAILURE !in stdOpts) {
                    throw DeleteFailureException(e)
                }
                return
            }

            if (!stdOpts.isIgnoreOnFailure) {
                throw e
            }
        }
    }

    override fun members(batch: Int): Collectable<QGMember> {
        require(batch > 0) { "'batch' must be > 0, but: $batch" }
        val guildId = guildId
        val guildIdLiteral = guildId.literal
        val roleId = source.id

        return GetGuildRoleMemberListApi.createFlow(
            guildId = guildIdLiteral, roleId = roleId, batch = batch
        ) { requestDataBy(bot.source) }
            .map {
                QGMemberImpl(
                    bot = bot, source = it, guildId = guildId, sourceGuild = this.sourceGuild
                )
            }.asCollectable()
    }
}


internal fun Role.toGuildRole(
    bot: QGBotImpl,
    guildId: ID,
    sourceGuild: QGGuildImpl? = null
): QGGuildRoleImpl = QGGuildRoleImpl(
    bot = bot,
    guildId = guildId,
    source = this,
    sourceGuild = sourceGuild
)
