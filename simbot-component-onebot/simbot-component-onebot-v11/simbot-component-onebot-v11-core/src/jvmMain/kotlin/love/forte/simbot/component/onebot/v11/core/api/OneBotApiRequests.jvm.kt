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
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.Charset
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import love.forte.simbot.suspendrunner.runInAsync
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.text.Charsets

/**
 * 校验当前运行时是否存在
 * [ContentNegotiation][io.ktor.client.plugins.contentnegotiation.ContentNegotiation]。
 */
internal actual val isContentNegotiationRuntimeAvailable: Boolean by lazy {
    runCatching {
        Class.forName(CONTENT_NEGOTIATION_CLASS)
        true
    }.getOrDefault(false)
}

private const val CONTENT_NEGOTIATION_CLASS = "io.ktor.client.plugins.contentnegotiation.ContentNegotiation"

//region blocking

/**
 * 阻塞地请求 [BasicOneBotApi].
 * @see request
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestBlocking(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
): HttpResponse = runInNoScopeBlocking { request(client, host, accessToken, actionSuffixes) }

/**
 * 阻塞地请求 [BasicOneBotApi].
 * @see request
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestBlocking(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
): HttpResponse = runInNoScopeBlocking { request(client, host, accessToken, actionSuffixes) }

/**
 * 阻塞地请求 [BasicOneBotApi].
 * @see requestRaw
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestRawBlocking(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8
): String = runInNoScopeBlocking { requestRaw(client, host, accessToken, actionSuffixes, charset) }

/**
 * 阻塞地请求 [BasicOneBotApi].
 * @see requestRaw
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestRawBlocking(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8
): String = runInNoScopeBlocking { requestRaw(client, host, accessToken, actionSuffixes, charset) }

/**
 * 阻塞地请求 [BasicOneBotApi].
 * @see requestResult
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestResultBlocking(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    decoder: Json = OneBot11.DefaultJson,
): OneBotApiResult<T> = runInNoScopeBlocking {
    requestResult(
        client,
        host,
        accessToken,
        actionSuffixes,
        charset,
        decoder
    )
}

/**
 * 阻塞地请求 [BasicOneBotApi].
 * @see requestResult
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestResultBlocking(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    decoder: Json = OneBot11.DefaultJson,
): OneBotApiResult<T> = runInNoScopeBlocking {
    requestResult(
        client,
        host,
        accessToken,
        actionSuffixes,
        charset,
        decoder
    )
}

/**
 * 阻塞地请求 [BasicOneBotApi].
 * @see requestData
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestDataBlocking(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    decoder: Json = OneBot11.DefaultJson,
): T = runInNoScopeBlocking {
    requestData(
        client,
        host,
        accessToken,
        actionSuffixes,
        charset,
        decoder
    )
}

/**
 * 阻塞地请求 [BasicOneBotApi].
 * @see requestData
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestDataBlocking(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    decoder: Json = OneBot11.DefaultJson,
): T = runInNoScopeBlocking {
    requestData(
        client,
        host,
        accessToken,
        actionSuffixes,
        charset,
        decoder
    )
}
//endregion

//region async

/**
 * 异步地请求 [BasicOneBotApi].
 * @see request
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestAsync(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    scope: CoroutineScope? = null,
): CompletableFuture<out HttpResponse> =
    runInAsync(scope ?: client) { request(client, host, accessToken, actionSuffixes) }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see request
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestAsync(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    scope: CoroutineScope? = null,
): CompletableFuture<out HttpResponse> =
    runInAsync(scope ?: client) { request(client, host, accessToken, actionSuffixes) }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestRaw
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestRawAsync(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
): CompletableFuture<out String> =
    runInAsync(scope ?: client) { requestRaw(client, host, accessToken, actionSuffixes, charset) }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestRaw
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestRawAsync(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
): CompletableFuture<out String> =
    runInAsync(scope ?: client) { requestRaw(client, host, accessToken, actionSuffixes, charset) }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestResult
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestResultAsync(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
    decoder: Json = OneBot11.DefaultJson,
): CompletableFuture<out OneBotApiResult<T>> =
    runInAsync(scope ?: client) {
        requestResult(
            client,
            host,
            accessToken,
            actionSuffixes,
            charset,
            decoder
        )
    }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestResult
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestResultAsync(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
    decoder: Json = OneBot11.DefaultJson,
): CompletableFuture<out OneBotApiResult<T>> =
    runInAsync(scope ?: client) {
        requestResult(
            client,
            host,
            accessToken,
            actionSuffixes,
            charset,
            decoder
        )
    }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestData
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestDataAsync(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
    decoder: Json = OneBot11.DefaultJson,
): CompletableFuture<out T> =
    runInAsync(scope ?: client) {
        requestData(
            client,
            host,
            accessToken,
            actionSuffixes,
            charset,
            decoder
        )
    }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestData
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestDataAsync(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
    decoder: Json = OneBot11.DefaultJson,
): CompletableFuture<out T> =
    runInAsync(scope ?: client) {
        requestData(
            client,
            host,
            accessToken,
            actionSuffixes,
            charset,
            decoder
        )
    }
//endregion

//region suspend reserve

/**
 * 异步地请求 [BasicOneBotApi].
 * @see request
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestReserve(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    scope: CoroutineScope? = null,
): SuspendReserve<HttpResponse> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) { request(client, host, accessToken, actionSuffixes) }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see request
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestReserve(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    scope: CoroutineScope? = null,
): SuspendReserve<HttpResponse> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) { request(client, host, accessToken, actionSuffixes) }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestRaw
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestRawReserve(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
): SuspendReserve<String> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) {
        requestRaw(
            client,
            host,
            accessToken,
            actionSuffixes,
            charset
        )
    }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestRaw
 */
@Api4J
@JvmOverloads
public fun BasicOneBotApi<*>.requestRawReserve(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
): SuspendReserve<String> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) {
        requestRaw(
            client,
            host,
            accessToken,
            actionSuffixes,
            charset
        )
    }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestResult
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestResultReserve(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
    decoder: Json = OneBot11.DefaultJson,
): SuspendReserve<OneBotApiResult<T>> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) {
        requestResult(
            client,
            host,
            accessToken,
            actionSuffixes,
            charset,
            decoder
        )
    }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestResult
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestResultReserve(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
    decoder: Json = OneBot11.DefaultJson,
): SuspendReserve<OneBotApiResult<T>> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) {
        requestResult(
            client,
            host,
            accessToken,
            actionSuffixes,
            charset,
            decoder
        )
    }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestData
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestDataReserve(
    client: HttpClient,
    host: Url,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
    decoder: Json = OneBot11.DefaultJson,
): SuspendReserve<T> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) {
        requestData(
            client,
            host,
            accessToken,
            actionSuffixes,
            charset,
            decoder
        )
    }

/**
 * 异步地请求 [BasicOneBotApi].
 * @see requestData
 */
@Api4J
@JvmOverloads
public fun <T : Any> BasicOneBotApi<T>.requestDataReserve(
    client: HttpClient,
    host: String,
    accessToken: String? = null,
    actionSuffixes: Collection<String>? = null,
    charset: Charset = Charsets.UTF_8,
    scope: CoroutineScope? = null,
    decoder: Json = OneBot11.DefaultJson,
): SuspendReserve<T> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) {
        requestData(
            client,
            host,
            accessToken,
            actionSuffixes,
            charset,
            decoder
        )
    }
//endregion


internal actual fun initConfig(key: String, default: String?): String? =
    System.getProperty(key) ?: default
