/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

@file:JvmName("ContinuousSessionScopeContextUtil")

package love.forte.simbot.event

import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.randomID
import java.util.concurrent.Future
import kotlin.experimental.ExperimentalTypeInference
import kotlin.time.Duration


/**
 *
 * 持续会话的作用域, 通过此作用域在监听函数监听过程中进行会话嵌套。
 *
 * [waitingFor] 与 [waiting] 中注册的临时listener将会在所有监听函数被**触发前**, 依次作为一个独立的**异步任务**执行，
 * 并且因为 [ResumedListener.invoke] 不存在返回值, 因此所有的临时会话监听函数均**无法**对任何正常的监听流程产生影响，也**无法**参与到正常流程中的结果返回中。
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
 *          val num0: Int = sessionContext.waitingFor(randomID()) { context, provider ->
 *              delay(100)
 *              provider.push(1)
 *          }
 *
 *          assert(num0 == 1)
 *
 *          // 使用 waitFor<GroupEvent, Int>
 *          val num: Int = sessionContext.waitFor(randomID()) { event: GroupEvent, context, provider ->
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
 * 当然，不论是 [waitingFor]、[waiting] 还是 [waitFor], 它们都有 `timeout` 参数，来实现在超时异常的处理。当超时的时候，挂起点将会抛出 [kotlinx.coroutines.TimeoutCancellationException] 异常。
 * ```
 * session.waitingFor(randomID(), 5000) { ... }
 *
 * session.waitingFor(randomID(), 5.seconds) { ... }
 * ```
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
     */
    @OptIn(ExperimentalTypeInference::class)
    @JvmSynthetic
    public abstract suspend fun <T> waitingFor(
        id: ID = randomID(),
        timeout: Long = 0,
        listener: ResumedListener<T>
    ): T


    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     */
    @JvmSynthetic
    public abstract suspend fun <E : Event, T> waitingFor(
        id: ID = randomID(),
        timeout: Long = 0,
        eventKey: Event.Key<E>,
        listener: ClearTargetResumedListener<E, T>
    ): T

    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突，则上一个函数将会被立即关闭处理。
     *
     */
    @JvmSynthetic
    public abstract fun <T> waiting(
        id: ID = randomID(),
        timeout: Long = 0,
        listener: ResumedListener<T>
    ): ContinuousSessionReceiver<T>


    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     */
    @JvmSynthetic
    public abstract fun <E : Event, T> waiting(
        id: ID = randomID(),
        timeout: Long = 0,
        eventKey: Event.Key<E>,
        listener: ClearTargetResumedListener<E, T>
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
        listener: BlockingResumedListener<T>
    ): ContinuousSessionReceiver<T> = waiting(id, timeout, listener.parse())


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
        listener: BlockingClearTargetResumedListener<E, T>
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
        listener: BlockingClearTargetResumedListener<E, T>
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
 * [waitingFor] 的内联简化函数，通过 [E] 和 [T] 来决定 waitingFor 的事件内容与返回类型。
 *
 */
public suspend inline fun <reified E : Event, T> ContinuousSessionContext.waitFor(
    id: ID = randomID(),
    timeout: Long = 0,
    listener: ClearTargetResumedListener<E, T>
): T {
    return waitingFor(id, timeout, Event.Key.getKey(), listener)
}

public suspend inline fun <reified E : Event, T> ContinuousSessionContext.waitFor(
    id: ID = randomID(),
    timeout: Duration,
    listener: ClearTargetResumedListener<E, T>
): T = waitFor(id, timeout.inWholeMilliseconds, listener)


public suspend fun <E : Event, T> ContinuousSessionContext.waitingFor(
    id: ID = randomID(),
    timeout: Duration,
    eventKey: Event.Key<E>,
    listener: ClearTargetResumedListener<E, T>
): T = waitingFor(id, timeout.inWholeMilliseconds, eventKey, listener)


public suspend fun <T> ContinuousSessionContext.waitingFor(
    id: ID = randomID(),
    timeout: Duration,
    listener: ResumedListener<T>
): T = waitingFor(id, timeout.inWholeMilliseconds, listener)


public fun <E : Event, T> ContinuousSessionContext.waiting(
    id: ID = randomID(),
    timeout: Duration,
    eventKey: Event.Key<E>,
    listener: ClearTargetResumedListener<E, T>
): ContinuousSessionReceiver<T> = waiting(id, timeout.inWholeMilliseconds, eventKey, listener)


public fun <T> ContinuousSessionContext.waiting(
    id: ID = randomID(),
    timeout: Duration,
    listener: ResumedListener<T>
): ContinuousSessionReceiver<T> = waiting(id, timeout.inWholeMilliseconds, listener)


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
 * @see BlockingResumedListener
 * @see ClearTargetResumedListener
 * @see BlockingClearTargetResumedListener
 */
public fun interface ResumedListener<T> {
    public suspend operator fun invoke(context: EventProcessingContext, provider: ContinuousSessionProvider<T>)
}

/**
 * 阻塞的 [ResumedListener].
 */
@Api4J
public fun interface BlockingResumedListener<T> {
    public operator fun invoke(context: EventProcessingContext, provider: ContinuousSessionProvider<T>)

    // override suspend operator fun invoke(context: EventProcessingContext, provider: ContinuousSessionProvider<T>) {
    //     invokeBlocking(context, provider)
    // }
}

@OptIn(Api4J::class)
internal fun <T> BlockingResumedListener<T>.parse(): ResumedListener<T> =
    ResumedListener { context, provider -> this(context, provider) }

/**
 * 有着明确监听目标的 [ResumedListener]。
 *
 * @see BlockingClearTargetResumedListener
 */
public fun interface ClearTargetResumedListener<E : Event, T> {
    public suspend operator fun invoke(
        event: E,
        context: EventProcessingContext,
        provider: ContinuousSessionProvider<T>
    )
}


/**
 * 有着明确监听目标的 [ResumedListener]。需要考虑重写 [invoke] 来实现事件类型的准确转化。
 *
 * @see ClearTargetResumedListener
 * @see ResumedListener
 */
@Api4J
public fun interface BlockingClearTargetResumedListener<E : Event, T> {
    // override suspend fun invoke(event: E, context: EventProcessingContext, provider: ContinuousSessionProvider<T>) {
    //     invokeBlocking(event, context, provider)
    // }

    public operator fun invoke(event: E, context: EventProcessingContext, provider: ContinuousSessionProvider<T>)
}

@OptIn(Api4J::class)
internal fun <E : Event, T> BlockingClearTargetResumedListener<E, T>.parse(): ClearTargetResumedListener<E, T> =
    ClearTargetResumedListener { event, context, provider -> this(event, context, provider) }