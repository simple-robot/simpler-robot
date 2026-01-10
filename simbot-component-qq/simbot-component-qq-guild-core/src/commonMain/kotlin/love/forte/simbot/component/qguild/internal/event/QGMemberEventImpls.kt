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

package love.forte.simbot.component.qguild.internal.event

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.event.QGMemberAddEvent
import love.forte.simbot.component.qguild.event.QGMemberRemoveEvent
import love.forte.simbot.component.qguild.event.QGMemberUpdateEvent
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl
import love.forte.simbot.component.qguild.internal.guild.QGMemberImpl
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.addStackTrace
import love.forte.simbot.qguild.event.EventMember
import love.forte.simbot.qguild.isUnauthorized


internal class QGMemberAddEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: EventMember,
    private val _member: QGMemberImpl,
) : QGMemberAddEvent() {
    override val id: ID get() = memberEventId(0, bot.id, sourceEventEntity.user.id, hashCode())
    override suspend fun member(): QGMemberImpl = _member

    override suspend fun operator(): QGMemberImpl? {
        return with(sourceEventEntity) {
            try {
                bot.queryMember(guildId, opUserId)
            } catch (apiEx: QQGuildApiException) {
                // process no auth
                if (apiEx.isUnauthorized) null else throw apiEx.addStackTrace { "QGMemberAddEvent.operator(opUserId=$opUserId)" }
            }
        }
    }

    override suspend fun content(): QGGuild {
        return with(sourceEventEntity) {
            bot.queryGuild(guildId) ?: throw NoSuchElementException("guild(id=$guildId)")
        }
    }
}

internal class QGMemberUpdateEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: EventMember,
    private val _member: QGMemberImpl,
) : QGMemberUpdateEvent() {
    override val id: ID get() = memberEventId(1, bot.id, sourceEventEntity.user.id, hashCode())

    override suspend fun source(): QGGuild {
        return with(sourceEventEntity) {
            bot.queryGuild(guildId) ?: throw NoSuchElementException("guild(id=$guildId)")
        }
    }

    override suspend fun operator(): QGMemberImpl? {
        return with(sourceEventEntity) {
            try {
                bot.queryMember(guildId, opUserId)
            } catch (apiEx: QQGuildApiException) {
                // process no auth
                if (apiEx.isUnauthorized) null else throw apiEx.addStackTrace { "QGMemberAddEvent.operator(opUserId=$opUserId)" }
            }
        }
    }

    override suspend fun content(): QGMember = _member
}

internal class QGMemberRemoveEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: EventMember,
    private val _member: QGMemberImpl,
) : QGMemberRemoveEvent() {
    override val id: ID get() = memberEventId(2, bot.id, sourceEventEntity.user.id, hashCode())
    override suspend fun member(): QGMemberImpl = _member

    override suspend fun operator(): QGMemberImpl? {
        return with(sourceEventEntity) {
            try {
                bot.queryMember(guildId, opUserId)
            } catch (apiEx: QQGuildApiException) {
                // process no auth
                if (apiEx.isUnauthorized) null else throw apiEx.addStackTrace { "QGMemberAddEvent.operator(opUserId=$opUserId)" }
            }
        }
    }

    override suspend fun content(): QGGuildImpl {
        return with(sourceEventEntity) {
            bot.queryGuild(guildId) ?: throw NoSuchElementException("guild(id=$guildId)")
        }
    }
}

private fun memberEventId(t: Int, sourceBot: ID, sourceUserId: String, hash: Int): ID =
    "$t$sourceBot.$sourceUserId.$hash".ID
