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

@file:JvmName("EventInterceptors")
@file:JvmMultifileClass

package love.forte.simbot.event

import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.publish
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import org.jetbrains.annotations.NonBlocking
import org.reactivestreams.Publisher
import kotlin.coroutines.CoroutineContext

/**
 * 通过 Reactive Streams [Publisher] 实现事件拦截器。
 * 运行时需要提供 `kotlinx-coroutines-reactive` 及 Reactive Streams。
 * 返回的 publisher 由本次拦截调用订阅，调用取消时取消订阅。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun interface JReactiveEventInterceptor : EventInterceptor {
    /**
     * 拦截事件并返回发送一个 [EventResult] 后完成的 publisher。
     */
    @NonBlocking
    @Throws(Exception::class)
    public fun intercept(context: Context): Publisher<out EventResult>

    @JvmSynthetic
    override suspend fun EventInterceptor.Context.intercept(): EventResult =
        intercept(Context(this, currentCoroutineContext().minusKey(Job))).awaitSingle()

    /**
     * 本次事件拦截的响应式上下文。
     * 应在本次调用中组合 [invoke] 返回的 publisher，并只订阅一次。
     * 取消订阅会取消该次后续处理。
     */
    public class Context internal constructor(
        /**
         * 当前拦截调用的原始上下文。
         */
        public val source: EventInterceptor.Context,
        private val coroutineContext: CoroutineContext,
    ) {
        /**
         * 构建执行后续处理的冷 publisher。
         */
        public fun invoke(): Publisher<EventResult> = publish(coroutineContext) {
            send(source.invoke())
        }

        /**
         * 构建使用 [eventListenerContext] 执行后续处理的冷 publisher。
         */
        public fun invoke(eventListenerContext: EventListenerContext): Publisher<EventResult> =
            publish(coroutineContext) {
                send(source.invoke(eventListenerContext))
            }
    }
}

/**
 * 将 Java Lambda [function] 用作响应式事件拦截器。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun reactive(function: JReactiveEventInterceptor): JReactiveEventInterceptor = function
