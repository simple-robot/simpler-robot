/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

@file:JvmName("KookBotRequests")
@file:JvmMultifileClass

package love.forte.simbot.component.kook.util

import io.ktor.client.statement.*
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.kook.api.ApiResult
import love.forte.simbot.kook.api.KookApi
import love.forte.simbot.suspendrunner.asReserve
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.runInAsync
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture

//region request

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.request
 * @since 4.1.1
 */
@Api4J
public fun KookBot.requestBlocking(api: KookApi<*>): HttpResponse =
    runInNoScopeBlocking { request(api) }

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.request
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookBot.requestAsync(api: KookApi<*>): CompletableFuture<out HttpResponse> =
    runInAsync(scope = this) { request(api) }

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.request
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookBot.requestReserve(api: KookApi<*>): SuspendReserve<HttpResponse> =
    asReserve(scope = this) { request(api) }

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.requestData
 * @since 4.1.1
 */
@Api4J
public fun <T : Any> KookBot.requestDataBlocking(api: KookApi<T>): T =
    runInNoScopeBlocking { requestData(api) }

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.requestData
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun <T : Any> KookBot.requestDataAsync(api: KookApi<T>): CompletableFuture<out T> =
    runInAsync(scope = this) { requestData(api) }

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.requestData
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun <T : Any> KookBot.requestDataReserve(api: KookApi<T>): SuspendReserve<T> =
    asReserve(scope = this) { requestData(api) }

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.requestText
 * @since 4.1.1
 */
@Api4J
public fun KookBot.requestTextBlocking(api: KookApi<*>): String =
    runInNoScopeBlocking { requestText(api) }

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.requestText
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookBot.requestTextAsync(api: KookApi<*>): CompletableFuture<out String> =
    runInAsync(scope = this) { requestText(api) }

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.requestText
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookBot.requestTextReserve(api: KookApi<*>): SuspendReserve<String> =
    asReserve(scope = this) { requestText(api) }

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.requestResult
 * @since 4.1.1
 */
@Api4J
public fun KookBot.requestResultBlocking(api: KookApi<*>): ApiResult =
    runInNoScopeBlocking { requestResult(api) }

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.requestResult
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookBot.requestResultAsync(api: KookApi<*>): CompletableFuture<out ApiResult> =
    runInAsync(scope = this) { requestResult(api) }

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 * @see KookBot.requestResult
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookBot.requestResultReserve(api: KookApi<*>): SuspendReserve<ApiResult> =
    asReserve(scope = this) { requestResult(api) }
//endregion

//region requestBy

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestBy
 * @since 4.1.1
 */
@Api4J
public fun KookApi<*>.requestByBlocking(bot: KookBot): HttpResponse =
    runInNoScopeBlocking { requestBy(bot) }

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestByAsync(bot: KookBot): CompletableFuture<out HttpResponse> =
    runInAsync(scope = bot) { requestBy(bot) }

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestByReserve(bot: KookBot): SuspendReserve<HttpResponse> =
    asReserve(scope = bot) { requestBy(bot) }

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestDataBy
 * @since 4.1.1
 */
@Api4J
public fun <T : Any> KookApi<T>.requestDataByBlocking(bot: KookBot): T =
    runInNoScopeBlocking { requestDataBy(bot) }

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestDataBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun <T : Any> KookApi<T>.requestDataByAsync(bot: KookBot): CompletableFuture<out T> =
    runInAsync(scope = bot) { requestDataBy(bot) }

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestDataBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun <T : Any> KookApi<T>.requestDataByReserve(bot: KookBot): SuspendReserve<T> =
    asReserve(scope = bot) { requestDataBy(bot) }

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestTextBy
 * @since 4.1.1
 */
@Api4J
public fun KookApi<*>.requestTextByBlocking(bot: KookBot): String =
    runInNoScopeBlocking { requestTextBy(bot) }

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestTextBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestTextByAsync(bot: KookBot): CompletableFuture<out String> =
    runInAsync(scope = bot) { requestTextBy(bot) }

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestTextBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestTextByReserve(bot: KookBot): SuspendReserve<String> =
    asReserve(scope = bot) { requestTextBy(bot) }

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestResultBy
 * @since 4.1.1
 */
@Api4J
public fun KookApi<*>.requestResultByBlocking(bot: KookBot): ApiResult =
    runInNoScopeBlocking { requestResultBy(bot) }

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestResultBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestResultByAsync(bot: KookBot): CompletableFuture<out ApiResult> =
    runInAsync(scope = bot) { requestResultBy(bot) }

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 * @see KookApi.requestResultBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestResultByReserve(bot: KookBot): SuspendReserve<ApiResult> =
    asReserve(scope = bot) { requestResultBy(bot) }
//endregion
