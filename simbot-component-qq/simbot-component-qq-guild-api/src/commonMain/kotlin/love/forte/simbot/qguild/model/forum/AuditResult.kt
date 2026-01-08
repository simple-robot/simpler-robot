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
 *
 * 论坛帖子审核结果事件
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public data class AuditResult(
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
     * AuditType审核的类型
     *
     * @see AuditTypes
     *
     * _uint32_
     */
    val type: Int,
    /**
     * 审核结果. 0:成功 1:失败
     * _uint32_
     */
    val result: Int,
    /**
     * result不为0时错误信息
     */
    @SerialName("err_msg")
    val errMsg: String,
) : ForumSourceInfo

/**
 * 部分审核类型的常量。
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 * @author ForteScarlet
 */
public object AuditTypes {
    /**
     * 帖子
     */
    public const val PUBLISH_THREAD: Int = 1

    /**
     * 评论
     */
    public const val PUBLISH_POST: Int = 2

    /**
     * 回复
     */
    public const val PUBLISH_REPLY: Int = 3
}
