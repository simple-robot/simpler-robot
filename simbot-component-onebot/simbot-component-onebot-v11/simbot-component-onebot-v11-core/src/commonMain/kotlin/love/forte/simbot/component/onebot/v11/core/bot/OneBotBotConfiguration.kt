/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.bot

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.overwriteWith
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.component.onebot.common.annotations.ExperimentalOneBotAPI
import love.forte.simbot.component.onebot.v11.core.event.CustomEventResolver
import love.forte.simbot.component.onebot.v11.core.event.CustomKotlinSerializationEventResolver
import love.forte.simbot.component.onebot.v11.core.event.ExperimentalCustomEventResolverApi
import love.forte.simbot.component.onebot.v11.message.segment.OneBotImage
import love.forte.simbot.event.Event
import love.forte.simbot.resource.Resource
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 用于 [OneBotBot] 的配置类。
 *
 * @author ForteScarlet
 */
public class OneBotBotConfiguration {
    /**
     * 用于 Bot 的协程上下文实例。
     *
     * 如果其中包含 [Job][kotlinx.coroutines.Job]，
     * 则会在与 [OneBotBotManager]
     * 中（也可能存在的）[Job][kotlinx.coroutines.Job] 合并后，
     * 作为一个 parent Job 使用。
     */
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext

    /**
     * 必填属性，在OneBot组件中用于区分不同Bot的唯一ID，
     * 建议可以直接使用QQ号。
     * 更多说明参考 [OneBotBot.id]。
     */
    public var botUniqueId: String? = null

    /**
     * 参考 [鉴权](https://github.com/botuniverse/onebot-11/blob/master/communication/authorization.md)
     *
     * 可分开配置 [apiAccessToken] 或 [eventAccessToken]，或使用函数 [accessToken] 统一配置。
     *
     * @see apiAccessToken
     * @see eventAccessToken
     */
    @Deprecated("Use `apiAccessToken` or `eventAccessToken`")
    public var accessToken: String?
        get() = null
        set(value) {
            accessToken(value)
        }

    /**
     * 用于正向请求API的 accessToken。
     * 参考 [鉴权](https://github.com/botuniverse/onebot-11/blob/master/communication/authorization.md)
     *
     */
    public var apiAccessToken: String? = null

    /**
     * 用于连接正向ws接收事件的 accessToken。
     * 参考 [鉴权](https://github.com/botuniverse/onebot-11/blob/master/communication/authorization.md)
     *
     */
    public var eventAccessToken: String? = null

    /**
     * 同时配置 [apiAccessToken] 和 [eventAccessToken]。
     *
     * @see apiAccessToken
     * @see eventAccessToken
     */
    public fun accessToken(value: String?) {
        apiAccessToken = value
        eventAccessToken = value
    }

    /**
     * **额外的**序列化模块信息。
     * 如果不为 `null`，则 [OneBotBot.decoderJson]
     * 中的序列化模块会
     * [overwriteWith][SerializersModule.overwriteWith] 此 [serializersModule]。
     *
     */
    public var serializersModule: SerializersModule? = null

    /**
     * 必填属性，HTTP API的目标服务器地址。
     */
    public var apiServerHost: Url = Url("http://localhost:3001")

    /**
     * 配置 [apiServerHost]
     *
     * @see apiServerHost
     */
    public fun setApiServerHost(urlString: String) {
        apiServerHost = Url(urlString)
    }

    /**
     * 订阅事件的目标服务器地址。应当是ws或wss协议。
     * 如果为 `null` 则不启用 ws 连接。
     */
    public var eventServerHost: Url? = null

    /**
     * 配置 [eventServerHost]
     *
     * @see eventServerHost
     */
    public fun setEventServerHost(urlString: String?) {
        eventServerHost = urlString?.let { Url(it) }
    }

    /**
     * 用于API请求的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html)。
     *
     * 如果 [apiClientEngine] 和 [apiClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    public var apiClientEngine: HttpClientEngine? = null

    /**
     * 用于API请求的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html) 工厂。
     *
     * 如果 [apiClientEngine] 和 [apiClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    public var apiClientEngineFactory: HttpClientEngineFactory<*>? = null

    /**
     * API请求中的超时请求配置。
     *
     * 如果 [apiHttpRequestTimeoutMillis]、[apiHttpRequestTimeoutMillis]、[apiHttpRequestTimeoutMillis] 都为null，则不会配置timeout。
     *
     * @see HttpTimeout.HttpTimeoutCapabilityConfiguration.requestTimeoutMillis
     */
    public var apiHttpRequestTimeoutMillis: Long? = null


    /**
     * API请求中的超时请求配置。
     *
     * 如果 [apiHttpConnectTimeoutMillis]、[apiHttpConnectTimeoutMillis]、[apiHttpConnectTimeoutMillis] 都为null，则不会配置timeout。
     *
     * @see HttpTimeout.HttpTimeoutCapabilityConfiguration.connectTimeoutMillis
     */
    public var apiHttpConnectTimeoutMillis: Long? = null

    /**
     * API请求中的超时请求配置。
     *
     * 如果 [apiHttpSocketTimeoutMillis]、[apiHttpSocketTimeoutMillis]、[apiHttpSocketTimeoutMillis] 都为null，则不会配置timeout。
     *
     * @see HttpTimeout.HttpTimeoutCapabilityConfiguration.socketTimeoutMillis
     */
    public var apiHttpSocketTimeoutMillis: Long? = null

    /**
     * A configurer function for API requests [HttpClient].
     */
    public var apiClientConfigurer: ConfigurerFunction<HttpClientConfig<*>>? = null

    /**
     * config with [apiClientConfigurer].
     */
    public fun apiClientConfigurer(
        configurer: ConfigurerFunction<HttpClientConfig<*>>
    ): OneBotBotConfiguration =
        apply {
            apiClientConfigurer = apiClientConfigurer?.let { old ->
                ConfigurerFunction {
                    invokeBy(old)
                    invokeBy(configurer)
                }
            } ?: configurer
        }

    /**
     * 用于ws连接的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html)。
     *
     * 如果 [wsClientEngine] 和 [wsClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    public var wsClientEngine: HttpClientEngine? = null

    /**
     * 用于ws连接的 [HttpClient] 所使用的[引擎](https://ktor.io/docs/http-client-engines.html) 工厂。
     *
     * 如果 [apiClientEngine] 和 [apiClientEngineFactory] 都为null，则会尝试使用一个默认引擎。
     *
     */
    public var wsClientEngineFactory: HttpClientEngineFactory<*>? = null

    /**
     * 每次尝试连接到 ws 服务时的最大重试次数。
     * 如果小于等于0则代表无限。
     * 默认为 `2147483647`。
     */
    public var wsConnectMaxRetryTimes: Int = Int.MAX_VALUE

    /**
     * 每次尝试连接到 ws 服务时，如果需要重新尝试，则每次尝试之间的等待时长，
     * 单位为毫秒。
     * 如果小于等于 `0` 则代表不等待。
     * 默认为 `3500`。
     *
     */
    public var wsConnectRetryDelayMillis: Long = 3500L

    /**
     * 当使用非 [OneBotImage] 类型作为图片资源发送消息时，
     * 默认根据 [Resource] 得到一个可能存在的 [OneBotImage.AdditionalParams]。
     * 注意！这无法影响直接使用 [OneBotImage] 的情况。
     */
    @ExperimentalOneBotAPI
    public var defaultImageAdditionalParamsProvider: ((Resource) -> OneBotImage.AdditionalParams?)? = null

    /**
     * 配置 [defaultImageAdditionalParamsProvider]
     */
    @ExperimentalOneBotAPI
    public fun defaultImageAdditionalParamsProvider(block: ((Resource) -> OneBotImage.AdditionalParams?)?) {
        defaultImageAdditionalParamsProvider = block
    }

    /**
     * 配置 [defaultImageAdditionalParamsProvider]
     */
    @ExperimentalOneBotAPI
    public fun defaultImageAdditionalParams(params: OneBotImage.AdditionalParams?) {
        defaultImageAdditionalParamsProvider = { params }
    }

    /**
     *
     * @since 1.8.0
     */
    @ExperimentalCustomEventResolverApi
    internal val customEventResolvers: MutableList<CustomEventResolver> = mutableListOf()

    /**
     * 注册一个 [CustomEventResolver]。
     * @since 1.8.0
     */
    @ExperimentalCustomEventResolverApi
    public fun addCustomEventResolver(customEventResolver: CustomEventResolver) {
        customEventResolvers.add(customEventResolver)
    }
}

/**
 * 添加一个 [CustomKotlinSerializationEventResolver]。
 *
 * @see OneBotBotConfiguration.addCustomEventResolver
 * @since 1.8.0
 */
@ExperimentalCustomEventResolverApi
public fun OneBotBotConfiguration.addCustomKotlinSerializationEventResolver(
    resolver: CustomKotlinSerializationEventResolver
) {
    addCustomEventResolver(resolver)
}

/**
 * 添加一个 [CustomKotlinSerializationEventResolver]。
 * 原则上通过 `postType` 和 `subType` 可以定位唯一一个事件类型。
 *
 * @see OneBotBotConfiguration.addCustomEventResolver
 * @since 1.8.0
 */
@ExperimentalCustomEventResolverApi
public inline fun OneBotBotConfiguration.addCustomKotlinSerializationEventResolver(
    postType: String,
    subType: String,
    crossinline deserializationStrategy: () -> DeserializationStrategy<Event>
) {
    addCustomKotlinSerializationEventResolver { context ->
        val rawEventResolveResult = context.rawEventResolveResult
        if (rawEventResolveResult.postType == postType && rawEventResolveResult.subType == subType) {
            deserializationStrategy()
        } else {
            null
        }
    }
}
