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

import love.forte.simbot.ability.DeleteFailureException
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption.Companion.standardAnalysis
import love.forte.simbot.ability.StandardDeleteOption.IGNORE_ON_FAILURE
import love.forte.simbot.ability.StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET
import love.forte.simbot.ability.isIgnoreOnFailure
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.role.QGMemberRole
import love.forte.simbot.component.qguild.role.QGRoleUpdater
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import love.forte.simbot.qguild.api.role.RemoveMemberRoleApi
import love.forte.simbot.qguild.initCause0
import love.forte.simbot.qguild.isNotFound
import love.forte.simbot.qguild.model.Role
import love.forte.simbot.qguild.stdlib.requestDataBy


/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalQGApi::class)
internal class QGMemberRoleImpl(
    bot: QGBotImpl,
    override val guildRole: QGGuildRoleImpl,
    override val memberId: ID,
) : BaseQGRole(bot), QGMemberRole {

    override val guildId: ID
        get() = guildRole.guildId

    override var source: Role by guildRole::source

    override fun updater(): QGRoleUpdater = guildRole.updater()

    override suspend fun delete(vararg options: DeleteOption) {
        delete0(channelId = null, options = options)
    }

    override suspend fun delete(channelId: ID?, vararg options: DeleteOption) {
        delete0(channelId = channelId?.literal, options = options)
    }

    private suspend fun delete0(channelId: String?, vararg options: DeleteOption) {
        kotlin.runCatching {
            RemoveMemberRoleApi.create(guildId.literal, memberId.literal, id.literal, channelId)
                .requestDataBy(bot.source)
        }.onFailure { e ->
            val stdOpts = options.standardAnalysis()
            if (e is QQGuildApiException) {
                if (e.isNotFound) {
                    if (IGNORE_ON_NO_SUCH_TARGET !in stdOpts) {
                        throw NoSuchElementException().apply { initCause0(e.addStackTrace { "QGMemberRole.delete" }) }
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
}


internal fun QGGuildRoleImpl.toMemberRole(
    bot: QGBotImpl = this.bot,
    memberId: ID,
): QGMemberRoleImpl = QGMemberRoleImpl(
    bot = bot,
    guildRole = this,
    memberId = memberId
)

