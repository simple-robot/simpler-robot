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

package love.forte.simbot.common.async

import kotlinx.coroutines.*
import love.forte.simbot.common.function.Action
import kotlin.js.Promise

/**
 * [Async] 类在 JS 平台的具体实现。
 *
 * 为了帮助在 JS 平台上的异步操作，[Async] 提供了方法 [asPromise] 来获取 [Promise] 表示的异步操作。
 *
 * @see Deferred
 */
public actual class Async<out T> @PublishedApi internal actual constructor(public actual val deferred: Deferred<T>) {

    /**
     * 操作是否正在进行。
     *
     * @see Deferred.isActive
     * @return 操作正在进行则为 true，否则为 false
     */
    public actual val isActive: Boolean
        get() = deferred.isActive

    /**
     * 操作是否已完成。
     *
     * @see Deferred.isCompleted
     * @return 操作已完成则为 true，否则为 false
     */
    public actual val isCompleted: Boolean
        get() = deferred.isCompleted

    /**
     * 操作是否已被取消。
     *
     * @see Deferred.isCancelled
     * @return 操作已被取消则为 true，否则为 false
     */
    public actual val isCancelled: Boolean
        get() = deferred.isCompleted

    /**
     * 当发生错误时的处理函数。
     *
     * [handler] 不会处理类型为 [CancellationException] 的异常。
     * 如果需要处理 [CancellationException]，请参考使用 [onCancellation] 或 [handle]。
     *
     * 处理函数会在任务完成的时候被立即调用，且会短时间内完成执行。
     * 处理函数需要保证不抛出任何异常，否则这些异常可以被捕获并作为 [kotlinx.coroutines.CompletionHandlerException] 重新抛出。
     *
     * @return 可释放的句柄，用于删除处理函数
     * @see Job.invokeOnCompletion
     */
    public actual fun onError(handler: Action<Throwable>): DisposableHandle {
        return deferred.invokeOnCompletion { e ->
            if (e is Throwable && e !is CancellationException) {
                handler.invoke(e)
            }
        }
    }

    /**
     * 当取消执行时的处理函数。
     *
     * 处理函数会在任务完成的时候被立即调用，且会短时间内完成执行。
     * 处理函数需要保证不抛出任何异常，否则这些异常可以被捕获并作为 [kotlinx.coroutines.CompletionHandlerException] 重新抛出。
     *
     * @return 可释放的句柄，用于删除处理函数
     * @see Job.invokeOnCompletion
     */
    public actual fun onCancellation(handler: Action<CancellationException>): DisposableHandle {
        return deferred.invokeOnCompletion { e ->
            if (e is CancellationException) {
                handler.invoke(e)
            }
        }
    }

    /**
     * 当完成执行时的处理函数。
     *
     * 处理函数会在任务完成的时候被立即调用，且会短时间内完成执行。
     * 处理函数需要保证不抛出任何异常，否则这些异常可以被捕获并作为 [kotlinx.coroutines.CompletionHandlerException] 重新抛出。
     *
     * @return 可释放的句柄，用于删除处理函数
     * @see Job.invokeOnCompletion
     */
    @ExperimentalCoroutinesApi
    public actual fun onCompletion(handler: Action<T>): DisposableHandle {
        return deferred.invokeOnCompletion { e ->
            if (e == null) {
                handler.invoke(deferred.getCompleted())
            }
        }
    }

    /**
     * 设置异步处理函数。
     *
     * 处理函数会在任务完成的时候被立即调用，且会短时间内完成执行。
     * 处理函数需要保证不抛出任何异常，否则这些异常可以被捕获并作为 [kotlinx.coroutines.CompletionHandlerException] 重新抛出。
     *
     * @return 可释放的句柄，用于删除处理函数
     * @see Job.invokeOnCompletion
     */
    public actual fun handle(handler: Action<Throwable?>): DisposableHandle {
        return deferred.invokeOnCompletion { e ->
            handler.invoke(e)
        }
    }

    /**
     * 取消异步操作的执行。
     *
     * @see Deferred.cancel
     */
    public actual fun cancel() {
        deferred.cancel()
    }

    /**
     * 以给定的原因取消异步操作的执行。
     *
     * @see Deferred.cancel
     */
    public actual fun cancelBy(cause: CancellationException?) {
        deferred.cancel(cause)
    }

    /**
     * 将 [Async] 对象转换为 [Promise]。
     *
     * @return 表示同一异步操作的 [Promise] 对象
     * @see Deferred.asPromise
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun asPromise(): Promise<JsAny?> = deferred.asPromise()

}
