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

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.component.kook.KookVoiceMember
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.util.requestDataBy
import love.forte.simbot.kook.api.channel.ChannelKickoutApi
import love.forte.simbot.kook.api.channel.ChannelMoveUserApi
import love.forte.simbot.kook.api.channel.VoiceChannelUser

/**
 *
 * @author ForteScarlet
 */
internal class KookVoiceMemberImpl(
    private val bot: KookBotImpl,
    private val delegate: KookMember,
    private val voiceChannelId: String,
    private val voiceChannelUser: VoiceChannelUser
) : KookVoiceMember, KookMember by delegate {

    override suspend fun move(targetChannel: ID) {
        ChannelMoveUserApi.create(targetChannel.literal, voiceChannelUser.id).requestDataBy(bot)
    }

    override suspend fun kickout() {
        ChannelKickoutApi.create(voiceChannelId, voiceChannelUser.id).requestDataBy(bot)
    }
}


internal fun VoiceChannelUser.toVoiceMember(bot: KookBotImpl, voiceChannelId: String, delegate: KookMember): KookVoiceMemberImpl =
    KookVoiceMemberImpl(bot, delegate, voiceChannelId, this)
