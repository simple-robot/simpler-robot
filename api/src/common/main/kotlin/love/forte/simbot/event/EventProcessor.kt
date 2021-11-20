package love.forte.simbot.event


/**
 *
 * 事件处理器，代表一个事件被触发的入口。
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
    public suspend fun push(event: Event) : EventProcessingResult

}


/**
 * 事件处理器对整个事件流程执行完毕后得到的最终响应。
 */
public interface EventProcessingResult {

    /**
     * 本次流程下执行后得到的所有响应结果。按照顺序计入。
     */
    public val results: List<EventResult>




}

