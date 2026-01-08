/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.qguild.model.forum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel

/**
 * 话题频道对帖子回复或删除时生产该事件中包含该对象
 *
 * 话题频道内对帖子的评论称为回复
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 *
 * author ForteScarlet
 */
@ApiModel
@Serializable
public data class Reply(
    /**
     * 频道ID
     */
    @SerialName("guild_id")
    override val guildId: String,
    /**
     * 子频道ID
     */
    @SerialName("channel_id")
    override val channelId: String,
    /**
     * 作者ID
     */
    @SerialName("author_id")
    override val authorId: String,
    /**
     * 回复内容
     *
     * @see ReplyInfo
     */
    @SerialName("reply_info")
    val replyInfo: ReplyInfo,
) : ForumSourceInfo

/**
 * 回复事件包含的回复内容信息
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class ReplyInfo(
    /**
     * 主题ID
     */
    @SerialName("thread_id")
    val threadId: String,
    /**
     * 帖子ID
     */
    @SerialName("post_id")
    val postId: String,
    /**
     * 回复ID
     */
    @SerialName("reply_id")
    val replyId: String,
    /**
     * 回复内容
     */
    val content: String,
    /**
     * 回复时间
     */
    @SerialName("date_time")
    val dateTime: String,
)

