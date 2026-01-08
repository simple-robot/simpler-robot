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

import love.forte.simbot.ability.DeleteFailureException
import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption.Companion.standardAnalysis
import love.forte.simbot.ability.StandardDeleteOption.IGNORE_ON_FAILURE
import love.forte.simbot.ability.StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET
import love.forte.simbot.ability.isIgnoreOnFailure
import love.forte.simbot.component.qguild.forum.QGThread
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.newSupervisorCoroutineContext
import love.forte.simbot.component.qguild.internal.channel.QGForumChannelImpl
import love.forte.simbot.component.qguild.internal.guild.QGGuildImpl
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.api.forum.DeleteThreadApi
import love.forte.simbot.qguild.initCause0
import love.forte.simbot.qguild.isNotFound
import love.forte.simbot.qguild.model.forum.Thread
import love.forte.simbot.qguild.stdlib.requestDataBy
import kotlin.coroutines.CoroutineContext


/**
 *
 * @author ForteScarlet
 */
internal class QGThreadImpl(
    private val bot: QGBotImpl,
    override val source: Thread,
    private val sourceChannel: QGForumChannelImpl?,
) : QGThread {
    override val coroutineContext: CoroutineContext = bot.newSupervisorCoroutineContext()

    override suspend fun author(): QGMember =
        bot.queryMember(source.guildId, source.authorId, sourceChannel?.sourceGuild as? QGGuildImpl)
            ?: throw NoSuchElementException("author(id=$authorId)")


    override suspend fun delete(vararg options: DeleteOption) {
        kotlin.runCatching {
            DeleteThreadApi.create(source.channelId, source.threadInfo.threadId).requestDataBy(bot.source)
        }.onFailure { e ->
            val stdOpts = options.standardAnalysis()
            if (e is QQGuildApiException) {
                if (e.isNotFound) {
                    if (IGNORE_ON_NO_SUCH_TARGET !in stdOpts) {
                        throw NoSuchElementException(e.message).apply { initCause0(e) }
                    }
                    return
                }

                if (IGNORE_ON_FAILURE !in stdOpts) {
                    throw DeleteFailureException(e)
                }

                return
            }

            if (!stdOpts.isIgnoreOnFailure) {
                throw e
            }
        }
    }

    override fun toString(): String {
        return "QGThreadImpl(id=${source.threadInfo.threadId}, title=$title, channelId=${source.channelId})"
    }
}
