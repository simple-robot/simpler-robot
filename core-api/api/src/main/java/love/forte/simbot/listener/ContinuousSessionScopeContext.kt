/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("ContinuousSessionScopeContextUtil")

package love.forte.simbot.listener

import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


/**
 * 持续会话的延续，其类似于 [Continuation], 用于向待延续结果中推送结果。
 * @since 2.3.0
 */
public interface ContinuousSessionContinuation<T> {

    /**
     * 推送一个结果。
     *
     * @throws IllegalStateException 如果结果已经被推送过
     * @return 当且仅当推送成功的时候得到true
     */
    fun push(value: T)


    /**
     * 推送一个错误。
     *
     * @throws IllegalStateException 如果结果已经被推送过
     * @return 当且仅当推送成功的时候得到true
     */
    fun pushException(e: Throwable)


    /**
     * 直接关闭.
     */
    fun cancel(e: Throwable? = null)
}


/**
 *
 * 持续会话的作用域.
 *
 *
 * @author ForteScarlet
 * @since 2.3.0
 */
public class ContinuousSessionScopeContext(
    private val coroutineScope: CoroutineScope,
    /** 默认的超时时间。默认超时1分钟. */
    private val defaultTimeout: Long = TimeUnit.MINUTES.toMillis(1),
) : ScopeContext {
    override val scope: ListenerContext.Scope
        get() = ListenerContext.Scope.CONTINUOUS_SESSION

    private companion object {
        private val logger = LoggerFactory.getLogger(ContinuousSessionScopeContext::class.java)
    }

    // TODO 双层Map
    private val continuationMap = ConcurrentHashMap<String, ContinuousSessionContinuation<*>>()

    /**
     * [get] 更多的用于判断 [key] 的存在与否，如果需要推送内容，使用 [push] 或者通过 [take] 获取后使用。
     *
     */
    override fun get(key: String): ContinuousSessionContinuation<*>? = continuationMap[key]

    /**
     * 推送得到的值.
     * @throws ClassCastException 当类型与实际所需不匹配的时候.
     */
    // TODO push(group, key, value)
    fun <T> push(key: String, value: T) {
        @Suppress("UNCHECKED_CAST")
        (take(key) as? ContinuousSessionContinuation<T>)?.push(value)
    }

    /**
     * 推送一个错误
     */
    // TODO push(group, key, value)
    fun pushException(key: String, value: Throwable) {
        take(key)?.pushException(value)
    }

    /**
     * 设置一个 [ContinuousSessionContinuation]. 此方法应由内部调用。
     * 当一个 [ContinuousSessionContinuation] 在 [ContinuousSessionScopeContext] 中被 [set] 覆盖时，会执行被覆盖者的 [ContinuousSessionContinuation.cancel]
     *
     * @throws IllegalStateException If `value !is ContinuousSessionContinuation<*>`
     */
    override fun set(key: String, value: Any) {
        check(value is ContinuousSessionContinuation<*>) { "Value must be ContinuousSessionContinuation instance." }
        val replaced = continuationMap.put(key, value)
        replaced?.cancel()
    }


    /**
     * 等待下一次的事件唤醒。
     *
     * Java请使用 [_waiting4J]
     *
     * @throws CancellationException 如果被手动关闭 []
     * @throws TimeoutException 如果设置了超时时间且超时
     * @throws Exception 其他可能由于 [ContinuousSessionContinuation.pushException] 而推送的异常
     */
    @JvmSynthetic
    suspend fun <T> waiting(
        key: String,
        timeout: Long = defaultTimeout,
        invokeOnCancellation: ((Throwable?) -> Unit)? = null,
    ): T = suspendCancellableCoroutine { cancellableContinuation ->
        val cancelJob: Job? = if (timeout > 0) {
            coroutineScope.launch(start = CoroutineStart.LAZY) {
                delay(timeout)
                take(key)?.cancel(TimeoutException())
            }
        } else {
            logger.debug("Your waiting task '{}' does not set timeout period or less then or equals 0.")
            null
        }
        invokeOnCancellation?.also { cancellableContinuation.invokeOnCancellation(it) }

        val session = cancellableContinuation.asContinuousSessionContinuation(cancelJob)
        set(key, session)
        cancelJob?.start()
    }

    /**
     * 等待下一次的事件唤醒。提供一个 [回调函数][SessionCallback] 来实现事件触发。
     *
     */
    @JvmOverloads
    @Suppress("FunctionName")
    @JvmName("waiting")
    fun <T> _waiting4J(key: String, timeout: Long = defaultTimeout, callback: SessionCallback<T>) {
        coroutineScope.launch {
            kotlin.runCatching {
                callback.onResume(waiting(key, timeout, callback::onCancel))
            }.getOrElse(callback::onError)
        }
    }

    /**
     * 等待下一次的事件唤醒。提供一个 [回调函数][SessionCallback] 来实现事件触发。
     *
     */
    @JvmOverloads
    @Suppress("FunctionName")
    @JvmName("waiting")
    fun <T> _waiting4J(key: String, timeout: Long = defaultTimeout, onResume: SessionCallbackBuilder.OnResume<T>) {
        coroutineScope.launch {
            onResume(waiting(key, timeout))
        }
    }


    /**
     * 移除某个指定的 [ContinuousSessionContinuation], 并关闭.
     * 注意，如果有对应的值，在返回之前就会执行 [ContinuousSessionContinuation.cancel]。
     *
     * 如果你只希望获取, 不希望推送关闭异常，请使用 [get].
     * 如果你希望获取并移除，不希望推送关闭异常，请使用 [take].
     */
    override fun remove(key: String): ContinuousSessionContinuation<*>? = take(key)?.also { it.cancel() }

    /**
     * 移除某个指定的 [ContinuousSessionContinuation].
     * 被移除的对象（如果有的话）不会进行推送操作，因此直到超时（如果有的话）之前，你或许需要主动进行推送。
     *
     */
    fun take(key: String): ContinuousSessionContinuation<*>? = continuationMap.remove(key)


    override fun size(): Int = continuationMap.size


    override val keys: Set<String>
        get() = continuationMap.keys
}

/**
 * @since 2.3.0
 */
internal fun <T> CancellableContinuation<T>.asContinuousSessionContinuation(cancelJob: Job? = null): ContinuousSessionContinuation<T> =
    ContinuousSessionContinuationImpl(this, cancelJob)


private class ContinuousSessionContinuationImpl<T>(
    private val continuation: CancellableContinuation<T>,
    private val cancelJob: Job? = null,
) :
    ContinuousSessionContinuation<T> {
    @OptIn(InternalCoroutinesApi::class)
    override fun push(value: T) {
        continuation.resume(value).also { cancelJob?.cancel() }
    }

    override fun pushException(e: Throwable) {
        continuation.resumeWithException(e).also { cancelJob?.cancel() }
    }

    override fun cancel(e: Throwable?) {
        continuation.cancel(e).also { cancelJob?.cancel() }
    }
}



