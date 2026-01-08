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

package love.forte.simbot.qguild.stdlib

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import love.forte.simbot.qguild.event.EventIntents
import love.forte.simbot.qguild.event.Intents
import love.forte.simbot.qguild.event.Shard
import love.forte.simbot.qguild.event.Signal
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmSynthetic

/**
 * [Bot] 所需的配置信息。
 */
public interface BotConfiguration {

    /**
     * Context.
     *
     * 如果存在Job，则会被作为parentJob。
     */
    public val coroutineContext: CoroutineContext

    /**
     * 此bot建立的链接所使用的 shard。默认情况下使用 [Shard.FULL].
     */
    public val shard: Shard

    /**
     * bot需要订阅的事件 [Intents].
     *
     * 默认订阅如下事件：
     * - [频道相关事件][EventIntents.Guilds]
     * - [频道成员相关事件][EventIntents.GuildMembers]
     * - [公域消息相关事件][EventIntents.PublicGuildMessages]
     */
    @get:JvmSynthetic
    public val intents: Intents

    /**
     * @see intents
     */
    public val intentsValue: Int get() = intents.value

    /**
     * 异常处理器。
     * TODO
     */
    public val exceptionHandler: ExceptionProcessor?

    /**
     * 用作 [Signal.Identify.Data.properties] 中的参数。
     *
     */
    public val clientProperties: Map<String, String>

    /**
     * 请求的服务器地址。默认为 [love.forte.simbot.qguild.QQGuild.URL]. 即正式地址。
     */
    public val serverUrl: Url


    /**
     * 用于API请求的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html)。
     *
     * 如果 [apiClientEngine] 和 [apiClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    public val apiClientEngine: HttpClientEngine?

    /**
     * 用于API请求的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html) 工厂。
     *
     * 如果 [apiClientEngine] 和 [apiClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    public val apiClientEngineFactory: HttpClientEngineFactory<*>?

    /**
     * API请求中的超时请求配置。
     *
     * 如果 [apiHttpRequestTimeoutMillis]、[apiHttpRequestTimeoutMillis]、[apiHttpRequestTimeoutMillis] 都为null，则不会配置timeout。
     *
     * @see HttpTimeout.HttpTimeoutCapabilityConfiguration.requestTimeoutMillis
     */
    public val apiHttpRequestTimeoutMillis: Long?

    /**
     * API请求中的超时请求配置。
     *
     * 如果 [apiHttpConnectTimeoutMillis]、[apiHttpConnectTimeoutMillis]、[apiHttpConnectTimeoutMillis] 都为null，则不会配置timeout。
     *
     * @see HttpTimeout.HttpTimeoutCapabilityConfiguration.connectTimeoutMillis
     */
    public val apiHttpConnectTimeoutMillis: Long?

    /**
     * API请求中的超时请求配置。
     *
     * 如果 [apiHttpSocketTimeoutMillis]、[apiHttpSocketTimeoutMillis]、[apiHttpSocketTimeoutMillis] 都为null，则不会配置timeout。
     *
     * @see HttpTimeout.HttpTimeoutCapabilityConfiguration.socketTimeoutMillis
     */
    public val apiHttpSocketTimeoutMillis: Long?

    /**
     * 用于ws的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html)。
     *
     * 如果 [wsClientEngine] 和 [wsClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    public val wsClientEngine: HttpClientEngine?

    /**
     * 用于连接ws的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html) 工厂。
     *
     * 如果 [wsClientEngine] 和 [wsClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    public val wsClientEngineFactory: HttpClientEngineFactory<*>?

    /**
     * 是否禁用 ws 连接。如果你打算使用 webhook，则设置为 `true`,
     * 届时在启动 bot 时不会再连接 ws 服务。
     *
     * @since 4.1.0
     */
    public val disableWs: Boolean

    /**
     * 用于API请求结果反序列化的 [Json].
     *
     * 如果为null则会使用一个默认的 Json:
     * ```kotlin
     * Json {
     *     isLenient = true
     *     ignoreUnknownKeys = true
     * }
     * ```
     *
     */
    public val apiDecoder: Json
}




/**
 * 一个 异常处理器.
 */
public interface ExceptionProcessor {

    /**
     * 对目标进行处理, 并得到一个结果。
     */
    @JvmSynthetic
    public suspend fun process(target: Throwable)
}
