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
 *
 */

@file:JvmName("ContinuousSessionScopeContextUtil")

package love.forte.simbot.event

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.*
import love.forte.simbot.utils.lazyValue
import love.forte.simbot.utils.runWithInterruptible
import java.util.concurrent.Future
import kotlin.time.Duration


/**
 *
 * 持续会话的作用域, 通过此作用域在监听函数监听过程中进行会话嵌套。
 *
 * [waitingFor] 与 [waiting] 中注册的临时listener将会在所有监听函数被**触发前**, 依次作为一个独立的**异步任务**执行，
 * 并且因为 [ResumeListener.invoke] 不存在返回值, 因此所有的临时会话监听函数均**无法**对任何正常的监听流程产生影响，也**无法**参与到正常流程中的结果返回中。
 *
 *
 * 在事件处理流程中，包含了临时监听函数的情况大概如下所示：
 * ```
 *
 * + ----- +   push    + -------------------- + 构建Context  + ---------- +
 * | Event | --------> | EventListenerManager | -----+----> | 正常处理流程 |
 * + ------+           + ---------------------+      |      + --------- +
 *                                                   |
 *                                                   | launch
 *                      + ------------------ +       |
 *                      | ResumedListener(s) | <-----+
 *                      + ------------------ +
 *                            All async
 * ```
 *
 * 在 [ContinuousSessionContext] 中，不论是 [provider][getProvider] 还是 [receiver][getReceiver], 它们都会在一次会话结束（使用了能够导致 [ContinuousSessionProvider.isCompleted] == true 的函数 ）后被移除。
 * 因此当会话结束后，[provider][getProvider] 还是 [receiver][getReceiver] 都会变为null。如果你希望得到这次会话的某个返回值，你需要通过 [waitingFor] 挂起等待，或者通过 [waiting] 得到并保存一个 [ContinuousSessionReceiver] 实例以备使用。
 *
 *
 * 对于较为简单的会话嵌套，（在kotlin中）你可以使用以下方式轻松完成：
 * ```kotlin
 * suspend fun EventListenerManager.myListener() {
 *      listen { context: EventListenerProcessingContext, event: GroupEvent ->
 *          // 获取 session context. 正常情况下不可能为null
 *          val sessionContext = context.getAttribute(EventProcessingContext.Scope.ContinuousSession) ?: error("不支持会话！")
 *
 *          println("event: $event")
 *
 *          // 构建一个ID
 *          val id = randomID()
 *          // 注册并等待一个临时监听函数提供结果. 此处挂起并等待
 *          val num = sessionContext.waitingFor<Int>(id) { c, provider ->
 *              if (c.event.key isSubFrom ChannelEvent) {
 *              // 这里使用的是 Event.Key 作为判断方式。
 *              // 当然，也可以用 is 判断: if (c.event is ChannelEvent)
 *
 *              // 假如监听到一个事件为 ChannelEvent 类型, 推送结果.
 *              provider.push(1)
 *              }
 *          }
 *
 *          assert(num == 1)
 *          null
 *      }
 *  }
 *
 * ```
 *
 * 不过假如你的逻辑比较复杂，可能在 `waitingFor` 中仍需要嵌套更多的其他listener，那么建议对整个函数进行拆分。比如：
 * ```
 *suspend fun EventListenerManager.myListener() {
 *      listen { context: EventListenerProcessingContext, _: GroupEvent ->
 *          // 获取 session context, 并认为它不为null。
 *          val sessionContext = context.getAttribute(EventProcessingContext.Scope.ContinuousSession) ?: error("不支持会话！")
 *
 *          // 假设你需要获取2个数字，每个数字中同样需要再次等待一个数字。
 *
 *          val num1 = sessionContext.waitingFor(randomID(), listener = ::getNum1)
 *          val num2 = sessionContext.waitingFor(randomID(), listener = ::getNum2)
 *
 *          // getNum1&2: suspend fun getNum1(context: EventProcessingContext, provider: ContinuousSessionProvider<Int>): Unit
 *
 *          assert((num1 + num2) == 10) // sum: 10
 *          null
 *      }
 *      }
 * ```
 *
 * 如果你有明确的监听类型目标，你也可以使用 [waitingFor] 或 [waitFor] 来简化类型匹配:
 * ```
 *suspend fun EventListenerManager.myListener() {
 *      listen { processingContext: EventListenerProcessingContext, _: GroupEvent ->
 *          // 获取 session context, 并认为它不为null。
 *          val sessionContext = processingContext.getAttribute(EventProcessingContext.Scope.ContinuousSession) ?: error("不支持会话！")
 *
 *          // 使用 waitingFor<Int>
 *          val num0: Int = sessionContext.waitingFor { context, provider ->
 *              delay(100)
 *              provider.push(1)
 *          }
 *
 *          assert(num0 == 1)
 *
 *          // 使用 waitFor<GroupEvent, Int>
 *          val num: Int = sessionContext.waitFor { event: GroupEvent, context, provider ->
 *              delay(100)
 *              provider.push(1)
 *          }
 *
 *          assert(num == 1)
 *
 *          null
 *      }
 *}
 * ```
 *
 * ## 超时
 *
 * 当然，不论是 [waitingFor]、[waiting] 还是 [waitFor], 它们都有 `timeout` 参数，来实现在超时异常的处理。当超时的时候，挂起点将会抛出 [kotlinx.coroutines.TimeoutCancellationException] 异常。
 * 作为参数的超时时间除了会在超时后抛出异常以外，还会关闭内部对应的会话，这是与你在外部直接使用 [kotlinx.coroutines.withTimeout] 有所不同的。
 * ```
 * session.waitingFor(randomID(), 5000) { ... }
 *
 * session.waitingFor(randomID(), 5.seconds) { ... }
 * ```
 *
 * 但是需要注意的是，通过 timeout 参数指定时间并超时后，抛出的异常是 [ContinuousSessionTimeoutException] 而并非 [kotlinx.coroutines.TimeoutCancellationException], 这也是与 [kotlinx.coroutines.withTimeout] 有所区别的地方。
 *
 * ## 会话清除
 * 当你通过 [waiting] 或者 [waitingFor] 注册了一个会话之后，只有在出现以下情况后，他们会被清除：
 * - 通过参数 `timeout` 指定了超时时间，且到达了超时时间。
 * - 通过 [ContinuousSessionProvider.push] 推送了结果或 [ContinuousSessionProvider.pushException] 推送了异常。
 * - 通过 [ContinuousSessionProvider.cancel] 或者 [ContinuousSessionReceiver.cancel] 进行了关闭操作。
 *
 *
 * @see ContinuousSessionProvider
 * @see ContinuousSessionReceiver
 * @see waitFor
 *
 * @author ForteScarlet
 */
public abstract class ContinuousSessionContext {

    protected abstract val coroutineScope: CoroutineScope

    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     * @throws ContinuousSessionTimeoutException 当 [timeout] 大于0 且持续时间超过此时限。
     */
    @JvmSynthetic
    public abstract suspend fun <T> waitingFor(
        id: ID = randomID(),
        timeout: Long = 0,
        listener: ResumeListener<T>
    ): T


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
     * ### [ChatroomMessageEvent]
     * 如果 [sourceEvent] 为 [ChatroomMessageEvent] 类型，
     * 则当下一个同类型或此事件子类型的事件被触发，且：
     * [ChatroomMessageEvent.author] 的id与 [sourceEvent] 中的author id一致；
     * [ChatroomMessageEvent.source] 的id与 [sourceEvent] 中的source id一致，
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
     * @throws SimbotIllegalArgumentException 如果监听的事件类型不是 [ChatroomMessageEvent] 或 [ContactMessageEvent] 类型的其中一种。
     * @throws ClassCastException 如果对于 [Event.Key] 的实现不够规范。
     */
    @JvmSynthetic
    public open suspend fun <E : MessageEvent, T> waitingForOnMessage(
        id: ID = randomID(),
        timeout: Long = 0,
        sourceEvent: E,
        listener: ClearTargetResumeListener<E, T>
    ): T {
        val key = sourceEvent.key
        return when {
            key isSubFrom ContactMessageEvent -> {
                sourceEvent as ContactMessageEvent
                val sourceUserId = sourceEvent.user().id
                waitingFor(id, timeout) { context, provider ->
                    doListenerOnContactMessage(key, sourceEvent.component, context, provider, listener) { sourceUserId }
                }
            }
            key isSubFrom ChatroomMessageEvent -> {
                sourceEvent as ChatroomMessageEvent
                val sourceAuthorId = sourceEvent.author().id
                val sourceChatroomId = sourceEvent.source().id
                waitingFor(id, timeout) { context, provider ->
                    doListenerOnChatroomMessage(key, sourceEvent.component, context, provider, listener,
                        { sourceAuthorId },
                        { sourceChatroomId }
                    )
                }
            }
            else -> throw SimbotIllegalArgumentException("Source event only support subtype of ContactMessageEvent or ChatroomMessageEvent.")
        }
    }

    private suspend inline fun <E : MessageEvent, T> doListenerOnContactMessage(
        sourceKey: Event.Key<*>,
        component: Component,
        context: EventProcessingContext,
        provider: ContinuousSessionProvider<T>,
        listener: ClearTargetResumeListener<E, T>,
        userIdBlock: () -> ID
    ) {
        val event = context.event
        if (event.component == component && event.key isSubFrom sourceKey) {
            event as ContactMessageEvent
            if (event.user().id == userIdBlock()) {
                @Suppress("UNCHECKED_CAST")
                listener(event as E, context, provider)
            }
        }
    }

    private suspend inline fun <E : MessageEvent, T> doListenerOnChatroomMessage(
        sourceKey: Event.Key<*>,
        component: Component,
        context: EventProcessingContext,
        provider: ContinuousSessionProvider<T>,
        listener: ClearTargetResumeListener<E, T>,
        authorIdBlock: () -> ID,
        chatroomIdBlock: () -> ID
    ) {
        val event = context.event
        if (event.component == component && event.key isSubFrom sourceKey) {
            event as ChatroomMessageEvent
            if (event.source().id == chatroomIdBlock() && event.author().id == authorIdBlock()) {
                @Suppress("UNCHECKED_CAST")
                listener(event as E, context, provider)
            }
        }
    }

    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     * @throws ContinuousSessionTimeoutException [timeout] 大于0 且持续时间超过此时限。
     */
    @JvmSynthetic
    public abstract suspend fun <E : Event, T> waitingFor(
        id: ID = randomID(),
        timeout: Long = 0,
        eventKey: Event.Key<E>,
        listener: ClearTargetResumeListener<E, T>
    ): T

    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突，则上一个函数将会被立即关闭处理。
     *
     */
    @JvmSynthetic
    public abstract fun <T> waiting(
        id: ID = randomID(),
        timeout: Long = 0,
        listener: ResumeListener<T>
    ): ContinuousSessionReceiver<T>


    /**
     * 提供一个 [MessageEvent] 作为参数，
     * 只有当另外一个同类型或者此类型的自类型的事件(同一个所属组件)被触发、且这个事件的源与发送人一致的时候才会继续触发后续事件。
     *
     * 具体说明参考 [waitingForOnMessage].
     *
     * @see waitingForOnMessage
     * @throws SimbotIllegalArgumentException 如果监听的事件类型不是 [ChatroomMessageEvent] 或 [ContactMessageEvent] 类型的其中一种。
     * @throws ClassCastException 如果对于 [Event.Key] 的实现不够规范。
     */
    @JvmSynthetic
    public fun <E : MessageEvent, T> waitingOnMessage(
        id: ID = randomID(),
        timeout: Long = 0,
        sourceEvent: MessageEvent,
        listener: ClearTargetResumeListener<E, T>
    ): ContinuousSessionReceiver<T> {
        val key = sourceEvent.key
        return when {
            key isSubFrom ContactMessageEvent -> {
                sourceEvent as ContactMessageEvent
                val sourceUserId = lazyValue { sourceEvent.user().id }
                waiting(id, timeout) { context, provider ->
                    doListenerOnContactMessage(
                        key,
                        sourceEvent.component,
                        context,
                        provider,
                        listener
                    ) { sourceUserId() }
                }
            }
            key isSubFrom ChatroomMessageEvent -> {
                sourceEvent as ChatroomMessageEvent
                val sourceAuthorId = lazyValue { sourceEvent.author().id }
                val sourceChatroomId = lazyValue { sourceEvent.source().id }
                waiting(id, timeout) { context, provider ->
                    doListenerOnChatroomMessage(key, sourceEvent.component, context, provider, listener,
                        { sourceAuthorId() },
                        { sourceChatroomId() }
                    )
                }
            }
            else -> throw SimbotIllegalArgumentException("Source event only support subtype of ContactMessageEvent or ChatroomMessageEvent.")
        }
    }


    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     */
    @JvmSynthetic
    public abstract fun <E : Event, T> waiting(
        id: ID = randomID(),
        timeout: Long = 0,
        eventKey: Event.Key<E>,
        listener: ClearTargetResumeListener<E, T>
    ): ContinuousSessionReceiver<T>


    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     * @see waiting
     */
    @Api4J
    @JvmOverloads
    @JvmName("waiting")
    public fun <T> waiting4J(
        id: ID = randomID(),
        timeout: Long = 0,
        listener: BlockingResumeListener<T>
    ): ContinuousSessionReceiver<T> = waiting(id, timeout, listener.parse())


    /**
     * 根据一个消息事件监听这个人下一个所发送的消息。
     *
     * @see waitingOnMessage
     */
    @Api4J
    @JvmOverloads
    @JvmName("waitingOnMessage")
    public fun <E : MessageEvent, T> waitingOnMessage4J(
        id: ID = randomID(),
        timeout: Long = 0,
        sourceEvent: E,
        listener: BlockingClearTargetResumeListener<E, T>
    ): ContinuousSessionReceiver<T> = waitingOnMessage(id, timeout, sourceEvent, listener.parse())


    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     * @see waiting
     */
    @Api4J
    @JvmOverloads
    @JvmName("waiting")
    public fun <E : Event, T> waiting4J(
        id: ID = randomID(), // randomID(),
        timeout: Long = 0,
        eventKey: Event.Key<E>,
        listener: BlockingClearTargetResumeListener<E, T>
    ): ContinuousSessionReceiver<T> = waiting(id, timeout, eventKey, listener.parse())

    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     * @see waiting
     */
    @Api4J
    @JvmOverloads
    @JvmName("waiting")
    public fun <E : Event, T> waiting4J(
        id: ID = randomID(),
        timeout: Long = 0,
        eventType: Class<E>,
        listener: BlockingClearTargetResumeListener<E, T>
    ): ContinuousSessionReceiver<T> = waiting(id, timeout, Event.Key.getKey(eventType), listener.parse())


    /**
     * 尝试通过ID获取一个 provider.
     *
     * @see ContinuousSessionProvider
     */
    public abstract fun <T> getProvider(id: ID): ContinuousSessionProvider<T>?

    /**
     * 尝试通过ID获取一个 receiver.
     *
     * @see ContinuousSessionReceiver
     */
    public abstract fun <T> getReceiver(id: ID): ContinuousSessionReceiver<T>?


}


/**
 * [ContinuousSessionContext] 出现内部超时所抛出的异常。
 */
public class ContinuousSessionTimeoutException(message: String) : CancellationException(message), SimbotError


/**
 * [ContinuousSessionContext.waitingFor] 的简化函数，通过 [E] 和 [T] 来决定 [ContinuousSessionContext.waitingFor] 的事件内容与返回类型。
 *
 * @see ContinuousSessionContext.waitingFor
 */
public suspend inline fun <reified E : Event, T> ContinuousSessionContext.waitFor(
    id: ID = randomID(),
    timeout: Long = 0,
    listener: ClearTargetResumeListener<E, T>
): T {
    return waitingFor(id, timeout, Event.Key.getKey(), listener)
}

/**
 * [ContinuousSessionContext.waitingFor] 的内联简化函数，通过 [E] 和 [T] 来决定 [ContinuousSessionContext.waitingFor] 的事件内容与返回类型。
 *
 * @see waitFor
 */
public suspend inline fun <reified E : Event, T> ContinuousSessionContext.waitFor(
    id: ID = randomID(),
    timeout: Duration,
    listener: ClearTargetResumeListener<E, T>
): T = waitFor(id, timeout.inWholeMilliseconds, listener)


public suspend inline fun <E : Event, T> ContinuousSessionContext.waitingFor(
    id: ID = randomID(),
    timeout: Duration,
    eventKey: Event.Key<E>,
    listener: ClearTargetResumeListener<E, T>
): T = waitingFor(id, timeout.inWholeMilliseconds, eventKey, listener)


public suspend inline fun <T> ContinuousSessionContext.waitingFor(
    id: ID = randomID(),
    timeout: Duration,
    listener: ResumeListener<T>
): T = waitingFor(id, timeout.inWholeMilliseconds, listener)


public suspend inline fun <E : MessageEvent, T> ContinuousSessionContext.waitingForOnMessage(
    id: ID = randomID(),
    timeout: Duration,
    sourceEvent: E,
    listener: ClearTargetResumeListener<E, T>
): T = waitingForOnMessage(id, timeout.inWholeMilliseconds, sourceEvent, listener)

public fun <E : Event, T> ContinuousSessionContext.waiting(
    id: ID = randomID(),
    timeout: Duration,
    eventKey: Event.Key<E>,
    listener: ClearTargetResumeListener<E, T>
): ContinuousSessionReceiver<T> = waiting(id, timeout.inWholeMilliseconds, eventKey, listener)


public fun <T> ContinuousSessionContext.waiting(
    id: ID = randomID(),
    timeout: Duration,
    listener: ResumeListener<T>
): ContinuousSessionReceiver<T> = waiting(id, timeout.inWholeMilliseconds, listener)

public fun <E : MessageEvent, T> ContinuousSessionContext.waitingOnMessage(
    id: ID = randomID(),
    timeout: Duration,
    sourceEvent: MessageEvent,
    listener: ClearTargetResumeListener<E, T>
): ContinuousSessionReceiver<T> = waitingOnMessage(id, timeout.inWholeMilliseconds, sourceEvent, listener)

/**
 * 持续会话的结果接收器，通过 [ContinuousSessionContext.waiting] 获取，
 * 用于挂起并等待一个结果。
 *
 * @see ContinuousSessionProvider
 */
public interface ContinuousSessionReceiver<T> {

    /**
     * 获取或挂起等待结果。
     */
    @JvmSynthetic
    public suspend fun await(): T

    /**
     * 终止此会话。不会对终止状态进行检测。
     */
    public fun cancel(reason: Throwable? = null)

    /**
     * 终止此会话。会对终止状态进行检测，如果已经终止或完成则不会进行cancel行为。
     */
    public fun tryCancel(reason: Throwable? = null): Boolean

    /**
     * 将当前这个 session 转化为 [Future].
     */
    public fun asFuture(): Future<T>
}

/**
 * 持续会话的结果提供者，通过 [ContinuousSessionContext.getReceiver] 构建获取，向目标会话推送一个结果。
 *
 * 当使用[push]推送结果、使用[pushException]推送错误或者通过[cancel]终止会话的时候，此提供者对应的接收者 [ContinuousSessionReceiver] 或者 [ContinuousSessionContext.waitFor] 都会有相对应的相应状况。
 *
 *
 * @see ContinuousSessionContext.getReceiver
 * @see ContinuousSessionReceiver
 */
public interface ContinuousSessionProvider<T> {

    /**
     * 向其对应的 [ContinuousSessionReceiver] 推送一个结果。
     * 正常来讲，一个 session 只能够推送一次，如果多次推送可能会导致异常, 注意处理。
     *
     * @see isCompleted
     */
    public fun push(value: T)

    /**
     * 是否已经完成。不论是 [push]、 [pushException] 还是 [cancel], 执行后都视为*完成*。
     */
    public val isCompleted: Boolean

    /**
     * 向目标推送一个错误。
     */
    public fun pushException(e: Throwable)

    /**
     * 关闭此会话。某种意义上这也相当于 [pushException]
     */
    public fun cancel(reason: Throwable? = null)

    /**
     * 当完成时执行.
     */
    public fun invokeOnCompletion(handler: CompletionHandler)
}


/**
 *
 * @see BlockingResumeListener
 * @see ClearTargetResumeListener
 * @see BlockingClearTargetResumeListener
 */
public fun interface ResumeListener<T> {
    public suspend operator fun invoke(context: EventProcessingContext, provider: ContinuousSessionProvider<T>)
}

/**
 * 阻塞的 [ResumeListener].
 */
@Api4J
public fun interface BlockingResumeListener<T> {
    public operator fun invoke(context: EventProcessingContext, provider: ContinuousSessionProvider<T>)
}

@OptIn(Api4J::class)
internal fun <T> BlockingResumeListener<T>.parse(): ResumeListener<T> =
    ResumeListener { context, provider -> runWithInterruptible { this(context, provider) } }

/**
 * 有着明确监听目标的 [ResumeListener]。
 *
 * @see BlockingClearTargetResumeListener
 */
public fun interface ClearTargetResumeListener<E : Event, T> {
    public suspend operator fun invoke(
        event: E,
        context: EventProcessingContext,
        provider: ContinuousSessionProvider<T>
    )
}


/**
 * 有着明确监听目标的 [ResumeListener]。需要考虑重写 [invoke] 来实现事件类型的准确转化。
 *
 * @see ClearTargetResumeListener
 * @see ResumeListener
 */
@Api4J
public fun interface BlockingClearTargetResumeListener<E : Event, T> {
    public operator fun invoke(event: E, context: EventProcessingContext, provider: ContinuousSessionProvider<T>)
}

@OptIn(Api4J::class)
internal fun <E : Event, T> BlockingClearTargetResumeListener<E, T>.parse(): ClearTargetResumeListener<E, T> =
    ClearTargetResumeListener { event, context, provider -> runWithInterruptible { this(event, context, provider) } }