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
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.model.forum.ForumSourceInfo

/**
 * [开放论坛事件(OpenForumEvent)](https://bot.q.qq.com/wiki/develop/api/gateway/open_forum.html) 相关的事件父类。
 *
 * ## 发送时机
 * 用户在话题子频道内发帖、评论、回复评论时产生该事件
 *
 *
 * ## 主题事件
 *
 * - OPEN_FORUM_THREAD_CREATE
 * - OPEN_FORUM_THREAD_UPDATE
 * - OPEN_FORUM_THREAD_DELETE
 *
 * 参考 [OpenForumThreadDispatch]
 *
 * ## 帖子（评论）事件
 * - OPEN_FORUM_POST_CREATE
 * - OPEN_FORUM_POST_DELETE
 *
 * 参考 [OpenForumPostDispatch]
 *
 * ## 回复事件
 * - OPEN_FORUM_REPLY_CREATE
 * - OPEN_FORUM_REPLY_DELETE
 *
 * 参考 [OpenForumReplyDispatch]
 *
 * @see EventIntents.OpenForumsEvent
 *
 * @see OpenForumThreadDispatch
 * @see OpenForumPostDispatch
 * @see OpenForumReplyDispatch
 *
 */
public sealed class OpenForumDispatch : Signal.Dispatch() {
    /**
     * 事件内容。
     */
    abstract override val data: OpenForumEventData
}

/**
 * 开放论坛事件中三种类型事件内信息的统一抽象。
 *
 * 截至最后一次落笔（2023/5/15），在文档中三种事件所表现的结构与字段是完全一样的：
 *
 * ```json
 * {
 *   "guild_id": "47129941624960822",
 *   "channel_id": "1661124",
 *   "author_id": "144115218182563108",
 * }
 * ```
 *
 * 但是既然他们文档中分了三段来说明，为了防止未来可能出现的突然变更（比如某一个类型中增加了新的属性），
 * 三个类型将会分别定义。
 *
 * @see OpenForumThreadData
 * @see OpenForumPostData
 * @see OpenForumReplyData
 *
 */
public sealed class OpenForumEventData : ForumSourceInfo {
    /**
     * 频道ID
     */
    public abstract override val guildId: String

    /**
     * 子频道ID
     */
    public abstract override val channelId: String

    /**
     * 发布人ID
     */
    public abstract override val authorId: String
}

/**
 * 开放论坛事件的 **_主题事件_**。
 */
public sealed class OpenForumThreadDispatch : OpenForumDispatch() {
    /**
     * 事件内容。
     */
    abstract override val data: OpenForumThreadData
}

/**
 * 主题事件：创建主题
 *
 * @see OpenForumDispatch
 */
@Serializable
@SerialName(EventIntents.OpenForumsEvent.OPEN_FORUM_THREAD_CREATE_TYPE)
@DispatchTypeName(EventIntents.OpenForumsEvent.OPEN_FORUM_THREAD_CREATE_TYPE)
public data class OpenForumThreadCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: OpenForumThreadData
) : OpenForumThreadDispatch()


/**
 * 主题事件：更新主题
 *
 * @see OpenForumDispatch
 */
@Serializable
@SerialName(EventIntents.OpenForumsEvent.OPEN_FORUM_THREAD_UPDATE_TYPE)
@DispatchTypeName(EventIntents.OpenForumsEvent.OPEN_FORUM_THREAD_UPDATE_TYPE)
public data class OpenForumThreadUpdate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: OpenForumThreadData
) : OpenForumThreadDispatch()


/**
 * 主题事件：删除主题
 *
 * @see OpenForumDispatch
 */
@Serializable
@SerialName(EventIntents.OpenForumsEvent.OPEN_FORUM_THREAD_DELETE_TYPE)
@DispatchTypeName(EventIntents.OpenForumsEvent.OPEN_FORUM_THREAD_DELETE_TYPE)
public data class OpenForumThreadDelete(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: OpenForumThreadData
) : OpenForumThreadDispatch()


/**
 *
 * 开放论坛事件中 **_主题事件_** 的内容。
 *
 * @see OpenForumEventData
 *
 */
@Serializable
@ApiModel
public data class OpenForumThreadData(
    @SerialName("guild_id") override val guildId: String,
    @SerialName("channel_id") override val channelId: String,
    @SerialName("author_id") override val authorId: String,
) : OpenForumEventData()


/**
 * 开放论坛事件的 **_帖子（评论）事件_**。
 */
public sealed class OpenForumPostDispatch : OpenForumDispatch() {
    /**
     * 事件内容。
     */
    abstract override val data: OpenForumPostData
}

/**
 * 帖子事件：创建帖子（评论）
 *
 * @see OpenForumDispatch
 */
@Serializable
@SerialName(EventIntents.OpenForumsEvent.OPEN_FORUM_POST_CREATE_TYPE)
@DispatchTypeName(EventIntents.OpenForumsEvent.OPEN_FORUM_POST_CREATE_TYPE)
public data class OpenForumPostCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: OpenForumPostData
) : OpenForumPostDispatch()


/**
 * 帖子事件：删除帖子（评论）
 *
 * @see OpenForumDispatch
 */
@Serializable
@SerialName(EventIntents.OpenForumsEvent.OPEN_FORUM_POST_DELETE_TYPE)
@DispatchTypeName(EventIntents.OpenForumsEvent.OPEN_FORUM_POST_DELETE_TYPE)
public data class OpenForumPostDelete(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: OpenForumPostData
) : OpenForumPostDispatch()


/**
 *
 * 开放论坛事件中 **_帖子（评论）事件_** 的内容。
 *
 * @see OpenForumEventData
 *
 */
@Serializable
@ApiModel
public data class OpenForumPostData(
    @SerialName("guild_id") override val guildId: String,
    @SerialName("channel_id") override val channelId: String,
    @SerialName("author_id") override val authorId: String,
) : OpenForumEventData()

/**
 * 开放论坛事件的 **_回复事件_**。
 */
public sealed class OpenForumReplyDispatch : OpenForumDispatch() {
    /**
     * 事件内容。
     */
    abstract override val data: OpenForumReplyData
}

/**
 * 回复事件：创建回复
 *
 * @see OpenForumDispatch
 */
@Serializable
@SerialName(EventIntents.OpenForumsEvent.OPEN_FORUM_REPLY_CREATE_TYPE)
@DispatchTypeName(EventIntents.OpenForumsEvent.OPEN_FORUM_REPLY_CREATE_TYPE)
public data class OpenForumReplyCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: OpenForumReplyData
) : OpenForumReplyDispatch()


/**
 * 回复事件：删除回复
 *
 * @see OpenForumDispatch
 */
@Serializable
@SerialName(EventIntents.OpenForumsEvent.OPEN_FORUM_REPLY_DELETE_TYPE)
@DispatchTypeName(EventIntents.OpenForumsEvent.OPEN_FORUM_REPLY_DELETE_TYPE)
public data class OpenForumReplyDelete(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d") override val data: OpenForumReplyData
) : OpenForumReplyDispatch()


/**
 *
 * 开放论坛事件中 **_回复事件_** 的内容。
 *
 * @see OpenForumEventData
 *
 */
@Serializable
@ApiModel
public data class OpenForumReplyData(
    @SerialName("guild_id") override val guildId: String,
    @SerialName("channel_id") override val channelId: String,
    @SerialName("author_id") override val authorId: String,
) : OpenForumEventData()
