/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.event

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.event.EventProcessingResult.Empty
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
    public suspend fun push(event: Event) : EventProcessingResult


    /**
     * 推送一个事件，并阻塞的等待结果。
     *
     * *不被推荐的使用方法。*
     */
    @Api4J
    public fun pushBlocking(event: Event): EventProcessingResult = runBlocking { push(event) }


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


public suspend inline fun EventProcessor.pushIfProcessable(eventKey: Event.Key<*>, block: () -> Event): EventProcessingResult? {
    if (isProcessable(eventKey)) {
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

