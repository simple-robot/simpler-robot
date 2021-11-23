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

import love.forte.simbot.event.EventProcessingResult.Empty
import org.jetbrains.annotations.NonBlocking


/**
 * 事件处理器，代表一个事件被触发的入口。
 *
 * 建议事件处理器对事件进行处理的时候，通过内置的调度器进行处理。
 *
 * @author ForteScarlet
 */
public interface EventProcessor {

    /**
     * 推送一个事件到当前事件处理器。
     *
     * 事件处理器会按照流程触发所有应被触发的事件，并将所有 [EventListener]
     *
     *
     * TODO 返回值应该是什么？ Flow<EventResult>?
     *
     */
    @NonBlocking
    public suspend fun push(event: Event) : EventProcessingResult

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
    public val results: List<EventResult>


    /**
     * [EventProcessingResult] 的特殊无效实现，一般使用在例如全局拦截器进行拦截的时候。
     *
     * [Empty] 的 [results][Empty.results] 永远返回空列表。
     */
    public companion object Empty : EventProcessingResult {
        override val results: List<EventResult> get() = emptyList()

    }
}

