/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.interceptor

import love.forte.simbot.Api4J
import love.forte.simbot.event.BlockingEventListenerInterceptor
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventResult


/**
 *
 * 一个应当仅用于 [@Interceptor][love.forte.simboot.annotation.Interceptor] 注解使用的标记性 [EventListenerInterceptor] 类型。
 *
 * 在 `boot` 下，实现了 [AnnotatedEventListenerInterceptor] 的拦截器将不会被自动加入到 **全局所有** 的监听函数中，
 * 而是通过 [@Interceptor(...)][love.forte.simboot.annotation.Interceptor]
 * 配合 [@Listener][love.forte.simboot.annotation.Listener] 进行使用。
 *
 *
 * [AnnotatedEventListenerInterceptor] 相对于 [EventListenerInterceptor] 比较特殊，它是针对并作用于某个**具体**的监听函数的，
 * 因此它无法在比监听函数更上层的交流来处理它，进而决定了 [AnnotatedEventListenerInterceptor] 将只支持对 [EventListener.invoke] 进行拦截，
 * 也就是说 [AnnotatedEventListenerInterceptor] 只会在 [EventListener.match] 匹配成功后才会执行。
 *
 * @see love.forte.simboot.annotation.Interceptor
 * @see EventListenerInterceptor
 * @see AnnotatedBlockingEventListenerInterceptor
 *
 * @author ForteScarlet
 */
public interface AnnotatedEventListenerInterceptor : EventListenerInterceptor {
    @JvmSynthetic
    override suspend fun intercept(context: EventListenerInterceptor.Context): EventResult
}

/**
 * 一个应当仅用于 [@Interceptor][love.forte.simboot.annotation.Interceptor] 注解使用的标记性 [EventListenerInterceptor] 类型,
 * 服务于 Java 等不支持挂起函数的实现方，提供阻塞的 [doIntercept] 拦截函数以供实现。
 *
 * @see EventListenerInterceptor
 * @see AnnotatedEventListenerInterceptor
 * @see BlockingEventListenerInterceptor
 */
@Api4J
public interface AnnotatedBlockingEventListenerInterceptor : AnnotatedEventListenerInterceptor,
    BlockingEventListenerInterceptor {
    @JvmSynthetic
    override suspend fun intercept(context: EventListenerInterceptor.Context): EventResult = doIntercept(context)
    override fun doIntercept(context: EventListenerInterceptor.Context): EventResult
}
