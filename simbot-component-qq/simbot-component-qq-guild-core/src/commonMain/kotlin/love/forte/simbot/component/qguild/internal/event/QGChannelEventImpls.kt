/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.channel.QGTextChannel
import love.forte.simbot.component.qguild.event.QGChannelCreateEvent
import love.forte.simbot.component.qguild.event.QGChannelDeleteEvent
import love.forte.simbot.component.qguild.event.QGChannelUpdateEvent
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.channel.QGTextChannelImpl
import love.forte.simbot.qguild.event.EventChannel


internal class QGChannelCreateEventImpl(
    override val sourceEventRaw: String,
    override val sourceEventEntity: EventChannel,
    override val bot: QGBotImpl,
    private val _channel: QGTextChannelImpl,
) : QGChannelCreateEvent() {
    override val id: ID get() = tcgChannelModifyId(0, bot.id, sourceEventEntity.id, hashCode())
    override suspend fun channel(): QGTextChannel = _channel
    override suspend fun content(): QGGuild = with(sourceEventEntity) {
        bot.queryGuild(guildId) ?: throw NoSuchElementException("guild(id=${guildId})")
    }
}

internal class QGChannelUpdateEventImpl(
    override val sourceEventRaw: String,
    override val sourceEventEntity: EventChannel,
    override val bot: QGBotImpl,
    private val _channel: QGTextChannelImpl,
) : QGChannelUpdateEvent() {
    override val id: ID get() = tcgChannelModifyId(1, bot.id, sourceEventEntity.id, hashCode())
    override suspend fun content(): QGTextChannel = _channel
    override suspend fun source(): QGGuild = with(sourceEventEntity) {
        bot.queryGuild(guildId) ?: throw NoSuchElementException("guild(id=${guildId})")
    }
}

internal class QGChannelDeleteEventImpl(
    override val sourceEventRaw: String,
    override val sourceEventEntity: EventChannel,
    override val bot: QGBotImpl,
    private val _channel: QGTextChannelImpl
) : QGChannelDeleteEvent() {
    override val id: ID get() = tcgChannelModifyId(2, bot.id, sourceEventEntity.id, hashCode())
    override suspend fun channel(): QGTextChannel = _channel
    override suspend fun content(): QGGuild = with(sourceEventEntity) {
        bot.queryGuild(guildId) ?: throw NoSuchElementException("guild(id=${guildId})")
    }
}


private fun tcgChannelModifyId(t: Int, sourceBot: ID, sourceChannel: String, hash: Int): ID =
    "$t$sourceBot.$sourceChannel.$hash".ID
