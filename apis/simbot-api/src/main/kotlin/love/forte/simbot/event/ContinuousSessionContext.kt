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

package love.forte.simbot.event

import kotlinx.coroutines.*
import love.forte.simbot.*
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageContent
import love.forte.simbot.utils.runInBlocking
import love.forte.simbot.utils.runInTimeoutBlocking
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


/**
 * [ContinuousSessionContext] 的基础接口。
 * 为 [ContinuousSessionContext] 提供最基础的核心的函数：[waiting].
 *
 * [ContinuousSessionContext] 中的所有函数实现都由 [waiting] 衍生而来。
 *
 * @see ContinuousSessionContext
 *
 */
// 实际上如果不是为了提供兼容的阻塞函数, [ContinuousSessionContext] 里的那些函数都能直接用扩展函数解决的，啧。
@ExperimentalSimbotApi
public interface BaseContinuousSessionContext {
    
    /**
     * 注册一个临时监听函数并挂起等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     * ```kotlin
     * val session: ContinuousSessionContext = ...
     *
     * session.waiting { provider -> // this: EventProcessingContext
     *    // ...
     *    provider.push(...)
     * }
     * ```
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
     * @throws CancellationException 被终止时
     *
     */
    @JvmSynthetic
    public suspend fun <T> waiting(
        id: ID = randomID(),
        listener: ResumeListener<T>,
    ): T
    
}


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
 * + ----- +   push    + -------------- + 构建Context  + ---------- +
 * | Event | --------> | EventProcessor | -----+----> | 正常处理流程 |
 * + ------+           + ---------------+      |      + ---------- +
 *                                             |
 *                                             | async launch
 *                 + ---------------- +        |
 *                 |  临时监听函数处理  | <------ +
 *                 + ---------------- +
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
 *              }
 *          }
 *
 *          assert(num == 1)
 *      }
 *  }
 *
 * ```
 *
 * 不过假如你的逻辑比较复杂，可能在 [waiting] 中仍需要嵌套更多的其他listener，那么对函数进行拆分也许是个不错的选择。比如：
 * ```
 * suspend fun EventListenersGenerator.myListener() {
 *      GroupEvent { event: GroupEvent  -> // this: EventListenerProcessingContext
 *              // 获取 session context, 并认为它不为null。
 *              val sessionContext = context.getAttribute(EventProcessingContext.Scope.ContinuousSession) ?: error("不支持会话！")
 *
 *              // 假设你需要获取2个数字，每个数字中同样需要再次等待一个数字。
 *
 *              val num1 = sessionContext.waitingFor(listener = EventProcessingContext::getNum1)
 *              val num2 = sessionContext.waitingFor(listener = ::getNum2)
 *
 *              assert((num1 + num2) == 10) // sum: 10
 *              null
 *          }
 *      }
 *
 *  suspend fun EventProcessingContext.getNum1(provider: ContinuousSessionProvider<Int>): Unit = 4
 *
 *  suspend fun getNum2(context: EventProcessingContext, provider: ContinuousSessionProvider<Int>): Unit = 6
 * ```
 *
 * 如果你有明确的监听类型目标，你也可以使用 `nextXxx` 或 `waitingForXxx` 的相关函数来简化类型匹配:
 
 *
 * ## 超时处理
 * 在 Kotlin 中，你可以使用 [withTimeout] 来包裹诸如 [waiting] 或者 [next] 等这类挂起函数来控制超时时间。
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
 * 而对于 Java 开发者，绝大多数相关的函数中都提供了 `timeout` 和 `timeUnit` 参数来控制超时时间。
 *
 *
 *
 * ## 会话清除
 * 当你通过 [waiting] 或者其他相关函数注册了一个临时监听函数之后，在出现以下情况后，他们会被清除：
 * - 通过 [withTimeout] (或者Java中使用参数 `timeout`) 指定了超时时间，且到达了超时时间。
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
public abstract class ContinuousSessionContext : BaseContinuousSessionContext {
    
    // region waiting
    /**
     * 注册一个临时监听函数并挂起等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     * ```kotlin
     * val session: ContinuousSessionContext = ...
     *
     * session.waiting { provider -> // this: EventProcessingContext
     *    // ...
     *    provider.push(...)
     * }
     * ```
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
     * @throws CancellationException 被终止时
     *
     */
    @JvmSynthetic
    abstract override suspend fun <T> waiting(
        id: ID,
        listener: ResumeListener<T>,
    ): T
    
    
    /**
     * 注册一个临时监听函数并阻塞的等待.
     *
     * 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     * @param id 注册的临时监听函数的唯一ID
     * @param timeout 超时时间。大于0的时候生效
     * @param timeUnit [timeout] 的时间单位。默认为毫秒
     * @param blockingListener 用于java的阻塞监听函数。是 `(EventProcessingContext, ContinuousSessionProvider) -> {}` 类型的函数接口
     *
     * @throws TimeoutCancellationException 如果超时
     * @throws CancellationException 被终止时
     *
     * @see waiting
     *
     */
    @Api4J
    @JvmOverloads
    @JvmName("waiting")
    public fun <T> waitBlocking(
        id: ID = randomID(),
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        blockingListener: BlockingResumeListener<T>,
    ): T {
        suspend fun doWait() = waiting(id, blockingListener.parse())
        
        val mill = timeUnit.toMillis(timeout)
        if (mill > 0) {
            return runInBlocking {
                withTimeout(mill) { doWait() }
            }
        }
        
        return runInBlocking { doWait() }
    }
    
    
    /**
     * 注册一个临时监听函数并阻塞的等待, id随机。
     *
     * @param timeout 超时时间，毫秒为单位。大于0的时候生效
     * @param blockingListener 用于java的阻塞监听函数。是 `(EventProcessingContext, ContinuousSessionProvider) -> {}` 类型的函数接口
     *
     * @throws TimeoutCancellationException 如果超时
     * @throws CancellationException 被终止时
     *
     * @see waiting
     */
    @Api4J
    @JvmOverloads
    @JvmName("waiting")
    public fun <T> waitBlocking(
        timeout: Long,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        blockingListener: BlockingResumeListener<T>,
    ): T =
        waitBlocking(id = randomID(), timeout = timeout, timeUnit = timeUnit, blockingListener = blockingListener)
    
    
    // endregion
    
    // region waitingForNext - matcher
    
    /**
     * 挂起并等待下一个符合 [类型][key] 与 [条件][matcher] 的 [事件][Event] 对象。
     *
     * ```kotlin
     * val session: ContinuousSessionContext = ...
     *
     * session.waitingForNext(id, FooEvent) { event -> // this: EventProcessingContext
     *    // ...
     *    true
     * }
     * ```
     *
     * 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     * ## 超时处理
     * 使用 [withTimeout] 或其衍生函数来进行超时控制。
     * ```kotlin
     * val session: ContinuousSessionContext = ...
     *
     * withTimeout(5.seconds) {
     *    session.waitingForNext(id, FooEvent) { event -> // this: EventProcessingContext
     *       // ...
     *       true
     *    }
     * }
     * ```
     *
     * ## 目标类型
     * 当使用事件类型参数 [key] 的时候，建议使用**明确、具体的**类型对象，如下示例：
     * ```kotlin
     * session.waitingForNext(id, FriendMessageEvent) { ... }
     * ```
     *
     * 不建议使用通过事件的 [Event.key] 属性参数而得到的实例。因为在事件处理过程中，你所得到的事件对象是经过层层实现与包装的，
     * 你无法保证通过 [Event.key] 的具体类型，进而可能导致一系列不可控问题。
     *
     * 比如如下示例中：
     * ```
     *                         + -------- +
     *                   + --> | BarEvent |
     *                   |     + -------- +
     * + -------- +      |
     * | FooEvent | ---- +
     * + -------- +      |
     *                   |     + -------- +
     *                   + --> | TarEvent |
     *                         + -------- +
     *
     * ```
     *
     * `FooEvent` 事件有两个实现：`BarEvent` 和 `TarEvent`。
     *
     * 如果你的监听函数为：
     * ```kotlin
     * suspend fun listener(event: FooEvent, session: ContinuousSessionContext) {
     *    session.waitingForNext(key = event.key) { true }
     * }
     * ```
     *
     * 那么此时，你通过 [waitingForNext] 所能得到的事件类型很有可能只能是 `BarEvent` 或 `TarEvent`
     * 中的 **某一种**，而并非所有可能的 `FooEvent` 。
     *
     * @throws CancellationException 被终止关闭时
     *
     * @param id 临时监听函数的唯一标识。
     * @param key 所需监听函数的类型。
     *
     */
    @JvmSynthetic
    public suspend fun <E : Event> waitingForNext(
        id: ID = randomID(),
        key: Event.Key<E>,
        matcher: EventMatcher<E> = EventMatcher,
    ): E {
        return waiting(id) { provider ->
            val event = key.safeCast(this.event) ?: return@waiting
            if (matcher.run { invoke(event) }) {
                provider.push(event)
            }
        }
    }
    
    
    // region waitForNextBlocking with key
    /**
     * 阻塞并等待下一个符合条件的 [事件][Event] 对象。
     *
     *
     * @param id 临时监听函数的唯一ID
     * @param key 事件类型 [Event.Key]
     * @param timeout 超时时间，大于0时生效。
     * @param timeUnit [timeout] 时间单位
     * @param matcher 匹配函数。相当于 `(EventProcessingContext, Event) -> Boolean`
     *
     * @throws TimeoutCancellationException 如果超时
     * @throws CancellationException 被终止
     *
     * @see waiting
     * @see waitingForNext
     */
    @Api4J
    @JvmOverloads
    @JvmName("waitingForNext")
    public fun <E : Event> waitForNextBlocking(
        id: ID,
        key: Event.Key<E>,
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        matcher: BlockingEventMatcher<E> = BlockingEventMatcher,
    ): E {
        suspend fun doWait() = waitingForNext(id, key, matcher.parse())
        
        val mill = timeUnit.toMillis(timeout)
        if (mill > 0) {
            return runInBlocking {
                withTimeout(mill) { doWait() }
            }
        }
        
        return runInBlocking { doWait() }
    }
    
    /**
     * 阻塞并等待下一个符合条件的 [事件][Event] 对象。
     *
     *
     * @param key 事件类型 [Event.Key]
     * @param timeout 超时时间，大于0时生效。
     * @param timeUnit [timeout] 时间单位
     * @param matcher 匹配函数。相当于 `(EventProcessingContext, Event) -> Boolean`
     *
     * @throws TimeoutCancellationException 如果超时
     * @throws CancellationException 被终止
     *
     * @see waiting
     * @see waitingForNext
     */
    @Api4J
    @JvmOverloads
    @JvmName("waitingForNext")
    public fun <E : Event> waitForNextBlocking(
        key: Event.Key<E>,
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        matcher: BlockingEventMatcher<E> = BlockingEventMatcher,
    ): E = waitForNextBlocking(id = randomID(), key, timeout, timeUnit, matcher)
    
    
    /**
     * 阻塞并等待下一个符合条件的 [事件][Event] 对象。
     *
     *
     * @param key 事件类型 [Event.Key]
     * @param matcher 匹配函数。相当于 `(EventProcessingContext, Event) -> Boolean`
     *
     * @throws CancellationException 被终止
     *
     * @see waiting
     * @see waitingForNext
     */
    @Api4J
    @JvmName("waitingForNext")
    public fun <E : Event> waitForNextBlocking(
        key: Event.Key<E>,
        matcher: BlockingEventMatcher<E> = BlockingEventMatcher,
    ): E =
        waitForNextBlocking(id = randomID(), key = key, matcher = matcher)
    
    
    // endregion
    
    
    /**
     * 挂起并等待下一个符合 [条件][matcher] 的 [事件][Event] 对象。
     *
     * 相当于:
     * ```kotlin
     * waitingForNext(id, Event.Root, matcher)
     * ```
     *
     * @throws CancellationException 被终止时
     *
     * @see waitingForNext
     *
     */
    @JvmSynthetic
    public suspend fun waitingForNext(
        id: ID = randomID(),
        matcher: EventMatcher<Event> = EventMatcher,
    ): Event {
        return waitingForNext(id, Event.Root, matcher)
    }
    
    
    // region waitForNextBlocking without key
    /**
     * 阻塞并等待下一个符合条件的 [事件][Event] 对象。
     *
     *
     * @param id 临时监听函数的唯一ID
     * @param timeout 超时时间，大于0时生效。
     * @param timeUnit [timeout] 时间单位
     * @param matcher 匹配函数。相当于 `(EventProcessingContext, Event) -> Boolean`
     *
     * @throws TimeoutCancellationException 如果超时
     * @throws CancellationException 被终止
     *
     * @see waiting
     * @see waitingForNext
     */
    @Api4J
    @JvmOverloads
    @JvmName("waitingForNext")
    public fun waitForNextBlocking(
        id: ID = randomID(),
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        matcher: BlockingEventMatcher<Event> = BlockingEventMatcher,
    ): Event {
        suspend fun doWait() = waitingForNext(id, matcher.parse())
        
        val mill = timeUnit.toMillis(timeout)
        if (mill > 0) {
            return runInBlocking {
                withTimeout(mill) { doWait() }
            }
        }
        
        return runInBlocking { doWait() }
    }
    
    /**
     * 阻塞并等待下一个符合条件的 [事件][Event] 对象。
     *
     *
     * @param timeout 超时时间，单位毫秒，大于0时生效。
     * @param matcher 匹配函数。相当于 `(EventProcessingContext, Event) -> Boolean`
     *
     * @throws TimeoutCancellationException 如果超时
     * @throws CancellationException 被终止
     *
     * @see waiting
     * @see waitingForNext
     */
    @Api4J
    @JvmOverloads
    @JvmName("waitingForNext")
    public fun waitForNextBlocking(
        timeout: Long,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        matcher: BlockingEventMatcher<Event> = BlockingEventMatcher,
    ): Event =
        waitForNextBlocking(randomID(), timeout, timeUnit, matcher)
    
    /**
     * 阻塞并等待下一个符合条件的 [事件][Event] 对象。
     *
     * id随机。
     *
     * @param timeout 超时时间，单位毫秒，大于0时生效。
     * @param matcher 匹配函数。相当于 `(EventProcessingContext, Event) -> Boolean`
     *
     * @throws TimeoutCancellationException 如果超时
     * @throws CancellationException 被终止
     *
     * @see waiting
     * @see waitingForNext
     */
    @Api4J
    @JvmName("waitingForNext")
    public fun waitForNextBlocking(timeout: Long, matcher: BlockingEventMatcher<Event>): Event =
        waitForNextBlocking(timeout, TimeUnit.MILLISECONDS, matcher)
    
    
    /**
     * 阻塞并等待下一个符合条件的 [事件][Event] 对象。
     *
     *
     * @param id 临时监听函数的唯一ID
     * @param matcher 匹配函数。相当于 `(EventProcessingContext, Event) -> Boolean`
     *
     * @throws CancellationException 被终止
     *
     * @see waiting
     * @see waitingForNext
     */
    @Api4J
    @JvmOverloads
    @JvmName("waitingForNext")
    public fun waitForNextBlocking(id: ID = randomID(), matcher: BlockingEventMatcher<Event>): Event =
        waitForNextBlocking(id = id, timeout = 0, matcher = matcher)
    
    // endregion
    // endregion
    
    // region next
    // region next with key
    /**
     * 挂起并等待在具体的事件 [Event] 环境下根据条件获取下一个匹配的 [事件][Event].
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [Event] 的类型进行匹配。
     *
     *
     * [next] 对事件类型进行的对应的匹配规则对照表如下:
     *
     * | 当前事件类型 |  [目标事件][key]同类型 | [目标事件][key]不同类型 |
     * | :------------------------------------------ | ------------------- | --------------------: |
     * | [Event]                | [bot][Event.bot] 的ID要相同                               | _不会出现不同类型_ |
     * | [OrganizationEvent]    | [organization][OrganizationEvent.organization] 的ID要相同 | 放行 |
     * | [UserEvent]            | [user][UserEvent.user] 的ID要相同                         | 放行 |
     * | [MessageEvent]         |  [source][MessageEvent.source] 的ID要相同                 | 放行 |
     * | [ChatRoomMessageEvent] |  [author][ChatRoomMessageEvent.author] 的ID要相同         | 放行 |
     *
     *
     * 如果你希望使用更复杂是匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * ## 超时处理
     * 使用 [withTimeout] 或其衍生函数来进行超时控制。
     * ```kotlin
     * val session: ContinuousSessionContext = ...
     *
     * withTimeout(5.seconds) {
     *    session {
     *       next(id, FooEvent) { event -> // this: EventProcessingContext
     *         // ...
     *         true
     *       }
     *    }
     * }
     * ```
     *
     * 你可能注意到了上述示例中出现了如下用法：
     * ```kotlin
     * session {
     *    next(id, FooEvent) { /* ... */ }
     * }
     * ```
     *
     * 这是通过扩展函数 [ContinuousSessionContext.invoke] 所提供的，旨在简化 [next] 这类函数的使用。
     *
     *
     * @param id 临时监听函数的唯一标识
     * @param key 所需目标函数类型
     *
     * @see ContinuousSessionContext.invoke
     *
     * @receiver 当前所处的事件环境
     */
    @JvmSynthetic
    public suspend fun <E : Event> Event.next(id: ID, key: Event.Key<E>): E {
        val currentEvent = this
        return waitingForNext(id, key, currentEvent.toMatcher())
    }
    
    
    /**
     * 挂起并等待在具体的事件 [Event] 环境下根据条件获取下一个匹配的 [事件][Event].
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [Event] 的类型进行匹配。
     *
     * 更多说明请参考 [Event.next].
     *
     * @see Event.next
     */
    @JvmSynthetic
    public suspend fun <E : Event> Event.next(key: Event.Key<E>): E {
        return next(randomID(), key)
    }
    
    
    
    
    /**
     * 挂起并等待在当前的事件处理上下文 [EventProcessingContext] 中根据条件获取下一个匹配的 [事件][Event].
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 更多说明参考 [Event.next].
     *
     * @receiver 当前的事件处理上下文环境
     * @see Event.next
     */
    @JvmSynthetic
    public suspend fun <E : Event> EventProcessingContext.next(id: ID, key: Event.Key<E>): E {
        return event.next(id, key)
    }
    
    
    /**
     * 挂起并等待在当前的事件处理上下文 [EventProcessingContext] 中根据条件获取下一个匹配的 [事件][Event].
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 更多说明参考 [Event.next].
     *
     * @receiver 当前的事件处理上下文环境
     *
     * @see Event.next
     */
    @JvmSynthetic
    public suspend fun <E : Event> EventProcessingContext.next(key: Event.Key<E>): E {
        return next(randomID(), key)
    }
    
    
    /**
     * 阻塞并等待在具体的事件 [Event] 环境下根据条件获取下一个匹配的 [事件][Event].
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [Event] 的类型进行匹配。
     *
     *
     * [next] 对事件类型进行的对应的匹配规则对照表如下:
     *
     * | 当前事件类型 |  [目标事件][key]同类型 | [目标事件][key]不同类型 |
     * | :------------------------------------------ | ------------------- | --------------------: |
     * | [Event]                | [bot][Event.bot] 的ID要相同                               | _不会出现不同类型_ |
     * | [OrganizationEvent]    | [organization][OrganizationEvent.organization] 的ID要相同 | 放行             |
     * | [UserEvent]            | [user][UserEvent.user] 的ID要相同                         | 放行             |
     * | [MessageEvent]         |  [source][MessageEvent.source] 的ID要相同                 | 放行             |
     * | [ChatRoomMessageEvent] |  [author][ChatRoomMessageEvent.author] 的ID要相同         | 放行             |
     *
     *
     * 如果你希望使用更复杂的匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param id 临时监听函数唯一标识
     * @param key 目标事件类型
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see waiting
     * @see waitingForNext
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("next")
    public fun <E : Event> Event.nextBlocking(
        id: ID = randomID(),
        key: Event.Key<E>,
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): E {
        val currentEvent = this
        suspend fun doWait() = waitingForNext(id, key, currentEvent.toMatcher())
        
        val mill = timeUnit.toMillis(timeout)
        if (mill > 0) {
            return runInBlocking {
                withTimeout(mill) { doWait() }
            }
        }
        
        return runInBlocking { doWait() }
        
    }
    
    
    /**
     * 阻塞并等待在具体的事件处理上下文 [EventProcessingContext] 环境下根据条件获取下一个匹配的 [事件][Event].
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 如果你希望使用更复杂是匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param id 临时监听函数唯一标识
     * @param key 目标事件类型
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see Event.next
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("next")
    public fun <E : Event> EventProcessingContext.nextBlocking(
        id: ID = randomID(),
        key: Event.Key<E>,
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): E {
        return event.nextBlocking(id, key, timeout, timeUnit)
    }
    // endregion
    
    // region next without key
    /**
     * 挂起并等待在具体的事件 [Event] 环境下根据条件获取下一个匹配的 [事件][Event].
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [Event] 的类型进行匹配。
     *
     *
     * [next] 对事件类型进行的对应的匹配规则对照表如下:
     *
     * | 当前事件类型 |  目标事件同类型 | 目标事件不同类型 |
     * | :------------------------------------------ | ------------------- | --------------------: |
     * | [OrganizationEvent]    | [organization][OrganizationEvent.organization] 的ID要相同 | 放行 |
     * | [UserEvent]            | [user][UserEvent.user] 的ID要相同                         | 放行 |
     * | [MessageEvent]         |  [source][MessageEvent.source] 的ID要相同                 | 放行 |
     * | [ChatRoomMessageEvent] |  [author][ChatRoomMessageEvent.author] 的ID要相同         | 放行 |
     *
     *
     * 如果你希望使用更复杂是匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * ## 超时处理
     * 使用 [withTimeout] 或其衍生函数来进行超时控制。
     * ```kotlin
     * val session: ContinuousSessionContext = ...
     *
     * withTimeout(5.seconds) {
     *    session {
     *       next { event -> // this: EventProcessingContext
     *         // ...
     *         true
     *       }
     *    }
     * }
     * ```
     *
     * 你可能注意到了上述示例中出现了如下用法：
     * ```kotlin
     * session {
     *    next(id, FooEvent) { /* ... */ }
     * }
     * ```
     *
     * 这是通过扩展函数 [ContinuousSessionContext.invoke] 所提供的，旨在简化 [next] 这类函数的使用。
     *
     *
     * @param id 临时监听函数的唯一标识
     *
     * @see ContinuousSessionContext.invoke
     *
     * @receiver 当前所处的事件环境
     */
    @JvmSynthetic
    public suspend fun Event.next(id: ID = randomID()): Event {
        return next(id, Event.Root)
    }
    
    
    /**
     * 挂起并等待在当前的事件处理上下文 [EventProcessingContext] 中根据条件获取下一个匹配的 [事件][Event].
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 更多说明参考 [Event.next].
     *
     * @receiver 当前的事件处理上下文环境
     * @see Event.next
     */
    @JvmSynthetic
    public suspend fun EventProcessingContext.next(id: ID = randomID()): Event {
        return event.next(id)
    }
    
    
    /**
     * 阻塞并等待在具体的事件 [Event] 环境下根据条件获取下一个匹配的 [事件][Event].
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [Event] 的类型进行匹配。
     *
     *
     * [next] 对事件类型进行的对应的匹配规则对照表如下:
     *
     * | 当前事件类型 |  目标事件同类型 | 目标事件不同类型 |
     * | :------------------------------------------ | ------------------- | --------------------: |
     * | [Event]                | [bot][Event.bot] 的ID要相同                               | _不会出现不同类型_ |
     * | [OrganizationEvent]    | [organization][OrganizationEvent.organization] 的ID要相同 | 放行             |
     * | [UserEvent]            | [user][UserEvent.user] 的ID要相同                         | 放行             |
     * | [MessageEvent]         |  [source][MessageEvent.source] 的ID要相同                 | 放行             |
     * | [ChatRoomMessageEvent] |  [author][ChatRoomMessageEvent.author] 的ID要相同         | 放行             |
     *
     *
     * 如果你希望使用更复杂的匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param id 临时监听函数唯一标识
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see waiting
     * @see waitingForNext
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("next")
    public fun Event.nextBlocking(
        id: ID = randomID(),
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): Event {
        return nextBlocking(id = id, key = Event.Root, timeout = timeout, timeUnit = timeUnit)
    }
    
    
    /**
     * 阻塞并等待在具体的事件处理上下文 [EventProcessingContext] 环境下根据条件获取下一个匹配的 [事件][Event].
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 如果你希望使用更复杂是匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param id 临时监听函数唯一标识
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see Event.next
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("next")
    public fun EventProcessingContext.nextBlocking(
        id: ID = randomID(),
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): Event {
        return event.nextBlocking(id, Event.Root, timeout, timeUnit)
    }
    // endregion
    // endregion
    
    
    // region waitingForNextMessage
    
    /**
     * 挂起并等待下一个符合条件的 [消息事件][MessageEvent] 中的消息体。
     *
     * 与 [waitingForNext] 类似，只不过 [waitingForNextMessage] 限制了等待的消息类型 [E]
     * 必须为 [消息事件][MessageEvent], 且返回值为 [MessageContent] 。
     *
     * **_更多内容参考 [waitingForNext] 文档注释。_**
     *
     * ## 超时处理
     * 使用 [withTimeout] 或其衍生函数来进行超时控制。
     * ```kotlin
     * val session: ContinuousSessionContext = ...
     *
     * withTimeout(5.seconds) {
     *    session.waitingForNextMessage(id, FooEvent) { event -> // this: EventProcessingContext
     *       // ...
     *       true
     *    }
     * }
     * ```
     *
     * ## 具体的 [Message.Element] ?
     * 当你想要得到一个具体的 [Message.Element] 对象，那么你需要想明白：
     * - 你需要的是对应事件中 _可能存在的_ 这个消息元素类型吗？
     * - 或者你需要的是 _必须存在_ 这个消息元素的事件？
     * - 你需要的是 _第一个_ 符合类型的元素？还是 _最后一个_ 符合类型的元素？
     * - 你是否对需要的消息元素中的 _属性_ 有所要求？
     * - ...
     *
     * 对于直接获取一个 [Message.Element] 的情况太过纷杂，如果你无法通过 [waitingForNextMessage] 或者 [nextMessage] 来满足需求，
     * 那么请考虑使用 [waiting] 或 [waitingForNext] 等更细致的函数。
     *
     *
     * @throws CancellationException 被终止时
     *
     * @see waitingForNext
     *
     */
    @JvmSynthetic
    public suspend fun <E : MessageEvent> waitingForNextMessage(
        id: ID,
        key: Event.Key<E>,
        matcher: EventMatcher<E> = EventMatcher,
    ): MessageContent {
        return waitingForNext(id, key, matcher).messageContent
    }
    
    /**
     * 挂起并等待下一个符合条件的 [消息事件][MessageEvent] 中的 [消息内容][MessageContent] 。
     *
     * 与 [waitingForNext] 类似，只不过 [waitingForNextMessage] 限制了等待的消息类型 [MessageEvent]
     * 必须为 [消息事件][MessageEvent], 且返回值为 [MessageContent] 。
     *
     * **_更多内容参考 [waitingForNext] 文档注释。_**
     *
     * ## 超时处理
     * 使用 [withTimeout] 或其衍生函数来进行超时控制。
     * ```kotlin
     * val session: ContinuousSessionContext = ...
     *
     * withTimeout(5.seconds) {
     *    session.waitingForNextMessage(id, FooEvent) { event -> // this: EventProcessingContext
     *       // ...
     *       true
     *    }
     * }
     * ```
     *
     * ## 具体的 [Message.Element] ?
     * 当你想要得到一个具体的 [Message.Element] 对象，那么你需要想明白：
     * - 你需要的是对应事件中 _可能存在的_ 这个消息元素类型吗？
     * - 或者你需要的是 _必须存在_ 这个消息元素的事件？
     * - 你需要的是 _第一个_ 符合类型的元素？还是 _最后一个_ 符合类型的元素？
     * - 你是否对需要的消息元素中的 _属性_ 有所要求？
     * - ...
     *
     * 对于直接获取一个 [Message.Element] 的情况太过纷杂，如果你无法通过 [waitingForNextMessage] 或者 [nextMessage] 来满足需求，
     * 那么请考虑使用 [waiting] 或 [waitingForNext] 等更细致的函数。
     *
     *
     * @throws CancellationException 被终止时
     *
     * @see waitingForNext
     *
     */
    @JvmSynthetic
    public suspend fun waitingForNextMessage(
        id: ID = randomID(),
        matcher: EventMatcher<MessageEvent> = EventMatcher,
    ): MessageContent {
        return waitingForNext(id, MessageEvent, matcher).messageContent
    }
    
    /**
     * 阻塞并等待下一个符合条件的 [消息事件][MessageEvent] 中的 [消息内容][MessageContent] 。
     *
     * 与 [waitingForNext] 类似，只不过 [waitingForNextMessage] 限制了等待的消息类型 [E]
     * 必须为 [消息事件][MessageEvent], 且返回值为 [MessageContent] 。
     *
     *
     * 更多说明参考 [waitingForNextMessage] 文档注释。
     *
     * @throws TimeoutCancellationException 当超时
     * @throws CancellationException 被终止时
     *
     * @param id 临时监听函数唯一标识
     * @param key 目标函数类型
     * @param timeout 超时时间。大于0时生效。
     * @param timeUnit [timeout] 时间单位。默认为 [毫秒][TimeUnit.MILLISECONDS]
     * @param matcher 匹配函数
     *
     * @see waitingForNextMessage
     *
     */
    @Api4J
    @JvmOverloads
    @JvmName("waitingForNextMessage")
    public fun <E : MessageEvent> waitForNextMessageBlocking(
        id: ID = randomID(),
        key: Event.Key<E>,
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        matcher: BlockingEventMatcher<E> = BlockingEventMatcher,
    ): MessageContent {
        suspend fun doWait() = waitingForNextMessage(id, key, matcher.parse())
        val mill = timeUnit.toMillis(timeout)
        if (mill > 0) {
            return runInBlocking {
                withTimeout(mill) { doWait() }
            }
        }
        
        return runInBlocking { doWait() }
    }
    
    /**
     * 阻塞并等待下一个符合条件的 [消息事件][MessageEvent] 中的 [消息内容][MessageContent] 。
     *
     * 与 [waitingForNext] 类似，只不过 [waitingForNextMessage] 限制了等待的消息类型 [MessageEvent]
     * 必须为 [消息事件][MessageEvent], 且返回值为 [MessageContent] 。
     *
     *
     * 更多说明参考 [waitingForNextMessage] 文档注释。
     *
     * @throws TimeoutCancellationException 当超时
     * @throws CancellationException 被终止时
     *
     * @param id 临时监听函数唯一标识
     * @param timeout 超时时间。大于0时生效。
     * @param timeUnit [timeout] 时间单位。默认为 [毫秒][TimeUnit.MILLISECONDS]
     * @param matcher 匹配函数
     *
     * @see waitingForNextMessage
     *
     */
    @Api4J
    @JvmOverloads
    @JvmName("waitingForNextMessage")
    public fun waitForNextMessageBlocking(
        id: ID = randomID(),
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
        matcher: BlockingEventMatcher<MessageEvent> = BlockingEventMatcher,
    ): MessageContent {
        return waitForNextMessageBlocking(id, MessageEvent, timeout, timeUnit, matcher)
    }
    
    /**
     * 阻塞并等待下一个符合条件的 [消息事件][MessageEvent] 中的 [消息内容][MessageContent] 。
     *
     * 与 [waitingForNext] 类似，只不过 [waitingForNextMessage] 限制了等待的消息类型 [MessageEvent]
     * 必须为 [消息事件][MessageEvent], 且返回值为 [MessageContent] 。
     *
     *
     * 更多说明参考 [waitingForNextMessage] 文档注释。
     *
     * @throws CancellationException 被终止时
     *
     * @param id 临时监听函数唯一标识
     * @param matcher 匹配函数
     *
     * @see waitingForNextMessage
     *
     */
    @Api4J
    @JvmOverloads
    @JvmName("waitingForNextMessage")
    public fun waitForNextMessageBlocking(
        id: ID = randomID(),
        matcher: BlockingEventMatcher<MessageEvent>,
    ): MessageContent {
        return waitForNextMessageBlocking(id = id, key = MessageEvent, timeout = 0, matcher = matcher)
    }
    
    /**
     * 阻塞并等待下一个符合条件的 [消息事件][MessageEvent] 中的 [消息内容][MessageContent] 。
     *
     * 与 [waitingForNext] 类似，只不过 [waitingForNextMessage] 限制了等待的消息类型 [MessageEvent]
     * 必须为 [消息事件][MessageEvent], 且返回值为 [MessageContent] 。
     *
     *
     * 更多说明参考 [waitingForNextMessage] 文档注释。
     *
     * @throws CancellationException 被终止时
     *
     * @param key 目标事件类型
     * @param matcher 匹配函数
     *
     * @see waitingForNextMessage
     *
     */
    @Api4J
    @JvmName("waitingForNextMessage")
    public fun <E : MessageEvent> waitForNextMessageBlocking(
        key: Event.Key<E>,
        matcher: BlockingEventMatcher<MessageEvent>,
    ): MessageContent {
        return waitForNextMessageBlocking(id = randomID(), key = key, timeout = 0, matcher = matcher)
    }
    // endregion
    
    // region Context.nextMessage
    
    /**
     * 挂起并等待符合当前 [Event] 作用域下的下一个消息事件的 [消息内容][MessageContent].
     *
     * 行为类似于 [next][Event.next], 区别在于 [nextMessage] 只允许目标为 [消息事件][MessageEvent]
     * 且返回值为 [MessageContent].
     *
     * 对于作用域下的事件匹配机制的说明参考 [next][Event.next] 文档注释。
     *
     * @throws CancellationException 被终止时
     *
     * @see Event.next
     * @see EventProcessingContext.nextMessage
     */
    @JvmSynthetic
    public suspend fun Event.nextMessage(
        id: ID,
        key: Event.Key<out MessageEvent>,
    ): MessageContent = next(id, key).messageContent
    
    /**
     * 挂起并等待符合当前 [Event] 作用域下的下一个消息事件的 [消息内容][MessageContent].
     *
     * 行为类似于 [next][Event.next], 区别在于 [nextMessage] 只允许目标为 [消息事件][MessageEvent]
     * 且返回值为 [MessageContent].
     *
     * 对于作用域下的事件匹配机制的说明参考 [next][Event.next] 文档注释。
     *
     * @throws CancellationException 被终止时
     *
     * @see Event.next
     * @see EventProcessingContext.nextMessage
     */
    @JvmSynthetic
    public suspend fun Event.nextMessage(
        key: Event.Key<out MessageEvent>,
    ): MessageContent = nextMessage(randomID(), key)
    
    
    /**
     * 挂起并等待符合当前 [Event] 作用域下的下一个消息事件的 [消息内容][MessageContent].
     *
     * 行为类似于 [next][EventProcessingContext.next], 区别在于 [nextMessage] 只允许目标为 [消息事件][MessageEvent]
     * 且返回值为 [MessageContent].
     *
     * 对于作用域下的事件匹配机制的说明参考 [next][EventProcessingContext.next] 文档注释。
     *
     * @throws CancellationException 被终止时
     *
     * @see EventProcessingContext.next
     * @see Event.nextMessage
     */
    @JvmSynthetic
    public suspend fun EventProcessingContext.nextMessage(
        id: ID,
        key: Event.Key<out MessageEvent>,
    ): MessageContent = next(id, key).messageContent
    
    
    /**
     * 挂起并等待符合当前 [Event] 作用域下的下一个消息事件的 [消息内容][MessageContent].
     *
     * 行为类似于 [next][EventProcessingContext.next], 区别在于 [nextMessage] 只允许目标为 [消息事件][MessageEvent]
     * 且返回值为 [MessageContent].
     *
     * 对于作用域下的事件匹配机制的说明参考 [next][EventProcessingContext.next] 文档注释。
     *
     * @throws CancellationException 被终止时
     *
     * @see EventProcessingContext.next
     * @see Event.nextMessage
     */
    @JvmSynthetic
    public suspend fun EventProcessingContext.nextMessage(
        key: Event.Key<out MessageEvent>,
    ): MessageContent = nextMessage(randomID(), key)
    
    
    /**
     * 阻塞并等待在具体的事件处理上下文 [EventProcessingContext] 环境下根据条件获取下一个匹配的 [消息事件][MessageEvent]
     * 中的 [消息内容][MessageContent] 。
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 具体说明参考 [EventProcessingContext.nextMessage].
     *
     * 如果你希望使用更复杂的匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param id 临时监听函数唯一标识
     * @param key 目标事件类型
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see Event.nextMessage
     * @see Event.next
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("nextMessage")
    public fun Event.nextMessageBlocking(
        id: ID,
        key: Event.Key<out MessageEvent>,
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): MessageContent {
        suspend fun doWait() = nextMessage(id, key)
        
        val mill = timeUnit.toMillis(timeout)
        if (mill > 0) {
            return runInBlocking {
                withTimeout(mill) { doWait() }
            }
        }
        
        return runInBlocking { doWait() }
    }
    
    /**
     * 阻塞并等待在具体的事件处理上下文 [EventProcessingContext] 环境下根据条件获取下一个匹配的 [消息事件][MessageEvent]
     * 中的 [消息内容][MessageContent] 。
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 具体说明参考 [EventProcessingContext.nextMessage].
     *
     * 如果你希望使用更复杂的匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param key 目标事件类型
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see Event.nextMessage
     * @see Event.next
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("nextMessage")
    public fun Event.nextMessageBlocking(
        key: Event.Key<out MessageEvent>,
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): MessageContent = nextMessageBlocking(id = randomID(), key = key, timeout = timeout, timeUnit = timeUnit)
    
    
    /**
     * 阻塞并等待在具体的事件处理上下文 [EventProcessingContext] 环境下根据条件获取下一个匹配的 [消息事件][MessageEvent]
     * 中的 [消息内容][MessageContent] 。
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 具体说明参考 [EventProcessingContext.nextMessage].
     *
     * 如果你希望使用更复杂的匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param id 临时监听函数的id
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see Event.nextMessage
     * @see Event.next
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("nextMessage")
    public fun Event.nextMessageBlocking(
        id: ID,
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): MessageContent = nextMessageBlocking(id = id, key = MessageEvent, timeout = timeout, timeUnit = timeUnit)
    
    
    /**
     * 阻塞并等待在具体的事件处理上下文 [EventProcessingContext] 环境下根据条件获取下一个匹配的 [消息事件][MessageEvent]
     * 中的 [消息内容][MessageContent] 。
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 具体说明参考 [EventProcessingContext.nextMessage].
     *
     * 如果你希望使用更复杂的匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see Event.nextMessage
     * @see Event.next
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("nextMessage")
    public fun Event.nextMessageBlocking(
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): MessageContent = nextMessageBlocking(id = randomID(), key = MessageEvent, timeout = timeout, timeUnit = timeUnit)
    
    
    /**
     * 阻塞并等待在具体的事件处理上下文 [EventProcessingContext] 环境下根据条件获取下一个匹配的 [消息事件][MessageEvent]
     * 中的 [消息内容][MessageContent] 。
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 具体说明参考 [EventProcessingContext.nextMessage].
     *
     * 如果你希望使用更复杂是匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param id 临时监听函数唯一标识
     * @param key 目标事件类型
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see EventProcessingContext.next
     * @see EventProcessingContext.nextMessage
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("nextMessage")
    public fun EventProcessingContext.nextMessageBlocking(
        id: ID,
        key: Event.Key<out MessageEvent>,
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): MessageContent {
        return event.nextMessageBlocking(id, key, timeout, timeUnit)
    }
    
    /**
     * 阻塞并等待在具体的事件处理上下文 [EventProcessingContext] 环境下根据条件获取下一个匹配的 [消息事件][MessageEvent]
     * 中的 [消息内容][MessageContent] 。
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 具体说明参考 [EventProcessingContext.nextMessage].
     *
     * 如果你希望使用更复杂是匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param key 目标事件类型
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see EventProcessingContext.next
     * @see EventProcessingContext.nextMessage
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("nextMessage")
    public fun EventProcessingContext.nextMessageBlocking(
        key: Event.Key<out MessageEvent>,
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): MessageContent {
        return event.nextMessageBlocking(randomID(), key, timeout, timeUnit)
    }
    
    /**
     * 阻塞并等待在具体的事件处理上下文 [EventProcessingContext] 环境下根据条件获取下一个匹配的 [消息事件][MessageEvent]
     * 中的 [消息内容][MessageContent] 。
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 具体说明参考 [EventProcessingContext.nextMessage].
     *
     * 如果你希望使用更复杂是匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param id 临时监听函数唯一标识
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see EventProcessingContext.next
     * @see EventProcessingContext.nextMessage
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("nextMessage")
    public fun EventProcessingContext.nextMessageBlocking(
        id: ID,
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): MessageContent {
        return event.nextMessageBlocking(id, MessageEvent, timeout, timeUnit)
    }
    
    /**
     * 阻塞并等待在具体的事件处理上下文 [EventProcessingContext] 环境下根据条件获取下一个匹配的 [消息事件][MessageEvent]
     * 中的 [消息内容][MessageContent] 。
     *
     * 通过 [next] 进行匹配的事件，会根据对应的事件类型结合当前的 [EventProcessingContext.event] 的类型进行匹配。
     *
     * 具体说明参考 [EventProcessingContext.nextMessage].
     *
     * 如果你希望使用更复杂是匹配逻辑，请通过 [waitingForNext] 来自行编写逻辑。
     *
     * @param timeout 超时时间。大于0时生效
     * @param timeUnit [timeout] 的时间类型
     *
     * @see EventProcessingContext.next
     * @see EventProcessingContext.nextMessage
     *
     * @receiver 当前所处的事件环境
     */
    @Api4J
    @JvmOverloads
    @JvmName("nextMessage")
    public fun EventProcessingContext.nextMessageBlocking(
        timeout: Long = 0,
        timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
    ): MessageContent {
        return event.nextMessageBlocking(randomID(), MessageEvent, timeout, timeUnit)
    }
    
    // endregion
    // region Event.nextMessage
    
    // endregion
    
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
 * | 当前事件类型 |  目标类型同类型 | target不同类型 |
 * | :------------------------------------------ | ------------------- | --------------------: |
 * | [OrganizationEvent]    | [organization][OrganizationEvent.organization] 的ID要相同 | 放行 |
 * | [UserEvent]            | [user][UserEvent.user] 的ID要相同                         | 放行 |
 * | [MessageEvent]         |  [source][MessageEvent.source] 的ID要相同                 | 放行 |
 * | [ChatRoomMessageEvent] |  [author][ChatRoomMessageEvent.author] 的ID要相同         | 放行 |
 */
private fun <E : Event> Event.toMatcher(): EventMatcher<E> {
    return EventMatcher { event ->
        val eventBot = event.bot
        if (bot !== eventBot && bot.isNotMe(eventBot.id)) {
            return@EventMatcher false
        }
        
        if (this is OrganizationEvent && event is OrganizationEvent) {
            if (organization().id != event.organization().id) return@EventMatcher false
        }
        
        if (this is UserEvent && event is UserEvent) {
            if (user().id != event.user().id) return@EventMatcher false
        }
        
        if (this is MessageEvent && event is MessageEvent) {
            if (source().id != event.source().id) return@EventMatcher false
            
            // chatRoom message
            if (this is ChatRoomMessageEvent && event is ChatRoomMessageEvent) {
                if (author().id != event.author().id) return@EventMatcher false
            }
        }
        
        
        true
    }
}


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

