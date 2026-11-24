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

package love.forte.simbot.application

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import org.jetbrains.annotations.NonBlocking
import org.reactivestreams.Publisher

/**
 * 通过 Reactive Streams [Publisher] 实现可挂起的应用事件处理器。
 * 运行时需要提供 `kotlinx-coroutines-reactive` 及 Reactive Streams。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun interface JReactiveSuspendApplicationEventHandler<in C> {
    /**
     * 处理当前阶段的 [context]，以 publisher 的完成或错误信号报告结果。
     */
    @NonBlocking
    @Throws(Exception::class)
    public fun invoke(context: C): Publisher<Void>

    @ExperimentalReactiveBridgingApi
    public companion object {
        /**
         * 将 [handler] 转为等待 publisher 完成的阶段处理器。
         */
        @JvmStatic
        public fun <C> toHandler(
            handler: JReactiveSuspendApplicationEventHandler<C>,
        ): SuspendApplicationEventHandler<C> = ReactiveApplicationEventHandlerAdapter(handler)
    }
}

@OptIn(Api4J::class, ExperimentalReactiveBridgingApi::class)
private data class ReactiveApplicationEventHandlerAdapter<C>(
    val handler: JReactiveSuspendApplicationEventHandler<C>,
) : SuspendApplicationEventHandler<C> {
    override suspend fun invoke(context: C) {
        handler.invoke(context).asFlow().collect()
    }
}
