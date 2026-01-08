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

import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.ExperimentalQGApi
import love.forte.simbot.component.qguild.role.QGRoleUpdater
import love.forte.simbot.qguild.api.role.ModifyGuildRoleApi
import love.forte.simbot.qguild.stdlib.requestDataBy


/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalQGApi::class)
internal class QGRoleUpdaterImpl(private val role: BaseQGRole) : QGRoleUpdater {
    override var name: String? = null
    override var color: Int? = null

    // 0-否, 1-是
    override var isHoist: Boolean? = null

    override suspend fun update() {
        if (name == null && color == null && isHoist == null) {
            throw IllegalArgumentException("No parameters are set")
        }
        val modified = ModifyGuildRoleApi.create(
            role.guildId.literal,
            role.source.id,
            name,
            color,
            isHoist?.let { if (it) 1 else 0 })
            .requestDataBy(role.bot.source)

        // update value
        role.source = modified.role
    }

}
