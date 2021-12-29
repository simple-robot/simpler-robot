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
    @JvmSynthetic override suspend fun intercept(context: EventListenerInterceptor.Context): EventResult
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
public interface AnnotatedBlockingEventListenerInterceptor : AnnotatedEventListenerInterceptor, BlockingEventListenerInterceptor {
    @JvmSynthetic override suspend fun intercept(context: EventListenerInterceptor.Context): EventResult = doIntercept(context)
    override fun doIntercept(context: EventListenerInterceptor.Context): EventResult
}