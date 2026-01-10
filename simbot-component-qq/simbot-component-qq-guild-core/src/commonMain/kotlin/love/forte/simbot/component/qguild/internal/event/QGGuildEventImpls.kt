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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.event.QGGuildCreateEvent
import love.forte.simbot.component.qguild.event.QGGuildDeleteEvent
import love.forte.simbot.component.qguild.event.QGGuildUpdateEvent
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl
import love.forte.simbot.qguild.event.EventGuild

internal class QGGuildCreateEventImpl(
    override val sourceEventRaw: String,
    override val sourceEventEntity: EventGuild,
    override val bot: QGBotImpl,
    private val guildValue: QGGuildImpl,
) : QGGuildCreateEvent() {
    override val id: ID get() = tcgGuildModifyId(0, bot.id, sourceEventEntity.id, hashCode())
    override suspend fun content(): QGGuild = guildValue
}


internal class QGGuildUpdateEventImpl(
    override val sourceEventRaw: String,
    override val sourceEventEntity: EventGuild,
    override val bot: QGBotImpl,
    private val guildValue: QGGuildImpl,
) : QGGuildUpdateEvent() {
    override val id: ID get() = tcgGuildModifyId(1, bot.id, sourceEventEntity.id, hashCode())
    override suspend fun content(): QGGuild = guildValue
}


internal class QGGuildDeleteEventImpl(
    override val sourceEventRaw: String,
    override val sourceEventEntity: EventGuild,
    override val bot: QGBotImpl,
    private val guildValue: QGGuildImpl?,
) : QGGuildDeleteEvent() {
    override val id: ID get() = tcgGuildModifyId(2, bot.id, sourceEventEntity.id, hashCode())

    @FragileSimbotAPI
    override val guild: QGGuild? get() = guildValue
}


private fun tcgGuildModifyId(t: Int, sourceBot: ID, sourceGuild: String, hash: Int): ID =
    "$t$sourceBot.$sourceGuild.$hash".ID
