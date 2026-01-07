/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.event.internal

import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.component.kook.KookChatChannel
import love.forte.simbot.component.kook.KookGuild
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.component.kook.bot.internal.KookBotImpl
import love.forte.simbot.component.kook.event.*
import love.forte.simbot.component.kook.internal.KookGuildImpl
import love.forte.simbot.component.kook.internal.KookMemberImpl
import love.forte.simbot.kook.event.*

internal data class KookMemberExitedChannelEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: Event<ExitedChannelEventExtra>,
    private val _guild: KookGuildImpl,
    private val _channel: KookChatChannel,
    private val _member: KookMemberImpl,
    override val sourceEventRaw: String
) : KookMemberExitedChannelEvent() {
    override suspend fun content() = _member
    override suspend fun channel(): KookChatChannel = _channel
    override suspend fun source(): KookGuild = _guild
}


internal data class KookMemberJoinedChannelEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: Event<JoinedChannelEventExtra>,
    private val _guild: KookGuildImpl,
    private val _channel: KookChatChannel,
    private val _member: KookMemberImpl,
    override val sourceEventRaw: String
) : KookMemberJoinedChannelEvent() {
    override suspend fun content(): KookMember = _member
    override suspend fun channel(): KookChatChannel = _channel
    override suspend fun source(): KookGuild = _guild
}


internal data class KookMemberExitedGuildEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: Event<ExitedGuildEventExtra>,
    private val _guild: KookGuildImpl,
    private val _member: KookMemberImpl,
    override val sourceEventRaw: String
) : KookMemberExitedGuildEvent() {
    override suspend fun content() = _guild
    override suspend fun member() = _member
}


internal data class KookMemberJoinedGuildEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: Event<JoinedGuildEventExtra>,
    private val _guild: KookGuildImpl,
    private val _member: KookMemberImpl,
    override val sourceEventRaw: String
) : KookMemberJoinedGuildEvent() {
    override suspend fun content() = _guild
    override suspend fun member() = _member
}


internal data class KookBotSelfExitedGuildEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: Event<SelfExitedGuildEventExtra>,
    private val _guild: KookGuildImpl,
    override val sourceEventRaw: String
) : KookBotSelfExitedGuildEvent() {
    override suspend fun content(): KookGuild = _guild
}


internal class KookBotSelfJoinedGuildEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: Event<SelfJoinedGuildEventExtra>,
    private val _guild: KookGuildImpl,
    override val sourceEventRaw: String
) : KookBotSelfJoinedGuildEvent() {
    override suspend fun content(): KookGuild = _guild
}


internal data class KookMemberOnlineEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: Event<GuildMemberOnlineEventExtra>,
    override val sourceEventRaw: String
) : KookMemberOnlineEvent() {
    override val guilds: Collectable<KookGuildImpl>
        get() = sourceBody.guilds.asSequence().map { bot.internalGuild(it) }.filterNotNull().asCollectable()
}

internal data class KookMemberOfflineEventImpl(
    override val bot: KookBotImpl,
    override val sourceEvent: Event<GuildMemberOfflineEventExtra>,
    override val sourceEventRaw: String
) : KookMemberOfflineEvent() {
    override val guilds: Collectable<KookGuildImpl>
        get() = sourceBody.guilds.asSequence().map { bot.internalGuild(it) }.filterNotNull().asCollectable()
}
