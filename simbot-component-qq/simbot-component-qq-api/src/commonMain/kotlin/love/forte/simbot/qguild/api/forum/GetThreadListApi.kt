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

package love.forte.simbot.qguild.api.forum

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.GetQQGuildApi
import love.forte.simbot.qguild.api.NumberAsBooleanSerializer
import love.forte.simbot.qguild.api.SimpleGetApiDescription
import love.forte.simbot.qguild.common.ApiModel
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities
import love.forte.simbot.qguild.common.PrivateDomainOnly
import love.forte.simbot.qguild.model.forum.Thread
import kotlin.jvm.JvmStatic

/**
 *
 * [获取帖子列表](https://bot.q.qq.com/wiki/develop/api/openapi/forum/get_threads_list.html)
 *
 * 该接口用于获取子频道下的帖子列表。
 *
 * @author ForteScarlet
 */
@PrivateDomainOnly
public class GetThreadListApi private constructor(channelId: String) : GetQQGuildApi<ThreadListResult>() {
    public companion object Factory : SimpleGetApiDescription("/channels/{channel_id}/threads") {

        /**
         * 构造 [GetThreadListApi].
         *
         * @param channelId 频道ID
         */
        @JvmStatic
        public fun create(channelId: String): GetThreadListApi =
            GetThreadListApi(channelId)
    }

    override val path: Array<String> = arrayOf("channels", channelId, "threads")

    override val resultDeserializationStrategy: DeserializationStrategy<ThreadListResult>
        get() = ThreadListResult.serializer()
}

/**
 * API [GetThreadListApi] 的响应体。
 *
 * @property threads 帖子列表对象（返回值里面的content字段，可参照RichText结构）
 * @property isFinish 是否拉取完毕(0:否；1:是)
 */
@ApiModel
@Serializable
public class ThreadListResult @ApiModelConstructor public constructor(
    public val threads: List<Thread>,
    @SerialName("is_finish")
    @Serializable(with = NumberAsBooleanSerializer::class)
    public val isFinish: Boolean
) : Iterable<Thread> by threads {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ThreadListResult) return false
        if (threads != other.threads) return false
        if (isFinish != other.isFinish) return false
        return true
    }

    override fun hashCode(): Int {
        var result = threads.hashCode()
        result = 31 * result + isFinish.hashCode()
        return result
    }

    override fun toString(): String = "ThreadListResult(threads=$threads, isFinish=$isFinish)"

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("threads"))
    public operator fun component1(): List<Thread> = threads

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("isFinish"))
    public operator fun component2(): Boolean = isFinish

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        threads: List<Thread> = this.threads,
        isFinish: Boolean = this.isFinish,
    ): ThreadListResult = ThreadListResult(threads, isFinish)
}
