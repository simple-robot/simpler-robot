/*
 *     Copyright (c) 2024-2025. ForteScarlet.
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

@file:JvmName("ContinuousSessions")
@file:JvmMultifileClass

package love.forte.simbot.extension.continuous.session

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import love.forte.simbot.ability.CompletionAware
import love.forte.simbot.ability.OnCompletion
import love.forte.simbot.extension.continuous.session.ContinuousSessionReceiver.Received
import love.forte.simbot.suspendrunner.ST
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic


/**
 * 一组 `Session` 的元素之一，
 * 用来向 [ContinuousSessionReceiver] 推送事件 [T] 并获悉结果 [R] 的“供应者”。
 *
 * ```kotlin
 * val session = context.session(Key()) {
 *     val next = await { v -> v.toResult() } // 假设 toResult() 将事件转化为结果
 * }         ↑                      |
 *       |-- | ---------------------|
 *       |   |-----------|
 *       ↓               |
 * val result = session.push(value) // 此处得到 v.toResult() 的结果
 * ```
 *
 * @author ForteScarlet
 */
@ExperimentalContinuousSessionAPI
public interface ContinuousSessionProvider<C, in T, out R> : CompletionAware {
    /**
     * 推送一个事件到对应的 [ContinuousSessionReceiver] 中并挂起直到将其
     * [消费][ContinuousSessionReceiver.await] 或被关闭。
     *
     * @throws CancellationException 如果会话已经结束或关闭
     * @throws SessionPushOnFailureException 如果推送行为本身失败，例如会话已经结束或关闭
     * @throws SessionAwaitOnFailureException 如果推送行为成功、
     * 但是在 [ContinuousSessionReceiver.await] 时出现了异常（例如构造响应结果时出现异常）
     * @throws Throwable 任何由 [SessionContinuation.resumeWithException] 直接 resume 的异常本身
     */
    @ST
    public suspend fun push(value: T, context: C): R

    /**
     * 关闭对应的 `session`。
     * 关闭后会同时将对应的 `session` 从对应的 [ContinuousSessionContext] 中移除。
     */
    public fun cancel(cause: Throwable?)

    /**
     * 关闭对应的 `session`。
     * 关闭后会同时将对应的 `session` 从对应的 [ContinuousSessionContext] 中移除。
     */
    public fun cancel() {
        cancel(null)
    }

    /**
     * 是否处于活跃状态。
     */
    public val isActive: Boolean

    /**
     * 是否已经完成。
     */
    public val isCompleted: Boolean

    /**
     * 是否由于 `cancel` 而完成。
     */
    public val isCancelled: Boolean

    /**
     * 挂起直到 `session` 完成任务或被关闭。
     */
    @ST(asyncBaseName = "asFuture", asyncSuffix = "")
    public suspend fun join()

    /**
     * 注册一个当 `session` 完成任务后执行的回调 [handle]。
     * 也可以通过此回调得知被终止时的异常。
     *
     * NOTE:回调的执行基于 [Job.invokeOnCompletion]，它的执行可能是并发的，
     * 它的逻辑应当快速、非阻塞。
     *
     * @see Job.invokeOnCompletion
     */
    override fun onCompletion(handle: OnCompletion)
}

/**
 * 推送一个事件到对应的 [ContinuousSessionReceiver] 中并挂起直到将其
 * [消费][ContinuousSessionReceiver.await] 或被关闭。
 *
 * @throws CancellationException 如果会话已经结束或关闭
 * @throws SessionPushOnFailureException 如果推送行为本身失败，例如会话已经结束或关闭
 * @throws SessionAwaitOnFailureException 如果推送行为成功、
 * 但是在 [ContinuousSessionReceiver.await] 时出现了异常（例如构造响应结果时出现异常）
 * @throws Throwable 任何由 [SessionContinuation.resumeWithException] 直接 resume 的异常本身
 */
@ST
public suspend fun <T, R> ContinuousSessionProvider<Unit, T, R>.push(value: T): R =
    push(value, Unit)

/**
 * 一组 `Session` 的元素之一，
 * 用来在异步中接收 [ContinuousSessionProvider] 推送的事件 [T]
 * 并根据此事件为其返回结果 [R]。
 *
 * ```kotlin
 * val session = context.session(Key()) {
 *     val next = await { v -> v.toResult() } // 假设 toResult() 将事件转化为结果
 * }         ↑                      |
 *       |-- | ---------------------|
 *       |   |-----------|
 *       ↓               |
 * val result = session.push(value) // 此处得到 v.toResult() 的结果
 * ```
 *
 * @author ForteScarlet
 */
@ExperimentalContinuousSessionAPI
public interface ContinuousSessionReceiver<C, out T, R> : CoroutineScope {
    @ExperimentalContinuousSessionAPI
    public interface Received<C, out T> {
        public val context: C
        public val value: T

        public operator fun component1(): C = context
        public operator fun component2(): T = value
    }

    /**
     * [ContinuousSessionReceiver] 作为 [CoroutineScope] 的协程上下文。
     * 其中不会包含 [Job]。
     */
    override val coroutineContext: CoroutineContext

    /**
     * 等待 [ContinuousSessionProvider] 的下一次 [推送][ContinuousSessionProvider.push]，
     * 并在接收到时恢复一个结果 [result]。
     *
     * ```kotlin
     * val (context, value) = await(result)
     * // ...
     * ```
     *
     * @param result 一个响应 [R]
     *
     * @throws CancellationException 如果内部的管道已经被关闭或任务已经结束
     * @throws ClosedReceiveChannelException 如果内部的管道的接收已经被关闭
     */
    @ST(asyncSuffix = "AsFuture")
    public suspend fun await(result: R): Received<C, T>

    /**
     * 等待 [ContinuousSessionProvider] 的下一次 [推送][ContinuousSessionProvider.push]，
     * 并在接收到时恢复一个由 [result] 计算的结果。
     *
     * 如果在 [await] 过程中出现异常，会在抛出此异常前，
     * 将此异常使用 [SessionAwaitOnFailureException]
     * 包装并恢复(`resume`)给 [ContinuousSessionProvider.push]。
     *
     * ```kotlin
     * val (context, value) = await { (c, v) -> v.toResult() /* 根据结果 value 计算一个结果 */ }
     * // ...
     * ```
     *
     * @param result 根据 [推送][ContinuousSessionProvider.push] 得到的结果值计算一个响应 [R]
     *
     * @throws CancellationException 如果内部的管道已经被关闭或任务已经结束
     * @throws ClosedReceiveChannelException 如果内部的管道的接收已经被关闭
     */
    @ST(asyncSuffix = "AsFuture")
    public suspend fun await(result: (Received<C, T>) -> R): Received<C, T>

    /**
     * 等待 [ContinuousSessionProvider] 的下一次 [推送][ContinuousSessionProvider.push] 结果，
     * 并将此结果和 推送][ContinuousSessionProvider.push] 处的挂起点打包为 [SessionContinuation]，
     * 并在稍后通过 [SessionContinuation.resume] 或 [SessionContinuation.resumeWithException] 恢复。
     *
     * 与 [await {...}][await] 相比，此函数可以延后 **[推送][ContinuousSessionProvider.push] 挂起点** 的恢复时机，
     * 用来处理一些更灵活的逻辑。你需要更加了解对挂起点的相关操作，并确保能够在合适的时机**恢复**它。
     * 否则，还是更建议使用 [await { ... }][await]。
     *
     * ```kotlin
     * val continuation = await()
     * val value = continuation.value
     * // 在异步中执行某些任务并稍后恢复结果
     * launch {
     *     try {
     *          val result = runTask(value)
     *          continuation.resume(result)
     *     } catch (cause: Throwable) {
     *          // 出现了异常，恢复一个异常情况下的结果，
     *          // 比如：
     *          // continuation.resume(resultOnError(cause))
     *
     *          // 或者如此示例，直接恢复一个异常
     *          // 这样对应的 `push` 处便会抛出此异常
     *          continuation.resumeWithException(cause)
     *     }
     * }.join() // 等待异步任务
     * ```
     *
     * 需要注意，上述示例中我们使用 [Job.join] 挂起了那个异步任务，
     * 因为如果当 [ContinuousSessionReceiver] 内作用域已经结束 (completed),
     * 而某个 **[推送][ContinuousSessionProvider.push] 挂起点** 尚未恢复，
     * 则会直接使用一个异常恢复此挂起点。
     * 因此，当你要在异步中延后恢复 **[推送][ContinuousSessionProvider.push] 挂起点** 时，
     * 需要确保 [ContinuousSessionReceiver] 作用域尚未结束，例如使用 [Job.join] 或 [coroutineScope] 等。
     *
     * @throws CancellationException 如果内部的管道已经被关闭或任务已经结束
     * @throws ClosedReceiveChannelException 如果内部的管道的接收已经被关闭
     *
     * @see SessionContinuation
     *
     */
    @ST(asyncSuffix = "AsFuture")
    public suspend fun await(): SessionContinuation<C, T, R>
}

/**
 * ```Kotlin
 * // It's inline fun
 * awaitWith { context, value -> result }
 * ```
 *
 * @since 4.14.0
 */
@ExperimentalContinuousSessionAPI
public suspend inline fun <C, T, R> ContinuousSessionReceiver<C, T, R>.awaitWith(
    block: (C, T) -> R
) {
    val continuation = await()
    try {
        continuation.resume(block(continuation.context, continuation.value))
    } catch (e: Throwable) {
        continuation.resumeWithException(e)
    }
}

/**
 * @since 4.14.0
 */
@ExperimentalContinuousSessionAPI
public sealed class MultipleAwaitResult<T> {
    public abstract val value: T

    public data class Valid<T>(override val value: T) : MultipleAwaitResult<T>()
    public data class Invalid<T>(override val value: T) : MultipleAwaitResult<T>()

    public companion object Factory {
        @JvmStatic
        public fun <T> valid(value: T): Valid<T> = Valid(value)

        @JvmStatic
        public fun <T> invalid(value: T): Invalid<T> = Invalid(value)
    }
}

/**
 * 进行多轮次处理。如果 [block] 的返回值类型不是 [MultipleAwaitResult.Valid],
 * 则 [multipleAwaitWith] 会继续 await 直到得到 [MultipleAwaitResult.Valid] 结果或产生了异常。
 *
 * @since 4.14.0
 */
@ExperimentalContinuousSessionAPI
public suspend inline fun <C, T, R> ContinuousSessionReceiver<C, T, R>.multipleAwaitWith(
    block: MultipleAwaitResult.Factory.(C, T) -> MultipleAwaitResult<R>
): R {
    while (true) {
        val continuation = await()
        try {
            val result = MultipleAwaitResult.block(continuation.context, continuation.value)
            val value = result.value
            continuation.resume(value)
            if (result is MultipleAwaitResult.Valid) {
                return value
            }
        } catch (e: Throwable) {
            continuation.resumeWithException(e)
            throw e
        }
    }
}


@ExperimentalContinuousSessionAPI
public suspend fun <C, T, R> ContinuousSessionReceiver<C, T, R>.awaitValue(result: R): T =
    await(result).value

@ExperimentalContinuousSessionAPI
public suspend fun <C, T, R> ContinuousSessionReceiver<C, T, R>.awaitValue(result: (Received<C, T>) -> R): T =
    await(result).value

/**
 * [ContinuousSessionReceiver.await] 的返回值类型，
 * 可用来获取到本次等待到的 [推送][ContinuousSessionProvider.push] 结果，
 * 并向其恢复一个结果或异常。
 *
 * [SessionContinuation] 的使用方式类似 [Continuation]，在收到 [SessionContinuation] 时，
 * 应当尽可能快速地通过 [resume] 或 [resumeWithException] 来恢复
 * [推送][ContinuousSessionProvider.push] 处，
 * 并应当尽可能保证能够调用 [resume] 或 [resumeWithException] 一次。
 */
@ExperimentalContinuousSessionAPI
public interface SessionContinuation<C, out T, in R> : ContinuousSessionReceiver.Received<C, T> {
    override val context: C

    /**
     * 获取本次接收到的 [ContinuousSessionProvider.push] 结果。
     */
    override val value: T

    public val isActive: Boolean
    public val isCancelled: Boolean
    public val isCompleted: Boolean

    /**
     * 响应恢复结果 [result] 到本次接收到的 [ContinuousSessionProvider.push]。
     * 类似于 [Continuation.resume]。
     */
    public fun resume(result: R)

    /**
     * 响应恢复异常 [cause] 到本次接收到的 [ContinuousSessionProvider.push]。
     * 类似于 [Continuation.resumeWithException]。
     */
    public fun resumeWithException(cause: Throwable)
}

/**
 * 构建一个基于 [CancellableContinuation] 实现的 [SessionContinuation] 实例。
 */
@ExperimentalContinuousSessionAPI
public fun <C, T, R> createSimpleSessionContinuation(
    context: C,
    value: T,
    continuation: CancellableContinuation<R>,
    handle: DisposableHandle? = null
): SessionContinuation<C, T, R> =
    SimpleSessionContinuation(context, value, continuation, handle)


private class SimpleSessionContinuation<C, out T, in R>(
    override val context: C,
    override val value: T,
    private val continuation: CancellableContinuation<R>,
    private val handle: DisposableHandle?
) : SessionContinuation<C, T, R> {
    override val isActive: Boolean
        get() = continuation.isActive

    override val isCancelled: Boolean
        get() = continuation.isCancelled

    override val isCompleted: Boolean
        get() = continuation.isCompleted

    override fun resume(result: R) {
        continuation.resume(result)
        handle?.dispose()
    }

    override fun resumeWithException(cause: Throwable) {
        continuation.resumeWithException(cause)
        handle?.dispose()
    }
}

/**
 * 组合 [ContinuousSessionProvider] 和 [ContinuousSessionReceiver]
 * 的 `session` 类型。
 */
@ExperimentalContinuousSessionAPI
public interface ContinuousSession<C, T, R> : ContinuousSessionProvider<C, T, R>, ContinuousSessionReceiver<C, T, R>
