package love.forte.simbot.event

import love.forte.simbot.Bot
import love.forte.simbot.message.doSafeCast


/**
 *
 * 与 **请求** 有关的事件。
 *
 * 对于一个请求，如果要对其响应，必然
 *
 * @author ForteScarlet
 */
public interface RequestEvent<
        AR, RR,
        AA : RequestEvent.AcceptAction<AR>,
        RA : RequestEvent.RejectAction<RR>> : Event {
    override val metadata: Event.Metadata
    override val bot: Bot

    /**
     * 对于请求事件，可见范围普遍为 [Event.VisibleScope.INTERNAL] 或 [Event.VisibleScope.PRIVATE].
     */
    override val visibleScope: Event.VisibleScope

    /**
     *
     */
    public suspend fun accept(action: AA): AR

    /**
     *
     */
    public suspend fun <R> reject(action: RA): RR


    public interface AcceptAction<R> {


        // TODO
        public companion object Direct : AcceptAction<Boolean>
    }


    public interface RejectAction<R> {


        // TODO
        public companion object Direct : RejectAction<Boolean>
    }



    public companion object Key : BaseEventKey<RequestEvent<*,*,*,*>>("api-request") {
        override fun safeCast(value: Any): RequestEvent<*,*,*,*>? = doSafeCast(value)
    }
}