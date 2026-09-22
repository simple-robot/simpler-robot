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
 *
 * 话题频道内发表的主帖称为主题
 *
 * 该事件在话题频道内新发表主题或删除时生产事件中包含该对象
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 *
 * @property guildId 频道ID
 * @property channelId 子频道ID
 * @property authorId 作者ID
 * @property threadInfo 主帖内容
 *
 * @see ThreadInfo
 *
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class Thread @ApiModelConstructor constructor(
    @SerialName("guild_id")
    override val guildId: String,
    @SerialName("channel_id")
    override val channelId: String,
    @SerialName("author_id")
    override val authorId: String,
    @SerialName("thread_info")
    public val threadInfo: ThreadInfo
) : ForumSourceInfo {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Thread) return false

        if (guildId != other.guildId) return false
        if (channelId != other.channelId) return false
        if (authorId != other.authorId) return false
        if (threadInfo != other.threadInfo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + channelId.hashCode()
        result = 31 * result + authorId.hashCode()
        result = 31 * result + threadInfo.hashCode()
        return result
    }

    override fun toString(): String {
        return "Thread(guildId='$guildId', channelId='$channelId', authorId='$authorId', threadInfo=$threadInfo)"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component1(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component2(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("authorId"))
    public operator fun component3(): String = authorId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("threadInfo"))
    public operator fun component4(): ThreadInfo = threadInfo

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        guildId: String = this.guildId,
        channelId: String = this.channelId,
        authorId: String = this.authorId,
        threadInfo: ThreadInfo = this.threadInfo,
    ): Thread = Thread(
        guildId = guildId,
        channelId = channelId,
        authorId = authorId,
        threadInfo = threadInfo,
    )
    //endregion
}

/**
 * 帖子事件包含的主帖内容相关信息
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api/openapi/forum/model.html#thread)
 *
 * @property threadId 主帖ID
 * @property title 帖子标题
 * @property content 帖子内容
 * @property dateTime 发表时间
 *
 * _ISO8601 timestamp_
 */
@ApiModel
@Serializable
public class ThreadInfo @ApiModelConstructor constructor(
    @SerialName("thread_id")
    public val threadId: String,
    public val title: String,
    public val content: String,
    @SerialName("date_time")
    public val dateTime: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ThreadInfo) return false

        if (threadId != other.threadId) return false
        if (title != other.title) return false
        if (content != other.content) return false
        if (dateTime != other.dateTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = threadId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + dateTime.hashCode()
        return result
    }

    override fun toString(): String {
        return "ThreadInfo(threadId='$threadId', title='$title', content='$content', dateTime='$dateTime')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("threadId"))
    public operator fun component1(): String = threadId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("title"))
    public operator fun component2(): String = title

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("content"))
    public operator fun component3(): String = content

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("dateTime"))
    public operator fun component4(): String = dateTime

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        threadId: String = this.threadId,
        title: String = this.title,
        content: String = this.content,
        dateTime: String = this.dateTime,
    ): ThreadInfo = ThreadInfo(
        threadId = threadId,
        title = title,
        content = content,
        dateTime = dateTime,
    )
    //endregion
}
