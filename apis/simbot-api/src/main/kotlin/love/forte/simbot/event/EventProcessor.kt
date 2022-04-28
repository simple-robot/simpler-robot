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

package love.forte.simbot.event

import love.forte.simbot.Api4J
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.event.EventProcessingResult.Empty
import love.forte.simbot.utils.runInBlocking
import org.jetbrains.annotations.UnmodifiableView
import java.util.concurrent.Future


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
    @JvmSynthetic
    public suspend fun push(event: Event): EventProcessingResult


    /**
     * 推送一个事件，并阻塞的等待结果。
     *
     * *不被推荐的使用方法。*
     */
    @Api4J
    public fun pushBlocking(event: Event): EventProcessingResult = runInBlocking { push(event) }


    /**
     * 推送一个事件，并异步的执行。返回一个 [Future].
     */
    @Api4J
    public fun pushAsync(event: Event): Future<EventProcessingResult>

    /**
     * 判断是否存在对应的事件监听器。
     */
    public fun isProcessable(eventKey: Event.Key<*>): Boolean

}


public suspend inline fun EventProcessor.pushIfProcessable(
    eventKey: Event.Key<*>,
    block: () -> Event
): EventProcessingResult? {
    if (isProcessable(eventKey)) {
        return push(block())
    }
    return null
}


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
     * 本次流程下执行后得到的所有响应结果。按照顺序计入。
     */
    public val results: @UnmodifiableView List<EventResult>


    /**
     * [EventProcessingResult] 的特殊无效实现，一般使用在例如全局拦截器进行拦截的时候。
     *
     * [Empty] 的 [results][Empty.results] 永远得到空列表。
     */
    public companion object Empty : EventProcessingResult {
        override val results: List<EventResult> get() = emptyList()

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
