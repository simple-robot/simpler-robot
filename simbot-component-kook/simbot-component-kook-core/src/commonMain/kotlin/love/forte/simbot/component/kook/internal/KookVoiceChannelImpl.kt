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

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.mapNotNull
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.flowCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.KookVoiceChannel
import love.forte.simbot.component.kook.KookVoiceMember
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.util.requestData
import love.forte.simbot.kook.api.channel.ChannelKickoutApi
import love.forte.simbot.kook.api.channel.ChannelMoveUserApi
import love.forte.simbot.kook.api.channel.GetChannelUserListApi
import love.forte.simbot.kook.objects.Channel
import love.forte.simbot.logger.LoggerFactory
import kotlin.coroutines.CoroutineContext

/**
 *
 * @since 4.2.0
 * @author ForteScarlet
 */
internal class KookVoiceChannelImpl(
    bot: KookBotImpl,
    source: Channel,
) : AbstractKookChatCapableChannelImpl(bot, source),
    KookVoiceChannel {
    companion object {
        private val logger = LoggerFactory.getLogger("love.forte.simbot.component.kook.KookVoiceChannel")
    }

    override val coroutineContext: CoroutineContext
        get() = bot.subContext

    override suspend fun kickoutMember(targetMember: ID) {
        val api = ChannelKickoutApi.create(source.id, targetMember.literal)
        bot.requestData(api)
    }

    override val members: Collectable<KookVoiceMember>
        get() = flowCollectable {
            val api = GetChannelUserListApi.create(source.id)
            val userList = bot.requestData(api)
            val bot = this@KookVoiceChannelImpl.bot

            emitAll(userList.asFlow().mapNotNull {
                val delegateMember = bot.internalMember(guildId = source.guildId, userId = it.id)
                logger.trace("Processing voice channel user-list(id={})'s delegate member: {}", it.id, delegateMember)
                if (delegateMember == null) {
                    logger.warn("Delegate member for voice channel's user({}) is null.", it)
                    return@mapNotNull null
                }
                it.toVoiceMember(bot, source.id, delegateMember)
            })
        }

    override suspend fun moveMember(
        targetChannel: ID,
        targetMembers: Iterable<ID>
    ) {
        val api = ChannelMoveUserApi.create(targetChannel.literal, targetMembers.map { it.literal })
        bot.requestData(api)
    }

    override fun toString(): String {
        return "KookVoiceChannel(id=${source.id}, name=${source.name}, guildId=${source.guildId})"
    }
}

internal fun Channel.toVoiceChannel(bot: KookBotImpl): KookVoiceChannelImpl {
    return KookVoiceChannelImpl(bot, this)
}
