/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

@file:JvmName("EventProcessors")
@file:JvmMultifileClass

package love.forte.simbot.kook.stdlib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.publish
import kotlinx.coroutines.runInterruptible
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import love.forte.simbot.kook.event.Event
import love.forte.simbot.kook.event.EventExtra
import org.jetbrains.annotations.Blocking
import org.jetbrains.annotations.NonBlocking
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

/**
 * 一个阻塞式的 [EventProcessor]，是提供给 Java 的友好类型。
 * 可直接使用 `EventProcessors.block((event, raw) -> { ... })` 构建。
 *
 * [block] 最终会在 [Dispatchers.IO] 的调度器下使用 [runInterruptible] 执行。
 */
@Api4J
public fun interface JBlockEventProcessor : EventProcessor {
    /**
     * 处理事件。
     */
    @Blocking
    @Throws(Exception::class)
    public fun block(event: Event<*>, raw: String)

    @JvmSynthetic
    override suspend fun Event<*>.invoke(raw: String) {
        runInterruptible(Dispatchers.IO) { block(this, raw) }
    }
}

/**
 * 以阻塞的形式实现 [EventProcessor]，是提供给 Java 的友好类型。
 * 可直接使用 `EventProcessors.block((event, raw) -> { ... })` 构建。
 *
 *  [block] 最终会在 [Dispatchers.IO] 的调度器下使用 [runInterruptible] 执行。
 */
@Api4J
public fun interface TypedJBlockEventProcessor<E : EventExtra> {
    /**
     * 处理事件。
     */
    @Blocking
    @Throws(Exception::class)
    public fun block(event: Event<E>, raw: String)
}

/**
 * 构建一个阻塞式的 [EventProcessor]
 */
@Api4J
public fun block(function: JBlockEventProcessor): EventProcessor = function

/**
 * 构建一个 [Event.type] 为 [type] 的阻塞式 [EventProcessor]
 */
@Api4J
public fun block(type: Event.Type, function: JBlockEventProcessor): EventProcessor =
    block { event, raw ->
        if (event.type == type) {
            function.block(event, raw)
        }
    }

/**
 * 构建一个处理目标类型 [E] 的阻塞式 [EventProcessor]
 */
@Suppress("UNCHECKED_CAST")
@Api4J
public fun <E : EventExtra> block(type: Class<E>, function: TypedJBlockEventProcessor<E>): EventProcessor =
    block { event, raw ->
        if (type.isInstance(event.extra)) {
            function.block(event as Event<E>, raw)
        }
    }

/**
 * 一个异步式的 [EventProcessor]，是提供给 Java 的友好类型。
 * 可直接使用 `EventProcessors.async((event, raw) -> { ... })` 构建。
 *
 * [block] 最终会在 [Dispatchers.IO] 的调度器下使用 [runInterruptible] 执行。
 */
@Api4J
public fun interface JAsyncEventProcessor : EventProcessor {
    /**
     * 处理事件。
     */
    @NonBlocking
    @Throws(Exception::class)
    public fun async(event: Event<*>, raw: String): CompletionStage<Void?>

    @JvmSynthetic
    override suspend fun Event<*>.invoke(raw: String) {
        async(this, raw).await()
    }
}

/**
 * 以异步的形式实现 [EventProcessor]，是提供给 Java 的友好类型。
 * 可直接使用 `EventProcessors.async((event, raw) -> { ... })` 构建。
 *
 *  [block] 最终会在 [Dispatchers.IO] 的调度器下使用 [runInterruptible] 执行。
 */
@Api4J
public fun interface TypedJAsyncEventProcessor<E : EventExtra> {
    /**
     * 处理事件。
     */
    @NonBlocking
    @Throws(Exception::class)
    public fun async(event: Event<E>, raw: String): CompletionStage<Void?>
}

/**
 * 构建一个异步式的 [EventProcessor]
 */
@Api4J
public fun async(function: JAsyncEventProcessor): EventProcessor = function

/**
 * 构建一个 [Event.type] 为 [type] 的 的异步式 [EventProcessor]
 */
@Api4J
public fun async(type: Event.Type, function: JAsyncEventProcessor): EventProcessor =
    async { event, raw ->
        if (event.type == type) {
            function.async(event, raw)
        } else {
            CompletableFuture.completedStage(null)
        }
    }

/**
 * 构建一个处理目标类型 [E] 的异步式 [EventProcessor]
 */
@Suppress("UNCHECKED_CAST")
@Api4J
public fun <E : EventExtra> async(type: Class<E>, function: TypedJAsyncEventProcessor<E>): EventProcessor =
    async { event, raw ->
        if (type.isInstance(event.extra)) {
            function.async(event as Event<E>, raw)
        } else {
            CompletableFuture.completedStage(null)
        }
    }

/**
 * 用于在 Java 中实现响应式 [EventProcessor]。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun interface JReactiveEventProcessor : EventProcessor {
    /**
     * 处理 [event] 并通过返回的 [Publisher] 报告完成或失败。
     *
     * @param raw 事件的原始 JSON 文本。
     */
    @NonBlocking
    @Throws(Exception::class)
    public fun reactive(event: Event<*>, raw: String): Publisher<Void>

    @JvmSynthetic
    override suspend fun Event<*>.invoke(raw: String) {
        reactive(this, raw).awaitFirstOrNull()
    }
}

/**
 * 用于在 Java 中为指定附加数据类型实现响应式 [EventProcessor]。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun interface TypedJReactiveEventProcessor<E : EventExtra> {
    /**
     * 处理 [event] 并通过返回的 [Publisher] 报告完成或失败。
     *
     * @param raw 事件的原始 JSON 文本。
     */
    @NonBlocking
    @Throws(Exception::class)
    public fun reactive(event: Event<E>, raw: String): Publisher<Void>
}

/**
 * 构建一个响应式 [EventProcessor]。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun reactive(function: JReactiveEventProcessor): EventProcessor = function

/**
 * 构建一个仅处理 [Event.type] 为 [type] 的响应式 [EventProcessor]。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun reactive(type: Event.Type, function: JReactiveEventProcessor): EventProcessor =
    reactive { event, raw ->
        if (event.type == type) {
            function.reactive(event, raw)
        } else {
            publish { }
        }
    }

/**
 * 构建一个仅处理指定附加数据类型的响应式 [EventProcessor]。
 *
 * @param type 事件附加数据的类型。
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
@Suppress("UNCHECKED_CAST")
public fun <E : EventExtra> reactive(
    type: Class<E>,
    function: TypedJReactiveEventProcessor<E>,
): EventProcessor =
    reactive { event, raw ->
        if (type.isInstance(event.extra)) {
            function.reactive(event as Event<E>, raw)
        } else {
            publish { }
        }
    }
