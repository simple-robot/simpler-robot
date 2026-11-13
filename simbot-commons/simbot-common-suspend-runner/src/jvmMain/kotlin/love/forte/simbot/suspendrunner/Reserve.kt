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

package love.forte.simbot.suspendrunner

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.annotations.InternalSimbotAPI
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext

/**
 * A class representing a coroutine-based reserve.
 *
 * @param T the type of the result produced by the reserve
 * @property scope the [CoroutineScope] to use for launching coroutines
 * @property context the [CoroutineContext] to use for running the coroutine
 * @property block the suspend function block that represents the reserve logic
 */
@Deprecated("Use SuspendReserve")
public class Reserve<out T>(
    private val scope: CoroutineScope,
    private val context: CoroutineContext,
    private val block: suspend () -> T
) {

    /**
     * 阻塞执行不带 [CoroutineScope] 接收者的挂起函数，不使用本实例的 scope。
     * 默认遵循 [runBlocking][kotlinx.coroutines.runBlocking] 的上下文、线程中断和子协程完成语义。
     *
     * @return the result of executing the block
     */
    public fun block(): T = runInNoScopeBlocking(context) { block.invoke() }

    /**
     * Executes the given block asynchronously and returns a CompletableFuture that represents the result.
     *
     * @return A CompletableFuture that represents the result of the asynchronous operation.
     */
    @OptIn(InternalSimbotAPI::class)
    public fun async(): CompletableFuture<out T> = runInAsync(scope, context) { block.invoke() }
}
