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

package love.forte.simbot.component.kook.internal

import love.forte.simbot.component.kook.KookChatChannel
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.kook.objects.Channel
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class KookChatChannelImpl(
    bot: KookBotImpl,
    source: Channel,
) : AbstractKookChatCapableChannelImpl(bot, source),
    KookChatChannel {
    override val coroutineContext: CoroutineContext
        get() = bot.subContext

    override fun toString(): String {
        return "KookChatChannel(id=${source.id}, name=${source.name}, guildId=${source.guildId})"
    }
}

internal fun Channel.toSimpleChatChannel(bot: KookBotImpl): KookChatChannelImpl {
    return KookChatChannelImpl(bot, this)
}
