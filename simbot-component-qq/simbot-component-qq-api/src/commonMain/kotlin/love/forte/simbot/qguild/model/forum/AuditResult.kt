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

@file:OptIn(ExperimentalStdlibApi::class)

package love.forte.simbot.qguild.model.forum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import kotlin.jvm.JvmExposeBoxed


/**
 *
 * 论坛帖子审核结果事件
 *
 * 参考[forum文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/channel/content/forum/model.html#AuditResult)
 *
 * @property guildId 频道ID
 * @property channelId 子频道ID
 * @property authorId 作者ID
 * @property threadId 主题ID
 * @property postId 帖子ID
 * @property replyId 回复ID
 * @property auditType 审核类型
 * @property result 审核结果. 0:成功 1:失败
 * @property errMsg result不为0时错误信息
 *
 * @since 5.0
 * @author ForteScarlet
 */
@ApiModel
@Serializable
public class AuditResult internal constructor(
    private val m: Int = 0,
    @SerialName("guild_id")
    override val guildId: String,
    @SerialName("channel_id")
    override val channelId: String,
    @SerialName("author_id")
    override val authorId: String,
    @SerialName("thread_id")
    public val threadId: String,
    @SerialName("post_id")
    public val postId: String,
    @SerialName("reply_id")
    public val replyId: String,
    @SerialName("type")
    @get:JvmExposeBoxed
    public val auditType: AuditType,
    public val result: Int,
    @SerialName("err_msg")
    public val errMsg: String,
) : ForumSourceInfo {
    @Deprecated(
        DataClassCompatibilities.DEPRECATED_CONSTRUCTOR_MESSAGE,
        level = DeprecationLevel.ERROR
    )
    @ApiModelConstructor
    public constructor(
        guildId: String,
        channelId: String,
        authorId: String,
        threadId: String,
        postId: String,
        replyId: String,
        type: Int,
        result: Int,
        errMsg: String,
    ) : this(
        m = 0,
        guildId = guildId,
        channelId = channelId,
        authorId = authorId,
        threadId = threadId,
        postId = postId,
        replyId = replyId,
        auditType = AuditType.of(type),
        result = result,
        errMsg = errMsg,
    )

    @Deprecated("Use auditType instead.", ReplaceWith("auditType.value"))
    public val type: Int
        get() = auditType.value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuditResult) return false

        if (guildId != other.guildId) return false
        if (channelId != other.channelId) return false
        if (authorId != other.authorId) return false
        if (threadId != other.threadId) return false
        if (postId != other.postId) return false
        if (replyId != other.replyId) return false
        if (auditType != other.auditType) return false
        if (result != other.result) return false
        if (errMsg != other.errMsg) return false

        return true
    }

    override fun hashCode(): Int {
        var hash = guildId.hashCode()
        hash = 31 * hash + channelId.hashCode()
        hash = 31 * hash + authorId.hashCode()
        hash = 31 * hash + threadId.hashCode()
        hash = 31 * hash + postId.hashCode()
        hash = 31 * hash + replyId.hashCode()
        hash = 31 * hash + auditType.hashCode()
        hash = 31 * hash + result
        hash = 31 * hash + errMsg.hashCode()
        return hash
    }

    override fun toString(): String {
        return "AuditResult(" +
            "guildId='$guildId', " +
            "channelId='$channelId', " +
            "authorId='$authorId', " +
            "threadId='$threadId', " +
            "postId='$postId', " +
            "replyId='$replyId', " +
            "type=$auditType, " +
            "result=$result, " +
            "errMsg='$errMsg')"
    }

    //region data-class 兼容
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("guildId"))
    public operator fun component1(): String = guildId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("channelId"))
    public operator fun component2(): String = channelId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("authorId"))
    public operator fun component3(): String = authorId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("threadId"))
    public operator fun component4(): String = threadId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("postId"))
    public operator fun component5(): String = postId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("replyId"))
    public operator fun component6(): String = replyId

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("auditType.value"))
    public operator fun component7(): Int = auditType.value

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("result"))
    public operator fun component8(): Int = result

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("errMsg"))
    public operator fun component9(): String = errMsg

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        guildId: String = this.guildId,
        channelId: String = this.channelId,
        authorId: String = this.authorId,
        threadId: String = this.threadId,
        postId: String = this.postId,
        replyId: String = this.replyId,
        type: Int = this.auditType.value,
        result: Int = this.result,
        errMsg: String = this.errMsg,
    ): AuditResult = AuditResult(
        guildId = guildId,
        channelId = channelId,
        authorId = authorId,
        threadId = threadId,
        postId = postId,
        replyId = replyId,
        auditType = AuditType.of(type),
        result = result,
        errMsg = errMsg,
    )
    //endregion
}

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
    @Deprecated(
        "Use AuditType.PUBLISH_THREAD_VALUE instead.",
        ReplaceWith(
            "AuditType.PUBLISH_THREAD_VALUE",
            "love.forte.simbot.qguild.model.forum.AuditType"
        )
    )
    public const val PUBLISH_THREAD: Int = AuditType.PUBLISH_THREAD_VALUE

    /**
     * 评论
     */
    @Deprecated(
        "Use AuditType.PUBLISH_POST_VALUE instead.",
        ReplaceWith(
            "AuditType.PUBLISH_POST_VALUE",
            "love.forte.simbot.qguild.model.forum.AuditType"
        )
    )
    public const val PUBLISH_POST: Int = AuditType.PUBLISH_POST_VALUE

    /**
     * 回复
     */
    @Deprecated(
        "Use AuditType.PUBLISH_REPLY_VALUE instead.",
        ReplaceWith(
            "AuditType.PUBLISH_REPLY_VALUE",
            "love.forte.simbot.qguild.model.forum.AuditType"
        )
    )
    public const val PUBLISH_REPLY: Int = AuditType.PUBLISH_REPLY_VALUE
}
