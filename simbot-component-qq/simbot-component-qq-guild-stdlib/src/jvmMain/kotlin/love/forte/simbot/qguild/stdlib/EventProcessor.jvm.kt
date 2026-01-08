/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("EventProcessors")
@file:JvmMultifileClass

package love.forte.simbot.qguild.stdlib


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runInterruptible
import love.forte.simbot.qguild.event.Signal
import org.jetbrains.annotations.Blocking
import org.jetbrains.annotations.NonBlocking
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

/**
 * 以阻塞的形式实现 [EventProcessor]，是提供给 Java 的友好类型。
 * 可直接使用 `EventProcessors.block((event, raw) -> { ... })` 构建。
 *
 *  [block] 最终会在 [Dispatchers.IO] 的调度器下使用 [runInterruptible] 执行。
 */
public fun interface JBlockEventProcessor : EventProcessor {
    /**
     * 处理事件。
     */
    @Blocking
    @Throws(Exception::class)
    public fun block(event: Signal.Dispatch, raw: String)

    @JvmSynthetic
    override suspend fun Signal.Dispatch.invoke(raw: String) {
        runInterruptible(Dispatchers.IO) { block(this, raw) }
    }
}

/**
 * 以阻塞的形式实现 [EventProcessor]，是提供给 Java 的友好类型。
 * 可直接使用 `EventProcessors.block((event, raw) -> { ... })` 构建。
 *
 *  [block] 最终会在 [Dispatchers.IO] 的调度器下使用 [runInterruptible] 执行。
 */
public fun interface TypedJBlockEventProcessor<T : Signal.Dispatch> {
    /**
     * 处理事件。
     */
    @Blocking
    @Throws(Exception::class)
    public fun block(event: T, raw: String)
}

/**
 * 以异步的形式实现 [EventProcessor]，是提供给 Java 的友好类型。
 * 可直接使用 `EventProcessors.async((event, raw) -> { ... })` 构建。
 */
public fun interface JAsyncEventProcessor : EventProcessor {
    /**
     * 处理事件。
     */
    @Throws(Exception::class)
    @NonBlocking
    public fun async(event: Signal.Dispatch, raw: String): CompletionStage<Void?>

    @JvmSynthetic
    override suspend fun Signal.Dispatch.invoke(raw: String) {
        async(this, raw).await()
    }
}

/**
 * 以异步的形式实现 [EventProcessor]，是提供给 Java 的友好类型。
 * 可直接使用 `EventProcessors.async((event, raw) -> { ... })` 构建。
 */
public fun interface TypedJAsyncEventProcessor<T : Signal.Dispatch> {
    /**
     * 处理事件。
     */
    @Throws(Exception::class)
    @NonBlocking
    public fun async(event: T, raw: String): CompletionStage<Void?>
}

/**
 * 构建一个 [JBlockEventProcessor].
 */
public fun block(function: JBlockEventProcessor): JBlockEventProcessor = function

/**
 * 构建一个 [JAsyncEventProcessor].
 */
public fun async(function: JAsyncEventProcessor): JAsyncEventProcessor = function

/**
 * 构建一个 [JBlockEventProcessor].
 */
public fun <T : Signal.Dispatch> block(type: Class<T>, function: TypedJBlockEventProcessor<T>): JBlockEventProcessor =
    JBlockEventProcessor { event, raw ->
        if (type.isInstance(event)) {
            function.block(type.cast(event), raw)
        }
    }

/**
 * 构建一个 [JAsyncEventProcessor].
 */
public fun <T : Signal.Dispatch> async(type: Class<T>, function: TypedJAsyncEventProcessor<T>): JAsyncEventProcessor =
    JAsyncEventProcessor { event, raw ->
        if (type.isInstance(event)) {
            function.async(type.cast(event), raw)
        } else {
            CompletableFuture.completedFuture(null)
        }
    }
