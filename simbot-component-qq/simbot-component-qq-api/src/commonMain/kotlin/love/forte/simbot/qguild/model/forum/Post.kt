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
 * 话题频道内对主题的评论称为帖子
 *
 * 话题频道内对帖子主题评论或删除时生产事件中包含该对象
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 *
 * @property guildId 频道ID
 * @property channelId 子频道ID
 * @property authorId 作者ID
 * @property postInfo 帖子内容
 *
 * @see PostInfo
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class Post @ApiModelConstructor constructor(
    @SerialName("guild_id")
    override val guildId: String,
    @SerialName("channel_id")
    override val channelId: String,
    @SerialName("author_id")
    override val authorId: String,
    @SerialName("post_info")
    public val postInfo: PostInfo
) : ForumSourceInfo {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Post) return false

        if (guildId != other.guildId) return false
        if (channelId != other.channelId) return false
        if (authorId != other.authorId) return false
        if (postInfo != other.postInfo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + authorId.hashCode()
        result = 31 * result + postInfo.hashCode()
        return result
    }

    override fun toString(): String {
        return "Post(guildId='$guildId', channelId='$channelId', authorId='$authorId', postInfo=$postInfo)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component1(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component2(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("authorId"))
    public operator fun component3(): String = authorId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("postInfo"))
    public operator fun component4(): PostInfo = postInfo

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        guildId: String = this.guildId,
        channelId: String = this.channelId,
        authorId: String = this.authorId,
        postInfo: PostInfo = this.postInfo,
    ): Post = Post(
        guildId = guildId,
        channelId = channelId,
        authorId = authorId,
        postInfo = postInfo,
    )
    //endregion
}

/**
 * 帖子事件包含的帖子内容信息
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 *
 * @property threadId 主题ID
 * @property postId 帖子ID
 * @property content 帖子内容
 * @property dateTime 评论时间
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class PostInfo @ApiModelConstructor constructor(
    @SerialName("thread_id")
    public val threadId: String,
    @SerialName("post_id")
    public val postId: String,
    public val content: String,
    @SerialName("date_time")
    public val dateTime: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PostInfo) return false

        if (threadId != other.threadId) return false
        if (postId != other.postId) return false
        if (content != other.content) return false
        if (dateTime != other.dateTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = threadId.hashCode()
        result = 31 * result + postId.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + dateTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "PostInfo(threadId='$threadId', postId='$postId', content='$content', dateTime='$dateTime')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("threadId"))
    public operator fun component1(): String = threadId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("postId"))
    public operator fun component2(): String = postId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("content"))
    public operator fun component3(): String = content

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("dateTime"))
    public operator fun component4(): String = dateTime

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        threadId: String = this.threadId,
        postId: String = this.postId,
        content: String = this.content,
        dateTime: String = this.dateTime,
    ): PostInfo = PostInfo(
        threadId = threadId,
        postId = postId,
        content = content,
        dateTime = dateTime,
    )
    //endregion
}
