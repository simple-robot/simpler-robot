/*
 * Copyright (c) 2024. ForteScarlet.
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

@file:JvmName("ApiRequests")
@file:JvmMultifileClass

package love.forte.simbot.qguild.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.StringFormat
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.json.Json
import love.forte.simbot.common.serialization.guessSerializer
import love.forte.simbot.logger.isDebugEnabled
import love.forte.simbot.qguild.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmSynthetic

/**
 * 格式值："BOT_APPID", 机器人 AppID
 */
public const val X_UNION_APPID_HEADER: String = "X-Union-Appid"

/**
 * 使用 [client] 向当前目标 API [QQGuildApi] 发起请求。
 *
 * see [鉴权方式](https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/interface-framework/api-use.html#鉴权方式)
 *
 * @param server 如果不为 null 则会取 [server] 中的 [Url.protocol]、[Url.hostWithPort]
 * 替换 [QQGuildApi.url] 中提供的值。
 *
 */
@JvmSynthetic
@JvmOverloads // 二进制兼容
public suspend fun <R : Any> QQGuildApi<R>.request(
    client: HttpClient,
    token: String? = null,
    server: Url? = null,
    appId: String? = null,
): HttpResponse {
    val api = this

    return client.request {
        method = api.method

        headers {
            token?.also { this[HttpHeaders.Authorization] = it }
            appId?.also { this[X_UNION_APPID_HEADER] = it }

            with(api.headers) {
                if (!isEmpty()) {
                    appendAll(api.headers)
                }
            }
            if (!contains(HttpHeaders.ContentType)) {
                contentType(ContentType.Application.Json)
            }
        }

        url {
            takeFrom(api.url)

            if (server != null) {
                protocol = server.protocol
                host = server.host
                port = server.port
            }
        }

        when (val body = api.body) {
            null -> {
                setBody(EmptyContent)
            }

            is OutgoingContent -> {
                setBody(body)
            }

            else -> {
                if (client.pluginOrNull(ContentNegotiation) != null) {
                    setBody(body)
                } else {
                    try {
                        val json = QQGuild.DefaultJson
                        val ser = guessSerializer(body, json.serializersModule)
                        val bodyJson = json.encodeToString(ser, body)
                        setBody(bodyJson)
                    } catch (e: Throwable) {
                        try {
                            setBody(body)
                        } catch (e0: Throwable) {
                            e0.addSuppressed(e)
                            throw e0
                        }
                    }
                }
            }
        }

        apiLogger.debug("[{} {}] =====> {}, body: {}", method.logName, url.encodedPath, url.host, api.body)
    }
}


/**
 * 通过 [request] 得到响应，读取为文本并输出debug日志后返回。
 * 不会进行校验。
 */
@OptIn(ExperimentalContracts::class)
public suspend inline fun <R : Any> QQGuildApi<R>.requestText(
    client: HttpClient,
    token: String?,
    server: Url? = QQGuild.URL,
    appId: String? = null,
    useResp: (HttpResponse) -> Unit = {}
): String {
    contract {
        callsInPlace(useResp, InvocationKind.EXACTLY_ONCE)
    }

    val resp = request(
        client = client,
        token = token,
        server = server,
        appId = appId
    )
    useResp(resp)

    val text = resp.bodyAsText()

    if (apiLogger.isDebugEnabled) {
        val traceId = resp.headers[TRACE_ID_HEAD]
        apiLogger.debug(
            "[{} {}] <===== status: {}, body: {}, traceID: {}",
            method.logName,
            resp.request.url.encodedPath,
            resp.status,
            text,
            traceId
        )
    }

    return text
}

/**
 * 使用此api发起一次请求，并得到预期中的结果。
 *
 * ## Body序列化
 *
 * 参数 [client] 不强制要求必须安装 [ContentNegotiation] 插件，
 * 如果未安装此插件且 API 的请求过程中存在 body，则内部会使用一个默认的序列化器 (不是 [decoder])
 * 进行一个与此插件 _类似的_ 逻辑去寻找 body 的序列化信息。此时要求 API 的 body 必须支持 Kotlinx 的序列化。
 *
 * @param client 用于本次http请求的client。
 * @param server 请求目标服务器。See also: [QQGuild.URL]、[QQGuild.SANDBOX_URL]。
 * @param token 用于本次请求鉴权的token。
 * @param decoder 用于本次请求结果的反序列化器。不出意外的话应该是 [Json] 序列化器，默认使用 [QQGuild.DefaultJson]。
 *
 * @throws Exception see [HttpClient.request], 可能会抛出任何ktor请求过程中的异常。
 * @throws love.forte.simbot.qguild.QQGuildApiException 请求过程中出现了错误。
 *
 * @see ErrInfo
 */
@JvmSynthetic
@JvmOverloads
public suspend fun <R : Any> QQGuildApi<R>.requestData(
    client: HttpClient,
    token: String?,
    server: Url? = QQGuild.URL,
    decoder: Json = QQGuild.DefaultJson,
    appId: String? = null,
): R {
    val resp: HttpResponse
    val text = requestText(client, token, server, appId) { resp = it }

    checkStatus(text, QQGuild.DefaultJson, resp.status, resp)

    return try {
        decodeResponse(decoder, text)
    } catch (serEx: SerializationException) {
        val status = resp.status
        // 反序列化异常
        throw QQGuildResultSerializationException(
            status.value,
            status.description,
            "Response(status=${status.value}) deserialization failed: ${serEx.message}"
        ).also {
            it.initCause0(serEx)
        }
    }
}


@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalSerializationApi::class)
internal fun <R : Any> QQGuildApi<R>.decodeResponse(
    decoder: Json, remainingText: String
): R {
    if (resultDeserializationStrategy === Unit.serializer()) {
        return Unit as R
    }

    if (remainingText.isEmpty() && resultDeserializationStrategy.descriptor.kind is StructureKind.OBJECT) {
        return decoder.decodeFromString(resultDeserializationStrategy, "{}")
    }

    return decoder.decodeFromString(resultDeserializationStrategy, remainingText)
}

internal fun checkStatus(
    remainingText: String,
    decoder: StringFormat,
    status: HttpStatusCode,
    resp: HttpResponse,
) {
    // 如果出现了序列化异常，抛出 QQGuildResultSerializationException
    fun <T> decodeFromStringWithCatch(deserializer: DeserializationStrategy<T>): T {
        return try {
            decoder.decodeFromString(deserializer, remainingText)
        } catch (serEx: SerializationException) {
            // 反序列化异常
            throw QQGuildResultSerializationException(
                status.value,
                status.description,
                "Response(status=${status.value}) deserialization failed: ${serEx.message}"
            ).also {
                it.initCause0(serEx)
            }
        }
    }


    // TODO 201,202 异步操作成功，虽然说成功，但是会返回一个 error body，需要特殊处理
    if (!status.isSuccess()) {
        val info = decodeFromStringWithCatch(ErrInfo.serializer())

        // throw err
        throw QQGuildApiException(info, status.value, status.description)
    }

    // 202 消息审核
    if (status == HttpStatusCode.Accepted) {
        val info = decodeFromStringWithCatch(ErrInfo.serializer())
        // maybe audited
        if (MessageAuditedException.isAuditResultCode(info.code)) {
            throw MessageAuditedException(
                QQGuild.DefaultJson.decodeFromJsonElement(MessageAudit.serializer(), info.data).messageAudit,
                info,
                resp.status.value,
                resp.status.description
            )
        }

        throw QQGuildApiException(info, resp.status.value, resp.status.description)
    }
}
