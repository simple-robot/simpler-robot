/*
 *     Copyright (c) 2023-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.qguild.model.forum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.ApiModelConstructor

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
public data class Reply @ApiModelConstructor constructor(
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
public data class ReplyInfo @ApiModelConstructor constructor(
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

