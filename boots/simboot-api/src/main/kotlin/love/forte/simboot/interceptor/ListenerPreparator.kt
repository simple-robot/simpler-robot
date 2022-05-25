package love.forte.simboot.interceptor

import love.forte.simbot.Api4J
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.utils.runWithInterruptible
import kotlin.coroutines.cancellation.CancellationException

/**
 * 监听准备。
 *
 * 由 boot 模块所提供，通过注解 [@Preparator][love.forte.simboot.annotation.Preparator] 解析到目标监听函数中。
 *
 * [ListenerPreparator] 概念与 [AnnotatedEventListenerInterceptor] 类似，但是 [ListenerPreparator] 是**单点插入**的形式，
 * 且无法进行 _拦截_ 操作，仅适用于一些数据的准备工作（例如在 [EventListener.match] 执行前对 [EventListenerProcessingContext.textContent] 做出调整）。
 *
 * 通常情况下 [ListenerPreparator] 不能也不应该对监听函数的流程造成硬影响，且准备工作应当是迅速的。在实现时需尽可能避免异常的产生和长时间的操作。
 *
 * ## 顺序
 * [ListenerPreparator] 直接作用于监听函数上，因此 [ListenerPreparator] 将始终晚于全局性的监听函数
 * （point 为 [AFTER_MATCH][EventListenerInterceptor.Point.AFTER_MATCH]的全局函数拦截器除外），且优先于
 * [AnnotatedEventListenerInterceptor].
 *
 * 对于不支持挂起函数的实现方，使用 [BlockingListenerPreparator]
 *
 * @see BlockingListenerPreparator
 *
 * @author ForteScarlet
 */
public interface ListenerPreparator {
    
    /**
     * 在 [EventListener.match] 前执行准备。
     */
    @JvmSynthetic
    public suspend fun prepareMatch(context: EventListenerProcessingContext)
    
    
    /**
     * 在 [EventListener.invoke] 前执行准备。
     */
    @JvmSynthetic
    public suspend fun prepareInvoke(context: EventListenerProcessingContext)
    
}


/**
 * 仅保留 [ListenerPreparator.prepareMatch] 行为的 [ListenerMatchPreparator] 实现接口。
 */
public interface ListenerMatchPreparator : ListenerPreparator {
    
    /**
     * 默认无行为
     */
    @JvmSynthetic
    override suspend fun prepareInvoke(context: EventListenerProcessingContext) {
        // do nothing
    }
}

/**
 * 仅保留 [ListenerPreparator.prepareInvoke] 行为的 [ListenerMatchPreparator] 实现接口。
 */
public interface ListenerInvokePreparator : ListenerPreparator {
    
    /**
     * 默认无行为
     */
    @JvmSynthetic
    override suspend fun prepareMatch(context: EventListenerProcessingContext) {
        // do nothing
    }
}


/**
 *
 * 阻塞式的 [ListenerPreparator] 类型。提供于不支持挂起函数的实现方，例如 Java.
 *
 * @see ListenerPreparator
 */
@Api4J
public interface BlockingListenerPreparator : ListenerPreparator {
    
    /**
     *
     * 在 [EventListener.match] 前执行准备。
     *
     * @see ListenerPreparator.prepareMatch
     */
    @Api4J
    public fun prepareMatchBlocking(context: EventListenerProcessingContext)
    
    
    /**
     *
     * 在 [EventListener.invoke] 前执行准备。
     *
     * @see ListenerPreparator.prepareInvoke
     */
    @Api4J
    public fun prepareInvokeBlocking(context: EventListenerProcessingContext)
    
    
    /**
     * 可中断的执行阻塞函数 [prepareMatchBlocking]
     * @throws CancellationException may be throw by [runWithInterruptible]
     */
    @JvmSynthetic
    override suspend fun prepareMatch(context: EventListenerProcessingContext) {
        runWithInterruptible { prepareMatchBlocking(context) }
        
    }
    
    /**
     * 可中断的执行阻塞函数 [prepareInvokeBlocking]
     * @throws CancellationException may be throw by [runWithInterruptible]
     */
    @JvmSynthetic
    override suspend fun prepareInvoke(context: EventListenerProcessingContext) {
        runWithInterruptible { prepareInvokeBlocking(context) }
    }
}