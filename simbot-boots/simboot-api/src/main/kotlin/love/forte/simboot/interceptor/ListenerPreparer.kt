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
 */

package love.forte.simboot.interceptor

import love.forte.simbot.Api4J
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.utils.runWithInterruptible
import kotlin.coroutines.cancellation.CancellationException

/**
 * 错误拼写类型的暂时兼容性保留。
 */
@Deprecated("Use ListenerPreparer.", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("ListenerPreparer", "love.forte.simboot.interceptor.ListenerPreparer"))
public interface ListenerPreparator : ListenerPreparer

/**
 * 错误拼写类型的暂时兼容性保留。
 */
@OptIn(Api4J::class)
@Deprecated("Use BlockingListenerPreparer.", level = DeprecationLevel.ERROR, replaceWith = ReplaceWith("BlockingListenerPreparer", "love.forte.simboot.interceptor.BlockingListenerPreparer"))
public interface BlockingListenerPreparator : BlockingListenerPreparer


/**
 * 监听准备。
 *
 * 由 boot 模块所提供，通过注解 [@Preparer][love.forte.simboot.annotation.Preparer] 解析到目标监听函数中。
 *
 * [ListenerPreparer] 概念与 [AnnotatedEventListenerInterceptor] 类似，但是 [ListenerPreparer] 是**单点插入**的形式，
 * 且无法进行 _拦截_ 操作，仅适用于一些数据的准备工作（例如在 [EventListener.match] 执行前对 [EventListenerProcessingContext.textContent] 做出调整）。
 *
 * 通常情况下 [ListenerPreparer] 不能也不应该对监听函数的流程造成硬影响，且准备工作应当是迅速的。在实现时需尽可能避免异常的产生和长时间的操作。
 *
 * ## 顺序
 * [ListenerPreparer] 直接作用于监听函数上，因此 [ListenerPreparer] 的执行将始终**晚于**全局性的 [监听拦截器][EventListenerInterceptor]
 * （ [point][EventListenerInterceptor.point] 为 [AFTER_MATCH][EventListenerInterceptor.Point.AFTER_MATCH] 的全局函数拦截器除外, [prepareMatch] 将会早于这些拦截器 ），且优先于
 * [AnnotatedEventListenerInterceptor].
 *
 * <br />
 *
 * 对于不支持挂起函数的实现方，使用 [BlockingListenerPreparer]
 *
 * @see BlockingListenerPreparer
 *
 * @author ForteScarlet
 */
public interface ListenerPreparer {
    
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
 * 仅保留 [ListenerPreparer.prepareMatch] 行为的 [ListenerMatchPreparer] 实现接口。
 */
public interface ListenerMatchPreparer : ListenerPreparer {
    
    /**
     * 默认无行为
     */
    @JvmSynthetic
    override suspend fun prepareInvoke(context: EventListenerProcessingContext) {
        // do nothing
    }
}

/**
 * 仅保留 [ListenerPreparer.prepareInvoke] 行为的 [ListenerMatchPreparer] 实现接口。
 */
public interface ListenerInvokePreparer : ListenerPreparer {
    
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
 * 阻塞式的 [ListenerPreparer] 类型。提供于不支持挂起函数的实现方，例如 Java.
 *
 * @see ListenerPreparer
 */
@Api4J
public interface BlockingListenerPreparer : ListenerPreparer {
    
    /**
     *
     * 在 [EventListener.match] 前执行准备。
     *
     * @see ListenerPreparer.prepareMatch
     */
    @Api4J
    public fun prepareMatchBlocking(context: EventListenerProcessingContext)
    
    
    /**
     *
     * 在 [EventListener.invoke] 前执行准备。
     *
     * @see ListenerPreparer.prepareInvoke
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