/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simboot.interceptor

import love.forte.simbot.Api4J
import love.forte.simbot.event.BlockingEventListenerInterceptor
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventResult


/**
 *
 * 一个应当仅用于 [@Interceptor][love.forte.simboot.annotation.Interceptor] 注解使用的标记性 [EventListenerInterceptor] 类型。
 *
 * 在 `boot` 下，实现了 [AnnotatedEventListenerInterceptor] 的拦截器将不会被自动加入到 **全局所有** 的监听函数中，而是通过 `@Interceptor(...)` 配合 `@Listener` 进行使用。
 *
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