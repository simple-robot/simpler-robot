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

@file:JvmName("InSessions")
@file:JvmMultifileClass

package love.forte.simbot.extension.continuous.session

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.runInterruptible
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import org.reactivestreams.Publisher
import reactor.core.publisher.Mono
import java.util.concurrent.CompletionStage
import kotlin.coroutines.CoroutineContext

/**
 * 以阻塞的API构造 [InSession] 实例。
 * 可通过 [InSessions.block][blockInSession] 构造。
 *
 * @see blockInSession
 */
public fun interface BlockInSession<C, T, R> : InSession<C, T, R> {
    override suspend fun ContinuousSessionReceiver<C, T, R>.invoke() {
        runInterruptible(Dispatchers.IO) { block(this) }
    }

    public fun block(receiver: ContinuousSessionReceiver<C, T, R>)
}

/**
 * Java 友好 API，用于构造一个阻塞风格的 [InSession] 实例。
 *
 * @param context 应用在 [runInterruptible] 中用于执行阻塞逻辑的协程上下文。如果为 `null` 则会默认使用 [Dispatchers.IO]。
 */
@JvmName("block")
@JvmOverloads
@Api4J
public fun <C, T, R> blockInSession(
    context: CoroutineContext? = null,
    function: BlockInSession<C, T, R>
): InSession<C, T, R> {
    if (context == null) return function

    return InSession { runInterruptible(context) { function.block(this) } }
}

/**
 * 以异步的API构造 [InSession] 实例。
 * 可通过 [InSessions.async][asyncInSession] 构造。
 *
 * @see asyncInSession
 */
public fun interface AsyncInSession<C, T, R> : InSession<C, T, R> {
    override suspend fun ContinuousSessionReceiver<C, T, R>.invoke() {
        async(this).await()
    }

    public fun async(receiver: ContinuousSessionReceiver<C, T, R>): CompletionStage<Void?>
}

/**
 * Java 友好 API，用于构造一个异步风格的 [InSession] 实例。
 */
@JvmName("async")
@Api4J
public fun <C, T, R> asyncInSession(function: AsyncInSession<C, T, R>): InSession<C, T, R> = function

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
public fun interface MonoInSession<C, T, R> : InSession<C, T, R> {
    override suspend fun ContinuousSessionReceiver<C, T, R>.invoke() {
        mono(this).awaitSingleOrNull()
    }

    public fun mono(receiver: ContinuousSessionReceiver<C, T, R>): Mono<Void?>
}

/**
 * Java 友好 API，用于构造一个响应式风格 ([Mono]) 的 [InSession] 实例。
 */
@JvmName("mono")
@Api4J
public fun <C, T, R> monoInSession(function: MonoInSession<C, T, R>): InSession<C, T, R> = function

/**
 * 通过 Reactive Streams [Publisher] 实现会话接收逻辑。
 * 会话等待完成信号，错误信号会终止会话，取消会话会取消订阅。
 * 运行时需要提供 `kotlinx-coroutines-reactive`。
 *
 * @see reactiveInSession
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun interface ReactiveInSession<C, T, R> : InSession<C, T, R> {
    @JvmSynthetic
    override suspend fun ContinuousSessionReceiver<C, T, R>.invoke() {
        reactive(this).asFlow().collect()
    }

    /**
     * 返回处理 [receiver] 的 publisher，通过完成信号报告会话接收逻辑结束。
     */
    @Throws(Exception::class)
    public fun reactive(receiver: ContinuousSessionReceiver<C, T, R>): Publisher<Void>
}

/**
 * 使用响应式回调 [function] 构造 [InSession]。
 *
 * @since 5.0
 */
@JvmName("reactive")
@Api4J
@ExperimentalReactiveBridgingApi
public fun <C, T, R> reactiveInSession(function: ReactiveInSession<C, T, R>): InSession<C, T, R> = function
