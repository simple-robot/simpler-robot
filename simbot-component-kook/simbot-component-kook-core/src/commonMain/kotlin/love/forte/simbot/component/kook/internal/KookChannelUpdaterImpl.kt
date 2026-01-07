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

package love.forte.simbot.component.kook.internal

import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.KookChannel
import love.forte.simbot.component.kook.KookChannelUpdater
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.kook.api.channel.UpdateChannelApi
import love.forte.simbot.kook.api.channel.toChannel

/**
 * @author ForteScarlet
 */
internal class KookChannelUpdaterImpl(override val channel: KookChannel, private val bot: KookBotImpl) :
    KookChannelUpdater {
    override var builder: UpdateChannelApi.Builder = UpdateChannelApi.builder(channel.id.literal)

    override suspend fun execute(): KookChannel {
        val api = builder.build()
        val result = api.requestDataBy(channel.bot)
        val resultChannel = result.toChannel()

        val updatedChannel = if (result.isCategory) {
            resultChannel.toCategoryChannel(bot, resultChannel.toCategory(bot, null))
        } else {
            resultChannel.toChatChannel(bot, null)
        }

        return updatedChannel
    }
}
