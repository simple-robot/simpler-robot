/*
 *     Copyright (c) 2023-2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.suspendrunner

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.suspendrunner.reserve.SuspendReserve
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.js.Promise


/**
 * 执行一个异步函数，得到 [Promise].
 */
@InternalSimbotAPI
public inline fun <T> runInPromise(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend CoroutineScope.() -> T,
): Promise<T> =
    scope.promise(context) { block() }

/**
 * 使用 [GlobalScope] 执行一个异步函数，得到 [Promise].
 */
@DelicateCoroutinesApi
@InternalSimbotAPI
public inline fun <T> runInPromise(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend CoroutineScope.() -> T,
): Promise<T> = runInPromise(scope = GlobalScope, context, block)

/**
 * 使用 [GlobalScope] 执行一个异步函数，得到 [Promise].
 */
@OptIn(DelicateCoroutinesApi::class)
@Suppress("FunctionName")
@InternalSimbotAPI
@Deprecated("Just used by compiler", level = DeprecationLevel.HIDDEN)
public fun <T> `$$runInPromise`(
    scope: CoroutineScope? = null,
    block: suspend () -> T
): Promise<T> = runInPromise(scope = scope ?: GlobalScope, EmptyCoroutineContext) { block() }


/**
 * @see SuspendReserve
 */
@InternalSimbotAPI
@OptIn(DelicateCoroutinesApi::class)
public fun <T> asReserve(
    scope: CoroutineScope? = null,
    context: CoroutineContext? = null,
    block: suspend () -> T
): SuspendReserve<T> =
    suspendReserve(scope = scope ?: GlobalScope, context = context ?: EmptyCoroutineContext, block = block)

/**
 * @see asReserve
 */
@Suppress("FunctionName")
@InternalSimbotAPI
@Deprecated("Just used by compiler", level = DeprecationLevel.HIDDEN)
public fun <T> `$$asReserve`(scope: CoroutineScope? = null, block: suspend () -> T): SuspendReserve<T> =
    asReserve(scope = scope, context = EmptyCoroutineContext, block = block)
