/*
 *     Copyright (c) 2026. ForteScarlet.
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

@file:JvmName("EventListeners")
@file:JvmMultifileClass

package love.forte.simbot.event

import kotlinx.coroutines.reactive.awaitSingle
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import org.jetbrains.annotations.NonBlocking
import org.reactivestreams.Publisher

/**
 * 通过 Reactive Streams [Publisher] 实现事件监听器。
 * 运行时需要提供 `kotlinx-coroutines-reactive` 及 Reactive Streams。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun interface JReactiveEventListener {
    /**
     * 处理事件并返回发送一个 [EventResult] 后完成的 publisher。
     * 如无需返回结果，可发送 [EventResult.empty]。
     */
    @NonBlocking
    @Throws(Exception::class)
    public fun handle(context: EventListenerContext): Publisher<out EventResult>

    public companion object {
        /**
         * 将 [listener] 转为等待 publisher 唯一结果的监听器。
         */
        @JvmStatic
        public fun toListener(listener: JReactiveEventListener): EventListener = ReactiveEventListenerAdapter(listener)
    }
}

/**
 * 通过 Reactive Streams [Publisher] 监听指定类型的事件。
 * 运行时需要提供 `kotlinx-coroutines-reactive` 及 Reactive Streams。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun interface TypedJReactiveEventListener<E : Event> {
    /**
     * 处理匹配的 [event] 并返回发送一个 [EventResult] 后完成的 publisher。
     */
    @NonBlocking
    @Throws(Exception::class)
    public fun handle(context: EventListenerContext, event: E): Publisher<out EventResult>

    public companion object {
        /**
         * 将 [listener] 转为监听 [type] 及其子类型的监听器。
         */
        @JvmStatic
        public fun <E : Event> toListener(type: Class<E>, listener: TypedJReactiveEventListener<E>): EventListener =
            TypedReactiveEventListenerAdapter(type, listener)
    }
}

/**
 * 将响应式回调 [function] 转为事件监听器。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun reactive(function: JReactiveEventListener): EventListener = JReactiveEventListener.toListener(function)

/**
 * 将响应式回调 [function] 转为监听 [type] 及其子类型的事件监听器。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun <E : Event> reactive(type: Class<E>, function: TypedJReactiveEventListener<E>): EventListener =
    TypedJReactiveEventListener.toListener(type, function)

@OptIn(Api4J::class, ExperimentalReactiveBridgingApi::class)
private data class ReactiveEventListenerAdapter(val handler: JReactiveEventListener) : EventListener {
    override suspend fun EventListenerContext.handle(): EventResult = handler.handle(this).awaitSingle()
}

@OptIn(Api4J::class, ExperimentalReactiveBridgingApi::class)
private data class TypedReactiveEventListenerAdapter<E : Event>(
    val type: Class<E>,
    val handler: TypedJReactiveEventListener<E>,
) : EventListener {
    override suspend fun EventListenerContext.handle(): EventResult {
        val event = context.event
        if (!type.isInstance(event)) return EventResult.invalid()

        return handler.handle(this, type.cast(event)).awaitSingle()
    }
}
