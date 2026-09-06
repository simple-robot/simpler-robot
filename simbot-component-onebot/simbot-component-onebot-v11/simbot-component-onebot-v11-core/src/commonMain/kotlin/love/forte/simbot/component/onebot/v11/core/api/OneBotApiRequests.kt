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

@file:JvmName("OneBotApiRequests")
@file:JvmMultifileClass

package love.forte.simbot.component.onebot.v11.core.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.charsets.*
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import love.forte.simbot.common.serialization.guessSerializer
import love.forte.simbot.component.onebot.common.annotations.ExperimentalOneBotAPI
import love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.LoggerFactory
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * 用于在对 [BasicOneBotApi] 发起请求时或得到想用后输出相关日志日志收集器。
 */
@InternalOneBotAPI
public val ApiLogger: Logger = LoggerFactory.getLogger("love.forte.simbot.component.onebot.v11.core.api.API")

private const val EMPTY_JSON_STR: String = "{}"

/**
 * 全局性的配置属性。
 */
@ExperimentalOneBotAPI
public object GlobalOneBotApiRequestConfiguration {
    private const val EMPTY_JSON_STRING_IF_BODY_NULL_KEY: String =
        "simbot.onebot11.api.request.emptyJsonStringIfBodyNull"

    /**
     * 如果 API 的 body 为 null, 则使用一个空的JSON字符串作为 Body。
     *
     * 在 JVM 平台中，可以通JVM属性
     * `simbot.onebot11.api.request.emptyJsonStringIfBodyNull`
     * 来设置初始值:
     * ```
     * -Dsimbot.onebot11.api.request.emptyJsonStringIfBodyNull=false
     * ```
     *
     * 默认为 `true`。
     */
    @Volatile
    public var emptyJsonStringIfBodyNull: Boolean =
        initConfig(EMPTY_JSON_STRING_IF_BODY_NULL_KEY, "true")
            .toBoolean()
}

internal expect fun initConfig(key: String, default: String?): String?

/**
 * 对 [this] 发起一次请求，并得到相应的 [HttpResponse] 响应。
 *
 * ### Body
 *
 * 当请求的 [BasicOneBotApi.body] 不为 `null` 时，会做如下处理：
 * - 如果它是 [OutgoingContent] 类型或者字符串类型，直接使用。
 * - 如果提供的 [client] 中安装了 [ContentNegotiation] 插件，则直接使用。
 * - 否则，尝试使用 [guessSerializer] 获取到它的序列化器，然后使用 [OneBot11.DefaultJson]
 * 将其序列化为JSON字符串后赋值。这过程中如果出现异常，则会放弃，并最终尝试直接使用 [BasicOneBotApi.body]。
 * - 上述一切均不符，则最终会抛出异常。
 *
 * @param client 用于请求的 [HttpClient].
 * @param host 用于请求的路径前缀。最终发起请求的完整地址为 [host] + [BasicOneBotApi.action] + [actionSuffixes].
 * @param accessToken 参考 [鉴权](https://github.com/botuniverse/onebot-11/blob/master/communication/authorization.md)
 * 中涉及到 `access token` 请求头的内容 (`Authorization`)。
 * 如果不为 `null`，会追加前缀 `Bearer ` 并添加到请求头 `Authorization` 中。
 * @param actionSuffixes 会被拼接到 [BasicOneBotApi.action] 的行为后缀，可参考 [BasicOneBotApi.Actions].
 */
@JvmSynthetic
public suspend fun BasicOneBotApi<*>.request(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
): HttpResponse {
    val api = this

    return client.request {
        this.method = api.method
        url {
            takeFrom(host)
            api.resolveUrl(this, actionSuffixes)
        }

        headers {
            contentType(ContentType.Application.Json)
            accessToken?.also { bearerAuth(it) }
        }

        var jsonStr: String? = null

        when (val b = api.body) {
            null -> {
                if (GlobalOneBotApiRequestConfiguration.emptyJsonStringIfBodyNull) {
                    setBody(EMPTY_JSON_STR)
                }
            }

            is OutgoingContent, is String -> {
                setBody(b)
            }

            else -> {
                if (isContentNegotiationRuntimeAvailable &&
                    client.pluginOrNull(ContentNegotiation) != null
                ) {
                    setBody(b)
                } else {
                    try {
                        val json = OneBot11.DefaultJson
                        val serializer = guessSerializer(b, json.serializersModule)
                        val jsonText = json.encodeToString(serializer, b)
                        jsonStr = jsonText
                        setBody(jsonText)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Throwable) {
                        try {
                            setBody(b)
                        } catch (e0: Throwable) {
                            e0.addSuppressed(e)
                            throw e0
                        }
                    }
                }
            }
        }

        ApiLogger.debug(
            "API [{}] REQ ===> {}, body: {}, json: {}",
            action,
            url,
            api.body,
            jsonStr
        )
    }.also { res ->
        ApiLogger.debug(
            "API [{}] RES <=== {}, status: {}",
            action,
            res.request.url,
            res.status,
        )
    }
}

/**
 * 对 [this] 发起一次请求，并得到相应的 [HttpResponse] 响应。
 *
 * ### Body
 *
 * 当请求的 [BasicOneBotApi.body] 不为 `null` 时，会做如下处理：
 * - 如果它是 [OutgoingContent] 类型或者字符串类型，直接使用。
 * - 如果提供的 [client] 中安装了 [ContentNegotiation] 插件，则直接使用。
 * - 否则，尝试使用 [guessSerializer] 获取到它的序列化器，然后使用 [OneBot11.DefaultJson]
 * 将其序列化为JSON字符串后赋值。这过程中如果出现异常，则会放弃，并最终尝试直接使用 [BasicOneBotApi.body]。
 * - 上述一切均不符，则最终会抛出异常。
 *
 * @param client 用于请求的 [HttpClient].
 * @param host 用于请求的路径前缀。最终发起请求的完整地址为 [host] + [BasicOneBotApi.action] + [actionSuffixes].
 * @param actionSuffixes 会被拼接到 [BasicOneBotApi.action] 的行为后缀，可参考 [BasicOneBotApi.Actions].
 */
@JvmSynthetic
public suspend fun BasicOneBotApi<*>.request(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
): HttpResponse = request(client, Url(host), accessToken, actionSuffixes)

/**
 * 对 [this] 发起一次请求，并得到响应体的字符串内容。
 *
 * 更多描述参考 [BasicOneBotApi.request].
 *
 * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
 * @see BasicOneBotApi.request
 *
 */
@JvmSynthetic
public suspend fun BasicOneBotApi<*>.requestRaw(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8
): String {
    val response = request(client, host, accessToken, actionSuffixes)
    val status = response.status
    val body = response.bodyAsText(charset)

    if (!status.isSuccess()) {
        throw OneBotApiResponseNotSuccessException(status, "status: $status, body: $body")
    }

    ApiLogger.debug(
        "API [{}] RES <=== {}, body: {}",
        action,
        response.request.url,
        body
    )

    return body
}

/**
 * 对 [this] 发起一次请求，并得到响应体的字符串内容。
 *
 * 更多描述参考 [BasicOneBotApi.request].
 *
 * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
 * @see BasicOneBotApi.request
 *
 */
@JvmSynthetic
public suspend fun BasicOneBotApi<*>.requestRaw(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8
): String = requestRaw(client, Url(host), accessToken, actionSuffixes, charset)

/**
 * 对 [this] 发起一次请求，并得到响应体的 [OneBotApiResult] 结果。
 *
 * 更多描述参考 [BasicOneBotApi.request].
 *
 * @param decoder 用于解析JSON字符串为 [OneBotApiResult] 的JSON解析器。
 * 如果要提供自定义解析器，尽可能使其支持 [OneBot11.serializersModule]，
 * 否则部分涉及到OneBot消息段多态类型的地方可能会出现问题。
 * 只有在 receiver 为 [BasicOneBotApi] 或 [CustomOneBotApi.apiResultDeserializer] != null 时，
 * 才会使用此解析器。
 *
 * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
 * @see BasicOneBotApi.request
 */
@OptIn(ExperimentalCustomOneBotApi::class)
@JvmSynthetic
public suspend fun <T : Any> BasicOneBotApi<T>.requestResult(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    decoder: Json = OneBot11.DefaultJson,
): OneBotApiResult<T> {
    val raw = requestRaw(client, host, accessToken, actionSuffixes, charset)

    val apiResultDeserializer: DeserializationStrategy<OneBotApiResult<T>>? = when (this) {
        is CustomOneBotApi<T> -> apiResultDeserializer
        is OneBotApi<T> -> apiResultDeserializer
    }

    if (apiResultDeserializer != null) {
        return decoder
            .decodeFromString(apiResultDeserializer, raw)
            .withRaw(raw)
    }

    return deserialize(raw).withRaw(raw)
}

/**
 * 对 [this] 发起一次请求，并得到响应体的 [OneBotApiResult] 结果。
 *
 * 更多描述参考 [BasicOneBotApi.request].
 *
 * @param decoder 用于解析JSON字符串为 [OneBotApiResult] 的JSON解析器。
 * 如果要提供自定义解析器，尽可能使其支持 [OneBot11.serializersModule]，
 * 否则部分涉及到OneBot消息段多态类型的地方可能会出现问题。
 *
 * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
 * @see BasicOneBotApi.request
 */
@JvmSynthetic
public suspend fun <T : Any> BasicOneBotApi<T>.requestResult(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    decoder: Json = OneBot11.DefaultJson,
): OneBotApiResult<T> = requestResult(
    client,
    Url(host),
    accessToken,
    actionSuffixes,
    charset,
    decoder
)

/**
 * 对 [this] 发起一次请求，并得到响应体的 [T] 类型结果。
 *
 * 更多描述参考 [BasicOneBotApi.request].
 *
 * @param decoder 用于解析JSON字符串为 [OneBotApiResult] 的JSON解析器。
 * 如果要提供自定义解析器，尽可能使其支持 [OneBot11.serializersModule]，
 * 否则部分涉及到OneBot消息段多态类型的地方可能会出现问题。
 *
 * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
 * @throws IllegalStateException 如果响应结果体的状态 [OneBotApiResult.retcode]
 * 不是成功或 [OneBotApiResult.data] 为 `null`
 * @see BasicOneBotApi.request
 */
@JvmSynthetic
public suspend fun <T : Any> BasicOneBotApi<T>.requestData(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    decoder: Json = OneBot11.DefaultJson,
): T {
    val result = requestResult(
        client,
        host,
        accessToken,
        actionSuffixes,
        charset,
        decoder
    )
    return result.dataOrThrow
}

/**
 * 对 [this] 发起一次请求，并得到响应体的 [T] 结果。
 *
 * 更多描述参考 [BasicOneBotApi.request].
 *
 * @param decoder 用于解析JSON字符串为 [OneBotApiResult] 的JSON解析器。
 * 如果要提供自定义解析器，尽可能使其支持 [OneBot11.serializersModule]，
 * 否则部分涉及到OneBot消息段多态类型的地方可能会出现问题。
 *
 * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
 * @throws IllegalStateException 如果响应结果体的状态 [OneBotApiResult.retcode]
 * 不是成功或 [OneBotApiResult.data] 为 `null`
 * @see BasicOneBotApi.request
 */
@JvmSynthetic
public suspend fun <T : Any> BasicOneBotApi<T>.requestData(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    decoder: Json = OneBot11.DefaultJson,
): T = requestData(client, Url(host), accessToken, actionSuffixes, charset, decoder)


/**
 * 判断在运行时是否可以直接使用 [ContentNegotiation] 类型。
 * 在JVM中默认为仅编译期可用，因此在JVM平台下需要通过类加载校验一下；
 * 其他平台始终得到 `true`。
 */
internal expect val isContentNegotiationRuntimeAvailable: Boolean


private fun <R : Any, T : OneBotApiResult<R>> T.withRaw(raw: String): T = apply {
    this.raw = raw
}
