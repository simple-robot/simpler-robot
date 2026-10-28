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
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities

/**
 * 话题频道对帖子回复或删除时生产该事件中包含该对象
 *
 * 话题频道内对帖子的评论称为回复
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 *
 * @property guildId 频道ID
 * @property channelId 子频道ID
 * @property authorId 作者ID
 * @property replyInfo 回复内容
 *
 * @see ReplyInfo
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class Reply @ApiModelConstructor constructor(
    @SerialName("guild_id")
    override val guildId: String,
    @SerialName("channel_id")
    override val channelId: String,
    @SerialName("author_id")
    override val authorId: String,
    @SerialName("reply_info")
    public val replyInfo: ReplyInfo,
) : ForumSourceInfo {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Reply) return false

        if (guildId != other.guildId) return false
        if (channelId != other.channelId) return false
        if (authorId != other.authorId) return false
        if (replyInfo != other.replyInfo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + authorId.hashCode()
        result = 31 * result + replyInfo.hashCode()
        return result
    }

    override fun toString(): String {
        return "Reply(guildId='$guildId', channelId='$channelId', authorId='$authorId', replyInfo=$replyInfo)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component1(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component2(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("authorId"))
    public operator fun component3(): String = authorId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("replyInfo"))
    public operator fun component4(): ReplyInfo = replyInfo

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        guildId: String = this.guildId,
        channelId: String = this.channelId,
        authorId: String = this.authorId,
        replyInfo: ReplyInfo = this.replyInfo,
    ): Reply = Reply(
        guildId = guildId,
        channelId = channelId,
        authorId = authorId,
        replyInfo = replyInfo,
    )
    //endregion
}

/**
 * 回复事件包含的回复内容信息
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 *
 * @property threadId 主题ID
 * @property postId 帖子ID
 * @property replyId 回复ID
 * @property content 回复内容
 * @property dateTime 回复时间, ISO8601 timestamp
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class ReplyInfo @ApiModelConstructor constructor(
    @SerialName("thread_id")
    public val threadId: String,
    @SerialName("post_id")
    public val postId: String,
    @SerialName("reply_id")
    public val replyId: String,
    public val content: String,
    @SerialName("date_time")
    public val dateTime: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReplyInfo) return false

        if (threadId != other.threadId) return false
        if (postId != other.postId) return false
        if (replyId != other.replyId) return false
        if (content != other.content) return false
        if (dateTime != other.dateTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = threadId.hashCode()
        result = 31 * result + postId.hashCode()
        result = 31 * result + replyId.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + dateTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "ReplyInfo(" +
            "threadId='$threadId', " +
            "postId='$postId', " +
            "replyId='$replyId', " +
            "content='$content', " +
            "dateTime='$dateTime')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("threadId"))
    public operator fun component1(): String = threadId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("postId"))
    public operator fun component2(): String = postId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("replyId"))
    public operator fun component3(): String = replyId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("content"))
    public operator fun component4(): String = content

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("dateTime"))
    public operator fun component5(): String = dateTime

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        threadId: String = this.threadId,
        postId: String = this.postId,
        replyId: String = this.replyId,
        content: String = this.content,
        dateTime: String = this.dateTime,
    ): ReplyInfo = ReplyInfo(
        threadId = threadId,
        postId = postId,
        replyId = replyId,
        content = content,
        dateTime = dateTime,
    )
    //endregion
}
