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

package love.forte.simbot.component.qguild.internal.forum

import love.forte.simbot.component.qguild.forum.QGPost
import love.forte.simbot.component.qguild.forum.QGThread
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.channel.QGForumChannelImpl
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.forum.GetThreadApi
import love.forte.simbot.qguild.ifNotFoundThenNoSuch
import love.forte.simbot.qguild.model.forum.Post
import love.forte.simbot.qguild.stdlib.requestDataBy

/**
 *
 * @author ForteScarlet
 */
internal class QGPostImpl(
    private val bot: QGBotImpl,
    override val source: Post,
    private val sourceChannel: QGForumChannelImpl?
) : QGPost {
    override suspend fun author(): QGMember =
        bot.queryMember(source.guildId, source.authorId, sourceChannel?.sourceGuild as? QGGuildImpl)
            ?: throw NoSuchElementException("author(id=$authorId)")

    override suspend fun thread(): QGThread {
        val (thread) = try {
            GetThreadApi.create(source.channelId, source.postInfo.threadId).requestDataBy(bot.source)
        } catch (apiEx: QQGuildApiException) {
            apiEx.ifNotFoundThenNoSuch { "thread(id=${source.postInfo.threadId}) by post(id=${source.postInfo.postId})" }
        }

        return QGThreadImpl(bot, thread, sourceChannel)
    }
}
