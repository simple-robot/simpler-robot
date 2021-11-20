package love.forte.simbot.event

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
    public val results: List<EventResult>


    // 其他参数

    // 事件 processor

}


// TODO 也许需要一个 context 的工厂?
public interface EventProcessContextFactory {

}