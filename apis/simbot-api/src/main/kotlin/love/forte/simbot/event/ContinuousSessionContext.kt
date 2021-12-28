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

@file:JvmName("ContinuousSessionScopeContextUtil2")

package love.forte.simbot.event

import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import java.util.concurrent.Future
import kotlin.random.Random


/**
 *
 * 持续会话的作用域, 通过此作用域在监听函数监听过程中进行会话嵌套。
 *
 * [waitingFor] 与 [waiting] 中注册的临时listener将会在所有监听函数被触发前, 依次作为一个独立的**异步任务**执行，
 * 并且因为 [ResumedListener.invoke] 不存在返回值, 因此所有的临时会话监听函数均无法对任何正常的监听流程产生影响，也无法参与到正常流程中的结果返回中。
 *
 *
 * @author ForteScarlet
 */
public abstract class ContinuousSessionContext {

    protected abstract val coroutineScope: CoroutineScope

    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     */
    @JvmSynthetic
    public abstract suspend fun <T> waitingFor(
        id: ID = Random.nextLong().ID,
        timeout: Long = 0,
        listener: ResumedListener<T>
    ): T


    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     */
    @JvmSynthetic
    public abstract suspend fun <E : Event, T> waitingFor(
        id: ID = Random.nextLong().ID,
        timeout: Long = 0,
        eventKey: Event.Key<E>,
        listener: ClearTargetResumedListener<E, T>
    ): T

    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     */
    public abstract fun <T> waiting(
        id: ID = Random.nextLong().ID,
        timeout: Long = 0,
        listener: ResumedListener<T>
    ): ContinuousSessionReceiver<T>


    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     */
    public abstract fun <E : Event, T> waiting(
        id: ID = Random.nextLong().ID,
        timeout: Long = 0,
        eventKey: Event.Key<E>,
        listener: ClearTargetResumedListener<E, T>
    ): ContinuousSessionReceiver<T>


    /**
     * 尝试通过ID获取一个 provider.
     */
    public abstract fun <T> getProvider(id: ID): ContinuousSessionProvider<T>?
    /**
     * 尝试通过ID获取一个 receiver.
     */
    public abstract fun <T> getReceiver(id: ID): ContinuousSessionReceiver<T>?


}


public suspend inline fun <reified E : Event, T> ContinuousSessionContext.waitFor(
    id: ID = Random.nextLong().ID,
    timeout: Long = 0,
    listener: ClearTargetResumedListener<E, T>
): T {
    return waitingFor(id, timeout, Event.Key.getKey(), listener)
}


/**
 * 持续会话的结果接收器，通过 [ContinuousSessionContext.waiting] 构建获取，
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
 * 持续会话的结果推送器，通过 [ContinuousSessionContext.getReceiver] 构建获取，向目标会话推送一个结果。
 *
 * @see ContinuousSessionProvider
 */
public interface ContinuousSessionProvider<T> {

    /**
     * 向其对应的 [ContinuousSessionReceiver] 推送一个结果。
     * 正常来讲，一个 session 只能够推送一次，如果多次推送可能会导致异常, 注意处理。
     */
    public fun push(value: T)

    /**
     * 向目标推送一个错误。
     */
    public fun pushException(e: Throwable)

    /**
     * 关闭此会话。某种意义上这也相当于 [pushException]
     */
    public fun cancel(reason: Throwable? = null)
}


/**
 *
 * @see BlockingResumedListener
 * @see ClearTargetResumedListener
 * @see BlockingClearTargetResumedListener
 */
public fun interface ResumedListener<T> : suspend (EventProcessingContext, ContinuousSessionProvider<T>) -> Unit {
    @JvmSynthetic
    override suspend fun invoke(context: EventProcessingContext, provider: ContinuousSessionProvider<T>)
}

/**
 * 阻塞的
 */
@Api4J
public fun interface BlockingResumedListener<T> : ResumedListener<T> {
    public fun invokeBlocking(context: EventProcessingContext, provider: ContinuousSessionProvider<T>)

    @JvmSynthetic
    override suspend fun invoke(context: EventProcessingContext, provider: ContinuousSessionProvider<T>) {
        invokeBlocking(context, provider)
    }
}

/**
 * 有着明确监听目标的 [ResumedListener]。
 */
public fun interface ClearTargetResumedListener<E : Event, T> : ResumedListener<T> {
    @JvmSynthetic
    override suspend fun invoke(context: EventProcessingContext, provider: ContinuousSessionProvider<T>) {
        @Suppress("UNCHECKED_CAST")
        (context.event as? E)?.also { event ->
            invoke(event, context, provider)
        }
    }

    @JvmSynthetic
    public suspend fun invoke(event: E, context: EventProcessingContext, provider: ContinuousSessionProvider<T>)
}


/**
 * 有着明确监听目标的 [ResumedListener]。需要考虑重写 [invoke] 来实现事件类型的准确转化。
 *
 * @see ClearTargetResumedListener
 * @see ResumedListener
 */
@Api4J
public fun interface BlockingClearTargetResumedListener<E : Event, T> : ClearTargetResumedListener<E, T> {
    public fun invokeBlocking(event: E, context: EventProcessingContext, provider: ContinuousSessionProvider<T>)

    @JvmSynthetic
    override suspend fun invoke(event: E, context: EventProcessingContext, provider: ContinuousSessionProvider<T>) {
        invokeBlocking(event, context, provider)
    }
}