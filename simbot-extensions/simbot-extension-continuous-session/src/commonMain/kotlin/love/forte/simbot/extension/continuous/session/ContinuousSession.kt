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

package love.forte.simbot.extension.continuous.session

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import love.forte.simbot.ability.CompletionAware
import love.forte.simbot.ability.OnCompletion
import kotlin.coroutines.CoroutineContext


/**
 * 一组 `Session` 的元素之一，
 * 用来向 [ContinuousSessionReceiver] 推送事件 [T] 并获悉结果 [R]。
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
public interface ContinuousSessionProvider<in T, out R> : CompletionAware {
    /**
     * 推送一个事件到对应的 [ContinuousSessionReceiver] 中并挂起直到将其
     * [消费][ContinuousSessionReceiver.await] 或被关闭。
     *
     * @throws SessionPushOnFailureException 如果推送行为本身失败，
     * 例如session已经结束或关闭。
     * @throws SessionAwaitOnFailureException 如果推送行为成功、
     * 但是在 [ContinuousSessionReceiver.await] 时出现了异常（例如构造响应结果时出现异常）
     * @throws
     */
    // TODO @ST?
    public suspend fun push(value: T): R

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
     * 是否由于 cancel 而完成。
     */
    public val isCancelled: Boolean

    /**
     * 挂起直到 `session` 完成任务或被关闭。
     */
    // TODO @ST?
    public suspend fun join()

    /**
     * 注册一个当 `session` 完成任务后执行的回调 [handle]。
     * 也可以通过此回调得知被终止时的异常。
     */
    override fun onCompletion(handle: OnCompletion)
}

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
public interface ContinuousSessionReceiver<out T, R> : CoroutineScope {
    /**
     * [ContinuousSessionReceiver] 作为 [CoroutineScope] 的协程上下文。
     * 其中不会包含 [Job]。
     */
    override val coroutineContext: CoroutineContext

    /**
     * 等待 [ContinuousSessionProvider] 的下一次 [推送][ContinuousSessionProvider.push]。
     *
     * 如果在 [await] 过程中出现异常，会在抛出此异常前，
     * 将此异常使用 [SessionAwaitOnFailureException]
     * 包装并恢复(`resume`)给 [ContinuousSessionProvider.push]。
     *
     * @param result
     *
     */
    // TODO @ST?
    public suspend fun await(result: (T) -> R): T

    // TODO await 如何延迟响应？

}

/**
 * 组合 [ContinuousSessionProvider] 和 [ContinuousSessionReceiver]
 * 的 `session` 类型。
 */
public interface ContinuousSession<T, R> : ContinuousSessionProvider<T, R>, ContinuousSessionReceiver<T, R>
