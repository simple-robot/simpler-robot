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

@file:JvmName("ContinuousSessionScopeContextUtil")

package love.forte.simbot.event

import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import java.util.concurrent.Future
import kotlin.random.Random


/**
 *
 * 持续会话的作用域, 通过此作用域在监听函数监听过程中进行会话嵌套。
 *
 *
 * TODO()
 *
 * @author ForteScarlet
 * @since 2.3.0
 */
public abstract class ContinuousSessionContext2 : CoroutineScope {

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
    ): ContinuousSession2<T>


    /**
     * 注册一个临时监听函数并等待. 如果注册时发现存在 [id] 冲突的临时函数，则上一个函数将会被立即关闭处理。
     *
     */
    public abstract fun <E : Event, T> waiting(
        id: ID = Random.nextLong().ID,
        timeout: Long = 0,
        eventKey: Event.Key<E>,
        listener: ClearTargetResumedListener<E, T>
    ): ContinuousSession2<T>


    /**
     * 尝试通过ID获取一个 session.
     */
    public abstract operator fun get(id: ID): ContinuousSession2<*>?


}



public suspend inline fun <reified E: Event, T> ContinuousSessionContext2.waitFor(id: ID = Random.nextLong().ID, timeout: Long = 0, listener: ClearTargetResumedListener<E, T>): T {
    return waitingFor(id, timeout, Event.Key.getKey(), listener)
}





/**
 * 持续会话。
 */
public interface ContinuousSession2<T> {

    /**
     * 获取或挂起等待结果。
     */
    @JvmSynthetic
    public suspend fun await(): T


    public fun cancel(reason: Throwable? = null)

    /**
     * 将当前这个 session 转化为 [Future].
     */
    public fun asFuture(): Future<T>

}


/**
 *
 * @see BlockingResumedListener
 * @see ClearTargetResumedListener
 * @see BlockingClearTargetResumedListener
 */
public fun interface ResumedListener<T> : suspend (EventProcessingContext, CancellableContinuation<T>) -> Unit {
    @JvmSynthetic
    override suspend fun invoke(context: EventProcessingContext, continuation: CancellableContinuation<T>)
}

/**
 *
 */
@Api4J
public fun interface BlockingResumedListener<T> : ResumedListener<T> {
    public fun invokeBlocking(context: EventProcessingContext, continuation: CancellableContinuation<T>)

    @JvmSynthetic
    override suspend fun invoke(context: EventProcessingContext, continuation: CancellableContinuation<T>) {
        invokeBlocking(context, continuation)
    }
}

/**
 * 有着明确监听目标的 [ResumedListener]。
 */
public fun interface ClearTargetResumedListener<E : Event, T> : ResumedListener<T> {
    @JvmSynthetic
    override suspend fun invoke(context: EventProcessingContext, continuation: CancellableContinuation<T>) {
        @Suppress("UNCHECKED_CAST")
        (context.event as? E)?.also { event ->
            invoke(event, context, continuation)
        }
    }

    @JvmSynthetic
    public suspend fun invoke(event: E, context: EventProcessingContext, continuation: CancellableContinuation<T>)
}


/**
 * 有着明确监听目标的 [ResumedListener]。需要考虑重写 [invoke] 来实现事件类型的准确转化。
 *
 * @see ClearTargetResumedListener
 * @see ResumedListener
 */
@Api4J
public fun interface BlockingClearTargetResumedListener<E : Event, T> : ClearTargetResumedListener<E, T> {
    public fun invokeBlocking(event: E, context: EventProcessingContext, continuation: CancellableContinuation<T>)

    @JvmSynthetic
    override suspend fun invoke(event: E, context: EventProcessingContext, continuation: CancellableContinuation<T>) {
        invokeBlocking(event, context, continuation)
    }
}