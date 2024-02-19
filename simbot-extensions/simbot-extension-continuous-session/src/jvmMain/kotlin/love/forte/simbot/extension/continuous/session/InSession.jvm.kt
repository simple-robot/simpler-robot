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

@file:JvmName("InSessions")
@file:JvmMultifileClass

package love.forte.simbot.extension.continuous.session

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runInterruptible
import love.forte.simbot.annotations.Api4J
import reactor.core.publisher.Mono
import java.util.concurrent.CompletionStage
import kotlin.coroutines.CoroutineContext

/**
 * 以阻塞的API构造 [InSession] 实例。
 * 可通过 [InSessions.block][blockInSession] 构造。
 *
 * @see blockInSession
 */
public fun interface BlockInSession<T, R> : InSession<T, R> {
    override suspend fun ContinuousSessionReceiver<T, R>.invoke() {
        runInterruptible(Dispatchers.IO) { block(this) }
    }

    public fun block(receiver: ContinuousSessionReceiver<T, R>)
}

/**
 * Java 友好 API，用于构造一个阻塞风格的 [InSession] 实例。
 *
 * @param context 应用在 [runInterruptible] 中用于执行阻塞逻辑的协程上下文。如果为 `null` 则会默认使用 [Dispatchers.IO]。
 */
@JvmName("block")
@JvmOverloads
@Api4J
public fun <T, R> blockInSession(context: CoroutineContext? = null, function: BlockInSession<T, R>): InSession<T, R> {
    if (context == null) return function

    return InSession { runInterruptible(context) { function.block(this) } }
}

/**
 * 以异步的API构造 [InSession] 实例。
 * 可通过 [InSessions.async][asyncInSession] 构造。
 *
 * @see asyncInSession
 */
public fun interface AsyncInSession<T, R> : InSession<T, R> {
    override suspend fun ContinuousSessionReceiver<T, R>.invoke() {
        async(this).await()
    }

    public fun async(receiver: ContinuousSessionReceiver<T, R>): CompletionStage<Void?>
}

/**
 * Java 友好 API，用于构造一个异步风格的 [InSession] 实例。
 */
@JvmName("async")
@Api4J
public fun <T, R> asyncInSession(function: AsyncInSession<T, R>): InSession<T, R> = function

/**
 * 以响应式风格 ([Mono]) 的API构造 [InSession] 实例。
 * 可通过 [InSessions.mono][monoInSession] 构造。
 *
 * 注意：如果要使用 [MonoInSession], 需要确保 runtime 环境中存在
 * [`kotlinx-coroutines-reactor`](https://github.com/Kotlin/kotlinx.coroutines/tree/master/reactive)
 * 依赖。
 *
 * @see Mono
 * @see monoInSession
 */
public fun interface MonoInSession<T, R> : InSession<T, R> {
    override suspend fun ContinuousSessionReceiver<T, R>.invoke() {
        mono(this).awaitSingleOrNull()
    }

    public fun mono(receiver: ContinuousSessionReceiver<T, R>): Mono<Void?>
}

/**
 * Java 友好 API，用于构造一个响应式风格 ([Mono]) 的 [InSession] 实例。
 */
@JvmName("mono")
@Api4J
public fun <T, R> monoInSession(function: MonoInSession<T, R>): InSession<T, R> = function
