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

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.blacklist.KookBlacklistItem
import love.forte.simbot.component.kook.blacklist.KookGuildBlacklistOperator
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.component.kook.util.requestData
import love.forte.simbot.kook.api.ListData
import love.forte.simbot.kook.api.blacklist.CreateBlacklistApi
import love.forte.simbot.kook.api.blacklist.DeleteBlacklistApi
import love.forte.simbot.kook.api.blacklist.GetBlacklistListApi
import love.forte.simbot.kook.api.blacklist.createFlow

/**
 *
 * @author ForteScarlet
 */
internal class KookGuildBlacklistOperatorImpl(
    private val bot: KookBot,
    override val guildId: ID
) : KookGuildBlacklistOperator {
    override suspend fun list(
        page: Int?,
        size: Int?
    ): ListData<KookBlacklistItem> {
        val api = GetBlacklistListApi.create(guildId = guildId.literal, page = page, pageSize = size)
        val raw = bot.requestData(api)
        return ListData(
            raw.items.map { it.toKookBlacklistItem(guildId, this) },
            raw.meta,
            raw.sort
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun flow(batchSize: Int?): Flow<KookBlacklistItem> {
        return GetBlacklistListApi.createFlow { page ->
            val api = GetBlacklistListApi.create(guildId = guildId.literal, page = page, pageSize = batchSize)
            bot.requestData(api)
        }.flatMapConcat { it.items.asFlow() }
            .map { it.toKookBlacklistItem(guildId, this) }
    }

    override suspend fun add(targetId: ID, remark: String?, delMsgDays: Int?) {
        val api = CreateBlacklistApi.create(guildId.literal, targetId.literal, remark, delMsgDays)
        bot.requestData(api)
    }

    override suspend fun delete(targetId: ID, vararg options: DeleteOption) {
        val api = DeleteBlacklistApi.create(guildId.literal, targetId.literal)

        if (options.contains(StandardDeleteOption.IGNORE_ON_FAILURE)) {
            runCatching { bot.requestData(api) }
            // Log?
            return
        }

        bot.requestData(api)
    }
}
