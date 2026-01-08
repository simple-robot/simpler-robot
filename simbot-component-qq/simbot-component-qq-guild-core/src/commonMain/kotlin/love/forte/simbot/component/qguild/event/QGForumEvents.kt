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
import love.forte.simbot.component.qguild.forum.QGPost
import love.forte.simbot.component.qguild.forum.QGReply
import love.forte.simbot.component.qguild.forum.QGThread
import love.forte.simbot.component.qguild.guild.QGGuild
import love.forte.simbot.component.qguild.guild.QGMember
import love.forte.simbot.event.ChannelEvent
import love.forte.simbot.event.FuzzyEventTypeImplementation
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.QQGuildApiException
import love.forte.simbot.qguild.event.*
import love.forte.simbot.qguild.model.forum.*
import love.forte.simbot.suspendrunner.STP


/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 事件。
 *
 * Forums 相关的事件面向 **私域BOT**，请注意确认bot类型。
 *
 * 基于 API 模块中的 [ForumDispatch]。
 *
 * @see ForumDispatch
 */
@STP
@PrivateDomainOnly
@SubclassOptInRequired(FuzzyEventTypeImplementation::class)
public abstract class QGForumEvent : QGBotEvent<ForumSourceInfo>(), ChannelEvent {
    /**
     * 事件ID。是一个随机ID。
     */
    override val id: ID = UUID.random()

    @OptIn(ExperimentalSimbotAPI::class)
    override val time: Timestamp = Timestamp.now()

    /**
     * 频道ID
     *
     * @see OpenForumEventData.guildId
     */
    public open val guildId: ID
        get() = sourceEventEntity.guildId.ID

    /**
     * 子频道ID
     *
     * @see OpenForumEventData.channelId
     */
    public open val channelId: ID
        get() = sourceEventEntity.channelId.ID

    /**
     * 发布人ID
     *
     * @see OpenForumEventData.authorId
     */
    public open val authorId: ID
        get() = sourceEventEntity.authorId.ID

    /**
     * 得到本次事件 [guildId] 对应的频道。
     *
     * @throws QQGuildApiException API请求过程出现异常
     * @throws NoSuchElementException 对应子频道已不存在
     */
    override suspend fun source(): QGGuild =
        bot.guildRelation.guild(guildId)
            ?: throw NoSuchElementException("guild(id=$guildId)")

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
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的主题事件。
 *
 * 基于 API 模块中的 [ForumThreadDispatch]。
 *
 * @see ForumThreadDispatch
 */
@OptIn(FuzzyEventTypeImplementation::class)
@PrivateDomainOnly
public abstract class QGForumThreadEvent : QGForumEvent() {
    abstract override val sourceEventEntity: Thread

    /**
     * 得到此事件中基于 [sourceEventEntity] 的 [QGThread]。
     */
    public abstract val thread: QGThread
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的创建主题事件。
 *
 * 基于 API 模块中的 [ForumThreadCreate]。
 *
 * @see ForumThreadCreate
 */
@PrivateDomainOnly
public abstract class QGForumThreadCreateEvent : QGForumThreadEvent() {
    override fun toString(): String =
        "QGForumThreadCreateEvent(sourceEventEntity=$sourceEventEntity)"
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的更新主题事件。
 *
 * 基于 API 模块中的 [ForumThreadUpdate]。
 *
 * @see ForumThreadUpdate
 */
@PrivateDomainOnly
public abstract class QGForumThreadUpdateEvent : QGForumThreadEvent() {
    override fun toString(): String =
        "QGForumThreadUpdateEvent(sourceEventEntity=$sourceEventEntity)"
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的删除主题事件。
 *
 * 基于 API 模块中的 [ForumThreadDelete]。
 *
 * @see ForumThreadDelete
 */
@PrivateDomainOnly
public abstract class QGForumThreadDeleteEvent : QGForumThreadEvent() {
    override fun toString(): String =
        "QGForumThreadDeleteEvent(sourceEventEntity=$sourceEventEntity)"
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的帖子（评论）事件。
 *
 * 基于 API 模块中的 [ForumPostDispatch]。
 *
 * @see ForumPostDispatch
 */
@PrivateDomainOnly
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGForumPostEvent : QGForumEvent() {
    abstract override val sourceEventEntity: Post

    /**
     * 得到此事件中基于 [sourceEventEntity] 的 [QGPost]。
     */
    public abstract val post: QGPost
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的评论创建事件。
 *
 * 基于 API 模块中的 [ForumPostCreate]。
 *
 * @see ForumPostCreate
 */
@PrivateDomainOnly
public abstract class QGForumPostCreateEvent : QGForumPostEvent() {
    override fun toString(): String =
        "QGForumPostCreateEvent(sourceEventEntity=$sourceEventEntity)"
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的评论删除事件。
 *
 * 基于 API 模块中的 [ForumPostDelete]。
 *
 * @see ForumPostDelete
 */
@PrivateDomainOnly
public abstract class QGForumPostDeleteEvent : QGForumPostEvent() {
    override fun toString(): String =
        "QGForumPostDeleteEvent(sourceEventEntity=$sourceEventEntity)"
}


/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的评论回复事件。
 *
 * 基于 API 模块中的 [ForumReplyDispatch]。
 *
 * @see ForumReplyDispatch
 */
@PrivateDomainOnly
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGForumReplyEvent : QGForumEvent() {
    abstract override val sourceEventEntity: Reply

    /**
     * 得到此事件中基于 [sourceEventEntity] 的 [QGReply]。
     */
    public abstract val post: QGReply
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的回复创建事件。
 *
 * 基于 API 模块中的 [ForumReplyCreate]。
 *
 * @see ForumReplyCreate
 */
@PrivateDomainOnly
public abstract class QGForumReplyCreateEvent : QGForumReplyEvent() {
    override fun toString(): String =
        "QGForumReplyCreateEvent(sourceEventEntity=$sourceEventEntity)"
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的回复删除事件。
 *
 * 基于 API 模块中的 [ForumReplyDelete]。
 *
 * @see ForumReplyDelete
 */
@PrivateDomainOnly
public abstract class QGForumReplyDeleteEvent : QGForumReplyEvent() {
    override fun toString(): String =
        "QGForumReplyDeleteEvent(sourceEventEntity=$sourceEventEntity)"
}

/**
 * QQ频道的 [EventIntents.ForumsEvent][EventIntents.ForumsEvent] 中的帖子审核通过事件。
 *
 * 基于 API 模块中的 [ForumPublishAuditResult]。
 *
 * @see ForumPublishAuditResult
 *
 */
@PrivateDomainOnly
@OptIn(FuzzyEventTypeImplementation::class)
public abstract class QGForumPublishAuditResultEvent : QGForumEvent() {
    /**
     * 得到此事件中的源数据 [AuditResult]。
     */
    abstract override val sourceEventEntity: AuditResult

    override fun toString(): String =
        "QGForumPublishAuditResultEvent(sourceEventEntity=$sourceEventEntity)"
}
