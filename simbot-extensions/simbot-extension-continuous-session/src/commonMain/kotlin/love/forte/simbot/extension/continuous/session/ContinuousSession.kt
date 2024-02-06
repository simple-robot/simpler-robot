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

import love.forte.simbot.ability.CompletionAware


/**
 *
 * @author ForteScarlet
 */
public interface ContinuousSessionProvider<in T, out R> : CompletionAware {
    /**
     * @throws SessionPushOnFailureException 如果推送行为本身失败，
     * 例如session已经结束或关闭。
     * @throws SessionAwaitOnFailureException 如果推送行为成功、
     * 但是在 [ContinuousSessionReceiver.await] 时出现了异常（例如构造响应结果时出现异常）
     * @throws
     */
    public suspend fun push(value: T): R

    public fun cancel(cause: Throwable?)

    public fun cancel() {
        cancel(null)
    }

    public val isActive: Boolean
    public val isCompleted: Boolean
    public val isCancelled: Boolean
    public suspend fun join()
}

/**
 *
 * @author ForteScarlet
 */
public interface ContinuousSessionReceiver<out T, R> {

    /**
     * 等待 [ContinuousSessionProvider] 的下一次 [推送][ContinuousSessionProvider.push]。
     *
     * 在 [await] 的实现中，
     * 如果在 [await] 过程中出现异常，会在抛出此异常前，
     * 将此异常使用 [SessionAwaitOnFailureException]
     * 包装并恢复(`resume`)给 [ContinuousSessionProvider.push]。
     *
     *
     * @param result
     *
     */
    public suspend fun await(result: (T) -> R): T

    // TODO await 如何延迟响应？

}


public interface ContinuousSession<T, R> : ContinuousSessionProvider<T, R>, ContinuousSessionReceiver<T, R>
