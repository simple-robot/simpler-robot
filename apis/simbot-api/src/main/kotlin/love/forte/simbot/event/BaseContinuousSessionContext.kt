package love.forte.simbot.event

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.*
import love.forte.simbot.event.Event.Key.Companion.isSub

@ExperimentalSimbotApi
public abstract class BaseContinuousSessionContext : ContinuousSessionContext() {
    
    protected abstract val coroutineScope: CoroutineScope
    
    @JvmSynthetic
    abstract override suspend fun <T> waiting(id: ID, listener: ResumeListener<T>): T
    

    
    
    /**
     * 提供一个 [MessageEvent] 作为参数，
     * 只有当另外一个同类型或者此类型的自类型的事件(同一个所属组件)被触发、且这个事件的源与发送人一致的时候才会继续触发后续事件。
     *
     * ### [ContactMessageEvent]
     * 如果 [sourceEvent] 为 [ContactMessageEvent] 类型，
     * 则当下一个同类型或此事件子类型的事件被触发，且：
     * [ContactMessageEvent.user] 的id与 [sourceEvent] 中的userId一致，则会触发 [listener].
     *
     *
     * ### [ChatRoomMessageEvent]
     * 如果 [sourceEvent] 为 [ChatRoomMessageEvent] 类型，
     * 则当下一个同类型或此事件子类型的事件被触发，且：
     * [ChatRoomMessageEvent.author] 的id与 [sourceEvent] 中的author id一致；
     * [ChatRoomMessageEvent.source] 的id与 [sourceEvent] 中的source id一致，
     * 则会触发 [listener].
     *
     * 目前仅支持上述两个 [MessageEvent] 下的类型，其他 [MessageEvent] 的额外实现不被支持并会抛出异常。
     *
     * 这种监听只会影响到同种类型的监听，比如对于一个 [ContactMessageEvent] 下的子类型 `MyMsgEvent1` 和 `MyMsgEvent2`,
     * 如果你的 [sourceEvent] 类型为 `MyMsgEvent1`, 那么便不会收到 `MyMsgEvent2` 事件类型的消息。
     *
     * 上述的各项判断通过 [Event.Key] 进行操作与判断，
     * 如果对于Key的实现中存在不规范的交叉继承，那么有可能会导致 [ClassCastException].
     *
     *
     * @throws SimbotIllegalArgumentException 如果监听的事件类型不是 [ChatRoomMessageEvent] 或 [ContactMessageEvent] 类型的其中一种。
     * @throws ClassCastException 如果对于 [Event.Key] 的实现不够规范。
     */
    @JvmSynthetic
    public open suspend fun <E : MessageEvent, T> waitingNextMessage(
        id: ID = randomID(),
        sourceEvent: E,
        listener: ClearTargetResumeListener<E, T>,
    ): T {
        val key = sourceEvent.key
        return when {
            key isSub ContactMessageEvent -> {
                sourceEvent as ContactMessageEvent
                val sourceUserId = sourceEvent.user().id
                waiting(id) { provider ->
                    doListenerOnContactMessage(
                        key, sourceEvent.component,
                        provider, listener, sourceUserId
                    )
                }
            }
            key isSub ChatRoomMessageEvent -> {
                sourceEvent as ChatRoomMessageEvent
                val sourceAuthorId = sourceEvent.author().id
                val sourceChatroomId = sourceEvent.source().id
                waiting(id) { provider ->
                    doListenerOnChatroomMessage(
                        key, sourceEvent.component, provider, listener,
                        sourceAuthorId,
                        sourceChatroomId
                    )
                }
            }
            else -> throw SimbotIllegalArgumentException("Source event only support subtype of ContactMessageEvent or ChatroomMessageEvent.")
        }
    }
    
    
    private suspend inline fun <E : MessageEvent, T> EventProcessingContext.doListenerOnContactMessage(
        sourceKey: Event.Key<*>,
        component: Component,
        provider: ContinuousSessionProvider<T>,
        listener: ClearTargetResumeListener<E, T>,
        userId: ID,
    ) {
        val event = event
        if (event.component == component && event.key isSub sourceKey) {
            event as ContactMessageEvent
            if (event.user().id == userId) {
                @Suppress("UNCHECKED_CAST")
                listener.run { invoke(event as E, provider) }
            }
        }
    }
    
    private suspend inline fun <E : MessageEvent, T> EventProcessingContext.doListenerOnChatroomMessage(
        sourceKey: Event.Key<*>,
        component: Component,
        provider: ContinuousSessionProvider<T>,
        listener: ClearTargetResumeListener<E, T>,
        authorId: ID,
        chatroomId: ID,
    ) {
        val event = event
        if (event.component == component && event.key isSub sourceKey) {
            event as ChatRoomMessageEvent
            if (event.source().id == chatroomId && event.author().id == authorId) {
                @Suppress("UNCHECKED_CAST")
                listener.run { invoke(event as E, provider) }
            }
        }
    }
    
    
}