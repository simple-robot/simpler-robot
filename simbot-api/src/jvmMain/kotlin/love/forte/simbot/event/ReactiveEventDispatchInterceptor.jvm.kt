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

@file:JvmName("EventDispatchInterceptors")

package love.forte.simbot.event

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.asPublisher
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import org.jetbrains.annotations.NonBlocking
import org.reactivestreams.Publisher

/**
 * 通过 Reactive Streams [Publisher] 实现事件调度拦截器。
 * 运行时需要提供 `kotlinx-coroutines-reactive` 及 Reactive Streams。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun interface JReactiveEventDispatchInterceptor : EventDispatchInterceptor {
    /**
     * 在构建调度链时返回本次事件的结果流。
     * 可组合 [context] 提供的后续结果流，或返回自己的结果流。
     */
    @NonBlocking
    @Throws(Exception::class)
    public fun intercept(context: Context): Publisher<out EventResult>

    override fun EventDispatchInterceptor.Context.intercept(): Flow<EventResult> =
        intercept(Context(this)).asFlow()

    /**
     * 本次事件调度的响应式上下文。
     */
    public class Context internal constructor(
        /**
         * 当前事件调度的原始上下文。
         */
        public val source: EventDispatchInterceptor.Context,
    ) {
        /**
         * 继续构建后续调度链，返回按订阅收集其结果的 publisher。
         */
        public fun invoke(): Publisher<EventResult> = source.invoke().asPublisher()

        /**
         * 使用 [eventContext] 继续构建后续调度链，返回按订阅收集其结果的 publisher。
         */
        public fun invoke(eventContext: EventContext): Publisher<EventResult> =
            source.invoke(eventContext).asPublisher()
    }
}

/**
 * 将 Java Lambda [function] 用作响应式事件调度拦截器。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun reactive(function: JReactiveEventDispatchInterceptor): JReactiveEventDispatchInterceptor = function
