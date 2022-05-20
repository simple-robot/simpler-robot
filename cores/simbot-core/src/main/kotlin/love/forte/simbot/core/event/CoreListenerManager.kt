/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simbot.core.event

import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.event.*


/**
 *
 * 监听函数执行的异常处理器。
 * 当 [CoreListenerManager] 中的某一个 [EventListener] 执行过程中出现了异常（包括其过程中所经过的拦截器或过滤器），
 * 则本次执行内容与对应异常将会交由一个 **唯一** 的一场管理器进行处理，并得到一个应得的结果。
 *
 * 原则上异常处理器内部应当尽可能避免再次出现异常。
 *
 * TODO
 */
public typealias EventListenerExceptionHandler = suspend (EventListenerProcessingContext, Throwable) -> EventResult


/**
 * 核心监听函数管理器。
 *
 * ## 调度
 * [CoreListenerManager] 遵守 [EventProcessor] 接口描述，通过当前事件中的 [bot][Event.bot] 所提供的作用域进行事件调度。
 *
 *
 * ## 异步函数
 * [CoreListenerManager] 中，对于一个异步函数 ([EventListener.isAsync] == true 的函数) 的处理方式与其接口定义的描述相同，
 * 对于这个异步函数的拦截器会与当前异步函数共同进入一个由当前事件管理器所提供的异步任务中，并对当前的 [EventProcessingContext]
 * 立即返回一个 [AsyncEventResult].
 *
 *
 *
 * ## 监听函数的解析、缓存与获取
 * 在 [CoreListenerManager] 中，真正被执行的监听函数是经过缓存与转化的，它们只会在遇到一个目前缓存中未知的事件类型的时候进行同步转化缓存。
 *
 * 因此先通过 [isProcessable] 判断是否支持当前事件类型，再进行事件的构建是个不错的优化方案：
 * ```kotlin
 * if(eventManager.isProcessable(FooEvent)) {
 *    val event = FooEvent(...)
 *    eventManager.push(event)
 * }
 * ```
 * 或使用扩展函数 [pushIfProcessable]：
 * ```kotlin
 * eventManager(FooEvent) {
 *    val event = FooEvent(...)
 *    eventManager.push(event)
 * }
 * ```
 *
 * 这样可以一定程度上避免或降低频繁创建不会被使用的事件对象。
 *
 * 但是这存在例外：当存在任何通过 [ContinuousSessionContext] 而注册的持续会话监听函数的时候，[isProcessable] 将会始终得到 `true`。
 *
 *
 *
 */
public interface CoreListenerManager : EventListenerManager {
    
    
    /**
     * 判断指定事件类型在当前事件管理器中是否能够被执行（存在任意对应的监听函数）。
     *
     * e.g.
     * ```kotlin
     * if (FriendMessageEvent in manager) { ... }
     * ```
     */
    public operator fun contains(eventType: Event.Key<*>): Boolean
    
    
    public companion object {
        /**
         * 通过配置信息构建一个 [CoreListenerManager] 实例。
         */
        @JvmStatic
        public fun newInstance(configuration: CoreListenerManagerConfiguration): CoreListenerManager =
            CoreListenerManagerImpl(configuration)
        
    }
}


/**
 * 事件流程上下文的管理器，[CoreListenerManager] 通过此接口实例完成对 [EventProcessingContext] 的统一管理。
 *
 *  在 [CoreListenerManager] 中仅会使用同一个 [EventProcessingContextResolver] 实例。
 *
 */
public interface EventProcessingContextResolver<C : EventProcessingContext> {
    
    /**
     * 获取为当前manager服务的全局作用域对象。
     * 作为一个全局作用域，它理应能够脱离事件调用流程之外而获取。
     */
    @ExperimentalSimbotApi
    public val globalContext: ScopeContext
    
    /**
     * 获取为当前manager服务的持续会话作用域。
     * 持续会话作用域与一个独立的监听函数无关，因此应当能够脱离监听函数流程之外而获取。
     */
    @ExperimentalSimbotApi
    public val continuousSessionContext: ContinuousSessionContext
    
    /**
     * 检测当前事件是否允许监听。
     * 会在监听函数管理器检测前进行检测， [isProcessable] 与 [EventListenerManager.isProcessable] 任意结果为true均会触发事件监听。
     *
     */
    public fun isProcessable(eventKey: Event.Key<*>): Boolean
    
    /**
     * 根据一个事件得到对应的流程上下文。
     * 只有在对应事件存在至少一个对应的监听函数的时候才会被触发。
     */
    @JvmSynthetic
    public suspend fun resolveEventToContext(event: Event, listenerSize: Int): C
    
    /**
     * 向提供的上下文 [C] 的 [EventProcessingContext.results] 中追加一个 [EventResult].
     *
     * [CoreListenerManager] 会对所有得到的结果进行尝试推送，包括 [EventResult.Invalid],
     * 但是建议不会真正的添加 [EventResult.Invalid].
     *
     *
     * ### Reactive API
     * 在核心模块的默认实现下，[appendResultIntoContext] 中支持对 `reactive API` 的相关支持。
     *
     *
     * 详情请参考 [kotlinx-coroutine-reactive](https://github.com/Kotlin/kotlinx.coroutines/blob/master/reactive/README.md) 。
     *
     * @see EventResult.content
     */
    @JvmSynthetic
    public suspend fun appendResultIntoContext(context: C, result: EventResult): ListenerInvokeType
}

/**
 * 监听函数执行状态。由 [EventProcessingContextResolver.appendResultIntoContext] 进行返回。
 */
public enum class ListenerInvokeType {
    /**
     * 继续后续监听函数的执行。
     */
    CONTINUE,
    
    /**
     * 截断后续执行，即在当前点终止。
     */
    TRUNCATED,
    
}

