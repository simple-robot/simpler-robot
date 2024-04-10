/*
 *     Copyright (c) 2024. ForteScarlet.
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

@file:JvmName("AsyncUtil")
@file:JvmMultifileClass

package love.forte.simbot.common.async

import kotlinx.coroutines.*
import kotlinx.coroutines.selects.SelectClause0
import kotlinx.coroutines.selects.SelectClause1
import love.forte.simbot.common.function.Action
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic


/**
 * 错误处理接口。
 *
 * 此接口用于处理异步操作中抛出的异常。
 *
 * @see Async.onError
 */
public fun interface ErrorHandler {
    /**
     * 当异常发生时，此方法将被调用。异常不会是 [CancellationException]
     *
     * @param throwable 异常实例
     */
    public fun invoke(throwable: Throwable)
}

/**
 * 完成处理接口。
 *
 * 此接口用于处理异步操作的完成。
 *
 * @see Async.onCompletion
 */
public fun interface CompletionHandler<in T> {
    /**
     * 当异步操作完成时，此方法将被调用。
     *
     * @param value 操作的结果
     */
    public fun invoke(value: T)
}

/**
 * 取消处理接口。
 *
 * 此接口用于处理异步操作的取消。
 *
 * @see Async.onCancellation
 */
public fun interface CancellationHandler {
    /**
     * 当异步操作被取消时，此方法将被调用。
     *
     * @param cancellationException 中断异常实例
     */
    public fun invoke(cancellationException: CancellationException)
}

/**
 *  异步处理接口。
 *
 * 此接口用于处理异步操作的异常和完成。
 *
 * @see Async.handle
 */
public fun interface AsyncHandler {
    /**
     * 当异常发生或操作完成时，此方法将被调用。
     *
     * @param throwable 异常实例，如果操作正常完成，则为null
     */
    public fun invoke(throwable: Throwable?)
}

/**
 * 内含异步处理操作的类。
 *
 * [Async] 类主要用于处理和管理异步操作。
 * 为用户提供一种简洁有效的异步操作手段，使用户能够更方便地创建、取消、获取异步操作的完成状态以及处理各类异步操作可能出现的异常情况。
 *
 * [Async] 实例在创建时会生成一个异步任务，用户可以通过 [onError]、[onCancellation]、[onCompletion] 等方法添加处理器，
 * 用于处理异步操作过程中发生的错误、被取消和正常完成这些情况。
 * 同时，[Async] 还提供了 [isActive]、[isCompleted]、[isCancelled] 等方法，让用户可以查询异步操作的状态，
 * 并提供了 [cancel] 方法以取消异步操作。
 *
 * [Async] 作为 `expect class`, 在不同的平台上有着不同的实现:
 *
 * - 在 JVM 平台上，[Async] 可以转换为 `java.util.concurrent.CompletableFuture`。
 * - 在 JS 平台上，[Async] 可以转换为 `kotlin.js.Promise`。
 *
 */
public expect class Async<out T> @PublishedApi internal constructor(deferred: Deferred<T>) {

    /**
     * 内包含的异步操作。
     *
     * @return 操作的内部表示
     */
    public val deferred: Deferred<T>

    /**
     * 操作是否正在进行。
     *
     * @see Deferred.isActive
     * @return 操作正在进行则为 true，否则为 false
     */
    public val isActive: Boolean

    /**
     * 操作是否已完成。
     *
     * @see Deferred.isCompleted
     * @return 操作已完成则为 true，否则为 false
     */
    public val isCompleted: Boolean

    /**
     * 操作是否已被取消。
     *
     * @see Deferred.isCancelled
     * @return 操作已被取消则为 true，否则为 false
     */
    public val isCancelled: Boolean

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
    public fun onError(handler: Action<Throwable>): DisposableHandle

    /**
     * 当取消执行时的处理函数。
     *
     * 处理函数会在任务完成的时候被立即调用，且会短时间内完成执行。
     * 处理函数需要保证不抛出任何异常，否则这些异常可以被捕获并作为 [kotlinx.coroutines.CompletionHandlerException] 重新抛出。
     *
     * @return 可释放的句柄，用于删除处理函数
     * @see Job.invokeOnCompletion
     */
    public fun onCancellation(handler: Action<CancellationException>): DisposableHandle

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
    public fun onCompletion(handler: Action<T>): DisposableHandle

    /**
     * 设置异步处理函数。
     *
     * 处理函数会在任务完成的时候被立即调用，且会短时间内完成执行。
     * 处理函数需要保证不抛出任何异常，否则这些异常可以被捕获并作为 [kotlinx.coroutines.CompletionHandlerException] 重新抛出。
     *
     * @return 可释放的句柄，用于删除处理函数
     * @see Job.invokeOnCompletion
     */
    public fun handle(handler: Action<Throwable?>): DisposableHandle

    /**
     * 取消异步操作的执行。
     *
     * @see Deferred.cancel
     */
    public fun cancel()

    /**
     * 以给定的原因取消异步操作的执行。
     *
     * @see Deferred.cancel
     */
    public fun cancelBy(cause: CancellationException?)
}

/**
 * 在 [CoroutineScope] 上下文中异步执行给定的 block，
 * 并返回一个可以用于获取结果、取消任务或添加任务生命周期处理器的 [Async] 对象。
 *
 * @param block 将要异步执行的操作
 * @return 表示异步操作的 [Async] 对象
 */
public inline fun <T> CoroutineScope.toAsync(crossinline block: suspend () -> T): Async<T> = Async(async { block() })

/**
 * 在 [CoroutineScope] 上下文中异步执行给定的 block，
 * 并返回一个可以用于获取结果、取消任务或添加任务生命周期处理器的 [Async] 对象。
 *
 * @return 表示异步操作的 [Async] 对象
 */
public fun <T> Deferred<T>.asAsync(): Async<T> = Async(this)

/**
 * 将给定的值封装为一个已完成的 `Async<T>` 对象。
 *
 * @param T 泛型参数，表示输入值和Async对象包含的结果类型
 * @param value 需要被封装的值
 * @return 返回一个包装了给定值的 `Async<T>` 对象（已完成状态）
 */
public fun <T> completedAsync(value: T): Async<T> = Async(CompletableDeferred(value))

/**
 * 在协程中等待此 [Async] 任务完成，并返回它的结果（如果任务成功完成）或抛出异步抛出的异常（如果任务失败）。
 * 此函数会挂起协程直到任务完成。
 *
 * @see Deferred.await
 */
@JvmSynthetic
public suspend fun <T> Async<T>.await(): T = deferred.await()

/**
 * 在协程中等待此 [Async] 任务完成。
 * 此函数挂起协程直到任务完成，但不获取任务结果，也不处理任务失败时的异常。
 *
 * @see Deferred.join
 */
@JvmSynthetic
public suspend fun Async<*>.join(): Unit = deferred.join()

/**
 * 获取一个表示此 [Async] 任务 join 操作的 [SelectClause0]。
 *
 * @return 表示 join 操作的 [SelectClause0]
 * @see Deferred.onJoin
 */
public val Async<*>.onJoin: SelectClause0
    get() = deferred.onJoin

/**
 * 获取一个表示此 [Async] 任务 await 操作的 [SelectClause1]。
 *
 * @return 表示 await 操作的 [SelectClause1]
 * @see Deferred.onAwait
 */
public val <T> Async<T>.onAwait: SelectClause1<T>
    get() = deferred.onAwait

/**
 * 取消此 [Async] 任务，特别是将在任务中抛出一个 [CancellationException] 以终结任务。
 * 如果 [message] 或 [cause] 指定了，则它们将被用于创建 [CancellationException]。
 *
 * @param message 取消原因，可以为 null
 * @param cause 引发取消的异常，可以为 null
 */
public fun <T> Async<T>.cancelBy(message: String? = null, cause: Throwable?) {
    cancelBy(cause?.let { CancellationException(message ?: it.message, it) })
}
