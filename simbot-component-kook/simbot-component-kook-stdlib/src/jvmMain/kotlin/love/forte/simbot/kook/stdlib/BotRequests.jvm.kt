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

@file:JvmName("BotRequests")
@file:JvmMultifileClass

package love.forte.simbot.kook.stdlib

import io.ktor.client.statement.*
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.kook.api.ApiResult
import love.forte.simbot.kook.api.KookApi
import love.forte.simbot.suspendrunner.asReserve
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.runInAsync
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestBy
 * @since 4.1.1
 */
@Api4J
public fun KookApi<*>.requestByBlocking(bot: Bot): HttpResponse =
    runInNoScopeBlocking { requestBy(bot) }

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestByAsync(bot: Bot): CompletableFuture<out HttpResponse> =
    runInAsync(scope = bot) { requestBy(bot) }

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestByReserve(bot: Bot): SuspendReserve<HttpResponse> =
    asReserve(scope = bot) { requestBy(bot) }

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestTextBy
 * @since 4.1.1
 */
@Api4J
public fun KookApi<*>.requestTextByBlocking(bot: Bot): String =
    runInNoScopeBlocking { requestTextBy(bot) }

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestTextBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestTextByAsync(bot: Bot): CompletableFuture<out String> =
    runInAsync(scope = bot) { requestTextBy(bot) }

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestTextBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestTextByReserve(bot: Bot): SuspendReserve<String> =
    asReserve(scope = bot) { requestTextBy(bot) }

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestResultBy
 * @since 4.1.1
 */
@Api4J
public fun KookApi<*>.requestResultByBlocking(bot: Bot): ApiResult =
    runInNoScopeBlocking { requestResultBy(bot) }

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestResultBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestResultByAsync(bot: Bot): CompletableFuture<out ApiResult> =
    runInAsync(scope = bot) { requestResultBy(bot) }

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestResultBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun KookApi<*>.requestResultByReserve(bot: Bot): SuspendReserve<ApiResult> =
    asReserve(scope = bot) { requestResultBy(bot) }

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestDataBy
 * @since 4.1.1
 */
@Api4J
public fun <T : Any> KookApi<T>.requestDataByBlocking(bot: Bot): T =
    runInNoScopeBlocking { requestDataBy(bot) }

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestDataBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun <T : Any> KookApi<T>.requestDataByAsync(bot: Bot): CompletableFuture<out T> =
    runInAsync(scope = bot) { requestDataBy(bot) }

/**
 * 使用 [Bot] 向 [KookApi] 发起请求。
 * @see KookApi.requestDataBy
 * @since 4.1.1
 */
@Api4J
@OptIn(InternalSimbotAPI::class)
public fun <T : Any> KookApi<T>.requestDataByReserve(bot: Bot): SuspendReserve<T> =
    asReserve(scope = bot) { requestDataBy(bot) }
