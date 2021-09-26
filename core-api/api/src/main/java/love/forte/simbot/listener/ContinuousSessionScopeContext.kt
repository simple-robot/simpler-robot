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
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
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
 * [ContinuousSessionContinuation] 的容器, 面向外部的接口。
 *
 */
public interface ContinuousSessionContinuationContainer {
    /**
     * 分组名称
     */
    val group: String

    /**
     * 根据key获取一个 [ContinuousSessionContinuation].
     */
    operator fun get(key: String): ContinuousSessionContinuation<*>?

    /**
     * 获取所有的key
     */
    val keys: Set<String>

    /**
     * 得到其中的元素数量
     */
    val size: Int
}


public fun ContinuousSessionContinuationContainer.cancel(cause: Throwable? = null) {
    if (this is MapContinuousSessionContinuationContainer) {
        this.values.forEach { it.cancel(cause) }
    } else {
        this.keys.forEach { key -> this[key]?.cancel(cause) }
    }
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

    private val lock = ReentrantReadWriteLock()
    private val continuationMap = mutableMapOf<String, MapContinuousSessionContinuationContainer>()

    /**
     * 获取一个[group]下对应的[ContinuousSessionContinuationContainer].
     *
     * [get] 更多的用于判断 [group] 的存在与否，如果需要推送内容，使用 [push] 或者通过 [take] 获取后使用。
     *
     */
    override fun get(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") group: String): ContinuousSessionContinuationContainer? =
        lock.read { continuationMap[group] }


    /**
     * 获取一个[group]和[key]下对应的[ContinuousSessionContinuation].
     *
     * [get] 更多的用于判断 [group]中[key] 的存在与否，如果需要推送内容，使用 [push] 或者通过 [take] 获取后使用。
     *
     */
    operator fun get(group: String, key: String): ContinuousSessionContinuation<*>? =
        lock.read { continuationMap[group]?.get(key) }

    /**
     * 推送得到的值.
     *
     * @throws ClassCastException 当类型与实际所需不匹配的时候.
     * @throws IllegalStateException 如果被推送目标已经被推送过了（这一般发生在通过 [get] 得到 [ContinuousSessionContinuation] 但是没有去移除它而导致的）
     * @return 只有当推送成功的时候得到true
     */
    fun <T> push(group: String, key: String, value: T): Boolean {
        @Suppress("UNCHECKED_CAST")
        return (take(group, key) as? ContinuousSessionContinuation<T>)?.push(value)?.let { true } ?: false
    }

    /**
     * 推送一个错误
     */
    fun pushException(group: String, key: String, value: Throwable) {
        take(group, key)?.pushException(value)
    }

    /**
     * 在此作用域下，[set] 暂无作用。
     *
     */
    @Deprecated("Unused.")
    override fun set(key: String, value: Any) {
        // Invalid.
        logger.warn("Under Scope.CONTINUOUS_SESSION, set is invalid.")
    }


    /**
     * 设置一个 [session] 到当前作用域。如果发生了覆盖，则会执行被覆盖者的 [cancel][ContinuousSessionContinuation.cancel]
     */
    private fun set(group: String, key: String, session: ContinuousSessionContinuation<*>): Unit = lock.write {
        val container = continuationMap.computeIfAbsent(group, ::MapContinuousSessionContinuationContainer)
        container.merge(key, session) { old, now ->
            now.also { old.cancel() }
        }
    }

    /**
     * 等待下一次的事件唤醒。
     *
     * @throws CancellationException 如果被手动关闭
     * @throws TimeoutException 如果设置了超时时间且超时
     * @throws Exception 其他可能由于 [ContinuousSessionContinuation.pushException] 而推送的异常
     */
    @JvmSynthetic
    suspend fun <T> waiting(
        group: String,
        key: String,
        timeout: Long = defaultTimeout,
        invokeOnCancellation: ((Throwable?) -> Unit)? = null,
    ): T = suspendCancellableCoroutine { cancellableContinuation ->
        val cancelJob: Job? = if (timeout > 0) {
            // Exception stack for external
            val timeoutException = if (logger.isDebugEnabled) {
                TimeoutException("group=$group, key=$key, timeout=$timeout ")
            } else TimeoutException(timeout.toString())

            coroutineScope.launch(start = CoroutineStart.LAZY) {
                delay(timeout)
                take(group, key)?.cancel(timeoutException)
            }
        } else {
            logger.debug("Your waiting task(group={}, key={}) does not set timeout period or less then or equals 0.",
                group,
                key)
            null
        }
        invokeOnCancellation?.also { invokeOnCancellation ->
            cancellableContinuation.invokeOnCancellation(invokeOnCancellation)
        }

        val session = cancellableContinuation.asContinuousSessionContinuation(cancelJob)
        set(group, key, session)
        cancelJob?.start()
    }

    /**
     * 等待下一次的事件唤醒。提供一个 [回调函数][SessionCallback] 来实现事件触发。
     *
     */
    @JvmOverloads
    @Suppress("FunctionName")
    @JvmName("waiting")
    fun <T> _waiting4J(group: String, key: String, timeout: Long = defaultTimeout, callback: SessionCallback<T>) {
        coroutineScope.launch {
            kotlin.runCatching {
                callback.onResume(waiting(group, key, timeout, callback::onCancel))
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
    fun <T> _waiting4J(
        group: String,
        key: String,
        timeout: Long = defaultTimeout,
        onResume: SessionCallbackBuilder.OnResume<T>,
    ) {
        coroutineScope.launch {
            onResume(waiting(group, key, timeout))
        }
    }

    /**
     * 阻塞的会话等待。
     *
     * **需要注意，阻塞会话等待又概率会导致一些问题，例如资源不足或死锁，请斟酌使用。**
     *
     * @see [waiting][_waiting4J]
     * @throws TimeoutException 当超时的时候
     * @throws CancellationException 当被普通的主动关闭时
     */
    @JvmOverloads
    @Suppress("FunctionName")
    @JvmName("waitBlocking")
    @Throws(TimeoutException::class, CancellationException::class)
    fun <T> _waiting4JBlocking(group: String, key: String, timeout: Long = defaultTimeout): T =
        runBlocking { waiting(group, key, timeout) }

    /**
     * 移除某个指定分组下的 [ContinuousSessionContinuationContainer], 并关闭.
     * 注意，如果有对应的值，在返回之前就会执行 [ContinuousSessionContinuationContainer.cancel]。
     *
     * 如果你只希望获取, 不希望关闭，请使用 [get].
     * 如果你希望获取并移除，不希望关闭，请使用 [take].
     */
    override fun remove(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") group: String): ContinuousSessionContinuationContainer? =
        take(group)?.also { it.cancel() }

    /**
     * 移除某个指定的 [ContinuousSessionContinuation], 并关闭.
     * 注意，如果有对应的值，在返回之前就会执行 [ContinuousSessionContinuation.cancel]。
     *
     * 如果你只希望获取, 不希望关闭，请使用 [get].
     * 如果你希望获取并移除，不希望关闭，请使用 [take].
     */
    fun remove(group: String, key: String): ContinuousSessionContinuation<*>? = take(group, key)?.also { it.cancel() }

    /**
     * 移除某个指定的 [ContinuousSessionContinuation].
     * 被移除的对象（如果有的话）不会进行推送操作，因此直到超时（如果有的话）之前，你或许需要主动进行推送。
     *
     */
    fun take(group: String, key: String): ContinuousSessionContinuation<*>? = lock.write {
        val container = continuationMap[group]
        if (container != null) {
            val removed = container.remove(key)
            if (container.isEmpty()) {
                continuationMap.remove(group)
            }
            return@write removed
        }
        null
    }

    /**
     * 直接移除某指定的 [ContinuousSessionContinuationContainer].
     * 被移除的对象（如果有的话）不会进行任何操作
     */
    fun take(group: String): ContinuousSessionContinuationContainer? = lock.write { continuationMap.remove(group) }


    /**
     * 获取所有元素的数量，此处的数量指的是 [ContinuousSessionContinuationContainer] 的数量。
     */
    override fun size(): Int = continuationMap.size


    /**
     * 获取所有的 `groups`
     *
     */
    override val keys: Set<String>
        get() = lock.read { continuationMap.keys }
}


/**
 *
 * 基于 [ConcurrentMap] 的 [ContinuousSessionContinuationContainer] 实现。
 *
 */
internal class MapContinuousSessionContinuationContainer(
    override val group: String,
    map: ConcurrentMap<String, ContinuousSessionContinuation<*>> = ConcurrentHashMap(),
) : ContinuousSessionContinuationContainer, ConcurrentMap<String, ContinuousSessionContinuation<*>> by map


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
        println("pushed.")
    }

    override fun pushException(e: Throwable) {
        continuation.resumeWithException(e).also { cancelJob?.cancel() }
        println("pushException.")
    }

    override fun cancel(e: Throwable?) {
        continuation.cancel(e).also { cancelJob?.cancel() }
    }
}



