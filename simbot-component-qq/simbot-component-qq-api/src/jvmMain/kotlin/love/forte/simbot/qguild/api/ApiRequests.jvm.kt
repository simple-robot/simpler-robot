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

@file:JvmName("ApiRequests")
@file:JvmMultifileClass

package love.forte.simbot.qguild.api

import io.ktor.client.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.qguild.common.QQ
import love.forte.simbot.suspendrunner.asPublisher
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 用于在Java中构建一个默认的 [HttpClient]。
 */
@Api4J
public fun newHttpClient(): HttpClient = HttpClient()

/**
 * 用于在Java中构建一个 [Json]。
 */
@Api4J
@JvmOverloads
public fun newJson(
    build: Consumer<JsonBuilder> = Consumer {
        it.apply {
            isLenient = true
            ignoreUnknownKeys = true
        }
    },
): Json = Json {
    build.accept(this)
}


/**
 * [QQGuildApi.request] for Java
 *
 */
@Api4J
@JvmOverloads
public fun QQGuildApi<*>.requestBlocking(
    client: HttpClient,
    token: String?,
    server: Url = Url(QQ.URL),
    appId: String? = null,
): HttpResponse = runInNoScopeBlocking {
    request(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestText] for Java
 *
 */
@Api4J
@JvmOverloads
public fun QQGuildApi<*>.requestTextBlocking(
    client: HttpClient,
    token: String?,
    server: Url = Url(QQ.URL),
    appId: String? = null,
): String = runInNoScopeBlocking {
    requestText(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestData] for Java
 */
@Api4J
@JvmOverloads
public fun <R : Any> QQGuildApi<R>.requestDataBlocking(
    client: HttpClient,
    token: String?,
    server: Url = Url(QQ.URL),
    appId: String? = null,
): R = runInNoScopeBlocking {
    requestData(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.request] for Java
 *
 */
@Api4J
@JvmOverloads
public fun QQGuildApi<*>.requestAsync(
    client: HttpClient,
    token: String,
    server: Url = Url(QQ.URL),
    scope: CoroutineScope? = null,
    appId: String? = null,
): CompletableFuture<HttpResponse> = (scope ?: client).future {
    request(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestText] for Java
 *
 */
@Api4J
@JvmOverloads
public fun QQGuildApi<*>.requestTextAsync(
    client: HttpClient,
    token: String,
    server: Url = Url(QQ.URL),
    scope: CoroutineScope? = null,
    appId: String? = null,
): CompletableFuture<String> = (scope ?: client).future {
    requestText(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestData] for Java
 */
@Api4J
@JvmOverloads
public fun <R : Any> QQGuildApi<R>.requestDataAsync(
    client: HttpClient,
    token: String,
    server: Url = Url(QQ.URL),
    scope: CoroutineScope? = null,
    appId: String? = null,
): CompletableFuture<R> = (scope ?: client).future {
    requestData(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.request] for Java
 *
 * @see SuspendReserve
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun QQGuildApi<*>.requestReserve(
    client: HttpClient,
    token: String,
    server: Url = Url(QQ.URL),
    scope: CoroutineScope? = null,
    appId: String? = null,
): SuspendReserve<HttpResponse> = suspendReserve(scope = scope ?: client, context = EmptyCoroutineContext) {
    request(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestText] for Java
 *
 * @see SuspendReserve
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun QQGuildApi<*>.requestTextReserve(
    client: HttpClient,
    token: String,
    server: Url = Url(QQ.URL),
    scope: CoroutineScope? = null,
    appId: String? = null,
): SuspendReserve<String> = suspendReserve(scope = scope ?: client, context = EmptyCoroutineContext) {
    requestText(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestData] for Java
 *
 * @see SuspendReserve
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun <R : Any> QQGuildApi<R>.requestDataReserve(
    client: HttpClient,
    token: String,
    server: Url = Url(QQ.URL),
    scope: CoroutineScope? = null,
    appId: String? = null,
): SuspendReserve<R> = suspendReserve(scope = scope ?: client, context = EmptyCoroutineContext) {
    requestData(client = client, token = token, server = server, appId = appId)
}

/**
 * 以响应式方式请求并获取原始 HTTP 响应。每次订阅都会发起一次请求。
 * 请求失败或响应反序列化失败时，错误也会通过 Publisher 传递给订阅者。
 *
 * @param client 用于本次请求的 HTTP 客户端。
 * @param token 用于本次请求鉴权的 token。
 * @param server 请求目标服务器。
 * @param appId 应用 ID，提供时会添加到请求头。
 * @return 在订阅时执行请求的 Publisher。
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun QQGuildApi<*>.requestReactive(
    client: HttpClient,
    token: String,
    server: Url = Url(QQ.URL),
    appId: String? = null,
): Publisher<HttpResponse> = asPublisher {
    request(client = client, token = token, server = server, appId = appId)
}

/**
 * 以响应式方式请求并获取响应文本。每次订阅都会发起一次请求。
 * 请求失败或响应反序列化失败时，错误也会通过 Publisher 传递给订阅者。
 *
 * @param client 用于本次请求的 HTTP 客户端。
 * @param token 用于本次请求鉴权的 token。
 * @param server 请求目标服务器。
 * @param appId 应用 ID，提供时会添加到请求头。
 * @return 在订阅时执行请求的 Publisher。
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun QQGuildApi<*>.requestTextReactive(
    client: HttpClient,
    token: String,
    server: Url = Url(QQ.URL),
    appId: String? = null,
): Publisher<String> = asPublisher {
    requestText(client = client, token = token, server = server, appId = appId)
}

/**
 * 以响应式方式请求并获取反序列化结果。每次订阅都会发起一次请求。
 * 请求失败或响应反序列化失败时，错误也会通过 Publisher 传递给订阅者。
 *
 * @param client 用于本次请求的 HTTP 客户端。
 * @param token 用于本次请求鉴权的 token。
 * @param server 请求目标服务器。
 * @param appId 应用 ID，提供时会添加到请求头。
 * @return 在订阅时执行请求的 Publisher。
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun <R : Any> QQGuildApi<R>.requestDataReactive(
    client: HttpClient,
    token: String,
    server: Url = Url(QQ.URL),
    appId: String? = null,
): Publisher<R> = asPublisher {
    requestData(client = client, token = token, server = server, appId = appId)
}
