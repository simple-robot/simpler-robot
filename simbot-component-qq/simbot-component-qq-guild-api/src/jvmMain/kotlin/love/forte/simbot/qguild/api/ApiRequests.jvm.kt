/*
 * Copyright (c) 2023-2024. ForteScarlet.
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
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.qguild.QGApi4J
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 用于在Java中构建一个默认的 [HttpClient]。
 */
@QGApi4J
public fun newHttpClient(): HttpClient = HttpClient()

/**
 * 用于在Java中构建一个 [Json]。
 */
@QGApi4J
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
@QGApi4J
@JvmOverloads
public fun QQGuildApi<*>.requestBlocking(
    client: HttpClient,
    token: String?,
    server: Url = QQGuild.URL,
    appId: String? = null,
): HttpResponse = runInNoScopeBlocking {
    request(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestText] for Java
 *
 */
@QGApi4J
@JvmOverloads
public fun QQGuildApi<*>.requestTextBlocking(
    client: HttpClient,
    token: String?,
    server: Url = QQGuild.URL,
    appId: String? = null,
): String = runInNoScopeBlocking {
    requestText(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestData] for Java
 */
@QGApi4J
@JvmOverloads
public fun <R : Any> QQGuildApi<R>.requestDataBlocking(
    client: HttpClient,
    token: String?,
    server: Url = QQGuild.URL,
    appId: String? = null,
): R = runInNoScopeBlocking {
    requestData(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.request] for Java
 *
 */
@QGApi4J
@JvmOverloads
public fun QQGuildApi<*>.requestAsync(
    client: HttpClient,
    token: String,
    server: Url = QQGuild.URL,
    scope: CoroutineScope? = null,
    appId: String? = null,
): CompletableFuture<HttpResponse> = (scope ?: client).future {
    request(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestText] for Java
 *
 */
@QGApi4J
@JvmOverloads
public fun QQGuildApi<*>.requestTextAsync(
    client: HttpClient,
    token: String,
    server: Url = QQGuild.URL,
    scope: CoroutineScope? = null,
    appId: String? = null,
): CompletableFuture<String> = (scope ?: client).future {
    requestText(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestData] for Java
 */
@QGApi4J
@JvmOverloads
public fun <R : Any> QQGuildApi<R>.requestDataAsync(
    client: HttpClient,
    token: String,
    server: Url = QQGuild.URL,
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
@QGApi4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun QQGuildApi<*>.requestReserve(
    client: HttpClient,
    token: String,
    server: Url = QQGuild.URL,
    scope: CoroutineScope? = null,
    appId: String? = null,
): SuspendReserve<HttpResponse> = suspendReserve(scope = (scope ?: client), context = EmptyCoroutineContext) {
    request(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestText] for Java
 *
 * @see SuspendReserve
 */
@QGApi4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun QQGuildApi<*>.requestTextReserve(
    client: HttpClient,
    token: String,
    server: Url = QQGuild.URL,
    scope: CoroutineScope? = null,
    appId: String? = null,
): SuspendReserve<String> = suspendReserve(scope = (scope ?: client), context = EmptyCoroutineContext) {
    requestText(client = client, token = token, server = server, appId = appId)
}

/**
 * [QQGuildApi.requestData] for Java
 *
 * @see SuspendReserve
 */
@QGApi4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun <R : Any> QQGuildApi<R>.requestDataReserve(
    client: HttpClient,
    token: String,
    server: Url = QQGuild.URL,
    scope: CoroutineScope? = null,
    appId: String? = null,
): SuspendReserve<R> = suspendReserve(scope = (scope ?: client), context = EmptyCoroutineContext) {
    requestData(client = client, token = token, server = server, appId = appId)
}
