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

package love.forte.simbot.qguild.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.PrivateDomainOnly
import love.forte.simbot.qguild.model.forum.*


/**
 * [论坛事件(ForumEvent)](https://bot.q.qq.com/wiki/develop/api/gateway/forum.html#forum-event-intents-forum-event)
 *
 * ### 发送时机
 * 用户在话题子频道内发帖、评论、回复评论时产生该事件
 *
 * ### 主题事件
 * - FORUM_THREAD_CREATE
 * - FORUM_THREAD_UPDATE
 * - FORUM_THREAD_DELETE
 *
 * 事件内容为 [Thread] 对象
 *
 * ### 帖子事件
 * - FORUM_POST_CREATE
 * - FORUM_POST_DELETE
 *
 * 事件内容为 [Post] 对象
 *
 * ### 回复事件
 * - FORUM_REPLY_CREATE
 * - FORUM_REPLY_DELETE
 *
 * 事件内容为 [Reply] 对象
 *
 * ### 帖子审核事件
 * - FORUM_PUBLISH_AUDIT_RESULT
 *
 * 事件内容为 [AuditResult] 对象
 *
 * @see EventIntents.ForumsEvent
 *
 * @see ForumThreadDispatch
 * @see ForumPostDispatch
 * @see ForumReplyDispatch
 * @see ForumPublishAuditResult
 *
 */
@PrivateDomainOnly
public sealed class ForumDispatch : Signal.Dispatch() {
    /**
     * 事件内容。
     *
     * 具体的事件内容类型由 [ForumDispatch] 的实现类型决定。
     *
     */
    public abstract override val data: ForumSourceInfo
}

/**
 * 论坛事件：主题事件
 *
 * @see ForumDispatch
 */
public sealed class ForumThreadDispatch : ForumDispatch() {
    /**
     * 事件内容。
     */
    abstract override val data: Thread
}

/**
 * 主题创建事件。
 *
 * @see ForumDispatch
 * @see ForumThreadDispatch
 */
@Serializable
@SerialName(EventIntents.ForumsEvent.FORUM_THREAD_CREATE_TYPE)
@DispatchTypeName(EventIntents.ForumsEvent.FORUM_THREAD_CREATE_TYPE)
public data class ForumThreadCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: Thread
) : ForumThreadDispatch()

/**
 * 主题更新事件。
 *
 * @see ForumDispatch
 * @see ForumThreadDispatch
 */
@Serializable
@SerialName(EventIntents.ForumsEvent.FORUM_THREAD_UPDATE_TYPE)
@DispatchTypeName(EventIntents.ForumsEvent.FORUM_THREAD_UPDATE_TYPE)
public data class ForumThreadUpdate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: Thread
) : ForumThreadDispatch()

/**
 * 主题删除事件。
 *
 * @see ForumDispatch
 * @see ForumThreadDispatch
 */
@Serializable
@SerialName(EventIntents.ForumsEvent.FORUM_THREAD_DELETE_TYPE)
@DispatchTypeName(EventIntents.ForumsEvent.FORUM_THREAD_DELETE_TYPE)
public data class ForumThreadDelete(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: Thread
) : ForumThreadDispatch()


/**
 * 论坛事件：帖子事件
 *
 * @see ForumDispatch
 */
public sealed class ForumPostDispatch : ForumDispatch() {
    /**
     * 事件内容。
     */
    abstract override val data: Post
}

/**
 * 帖子创建事件
 *
 * @see ForumDispatch
 * @see ForumPostDispatch
 */
@Serializable
@SerialName(EventIntents.ForumsEvent.FORUM_POST_CREATE_TYPE)
@DispatchTypeName(EventIntents.ForumsEvent.FORUM_POST_CREATE_TYPE)
public data class ForumPostCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: Post
) : ForumPostDispatch()

/**
 * 帖子删除事件
 *
 * @see ForumDispatch
 * @see ForumPostDispatch
 */
@Serializable
@SerialName(EventIntents.ForumsEvent.FORUM_POST_DELETE_TYPE)
@DispatchTypeName(EventIntents.ForumsEvent.FORUM_POST_DELETE_TYPE)
public data class ForumPostDelete(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: Post
) : ForumPostDispatch()


/**
 * 论坛事件：回复事件
 *
 * @see ForumDispatch
 */
public sealed class ForumReplyDispatch : ForumDispatch() {
    /**
     * 事件内容。
     */
    abstract override val data: Reply
}

/**
 * 回复创建事件
 *
 * @see ForumDispatch
 * @see ForumPostDispatch
 */
@Serializable
@SerialName(EventIntents.ForumsEvent.FORUM_REPLY_CREATE_TYPE)
@DispatchTypeName(EventIntents.ForumsEvent.FORUM_REPLY_CREATE_TYPE)
public data class ForumReplyCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: Reply
) : ForumReplyDispatch()

/**
 * 回复删除事件
 *
 * @see ForumDispatch
 * @see ForumPostDispatch
 */
@Serializable
@SerialName(EventIntents.ForumsEvent.FORUM_REPLY_DELETE_TYPE)
@DispatchTypeName(EventIntents.ForumsEvent.FORUM_REPLY_DELETE_TYPE)
public data class ForumReplyDelete(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: Reply
) : ForumReplyDispatch()

/**
 * 帖子审核事件
 *
 * @see ForumDispatch
 */
@Serializable
@SerialName(EventIntents.ForumsEvent.FORUM_PUBLISH_AUDIT_RESULT_TYPE)
@DispatchTypeName(EventIntents.ForumsEvent.FORUM_PUBLISH_AUDIT_RESULT_TYPE)
public data class ForumPublishAuditResult(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: AuditResult
) : ForumDispatch()
