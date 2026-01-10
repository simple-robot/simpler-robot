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

package love.forte.simbot.kook.stdlib

import io.ktor.client.engine.*
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * [BotConfiguration] 中 [BotConfiguration.isCompress] 的默认值。
 */
public expect val DEFAULT_COMPRESS: Boolean

/**
 *
 * [Bot] 使用的配置。
 *
 * @author ForteScarlet
 */
public class BotConfiguration {
    /**
     * 设置bot进行连接的时候使用要使用压缩数据。
     *
     * 默认值参考 [DEFAULT_COMPRESS]，不同的平台实现对 compress 的支持是不同的。
     *
     */
    public var isCompress: Boolean = DEFAULT_COMPRESS

    /**
     * 为bot提供一个 [CoroutineContext]。
     * 如果其中存在 [kotlinx.coroutines.Job], 则会作为 parent job。
     */
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext

    // region request client

    /**
     * 配置bot内部要使用的client Engine。
     */
    public var clientEngine: HttpClientEngine? = null

    /**
     * 配置bot内部要使用的client Engine factory。
     *
     * 如果 [clientEngine] 存在，则优先使用 [clientEngine].
     */
    public var clientEngineFactory: HttpClientEngineFactory<*>? = null

    /**
     * 配置bot内部要使用的 ws 连接用的 client Engine。
     */
    public var wsEngine: HttpClientEngine? = null

    /**
     * 配置bot内部要使用的 ws Engine factory。
     *
     * 如果 [wsEngine] 存在，则优先使用 [wsEngine].
     */
    public var wsEngineFactory: HttpClientEngineFactory<*>? = null

    /**
     * 针对 [HttpClientEngineConfig] 中的通用默认属性的配置。
     *
     * 用于配置最终的 HTTP API Client 所使用的引擎。
     *
     * @see EngineConfiguration
     */
    public var clientEngineConfig: EngineConfiguration? = null

    /**
     * 针对 [HttpClientEngineConfig] 中的通用默认属性的配置。
     *
     * 用于配置最终的 ws Client 所使用的引擎。
     *
     * @see EngineConfiguration
     */
    public var wsEngineConfig: EngineConfiguration? = null

    /**
     * 针对 [HttpClientEngineConfig] 中的通用默认属性的配置。
     *
     */
    @Serializable
    public data class EngineConfiguration(
        /**
         * 参考 [HttpClientEngineConfig.pipelining][io.ktor.client.engine.HttpClientEngineConfig.pipelining]
         *
         * 默认为 `null`。为 `null` 时不进行显示配置。
         *
         * @see io.ktor.client.engine.HttpClientEngineConfig.pipelining
         */
        public var pipelining: Boolean? = null,

        // TODO proxy support?
//        public var proxy: ProxyConfig? = null,
    )

    // endregion

    // region api request timeout

    /**
     * api请求的超时配置。
     * 如果为null则不会在 `httpClient` 中安装 [io.ktor.client.plugins.HttpTimeout]。
     *
     * 默认情况下配置:
     * - [connectTimeoutMillis][TimeoutConfiguration.connectTimeoutMillis] = 5000ms
     * - [requestTimeoutMillis][TimeoutConfiguration.requestTimeoutMillis] = 5000ms
     * - [socketTimeoutMillis][TimeoutConfiguration.socketTimeoutMillis] = null
     */
    public var timeout: TimeoutConfiguration? = TimeoutConfiguration(
        connectTimeoutMillis = 5000L,
        requestTimeoutMillis = 5000L,
        socketTimeoutMillis = null,
    )


    /**
     * api请求的统一超时时间配置。
     */
    @Serializable
    public data class TimeoutConfiguration(
        /**
         * 参考 [HttpTimeout.connectTimeoutMillis][io.ktor.client.plugins.HttpTimeout.connectTimeoutMillis]
         *
         * @see io.ktor.client.plugins.HttpTimeout.connectTimeoutMillis
         */
        var connectTimeoutMillis: Long? = null,
        /**
         * 参考 [HttpTimeout.requestTimeoutMillis][io.ktor.client.plugins.HttpTimeout.requestTimeoutMillis]
         *
         * @see io.ktor.client.plugins.HttpTimeout.requestTimeoutMillis
         */
        var requestTimeoutMillis: Long? = null,
        /**
         * 参考 [HttpTimeout.socketTimeoutMillis][io.ktor.client.plugins.HttpTimeout.socketTimeoutMillis]
         *
         * @see io.ktor.client.plugins.HttpTimeout.socketTimeoutMillis
         */
        var socketTimeoutMillis: Long? = null,
    ) {
        public constructor() : this(null, null, null)
    }

    /**
     * 禁用超时配置。
     */
    public fun disableTimeout() {
        timeout = null
    }

    /**
     * HTTP API 请求的连接超时时间。
     *
     * @see TimeoutConfiguration.connectTimeoutMillis
     */
    public var connectTimeoutMillis: Long?
        get() = timeout?.connectTimeoutMillis
        set(value) {
            timeout?.let { t -> t.connectTimeoutMillis = value } ?: apply {
                timeout = TimeoutConfiguration(connectTimeoutMillis = value)
            }
        }

    /**
     * HTTP API 请求的请求超时时间。
     *
     * @see TimeoutConfiguration.requestTimeoutMillis
     */
    public var requestTimeoutMillis: Long?
        get() = timeout?.requestTimeoutMillis
        set(value) {
            timeout?.let { t -> t.requestTimeoutMillis = value } ?: apply {
                timeout = TimeoutConfiguration(requestTimeoutMillis = value)
            }
        }

    /**
     * HTTP API 请求的请求超时时间。
     *
     * @see TimeoutConfiguration.socketTimeoutMillis
     */
    public var socketTimeoutMillis: Long?
        get() = timeout?.socketTimeoutMillis
        set(value) {
            timeout?.let { t -> t.socketTimeoutMillis = value } ?: apply {
                timeout = TimeoutConfiguration(socketTimeoutMillis = value)
            }
        }
    // endregion

    /**
     * ws连接超时时间，单位 ms 。默认为 [DEFAULT_WS_CONNECT_TIMEOUT].
     */
    public var wsConnectTimeout: Long = DEFAULT_WS_CONNECT_TIMEOUT


    /**
     * [SubscribeSequence.NORMAL] 类型的事件处理器是否在异步中执行。
     * 默认为 `true`。
     * 当为 `false` 时, [SubscribeSequence.NORMAL] 的表现效果将会与
     * [SubscribeSequence.PREPARE] 基本类似。
     */
    public var isNormalEventProcessAsync: Boolean = true


    public companion object {
        /**
         * 默认的连接超时时间。
         */
        public const val DEFAULT_WS_CONNECT_TIMEOUT: Long = 6000L

//        /**
//         * [KookBotConfiguration] 默认使用的解析器。
//         */
//        @OptIn(ExperimentalSerializationApi::class)
//        public val defaultDecoder: Json = Json {
//            isLenient = true
//            ignoreUnknownKeys = true
//            encodeDefaults = true
//            // see https://github.com/kaiheila/api-docs/issues/174
//            explicitNulls = false
//        }
    }
}

/**
 * 配置 [BotConfiguration.timeout]。
 *
 * 只有当 [timeout][BotConfiguration.timeout] 不为null时或者 [init] == true 时才会触发。
 *
 * @param init 如果 timeout 为null且 [init] 为true，则会初始化一个 [timeout][BotConfiguration.timeout]
 *
 */
public inline fun BotConfiguration.timeout(
    init: Boolean = true,
    block: BotConfiguration.TimeoutConfiguration.() -> Unit,
) {
    val timeout = this.timeout
    if (timeout == null && init) {
        this.timeout = BotConfiguration.TimeoutConfiguration().also(block)
    } else {
        timeout?.block()
    }
}

/**
 * 配置 [BotConfiguration.clientEngineConfig]。
 *
 * 只有当 [engineConfig][BotConfiguration.clientEngineConfig] 不为null时或者 [init] == true 时才会触发。
 *
 * @param init 如果 engineConfig 为null且 [init] 为true，则会初始化一个 [engineConfig][BotConfiguration.clientEngineConfig]
 *
 */
public inline fun BotConfiguration.engine(
    init: Boolean = true,
    block: BotConfiguration.EngineConfiguration.() -> Unit,
) {
    val engineConfig = this.clientEngineConfig
    if (engineConfig == null && init) {
        this.clientEngineConfig = BotConfiguration.EngineConfiguration().also(block)
    } else {
        engineConfig?.block()
    }
}
