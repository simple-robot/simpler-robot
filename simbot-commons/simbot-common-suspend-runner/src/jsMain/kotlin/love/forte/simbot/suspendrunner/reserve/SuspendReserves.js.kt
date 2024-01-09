/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.suspendrunner.reserve

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext
import kotlin.js.Promise

/**
 * 得到一个将结果转化为 [Promise] 的异步执行转化器。
 */
@Suppress("UNCHECKED_CAST")
public fun <T> promise(): SuspendReserve.Transformer<T, Promise<T>> =
    AsyncTransformer as SuspendReserve.Transformer<T, Promise<T>>

/**
 * 得到一个将结果转化为 [Promise] 的异步执行转化器。
 */
@Suppress("UNCHECKED_CAST")
@JsName("toPromise")
public fun <T> SuspendReserve<T>.promise(): Promise<T> =
    transform(AsyncTransformer as SuspendReserve.Transformer<T, Promise<T>>)


private object AsyncTransformer : SuspendReserve.Transformer<Any?, Promise<*>> {
    override fun <T1> invoke(
        scope: CoroutineScope,
        context: CoroutineContext,
        block: suspend () -> T1
    ): Promise<*> = scope.promise(context) { block() }
}
