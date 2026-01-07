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

@file:JvmName("ApiRequests") @file:JvmMultifileClass

package love.forte.simbot.kook.api

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import love.forte.simbot.suspendrunner.runInAsync
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import kotlin.coroutines.EmptyCoroutineContext


//region HttpClients
/**
 * 直接构建一个 [HttpClient]. 需要环境中存在可用的引擎。
 *
 * @see HttpClient
 */
public fun createHttpClient(): HttpClient = HttpClient()

/**
 * 构建一个 [HttpClient].
 *
 * @param engine 使用的引擎
 * @param config [HttpClient] 构建的配置
 * @see HttpClient
 */
@JvmOverloads
public fun createHttpClient(engine: HttpClientEngine, config: Consumer<HttpClientConfig<*>>? = null): HttpClient =
    HttpClient(engine) { config?.accept(this) }


/**
 * 直接构建一个 [HttpClient].
 *
 * @param engineFactory 引擎工厂。
 * @param config [HttpClient] 的构建配置
 * @see HttpClient
 */
@JvmOverloads
public fun <T : HttpClientEngineConfig> createHttpClient(
    engineFactory: HttpClientEngineFactory<T>, config: Consumer<HttpClientConfig<T>>? = null
): HttpClient = HttpClient(engineFactory) { config?.accept(this) }
//endregion

//region block
/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个 [ApiResult]。
 *
 * 得到原始的 [HttpResponse] 而不对结果有任何处理。
 */
@Api4J
public fun KookApi<*>.requestBlocking(client: HttpClient, authorization: String): HttpResponse =
    runInNoScopeBlocking { request(client, authorization) }

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个结果字符串。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 */
@Api4J
public fun KookApi<*>.requestTextBlocking(client: HttpClient, authorization: String): String =
    runInNoScopeBlocking { requestText(client, authorization) }

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个 [ApiResult] 结果。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 */
@Api4J
public fun KookApi<*>.requestResultBlocking(client: HttpClient, authorization: String): ApiResult =
    runInNoScopeBlocking { requestResult(client, authorization) }

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个具体结果。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 * @throws ApiResultException 请求结果的 [ApiResult.code] 校验失败
 */
@Api4J
public fun <T : Any> KookApi<T>.requestDataBlocking(client: HttpClient, authorization: String): T =
    runInNoScopeBlocking { requestData(client, authorization) }
//endregion

//region async
/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个 [ApiResult]。
 *
 * 得到原始的 [HttpResponse] 而不对结果有任何处理。
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestAsync(client: HttpClient, authorization: String, scope: CoroutineScope? = null): CompletableFuture<HttpResponse> =
    runInAsync(scope ?: client) { request(client, authorization) }

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个结果字符串。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestTextAsync(client: HttpClient, authorization: String, scope: CoroutineScope? = null): CompletableFuture<String> =
    runInAsync(scope ?: client) { requestText(client, authorization) }

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个 [ApiResult] 结果。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestResultAsync(client: HttpClient, authorization: String, scope: CoroutineScope? = null): CompletableFuture<ApiResult> =
    runInAsync(scope ?: client) { requestResult(client, authorization) }

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个具体结果。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 * @throws ApiResultException 请求结果的 [ApiResult.code] 校验失败
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun <T : Any> KookApi<T>.requestDataAsync(client: HttpClient, authorization: String, scope: CoroutineScope? = null): CompletableFuture<T> =
    runInAsync(scope ?: client) { requestData(client, authorization) }
//endregion

//region reserve
/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个 [ApiResult]。
 *
 * 得到原始的 [HttpResponse] 而不对结果有任何处理。
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestReserve(client: HttpClient, authorization: String, scope: CoroutineScope? = null): SuspendReserve<HttpResponse> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) { request(client, authorization) }

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个结果字符串。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestTextReserve(client: HttpClient, authorization: String, scope: CoroutineScope? = null): SuspendReserve<String> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) { requestText(client, authorization) }

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个 [ApiResult] 结果。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestResultReserve(client: HttpClient, authorization: String, scope: CoroutineScope? = null): SuspendReserve<ApiResult> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) { requestResult(client, authorization) }

/**
 * 通过一个 [HttpClient] 和校验信息 [authorization] 对当前API发起请求，并得到一个具体结果。
 *
 * @throws ApiResponseException 请求结果的状态码不是 200..300 之间
 * @throws ApiResultException 请求结果的 [ApiResult.code] 校验失败
 */
@Api4J
@JvmOverloads
@OptIn(InternalSimbotAPI::class)
public fun <T : Any> KookApi<T>.requestDataReserve(client: HttpClient, authorization: String, scope: CoroutineScope? = null): SuspendReserve<T> =
    suspendReserve(scope ?: client, EmptyCoroutineContext) { requestData(client, authorization) }
//endregion





