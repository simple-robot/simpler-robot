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

package love.forte.simbot.event

import kotlinx.coroutines.flow.Flow
import love.forte.simbot.common.PriorityConstant


/**
 * 事件调度拦截器。针对一次完整事件调度链路产生的拦截器。
 *
 * [EventDispatchInterceptor] 拦截整个产生事件调度结果的过程，
 * 并可以对调度结果的产生方式或结果流进行影响。
 *
 * [EventDispatchInterceptor] 的拦截发生在事件调度链产生之前，不参与具体的事件调度逻辑，
 * 同 [EventDispatcher.push] 一致，拦截过程中不可挂起。
 *
 * @see EventInterceptor
 *
 * @author ForteScarlet
 */
public fun interface EventDispatchInterceptor {

    /**
     * 对被拦截的内容进行处理。
     *
     * 想要继续流程则使用 [Context.invoke] 进入到下一个拦截器，或者进入正常流程。
     *
     * 例如放行:
     * ```kotlin
     * override fun intercept(context: Context): Flow<EventResult> {
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
     * override fun intercept(context: Context): Flow<EventResult> {
     *    // 不执行 context.invoke() 就是拦截。
     *    // 返回一个空的结果流，实际上并没有真正执行事件调度链。
     *    return emptyFlow()
     * }
     * ```
     *
     *
     */
    @Throws(Exception::class)
    public fun intercept(context: Context): Flow<EventResult>

    /**
     * 拦截器中被拦截的对象信息。
     *
     * Note: [Context] 通常由内部调度器实现，其使用稳定而实现不稳定。
     */
    public interface Context {
        /**
         * 当前被处理的事件上下文。
         */
        public val eventContext: EventContext

        /**
         * 执行被拦截的逻辑并得到本次事件处理链的结果流。
         */
        @Throws(Exception::class)
        public fun invoke(): Flow<EventResult>

        /**
         * 执行被拦截的逻辑并得到本次事件处理链的结果流。
         */
        @Throws(Exception::class)
        public fun invoke(eventContext: EventContext): Flow<EventResult>
    }
}

/**
 * 注册 [EventDispatchInterceptor] 的额外属性。
 *
 * [EventDispatchInterceptorRegistrationProperties] 可由实现者自由扩展，
 * 但应当至少能够支持最基础的几项属性，并至少在不支持的情况下提供警告日志或异常。
 */
public interface EventDispatchInterceptorRegistrationProperties {
    /**
     * 优先级。数值越小优先级越高。通常默认为 [PriorityConstant.NORMAL]。
     */
    public var priority: Int
}
