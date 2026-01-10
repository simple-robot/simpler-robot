/*
 * Copyright (c) 2022-2025. ForteScarlet.
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
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.qguild.event.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic


/**
 * 对于一个Bot的配置信息。
 * 如果在配置bot之后对内容进行后续修改，可能会影响到当前bot的使用。
 */
@Suppress("MemberVisibilityCanBePrivate")
public class ConfigurableBotConfiguration : BotConfiguration, IntentsAppender {

    /**
     * Context.
     *
     * 如果存在Job，则会被作为parentJob。
     */
    override var coroutineContext: CoroutineContext = EmptyCoroutineContext

    /**
     * 此bot建立的链接所使用的 shard。默认情况下使用 [Shard.FULL].
     */
    override var shard: Shard = Shard.FULL

    /**
     * bot需要订阅的事件 [Intents].
     *
     * 默认订阅如下事件：
     * - [频道相关事件][EventIntents.Guilds]
     * - [频道成员相关事件][EventIntents.GuildMembers]
     * - [公域消息相关事件][EventIntents.PublicGuildMessages]
     */
    @get:JvmSynthetic
    @set:JvmSynthetic
    override var intents: Intents =
        EventIntents.Guilds.intents + EventIntents.GuildMembers.intents + EventIntents.PublicGuildMessages.intents

    override fun appendIntents(intents: Intents) {
        this.intents += intents
    }

    /**
     * bot需要订阅的事件 [Intents] （的整型字面值）.
     *
     * 是 [intents] 的兼容性属性，用于兼容那些不支持内联类型的目标，例如JVM平台。
     *
     * @see intents
     */
    public override var intentsValue: Int
        get() = intents.value
        set(value) {
            intents = Intents(value)
        }

    /**
     * 向 [intents] 中添加一个 [Intents] 值。
     */
    @JvmName("addIntents")
    public fun addIntents(intents: Intents): ConfigurableBotConfiguration = apply {
        this.intents += intents
    }

    /**
     * @suppress 未实现
     */
    @Deprecated("Not implemented")
    override var exceptionHandler: ExceptionProcessor? = null

    /**
     * 用作 [Signal.Identify.Data.properties] 中的参数。
     *
     */
    override var clientProperties: Map<String, String> = emptyMap()

    /**
     * 请求的服务器地址。默认为 [QQGuild.URL]. 即正式地址。
     */
    override var serverUrl: Url = QQGuild.URL

    /**
     * 使 [BotConfiguration.serverUrl] 为 [QQGuild.SANDBOX_URL]
     */
    public fun useSandboxServerUrl() {
        serverUrl = QQGuild.SANDBOX_URL
    }

    /**
     * 用于API请求的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html)。
     *
     * 如果 [apiClientEngine] 和 [apiClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    override var apiClientEngine: HttpClientEngine? = null

    /**
     * 用于API请求的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html) 工厂。
     *
     * 如果 [apiClientEngine] 和 [apiClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    override var apiClientEngineFactory: HttpClientEngineFactory<*>? = null


    /**
     * API请求中的超时请求配置。
     *
     * 如果 [apiHttpRequestTimeoutMillis]、[apiHttpRequestTimeoutMillis]、[apiHttpRequestTimeoutMillis] 都为null，则不会配置timeout。
     *
     * @see HttpTimeout.HttpTimeoutCapabilityConfiguration.requestTimeoutMillis
     */
    override var apiHttpRequestTimeoutMillis: Long? = null


    /**
     * API请求中的超时请求配置。
     *
     * 如果 [apiHttpConnectTimeoutMillis]、[apiHttpConnectTimeoutMillis]、[apiHttpConnectTimeoutMillis] 都为null，则不会配置timeout。
     *
     * @see HttpTimeout.HttpTimeoutCapabilityConfiguration.connectTimeoutMillis
     */
    override var apiHttpConnectTimeoutMillis: Long? = null

    /**
     * API请求中的超时请求配置。
     *
     * 如果 [apiHttpSocketTimeoutMillis]、[apiHttpSocketTimeoutMillis]、[apiHttpSocketTimeoutMillis] 都为null，则不会配置timeout。
     *
     * @see HttpTimeout.HttpTimeoutCapabilityConfiguration.socketTimeoutMillis
     */
    override var apiHttpSocketTimeoutMillis: Long? = null

    /**
     * 用于ws连接的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html)。
     *
     * 如果 [wsClientEngine] 和 [wsClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    override var wsClientEngine: HttpClientEngine? = null

    /**
     * 用于ws连接的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html) 工厂。
     *
     * 如果 [apiClientEngine] 和 [apiClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    override var wsClientEngineFactory: HttpClientEngineFactory<*>? = null

    override var disableWs: Boolean = false

    /**
     * 用于API请求结果反序列化的 [Json].
     *
     * 如果为null则会使用默认 Json [QQGuild.DefaultJson]
     *
     */
    override var apiDecoder: Json = QQGuild.DefaultJson

    @Suppress("DEPRECATION")
    internal fun release(): BotConfiguration = BotConfigurationImpl(
        coroutineContext = coroutineContext,
        shard = shard,
        intents = intents,
        exceptionHandler = exceptionHandler,
        clientProperties = clientProperties,
        serverUrl = serverUrl,
        apiClientEngine = apiClientEngine,
        apiClientEngineFactory = apiClientEngineFactory,
        apiHttpRequestTimeoutMillis = apiHttpRequestTimeoutMillis,
        apiHttpConnectTimeoutMillis = apiHttpConnectTimeoutMillis,
        apiHttpSocketTimeoutMillis = apiHttpSocketTimeoutMillis,
        wsClientEngine = wsClientEngine,
        wsClientEngineFactory = wsClientEngineFactory,
        disableWs = disableWs,
        apiDecoder = apiDecoder,
    )

    public companion object
}

private class BotConfigurationImpl(
    override val coroutineContext: CoroutineContext,
    override val shard: Shard,
    override val intents: Intents,
    override val exceptionHandler: ExceptionProcessor?,
    override val clientProperties: Map<String, String>,
    override val serverUrl: Url,
    override val apiClientEngine: HttpClientEngine?,
    override val apiClientEngineFactory: HttpClientEngineFactory<*>?,
    override val apiHttpRequestTimeoutMillis: Long?,
    override val apiHttpConnectTimeoutMillis: Long?,
    override val apiHttpSocketTimeoutMillis: Long?,
    override val wsClientEngine: HttpClientEngine?,
    override val wsClientEngineFactory: HttpClientEngineFactory<*>?,
    override val disableWs: Boolean,
    override val apiDecoder: Json,
) : BotConfiguration
