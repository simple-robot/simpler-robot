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

@file:JvmName("BotRequests")
@file:JvmMultifileClass

package love.forte.simbot.qguild.stdlib

import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.suspendrunner.asPublisher
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 直接通过bot进行请求。
 */
@Api4J
public fun Bot.requestBlocking(api: QQGuildApi<*>): HttpResponse = runInNoScopeBlocking {
    request(api)
}

/**
 * 直接通过bot进行请求。
 */
@Api4J
public fun Bot.requestTextBlocking(api: QQGuildApi<*>): String = runInNoScopeBlocking {
    requestText(api)
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
public fun <R : Any> Bot.requestDataBlocking(api: QQGuildApi<R>): R = runInNoScopeBlocking {
    requestData(api)
}


/**
 * 直接通过bot进行请求。
 *
 * 异步请求使用的作用域 [scope] 默认为 [Bot.apiClient]。
 */
@Api4J
@JvmOverloads
public fun Bot.requestAsync(api: QQGuildApi<*>, scope: CoroutineScope? = null): CompletableFuture<HttpResponse> =
    (scope ?: apiClient).future {
        request(api)
    }

/**
 * 直接通过bot进行请求。
 *
 * 异步请求使用的作用域 [scope] 默认为 [Bot.apiClient]。
 */
@Api4J
@JvmOverloads
public fun Bot.requestTextAsync(api: QQGuildApi<*>, scope: CoroutineScope? = null): CompletableFuture<String> =
    (scope ?: apiClient).future {
        requestText(api)
    }

/**
 * 直接通过bot进行请求。
 *
 * 异步请求使用的作用域 [scope] 默认为 [Bot.apiClient]。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@Api4J
@JvmOverloads
public fun <R : Any> Bot.requestDataAsync(
    api: QQGuildApi<R>,
    scope: CoroutineScope? = null
): CompletableFuture<R> = (scope ?: apiClient).future {
    requestData(api)
}


/**
 * 直接通过bot进行请求。
 *
 * [SuspendReserve] 可能使用的作用域 [scope] 默认为 [Bot.apiClient]。
 *
 * @see SuspendReserve
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun Bot.requestReserve(api: QQGuildApi<*>, scope: CoroutineScope? = null): SuspendReserve<HttpResponse> =
    suspendReserve(scope ?: apiClient, EmptyCoroutineContext) {
        request(api)
    }

/**
 * 直接通过bot进行请求。
 *
 * [SuspendReserve] 可能使用的作用域 [scope] 默认为 [Bot.apiClient]。
 *
 * @see SuspendReserve
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun Bot.requestTextReserve(api: QQGuildApi<*>, scope: CoroutineScope? = null): SuspendReserve<String> =
    suspendReserve(scope ?: apiClient, EmptyCoroutineContext) {
        requestText(api)
    }

/**
 * 直接通过bot进行请求。
 *
 * [SuspendReserve] 可能使用的作用域 [scope] 默认为 [Bot.apiClient]。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 *
 * @see SuspendReserve
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun <R : Any> Bot.requestDataReserve(
    api: QQGuildApi<R>,
    scope: CoroutineScope? = null
): SuspendReserve<R> = suspendReserve(scope ?: apiClient, EmptyCoroutineContext) {
    requestData(api)
}

/**
 * 使用此 bot 以响应式方式请求并获取原始 HTTP 响应。每次订阅都会发起一次请求。
 * 请求失败或响应反序列化失败时，错误也会通过 Publisher 传递给订阅者。
 *
 * @param api 要执行的 API。
 * @return 在订阅时执行请求的 Publisher。
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
@OptIn(InternalSimbotAPI::class)
public fun Bot.requestReactive(api: QQGuildApi<*>): Publisher<HttpResponse> = asPublisher {
    request(api)
}

/**
 * 使用此 bot 以响应式方式请求并获取响应文本。每次订阅都会发起一次请求。
 * 请求失败或响应反序列化失败时，错误也会通过 Publisher 传递给订阅者。
 *
 * @param api 要执行的 API。
 * @return 在订阅时执行请求的 Publisher。
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
@OptIn(InternalSimbotAPI::class)
public fun Bot.requestTextReactive(api: QQGuildApi<*>): Publisher<String> = asPublisher {
    requestText(api)
}

/**
 * 使用此 bot 以响应式方式请求并获取反序列化结果。每次订阅都会发起一次请求。
 * 请求失败或响应反序列化失败时，错误也会通过 Publisher 传递给订阅者。
 *
 * @param api 要执行的 API。
 * @return 在订阅时执行请求的 Publisher。
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
@OptIn(InternalSimbotAPI::class)
public fun <R : Any> Bot.requestDataReactive(api: QQGuildApi<R>): Publisher<R> = asPublisher {
    requestData(api)
}
