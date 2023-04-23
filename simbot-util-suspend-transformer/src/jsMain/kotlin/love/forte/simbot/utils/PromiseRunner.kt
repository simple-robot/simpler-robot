/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.utils

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import love.forte.simbot.InternalSimbotApi
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.js.Promise

@Suppress("unused", "ObjectPropertyName")
private val `$$DefaultScope`: CoroutineScope by lazy {
    CoroutineScope(CoroutineName("Default-Promise"))
}

/**
 * 执行一个异步函数，得到 [Promise].
 */
@InternalSimbotApi
public fun <T> runInPromise(
    scope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T,
): Promise<T> =
    scope.promise(context) { block() }

/**
 * 使用一个内部默认的 [CoroutineScope] 执行一个异步函数，得到 [Promise].
 */
@InternalSimbotApi
public fun <T> runInPromise(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> T,
): Promise<T> = runInPromise(scope = `$$DefaultScope`, context, block)
