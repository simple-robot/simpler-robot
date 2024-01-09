/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import love.forte.simbot.common.PriorityConstant
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic


/**
 * 事件拦截器。在 [EventDispatcher] 调度事件的过程中针对各监听器的拦截器。
 *
 * 也可以通过 [EventListenerRegistrationProperties] 针对某指定的监听器进行拦截。
 *
 * 对于Java实现者，考虑使用 `JAsyncEventInterceptor` 和 `JBlockEventInterceptor`,
 * 或 `EventInterceptors` 中提供的静态API，例如 `EventInterceptors.block(...)`。
 *
 * @see EventDispatchInterceptor
 *
 * @author ForteScarlet
 */
public fun interface EventInterceptor {

    /**
     * 对被拦截的内容进行处理。
     *
     * 想要继续流程则使用 [Context.invoke] 进入到下一个拦截器，或者进入正常流程。
     *
     * 例如放行:
     * ```kotlin
     * override suspend fun intercept(context: Context): EventResult {
     *    // do something...?
     *
     *    // 执行 context.invoke() 就是放行。
     *    val result = context.invoke()
     *    // and do something...?
     *
     *    return result
     * }
     * ```
     *
     * 例如拦截:
     * ```kotlin
     * override suspend fun intercept(context: Context): EventResult {
     *    // 不执行 context.invoke() 就是拦截。
     *    // 自行构建一个result实例
     *    return EventResult(...)
     * }
     * ```
     *
     *
     */
    @JvmSynthetic
    public suspend fun intercept(context: Context): EventResult

    /**
     * 拦截器中被拦截的对象信息。
     *
     * Note: [Context] 通常由内部调度器实现，其使用稳定而实现不稳定。
     */
    public interface Context {
        /**
         * 当前被处理的事件上下文。
         * 可能是中途被某个拦截器取代后的结果。
         */
        public val eventListenerContext: EventListenerContext

        /**
         * 执行被拦截的逻辑并得到事件处理结果 [EventResult]。
         */
        @JvmSynthetic
        @Throws(Exception::class)
        public suspend fun invoke(): EventResult

        /**
         * 执行被拦截的逻辑并得到事件处理结果 [EventResult]。
         * 向后传递的 [EventListenerContext] 会被参数 [eventListenerContext] 取代。
         */
        @JvmSynthetic
        @Throws(Exception::class)
        public suspend fun invoke(eventListenerContext: EventListenerContext): EventResult
    }
}


/**
 * 注册 [EventInterceptor] 的额外属性。
 *
 * [EventInterceptorRegistrationProperties] 可由 [EventListenerRegistrar] 的实现者自由扩展，
 * 但应当至少能够支持最基础的几项属性，并至少在不支持的情况下提供警告日志或异常。
 */
public interface EventInterceptorRegistrationProperties {
    /**
     * 优先级。数值越小优先级越高。通常默认为 [PriorityConstant.NORMAL]。
     * 此优先级与在 [EventDispatcher] 中全局性添加到所有事件监听器上的拦截器共用。
     */
    public var priority: Int
}
