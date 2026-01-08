/*
 * Copyright (c) 2023-2025. ForteScarlet.
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

package love.forte.simbot.component.qguild.event

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.common.time.Timestamp
import love.forte.simbot.component.qguild.channel.QGForumChannel
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.event.ChannelEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.event.*
import love.forte.simbot.suspendrunner.STP


/**
 * QQ频道的 [OpenForums][EventIntents.OpenForumsEvent] 事件。
 *
 * OpenForums 相关的事件面向公域BOT，但是没有任何可用的额外信息，基本上就是只包括了频道ID、创建人ID等id信息。
 *
 * 基于 API 模块中的 [OpenForumDispatch]。
 *
 * @see OpenForumDispatch
 */
@STP
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public abstract class QGOpenForumEvent : QGBotEvent<OpenForumEventData>(), ChannelEvent {
    override val id: ID = UUID.random()

    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 频道ID
     *
     * @see OpenForumEventData.guildId
     */
    public val guildId: ID
        get() = sourceEventEntity.guildId.ID

    /**
     * 子频道ID
     *
     * @see OpenForumEventData.channelId
     */
    public val channelId: ID
        get() = sourceEventEntity.channelId.ID

    /**
     * 发布人ID
     *
     * @see OpenForumEventData.authorId
     */
    public val authorId: ID
        get() = sourceEventEntity.authorId.ID

    /**
     * 得到本次事件 [guildId] 对应的频道。
     *
     * @throws QQGuildApiException API请求过程出现异常
     * @throws NoSuchElementException 对应子频道已不存在
     */
    override suspend fun source(): QGGuild =
        bot.guildRelation.guild(guildId) ?: throw NoSuchElementException("guild(id=$guildId)")

    /**
     * 得到本次事件 [channelId] 对应的论坛子频道。
     *
     * @throws QQGuildApiException API请求过程出现异常
     * @throws IllegalStateException 对应子频道已经不再是论坛类型
     * @throws NoSuchElementException 对应子频道已不存在
     */
    abstract override suspend fun content(): QGForumChannel

    /**
     * 得到本次事件 [authorId] 对应的频道中成员。
     *
     * @throws QQGuildApiException API请求过程出现异常
     * @throws NoSuchElementException 对应成员已不存在
     */
    public abstract suspend fun author(): QGMember
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的主题事件。
 *
 * 来自 API 模块的 [OpenForumThreadDispatch] 事件。
 *
 * @see OpenForumThreadDispatch
 * @see QGOpenForumEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGOpenForumThreadEvent : QGOpenForumEvent() {
    /**
     * API模块的原事件内容。
     */
    abstract override val sourceEventEntity: OpenForumThreadData
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的主题创建事件。
 *
 * 来自 API 模块的 [OpenForumThreadCreate] 事件。
 *
 * @see OpenForumThreadCreate
 * @see QGOpenForumThreadEvent
 */
public abstract class QGOpenForumThreadCreateEvent : QGOpenForumThreadEvent() {
    override fun toString(): String {
        return "QGOpenForumThreadCreateEvent(sourceEventEntity=$sourceEventEntity)"
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的主题更新事件。
 *
 * 来自 API 模块的 [OpenForumThreadUpdate] 事件。
 *
 * @see OpenForumThreadUpdate
 * @see QGOpenForumThreadEvent
 */
public abstract class QGOpenForumThreadUpdateEvent : QGOpenForumThreadEvent() {
    override fun toString(): String {
        return "QGOpenForumThreadUpdateEvent(sourceEventEntity=$sourceEventEntity)"
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的主题删除事件。
 *
 * 来自 API 模块的 [OpenForumThreadDelete] 事件。
 *
 * @see OpenForumThreadDelete
 * @see QGOpenForumThreadEvent
 */
public abstract class QGOpenForumThreadDeleteEvent : QGOpenForumThreadEvent() {
    override fun toString(): String {
        return "QGOpenForumThreadDeleteEvent(sourceEventEntity=$sourceEventEntity)"
    }
}


/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的帖子（评论）事件。
 *
 * 来自 API 模块的 [OpenForumPostDispatch] 事件。
 *
 * @see OpenForumPostDispatch
 * @see QGOpenForumEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGOpenForumPostEvent : QGOpenForumEvent() {
    /**
     * API模块的原事件内容。
     */
    abstract override val sourceEventEntity: OpenForumPostData
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的帖子（评论）创建事件。
 *
 * 来自 API 模块的 [OpenForumPostCreate] 事件。
 *
 * @see OpenForumPostCreate
 * @see QGOpenForumEvent
 */
public abstract class QGOpenForumPostCreateEvent : QGOpenForumPostEvent() {
    override fun toString(): String {
        return "QGOpenForumPostCreateEvent(sourceEventEntity=$sourceEventEntity)"
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的帖子（评论）删除事件。
 *
 * 来自 API 模块的 [OpenForumPostDelete] 事件。
 *
 * @see OpenForumPostDelete
 * @see QGOpenForumEvent
 */
public abstract class QGOpenForumPostDeleteEvent : QGOpenForumPostEvent() {
    override fun toString(): String {
        return "QGOpenForumPostDeleteEvent(sourceEventEntity=$sourceEventEntity)"
    }
}


/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的回复事件。
 *
 * 来自 API 模块的 [OpenForumReplyDispatch] 事件。
 *
 * @see OpenForumReplyDispatch
 * @see QGOpenForumEvent
 */
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGOpenForumReplyEvent : QGOpenForumEvent() {
    /**
     * API模块的原事件内容。
     */
    abstract override val sourceEventEntity: OpenForumReplyData
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的回复创建事件。
 *
 * 来自 API 模块的 [OpenForumReplyCreate] 事件。
 *
 * @see OpenForumReplyCreate
 * @see QGOpenForumEvent
 */
public abstract class QGOpenForumReplyCreateEvent : QGOpenForumReplyEvent() {
    override fun toString(): String {
        return "QGOpenForumReplyCreateEvent(sourceEventEntity=$sourceEventEntity)"
    }
}

/**
 * QQ频道 [OpenForums][EventIntents.OpenForumsEvent] 中的回复删除事件。
 *
 * 来自 API 模块的 [OpenForumReplyDelete] 事件。
 *
 * @see OpenForumReplyDelete
 * @see QGOpenForumEvent
 */
public abstract class QGOpenForumReplyDeleteEvent : QGOpenForumReplyEvent() {
    override fun toString(): String {
        return "QGOpenForumReplyDeleteEvent(sourceEventEntity=$sourceEventEntity)"
    }
}
