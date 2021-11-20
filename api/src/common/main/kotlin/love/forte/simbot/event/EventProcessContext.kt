package love.forte.simbot.event


/**
 *
 * 整个事件流程中进行传递的上下文。
 *
 * 此流程上下文由事件被触发开始，从头至尾完成参与完成流程下各个节点的信息传递。
 *
 *
 * @author ForteScarlet
 */
public interface EventProcessContext {

    /**
     * 本次监听流程中的事件主题。
     */
    public val event: Event

    // 事件 processor

}


// TODO 也许需要一个 context 的工厂
public interface EventProcessContextFactory {

}