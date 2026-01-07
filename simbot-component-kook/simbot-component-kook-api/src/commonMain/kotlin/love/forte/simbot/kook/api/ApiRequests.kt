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
@file:JvmName("ApiRequests")
@file:JvmMultifileClass

package love.forte.simbot.kook.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.common.serialization.guessSerializer
import love.forte.simbot.logger.LoggerFactory
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

private val apiLogger = LoggerFactory.getLogger("love.forte.simbot.kook.api")

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个 [ApiResult]。
 *
 * 得到原始的 [HttpResponse] 而不对结果有任何处理。
 */
@JvmSynthetic
public suspend fun KookApi<*>.request(client: HttpClient, authorization: String): HttpResponse {
    apiLogger.debug(
        "API[{} {}] ======> query: {}, body: {}",
        method.value,
        url.encodedPath,
        url.encodedQuery.ifEmpty { "<EMPTY>" },
        body
    )

    val response = reqForResp(client, authorization)

    apiLogger.debug(
        "API[{} {}] <====== status: {}, response: {}",
        method.value,
        url.encodedPath,
        response.status,
        response
    )

    return response
}

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个结果字符串。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 */
@JvmSynthetic
public suspend fun KookApi<*>.requestText(client: HttpClient, authorization: String): String {
    val response = request(client, authorization)
    val text = response.requireSuccess(this).bodyAsText()
    apiLogger.debug(
        "API[{} {}] <====== status: {}, response text: {}",
        method.value,
        url.encodedPath,
        response.status,
        text
    )
    return text
}


/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个 [ApiResult] 结果。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 */
@JvmSynthetic
public suspend fun KookApi<*>.requestResult(client: HttpClient, authorization: String): ApiResult {
    val response = request(client, authorization)
    val text = response.requireSuccess(this).bodyAsText()
    apiLogger.debug(
        "API[{} {}] <====== status: {}, response text: {}",
        method.value,
        url.encodedPath,
        response.status,
        text
    )

    val result = KookApi.DEFAULT_JSON.decodeFromString(ApiResult.serializer(), text)
    result.httpStatus = response.status
    result.raw = text

    val rateLimit = response.headers.createRateLimit().also {
        apiLogger.debug("API[{} {}] <====== rate limit: {}", method.value, url.encodedPath, it)
    }
    result.rateLimit = rateLimit

    return result
}

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个具体结果。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 * @throws ApiResultException 请求结果的 [ApiResult.code] 校验失败
 */
@JvmSynthetic
public suspend fun <T : Any> KookApi<T>.requestData(client: HttpClient, authorization: String): T {
    val result = requestResult(client, authorization)

    if (!result.isSuccess) {
        throw ApiResultException(result, "result.code is not success ($result)")
    }

    if (resultDeserializationStrategy == Unit.serializer()) {
        @Suppress("UNCHECKED_CAST")
        return Unit as T
    }

    return result.parseDataOrThrow(KookApi.DEFAULT_JSON, resultDeserializationStrategy)
}

private fun HttpResponse.requireSuccess(api: KookApi<*>): HttpResponse {
    if (!status.isSuccess()) {
        throw ApiResponseException(this, "API [$api] response status non-successful: $status")
    }

    return this
}

private suspend inline fun KookApi<*>.reqForResp(
    client: HttpClient,
    authorization: String,
    preAction: HttpRequestBuilder.() -> Unit = {},
    postAction: HttpRequestBuilder.() -> Unit = {}
): HttpResponse = client.request {
    preAction()
    this.method = this@reqForResp.method
    url(this@reqForResp.url)
    headers {
        append(HttpHeaders.Authorization, authorization)
        appendAll(this@reqForResp.headers)
    }
    if (HttpHeaders.ContentType !in headers) {
        headers[HttpHeaders.ContentType] = ContentType.Application.Json.toString()
    }

    // set Body
    when (val body = this@reqForResp.body) {
        null -> setBody(EmptyContent)
        is OutgoingContent -> setBody(body)
        else -> {
            if (client.pluginOrNull(ContentNegotiation) != null) {
                setBody(body)
            } else {
                try {
                    val ser = guessSerializer(body, KookApi.DEFAULT_JSON.serializersModule)
                    val bodyJson = KookApi.DEFAULT_JSON.encodeToString(ser, body)
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

    postAction()
}

