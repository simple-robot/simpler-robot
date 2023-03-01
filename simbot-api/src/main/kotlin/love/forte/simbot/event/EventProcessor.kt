/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.event

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.event.EventProcessingResult.Empty
import love.forte.simbot.utils.view.IndexAccessView
import love.forte.simbot.utils.view.emptyView


/**
 * 事件处理器，代表一个事件被触发的入口。
 *
 * [EventProcessor] 会通过当前事件中的 [bot][Event.bot] 所提供的作用域进行事件调度。
 *
 * @author ForteScarlet
 */
public interface EventProcessor {

    /**
     * 推送一个事件到当前事件处理器。
     *
     * 事件处理器会按照流程触发所有应被触发的事件，并将所有 [EventListener] 的执行结果汇总为 [EventProcessingResult] 并返回。
     *
     */
    @JvmBlocking
    @JvmAsync
    public suspend fun push(event: Event): EventProcessingResult

    /**
     * 判断是否存在对应的事件监听器。
     */
    public fun isProcessable(eventKey: Event.Key<*>): Boolean

}

/**
 * 当 [eventKey] 在当前事件处理器中能够被处理时（ [EventProcessor.isProcessable] == true ），
 * 通过 [block] 计算一个目标事件类型。
 *
 * [block] 计算的事件类型不做限制，但是应当尽量保证结果类型与 [eventKey] 类型一致
 *
 * ```kotlin
 * eventProcessor(FooEvent) {
 *    FooEventImpl()
 * }
 * ```
 *
 */
public suspend inline fun EventProcessor.pushIfProcessable(
    eventKey: Event.Key<*>,
    block: () -> Event
): EventProcessingResult? {
    if (isProcessable(eventKey)) {
        return push(block())
    }
    return null
}

/**
 * 当 [E] 在当前事件处理器中能够被处理时（ [EventProcessor.isProcessable] == true ），
 * 通过 [block] 计算一个目标事件类型。
 *
 * [E] 的 [Event.Key] 通过 [Event.Key.getKey] 获取。
 *
 * ```kotlin
 * eventProcessor<FooEvent> {
 *    FooEventImpl()
 * }
 * ```
 *
 * 如果可以明确事件类型，更推荐使用 `pushIfProcessable(EventKey) { EventImpl() }` 的方式来避免反射可能导致的未知问题。
 *
 */
public suspend inline fun <reified E : Event> EventProcessor.pushIfProcessable(block: () -> E): EventProcessingResult? {
    if (isProcessable(Event.Key.getKey<E>())) {
        return push(block())
    }
    return null
}


/**
 * 事件处理器对整个事件流程执行完毕后得到的最终响应。
 *
 * 提供一个特殊的空内容伴生实现 [Empty] 来得到一个结果为空的实现。
 */
public interface EventProcessingResult {

    /**
     * 本次流程下执行后得到的所有响应结果的视图的二次收集结果。
     * 请使用 [resultsView], 此api会在适当时机被删除。
     * @see resultsView
     */
    @Deprecated("Use 'resultsView'", replaceWith = ReplaceWith("resultsView"), level = DeprecationLevel.ERROR)
    public val results: List<EventResult> get() = resultsView.toList()
    
    
    /**
     * 本次流程下执行后得到的所有响应结果的视图。按照顺序计入。
     */
    public val resultsView: IndexAccessView<EventResult>

    /**
     * [EventProcessingResult] 的特殊无效实现，一般使用在例如全局拦截器进行拦截的时候。
     */
    public companion object Empty : EventProcessingResult {
        @Deprecated("Use 'resultsView'", replaceWith = ReplaceWith("resultsView"), level = DeprecationLevel.ERROR)
        override val results: List<EventResult>
            get() = emptyList()
        
        override val resultsView: IndexAccessView<EventResult>
            get() = emptyView()

    }
}


/**
 * 在事件处理的流程中可能出现的异常。
 */
public open class EventProcessingException : SimbotIllegalStateException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}


/**
 * 在监听函数执行过程中出现的异常。
 */
public class EventListenerProcessingException : EventProcessingException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}
