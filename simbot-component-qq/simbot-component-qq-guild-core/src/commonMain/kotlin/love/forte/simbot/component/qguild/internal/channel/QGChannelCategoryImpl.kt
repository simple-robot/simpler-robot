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

package love.forte.simbot.component.qguild.internal.channel

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.channel.QGCategory
import love.forte.simbot.component.qguild.channel.QGCategoryChannel
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.qguild.model.Channel
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext


internal class QGCategoryImpl(
    private val bot: QGBotImpl,
    private val guildId: ID,
    override val id: ID,
    private val sourceGuild: QGGuild? = null,
    source: QGCategoryChannel? = null
) : QGCategory {
    @Volatile
    private lateinit var _channel: QGCategoryChannel

    init {
        source?.also { _channel = it }
    }

    private val initLock = Mutex()

    override suspend fun resolveToChannel(): QGCategoryChannel {
        return if (::_channel.isInitialized) _channel else initLock.withLock {
            if (::_channel.isInitialized) _channel else {
                guild().category(id)?.also {
                    _channel = it
                } ?: throw NoSuchElementException("category(id=$id)")
            }
        }
    }

    override suspend fun name(): String = resolveToChannel().name

    private suspend fun guild(): QGGuild =
        sourceGuild
            ?: bot.queryGuild(guildId.literal)
            ?: throw NoSuchElementException("guild(id=$guildId)")

    override fun toString(): String {
        return "QGCategory(id=$id, guildId=$guildId)"
    }
}

internal class QGCategoryChannelImpl(
    private val bot: QGBotImpl,
    override val source: Channel,
    private val sourceGuild: QGGuild? = null,
) : QGCategoryChannel {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()
    override val category: QGCategory
        get() = QGCategoryImpl(bot, source.guildId.ID, id, sourceGuild, this)

    override fun toString(): String =
        "QGCategoryChannel(id=${source.id}, name=${source.name}, guildId=${source.guildId})"
}

internal fun Channel.toCategoryChannel(
    bot: QGBotImpl,
    sourceGuild: QGGuild? = null
): QGCategoryChannelImpl = QGCategoryChannelImpl(
    bot = bot,
    source = this,
    sourceGuild = sourceGuild,
)
