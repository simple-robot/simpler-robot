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

package love.forte.simbot.component.qguild.internal.channel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.qguild.channel.QGCategory
import love.forte.simbot.component.qguild.channel.QGChannel
import love.forte.simbot.component.qguild.channel.QGForumChannel
import love.forte.simbot.component.qguild.forum.QGThread
import love.forte.simbot.component.qguild.forum.QGThreadCreator
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.internal.forum.QGThreadCreatorImpl
import love.forte.simbot.component.qguild.internal.forum.QGThreadImpl
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.forum.GetThreadApi
import love.forte.simbot.qguild.api.forum.GetThreadListApi
import love.forte.simbot.qguild.ifNotFoundThenNull
import love.forte.simbot.qguild.model.Channel
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.forum.Thread
import love.forte.simbot.qguild.stdlib.requestDataBy
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGForumChannelImpl(
    private val bot: QGBotImpl,
    override val source: Channel,
    internal val sourceGuild: QGGuild?,
    category: QGCategory? = null
) : QGForumChannel {
    override val category: QGCategory = category ?: QGCategoryImpl(
        bot = bot,
        guildId = source.guildId.ID,
        id = source.parentId.ID,
        sourceGuild = sourceGuild,
    )

    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()

    private fun threadFlow(): Flow<Thread> = flow {
        GetThreadListApi.create(source.id).requestDataBy(bot.source).threads.forEach {
            emit(it)
        }
    }

    override val threads: Collectable<QGThread>
        get() = threadFlow().map { it.toQGThread() }.asCollectable()

    override suspend fun thread(id: ID): QGThread? {
        return try {
            GetThreadApi.create(source.id, id.literal).requestDataBy(bot.source).thread.toQGThread()
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNull()
        }
    }

    private fun Thread.toQGThread(): QGThread = QGThreadImpl(bot, this, this@QGForumChannelImpl)

    override fun threadCreator(): QGThreadCreator = QGThreadCreatorImpl(this, bot)

    override fun toString(): String {
        return "QGForumChannel(id=$id, name=$name, category=$category)"
    }
}

/**
 * @throws IllegalStateException Can't as [QGForumChannel]
 */
internal fun QGChannel.asForumChannel(source: Channel = this.source): QGForumChannel =
    this as? QGForumChannel
        ?: throw IllegalStateException("The type of channel(id=${source.id}, name=${source.name}) in guild(id=${source.guildId}) is not category (${ChannelType.CATEGORY}), but ${source.type}")

internal fun Channel.toForumChannel(
    bot: QGBotImpl,
    sourceGuild: QGGuild? = null,
    category: QGCategory? = null,
): QGForumChannelImpl {
    return QGForumChannelImpl(
        bot = bot,
        source = this,
        sourceGuild = sourceGuild,
        category = category
    )
}
