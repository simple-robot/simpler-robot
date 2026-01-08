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

import love.forte.simbot.component.qguild.channel.QGForumChannel
import love.forte.simbot.component.qguild.event.*
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.component.qguild.internal.bot.QGBotImpl
import love.forte.simbot.component.qguild.internal.bot.castChannel
import love.forte.simbot.qguild.event.OpenForumPostData
import love.forte.simbot.qguild.event.OpenForumReplyData
import love.forte.simbot.qguild.event.OpenForumThreadData
import love.forte.simbot.qguild.model.ChannelType
import love.forte.simbot.qguild.model.forum.ForumSourceInfo


internal suspend fun QGBotImpl.forumChannel(data: ForumSourceInfo): QGForumChannel {
    val channel = queryChannel(data.channelId, null)
        ?: throw NoSuchElementException("channel(id=${data.channelId})")

    return channel.castChannel<QGForumChannel> { ChannelType.FORUM }
}

internal suspend fun QGBotImpl.author(data: ForumSourceInfo): QGMember =
    queryMember(data.guildId, data.authorId, null)
        ?: throw NoSuchElementException("member(id=${data.authorId})")

internal class QGOpenForumThreadCreateEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: OpenForumThreadData,
) : QGOpenForumThreadCreateEvent() {
    override suspend fun content(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}

internal class QGOpenForumThreadUpdateEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: OpenForumThreadData,
) : QGOpenForumThreadUpdateEvent() {
    override suspend fun content(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}

internal class QGOpenForumThreadDeleteEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: OpenForumThreadData,
) : QGOpenForumThreadDeleteEvent() {
    override suspend fun content(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}


internal class QGOpenForumPostCreateEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: OpenForumPostData,
) : QGOpenForumPostCreateEvent() {
    override suspend fun content(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}

internal class QGOpenForumPostDeleteEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: OpenForumPostData,
) : QGOpenForumPostDeleteEvent() {
    override suspend fun content(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}

internal class QGOpenForumReplyCreateEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: OpenForumReplyData,
) : QGOpenForumReplyCreateEvent() {
    override suspend fun content(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}

internal class QGOpenForumReplyDeleteEventImpl(
    override val bot: QGBotImpl,
    override val sourceEventRaw: String,
    override val sourceEventEntity: OpenForumReplyData,
) : QGOpenForumReplyDeleteEvent() {
    override suspend fun content(): QGForumChannel = bot.forumChannel(sourceEventEntity)
    override suspend fun author(): QGMember = bot.author(sourceEventEntity)
}
