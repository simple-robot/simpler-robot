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

@file:JvmName("ContinuousSessionScopeContextUtil")

package love.forte.simbot.event

import kotlinx.coroutines.*
import love.forte.simbot.*
import love.forte.simbot.event.Event.Key.Companion.isSub
import love.forte.simbot.utils.runInBlocking
import love.forte.simbot.utils.runInTimeoutBlocking
import love.forte.simbot.utils.runWithInterruptible
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


/**
 *
 * 持续会话的作用域, 通过此作用域在监听函数监听过程中进行会话嵌套。
 *
 * **注: [ContinuousSessionContext] 尚处于实验阶段, 且目前对Java的友好度一般。如果是Java开发者目前请不要过度依赖此功能。**
 *
 * [waiting] 中注册的临时listener将会在所有监听函数被**触发前**, 依次作为一个各自独立的**异步任务**执行，
 * 并且因为 [ResumeListener.invoke] 不存在返回值, 因此所有的临时会话监听函数均**无法**对任何正常的监听流程产生影响，也**无法**参与到正常流程中的结果返回中。
 *
 *
 * 在事件处理流程中，包含了临时监听函数的情况大概如下所示：
 * ```
 * + ----- +   push    + -------------------- + 构建Context  + ---------- +
 * | Event | --------> | EventListenerManager | -----+----> | 正常处理流程 |
 * + ------+           + ---------------------+      |      + --------- +
 *                                                   |
 *                                                   | async launch
 *                      + ------------------ +       |
 *                      | ResumedListener(s) | <-----+
 *                      + ------------------ +
 *
 * ```
 *
 * 在 [ContinuousSessionContext] 中，不论是 [provider][getProvider] 还是 [receiver][getReceiver],
 * 它们都会在一次会话结束（使用了能够导致 [ContinuousSessionProvider.isCompleted] == true 的函数 ）后被移除。
 * 因此当会话结束后，不论 [provider][getProvider] 还是 [receiver][getReceiver] 都会变为null。
 * 如果你希望得到这次会话的某个返回值，你需要通过 [waiting] 挂起等待。
 *
 *
 * 对于较为简单的会话嵌套，（在kotlin中）你可以使用以下方式轻松完成：
 * ```kotlin
 * suspend fun EventListenersGenerator.fooListener() {
 *      GroupEvent { event: GroupEvent -> // this: EventListenerProcessingContext
 *          // 获取 session context. 正常情况下不可能为null
 *          val sessionContext = context.getAttribute(EventProcessingContext.Scope.ContinuousSession) ?: error("不支持会话！")
 *
 *          // 注册并等待一个临时监听函数提供结果. 此处挂起并等待
 *          val num: Int = sessionContext.waiting { provider -> // this: EventProcessingContext
 *              // 这里使用的是 Event.Key 作为判断方式。
 *              // 当然，也可以用 is 判断: if (event is ChannelEvent)
 *              if (event.key isSub ChannelEvent) {
 *                  // 假如监听到一个事件为 ChannelEvent 类型, 推送结果.
 *                  provider.push(1)
 *
 *              }
 *          }
 *
 *          assert(num == 1)
 *      }
 *  }
 *
 * ```
 *
 * 不过假如你的逻辑比较复杂，可能在 `waitingFor` 中仍需要嵌套更多的其他listener，那么建议对整个函数进行拆分。比如：
 * ```
 *suspend fun EventListenersGenerator.myListener() {
 *      GroupEvent { event: GroupEvent  -> // this: EventListenerProcessingContext
 *              // 获取 session context, 并认为它不为null。
 *              val sessionContext = context.getAttribute(EventProcessingContext.Scope.ContinuousSession) ?: error("不支持会话！")
 *
 *              // 假设你需要获取2个数字，每个数字中同样需要再次等待一个数字。
 *
 *              val num1 = sessionContext.waitingFor(randomID(), listener = ::getNum1)
 *              val num2 = sessionContext.waitingFor(randomID(), listener = ::getNum2)
 *
 *              // getNum1&2: suspend fun EventProcessingContext.getNum1(provider: ContinuousSessionProvider<Int>): Unit
 *
 *              assert((num1 + num2) == 10) // sum: 10
 *              null
 *          }
 *      }
 * ```
 *
 * 如果你有明确的监听类型目标，你也可以使用 [waiting] 或 [waitFor] 来简化类型匹配:
 * ```
 *suspend fun EventListenersGenerator.myListener() {
 *      GroupEvent { event : GroupEvent -> // this: EventListenerProcessingContext
 *          // 获取 session context, 并认为它不为null。
 *          val sessionContext = processingContext.getAttribute(EventProcessingContext.Scope.ContinuousSession) ?: error("不支持会话！")
 *
 *          assert(num == 1)
 *
 *          null
 *      }
 *}
 * ```
 *
 * ## 超时
 * 对于挂起函数, 你可以使用 [withTimeout] 来包裹诸如 [waiting] 或者 [waitingNextMessage] 等这类挂起函数。
 * ```kotlin
 * val session: ContinuousSessionContext = ...
 *
 * // throw Exception if timeout
 * withTimeout(5.seconds) {
 *    session.waiting { provider -> // this: EventProcessingContext
 *       // ...
 *    }
 * }
 * ```
 *
 *
 * ## 会话清除
 * 当你通过 [waiting] 注册了一个会话之后，只有在出现以下情况后，他们会被清除：
 * - 通过参数 `timeout` 指定了超时时间，且到达了超时时间。
 * - 通过 [ContinuousSessionProvider.push] 推送了结果或 [ContinuousSessionProvider.pushException] 推送了异常。
 * - 通过 [ContinuousSessionProvider.cancel] 或者 [ContinuousSessionReceiver.cancel] 进行了关闭操作。
 *
 *
 * @see ContinuousSessionProvider
 * @see ContinuousSessionReceiver
 *
 * @author ForteScarlet
 */
@ExperimentalSimbotApi
public abstract class ContinuousSessionContext {
    
    protected abstract val coroutineScope: CoroutineScope
    
    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     * ## 超时处理
     * 使用 [withTimeout] 或其衍生函数来进行超时控制。
     * ```kotlin
     * val session: ContinuousSessionContext = ...
     *
     * withTimeout(5.seconds) {
     *    session.waiting { provider -> // this: EventProcessingContext
     *       // ...
     *       provider.push(...)
     *    }
     * }
     * ```
     *
     */
    @JvmSynthetic
    public abstract suspend fun <T> waiting(
        id: ID = randomID(),
        listener: ResumeListener<T>,
    ): T
    
    
    @Api4J
    @JvmOverloads
    @JvmName("waiting")
    public fun <T> waiting4J(id: ID = randomID(), listener: BlockingResumeListener<T>): T {
        return runInBlocking { waiting(id, listener.parse()) }
    }
    
    
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
 * 持续会话的结果接收器，通过 [ContinuousSessionContext.getReceiver] 获取，
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
     * 阻塞的等待当前receiver得到结果或推送了异常。
     *
     * 会一直阻塞当前线程直到 [await] 返回。
     *
     * @throws InterruptedException 阻塞内容所在线程被中断
     */
    @Api4J
    @Throws(InterruptedException::class)
    public fun waiting(): T = runInBlocking { await() }
    
    
    /**
     * 阻塞的等待当前receiver得到结果或推送了异常，如果在规定时间内未结束则抛出超时异常。
     *
     * @throws InterruptedException 阻塞内容所在线程被中断
     * @throws TimeoutException 如果时限内未响应
     */
    @Api4J
    @Throws(InterruptedException::class, TimeoutException::class)
    public fun waiting(timeout: Long, timeUnit: TimeUnit): T =
        runInTimeoutBlocking(timeUnit.toMillis(timeout)) {
            await()
        }
    
    /**
     * 终止此会话。不会对终止状态进行检测。
     */
    public fun cancel(reason: Throwable? = null)
    
    /**
     * 将当前这个 session 转化为 [Future].
     */
    public fun asFuture(): Future<T>
}

/**
 * 持续会话的结果提供者，通过 [ContinuousSessionContext.getReceiver] 构建获取，向目标会话推送一个结果。
 *
 * 当使用[push]推送结果、使用[pushException]推送错误或者通过[cancel]终止会话的时候，
 * 此提供者对应的接收者 [ContinuousSessionReceiver] 会得到相对应的结果。
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
     * 关闭此会话。
     */
    public fun cancel(reason: Throwable? = null)
    
    /**
     * 当完成时执行.
     */
    public fun invokeOnCompletion(handler: CompletionHandler)
}


/**
 *
 * 相当于函数 `EventProcessingContext.(ContinuousSessionProvider<T>) -> Unit`。
 *
 * 对于Java使用者可以考虑使用 [BlockingResumeListener]。
 *
 * @see BlockingResumeListener
 * @see ClearTargetResumeListener
 * @see BlockingClearTargetResumeListener
 */
public fun interface ResumeListener<T> {
    public suspend operator fun EventProcessingContext.invoke(provider: ContinuousSessionProvider<T>)
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
    ResumeListener { provider -> runWithInterruptible { this@parse(this, provider) } }

/**
 * 有着明确监听目标的 [ResumeListener]。
 *
 * @see BlockingClearTargetResumeListener
 */
public fun interface ClearTargetResumeListener<E : Event, T> {
    public suspend operator fun EventProcessingContext.invoke(
        event: E,
        provider: ContinuousSessionProvider<T>,
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
    ClearTargetResumeListener { event, provider -> runWithInterruptible { this@parse(event, this, provider) } }