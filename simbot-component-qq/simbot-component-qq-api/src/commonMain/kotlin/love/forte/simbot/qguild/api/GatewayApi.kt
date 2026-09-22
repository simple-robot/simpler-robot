/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.qguild.api

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.common.ApiModelConstructor
import love.forte.simbot.qguild.common.DataClassCompatibilities


/**
 * 获取网关信息。
 *
 * 通过 [Normal] 或 [Shared] 的形式根据bot信息获取使用 Websocket 接入时间通知的链接。
 *
 * > [参考文档](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html)
 *
 * @see Normal
 * @see Shared
 *
 * @author ForteScarlet
 */
public sealed class GatewayApis<R : GatewayInfo>(
    override val path: Array<String>,
    override val resultDeserializationStrategy: DeserializationStrategy<R>
) : GetQQGuildApi<R>() {

    /**
     * 获取通用 WSS 接入点
     *
     * > [参考文档](https://bot.q.qq.com/wiki/develop/api/openapi/wss/url_get.html)
     */
    public object Normal : GatewayApis<Gateway>(arrayOf("gateway"), Gateway.serializer())


    /**
     * 获取带分片 WSS 接入点
     *
     * > [参考文档](https://bot.q.qq.com/wiki/develop/api/openapi/wss/shard_url_get.html)
     */
    public object Shared : GatewayApis<GatewayWithShard>(arrayOf("gateway", "bot"), GatewayWithShard.serializer())
}


/**
 * Base sealed class for [Gateway] and [GatewayWithShard].
 */
@Serializable
public sealed class GatewayInfo {
    public abstract val url: String
}

/**
 * 一个用于连接 websocket 的地址。[GatewayApis.Normal] 的响应体。
 *
 * > [参考文档](https://bot.q.qq.com/wiki/develop/api/openapi/wss/url_get.html#%E8%BF%94%E5%9B%9E)
 */
@SerialName("n")
@Serializable
public class Gateway @ApiModelConstructor public constructor(override val url: String) : GatewayInfo() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Gateway) return false
        if (url != other.url) return false
        return true
    }

    override fun hashCode(): Int = url.hashCode()
    override fun toString(): String = "Gateway(url=$url)"

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("url"))
    public operator fun component1(): String = url

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(url: String = this.url): Gateway = Gateway(url)
}


/**
 * 一个用于连接 websocket 的地址。同时返回建议的分片数，以及目前连接数使用情况。
 * [GatewayApis.Shared] 的响应体。
 *
 * [参考文档](https://bot.q.qq.com/wiki/develop/api/openapi/wss/shard_url_get.html#%E8%BF%94%E5%9B%9E)
 */
@SerialName("s")
@Serializable
public class GatewayWithShard @ApiModelConstructor public constructor(
    override val url: String,
    public val shards: Int,
    @SerialName("session_start_limit")
    public val sessionStartLimit: SessionStartLimit
) : GatewayInfo() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GatewayWithShard) return false
        if (url != other.url) return false
        if (shards != other.shards) return false
        if (sessionStartLimit != other.sessionStartLimit) return false
        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + shards
        result = 31 * result + sessionStartLimit.hashCode()
        return result
    }

    override fun toString(): String =
        "GatewayWithShard(url=$url, shards=$shards, sessionStartLimit=$sessionStartLimit)"

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("url"))
    public operator fun component1(): String = url

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("shards"))
    public operator fun component2(): Int = shards

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("sessionStartLimit"))
    public operator fun component3(): SessionStartLimit = sessionStartLimit

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        url: String = this.url,
        shards: Int = this.shards,
        sessionStartLimit: SessionStartLimit = this.sessionStartLimit,
    ): GatewayWithShard = GatewayWithShard(url, shards, sessionStartLimit)
}


/**
 *
 * [参考文档](https://bot.q.qq.com/wiki/develop/api/openapi/wss/shard_url_get.html#sessionstartlimit)
 *
 * @property total 每 24 小时可创建 Session 数
 * @property remaining 目前还可以创建的 Session 数
 * @property resetAfter 重置计数的剩余时间(ms)
 * @property maxConcurrency 每 5s 可以创建的 Session 数
 */
@Serializable
public class SessionStartLimit @ApiModelConstructor public constructor(
    public val total: Int,
    public val remaining: Int,
    @SerialName("reset_after")
    public val resetAfter: Int,
    @SerialName("max_concurrency")
    public val maxConcurrency: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SessionStartLimit) return false
        if (total != other.total) return false
        if (remaining != other.remaining) return false
        if (resetAfter != other.resetAfter) return false
        if (maxConcurrency != other.maxConcurrency) return false
        return true
    }

    override fun hashCode(): Int {
        var result = total
        result = 31 * result + remaining
        result = 31 * result + resetAfter
        result = 31 * result + maxConcurrency
        return result
    }

    override fun toString(): String =
        "SessionStartLimit(total=$total, remaining=$remaining, resetAfter=$resetAfter, maxConcurrency=$maxConcurrency)"

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("total"))
    public operator fun component1(): Int = total

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("remaining"))
    public operator fun component2(): Int = remaining

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("resetAfter"))
    public operator fun component3(): Int = resetAfter

    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE, ReplaceWith("maxConcurrency"))
    public operator fun component4(): Int = maxConcurrency

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated(DataClassCompatibilities.DEPRECATED_MESSAGE)
    public fun copy(
        total: Int = this.total,
        remaining: Int = this.remaining,
        resetAfter: Int = this.resetAfter,
        maxConcurrency: Int = this.maxConcurrency,
    ): SessionStartLimit = SessionStartLimit(total, remaining, resetAfter, maxConcurrency)
}
