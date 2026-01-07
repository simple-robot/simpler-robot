/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.blacklist.internal

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.kook.blacklist.KookBlacklistItem
import love.forte.simbot.component.kook.blacklist.KookGuildBlacklistOperator
import love.forte.simbot.kook.api.blacklist.BlacklistItem
import love.forte.simbot.kook.objects.User

/**
 *
 * @author ForteScarlet
 */
internal class KookBlacklistItemImpl(
    private val source: BlacklistItem,
    override val guildId: ID,
    private val operator: KookGuildBlacklistOperator
) : KookBlacklistItem {
    override val userId: ID
        get() = source.userId.ID
    override val remark: String
        get() = source.remark
    override val userInfo: User
        get() = source.user

    override val createdTime: Timestamp = Timestamp.ofMilliseconds(source.createdTime)

    override suspend fun delete(vararg options: DeleteOption) {
        operator.delete(targetId = userId, options = options)
    }

    override fun toString(): String {
        return "KookBlacklistItemImpl(source=$source, guildId=$guildId, userId=$userId)"
    }
}

internal fun BlacklistItem.toKookBlacklistItem(guildId: ID, operator: KookGuildBlacklistOperator): KookBlacklistItem =
    KookBlacklistItemImpl(this, guildId, operator)
