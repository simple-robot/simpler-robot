package love.forte.simboot.core.interceptor

import love.forte.simboot.core.listener.KFunctionEventListener
import love.forte.simbot.core.event.EventInterceptEntrance
import love.forte.simbot.event.EventInterceptor
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventProcessingContext
import love.forte.simbot.event.EventResult

/**
 *
 * 由 boot 模块提供的额外的特殊拦截器，用于配合在 boot 模块中配合 [KFunctionEventListener] 实例使用。
 *
 * [FunctionalListenerInterceptor] 与 [love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor]
 * 类似，都是由 boot 相关模块所提供的额外服务。但是 [FunctionalListenerInterceptor] 是 **全局性** 的，
 * 它会作用于所有的 [KFunctionEventListener] 实现（这一行为由具体应用的实现者决定）。
 *
 * ## 优先级
 * [FunctionalListenerInterceptor] 不同于 [love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor]，
 * 它是直接作用于 [KFunctionEventListener] 内部的，与 [KFunctionEventListener] 有着强绑定关系。
 * 也因此 [FunctionalListenerInterceptor] 会始终晚于其他拦截器或过滤器。
 *
 * @author ForteScarlet
 */
public interface FunctionalListenerInterceptor : EventInterceptor<FunctionalListenerInterceptor.Context, EventResult> {
    
    
    /**
     * [FunctionalListenerInterceptor] 所使用的上下文。
     */
    public interface Context : EventInterceptor.Context<EventResult> {
        override val eventContext: EventProcessingContext
        
        /**
         * 当前监听的目标监听函数。
         */
        public val listener: KFunctionEventListener<*>
    }
    
    public companion object {
        
        /**
         * 提供拦截器迭代器构建一个 [EventInterceptEntrance]。
         */
        public fun entrance(
            listener: KFunctionEventListener<*>,
            interceptorsIterable: Iterable<FunctionalListenerInterceptor>,
        ): EventInterceptEntrance<Context, EventResult, EventListenerProcessingContext> {
            if (interceptorsIterable is Collection && interceptorsIterable.isEmpty()) {
                return DirectKFunctionListenerInterceptorEntrance
            }
            if (!interceptorsIterable.iterator().hasNext()) {
                return DirectKFunctionListenerInterceptorEntrance
            }
            
            return KFunctionListenerInterceptorEntrance(listener, interceptorsIterable)
        }
    }
}

private object DirectKFunctionListenerInterceptorEntrance :
    EventInterceptEntrance<FunctionalListenerInterceptor.Context, EventResult, EventListenerProcessingContext>() {
    override suspend fun doIntercept(
        context: EventListenerProcessingContext,
        processing: suspend (EventListenerProcessingContext) -> EventResult,
    ): EventResult {
        return processing(context)
    }
}


internal class KFunctionListenerInterceptorEntrance(
    internal val listener: KFunctionEventListener<*>,
    internal val interceptorsIterable: Iterable<FunctionalListenerInterceptor>,
) : EventInterceptEntrance<FunctionalListenerInterceptor.Context, EventResult, EventListenerProcessingContext>() {
    
    
    override suspend fun doIntercept(
        context: EventListenerProcessingContext,
        processing: suspend (EventListenerProcessingContext) -> EventResult,
    ): EventResult {
        return KFunctionListenerInterceptorEntranceContext(context, processing).proceed()
    }
    
    internal inner class KFunctionListenerInterceptorEntranceContext(
        override val eventContext: EventListenerProcessingContext,
        processing: suspend (EventListenerProcessingContext) -> EventResult,
    ) : IteratorInterceptorContext<FunctionalListenerInterceptor.Context, FunctionalListenerInterceptor, EventResult, EventListenerProcessingContext>(
        interceptorsIterable.iterator(),
        processing
    ), FunctionalListenerInterceptor.Context {
        override val context: FunctionalListenerInterceptor.Context
            get() = this
        
        override val listener: KFunctionEventListener<*>
            get() = this@KFunctionListenerInterceptorEntrance.listener
        
        
        override suspend fun proceed(): EventResult = doProceed()
    }
}