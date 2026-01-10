/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.qguild.api

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.GatewayApis.Normal
import love.forte.simbot.qguild.api.GatewayApis.Shared


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
public data class Gateway(override val url: String) : GatewayInfo()


/**
 * 一个用于连接 websocket 的地址。同时返回建议的分片数，以及目前连接数使用情况。
 * [GatewayApis.Shared] 的响应体。
 *
 * [参考文档](https://bot.q.qq.com/wiki/develop/api/openapi/wss/shard_url_get.html#%E8%BF%94%E5%9B%9E)
 */
@SerialName("s")
@Serializable
public data class GatewayWithShard(
    override val url: String,
    public val shards: Int,
    @SerialName("session_start_limit")
    public val sessionStartLimit: SessionStartLimit
) : GatewayInfo()


/**
 *
 * [参考文档](https://bot.q.qq.com/wiki/develop/api/openapi/wss/shard_url_get.html#sessionstartlimit)
 */
@Serializable
public data class SessionStartLimit(
    /**
     * 每 24 小时可创建 Session 数
     */
    public val total: Int,
    /**
     * 目前还可以创建的 Session 数
     */
    public val remaining: Int,
    /**
     * 重置计数的剩余时间(ms)
     */
    @SerialName("reset_after")
    public val resetAfter: Int,
    /**
     * 每 5s 可以创建的 Session 数
     */
    @SerialName("max_concurrency")
    public val maxConcurrency: Int,
)
