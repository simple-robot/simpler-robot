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

import org.jetbrains.annotations.UnmodifiableView
import kotlin.coroutines.CoroutineContext


/**
 *
 * 整个事件流程中进行传递的上下文。
 *
 * 此流程上下文由事件被触发开始，从头至尾完成参与完成流程下各个节点的信息传递。
 *
 * 事件流程中进行流转的上下文也是一个协程上下文.
 * @author ForteScarlet
 */
public interface EventProcessContext : CoroutineContext.Element {
    public companion object Key : CoroutineContext.Key<EventProcessContext>
    override val key: CoroutineContext.Key<*> get() = Key

    // 实现 CoroutineContext.Element?

    /**
     * 本次监听流程中的事件主题。
     */
    public val event: Event

    /**
     * 已经执行过的所有监听函数的结果。
     *
     * 此列表仅由事件处理器内部操作，是一个对外不可变视图。
     */
    public val results: @UnmodifiableView List<EventResult>


    // 其他参数

    // 事件 processor

}


// TODO 也许需要一个 context 的工厂?
public interface EventProcessContextFactory {

}