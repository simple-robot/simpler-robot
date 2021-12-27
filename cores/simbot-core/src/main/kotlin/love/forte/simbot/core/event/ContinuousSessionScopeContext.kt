/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.core.event

import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import love.forte.simbot.Api4J
import love.forte.simbot.LoggerFactory
import love.forte.simbot.event.ContinuousSession
import love.forte.simbot.event.ContinuousSessionContainer
import love.forte.simbot.event.ContinuousSessionContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.Future
import java.util.concurrent.TimeoutException
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 *
 * 持续会话的作用域.
 *
 *
 * @author ForteScarCore * @since 2.3.0
 */
public class CoreContinuousSessionContext(
    override val coroutineScope: CoroutineScope,
    /** 默认的超时时间。默认没有超时限制。 */
    override val defaultTimeoutMills: Long = 0 // 1.minutes.inWholeMilliseconds
) : ContinuousSessionContext() {


    private companion object {
        private val logger = LoggerFactory.getLogger(CoreContinuousSessionContext::class.java)
    }

    private val lock = ReentrantReadWriteLock()
    private val continuationMap = ConcurrentHashMap<String, MapContinuousSessionContainer>()

    /**
     * 获取一个[group]下对应的[ContinuousSessionContainer].
     *
     * [get] 更多的用于判断 [group] 的存在与否，如果需要推送内容，使用 [push] 或者通过 [take] 获取后使用。
     *
     */
    override fun get(@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") group: String): ContinuousSessionContainer? =
        continuationMap[group]


    /**
     * 获取一个[group]和[key]下对应的[ContinuousSession].
     *
     * [get] 更多的用于判断 [group]中[key] 的存在与否，如果需要推送内容，使用 [push] 或者通过 [take] 获取后使用。
     *
     */
    override operator fun <T> get(group: String, key: String): ContinuousSession<T>? =
        continuationMap[group]?.get(key)


    /**
     * 设置一个 [session] 到当前作用域。如果发生了覆盖，则会执行被覆盖者的 [cancel][ContinuousSession.cancel]
     */
    private fun set(group: String, key: String, session: ContinuousSession<*>) {
        val container = continuationMap.computeIfAbsent(group, ::MapContinuousSessionContainer)
        container.merge(key, session) { old, now ->
            now.also { old.cancel() }
        }
    }

    /**
     * 等待下一次的事件唤醒。
     *
     * @throws CancellationException 如果被手动关闭
     * @throws TimeoutException 如果设置了超时时间且超时
     * @throws Exception 其他可能由于 [ContinuousSession.pushException] 而推送的异常
     */
    @JvmSynthetic
    private suspend fun <T> waitForResume(
        group: String,
        key: String,
        invokeOnCancellation: CompletionHandler? = null,
    ): T = suspendCancellableCoroutine { cancellableContinuation ->
        invokeOnCancellation?.also { invokeOnCancellation ->
            cancellableContinuation.invokeOnCancellation(invokeOnCancellation)
        }

        val session = cancellableContinuation.asContinuousSessionContinuation()
        set(group, key, session)
    }

    /**
     * 等待下一次的事件唤醒。
     *
     * @throws CancellationException 如果被手动关闭
     * @throws TimeoutException 如果设置了超时时间且超时
     * @throws Exception 其他可能由于 [ContinuousSession.pushException] 而推送的异常
     */
    @JvmSynthetic
    override suspend fun <T> waiting(
        group: String,
        key: String,
        timeout: Long,
        invokeOnCancellation: CompletionHandler?,
    ): T {
        return if (timeout <= 0) {
            logger.debug(
                "Your waiting task(group={}, key={}) does not set timeout period or less then or equals 0.",
                group,
                key
            )
            waitForResume(group, key, invokeOnCancellation)
        } else {
            withTimeout(timeout) {
                waitForResume(group, key, invokeOnCancellation)
            }
        }

    }

    @Api4J
    @Throws(TimeoutException::class)
    override fun <T> waitBlocking(group: String, key: String, timeout: Long): T {
        return waitFuture<T>(group, key, timeout).get()
    }


    @Api4J
    override fun <T> waitFuture(group: String, key: String, timeout: Long): Future<T> {
        return coroutineScope.async<T> {
            waiting(group, key, timeout)
        }.asCompletableFuture()
    }

    /**
     * 移除某个指定的 [ContinuousSession].
     * 被移除的对象（如果有的话）不会进行推送操作，因此直到超时（如果有的话）之前，你或许需要主动进行推送。
     *
     */
    override fun take(group: String, key: String): ContinuousSession<*>? = lock.write {
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
     * 直接获取并移除某指定的 [ContinuousSessionContainer].
     * 被移除的对象（如果有的话）不会进行任何操作
     */
    override fun take(group: String): ContinuousSessionContainer? = lock.write { continuationMap.remove(group) }


    /**
     * 获取所有元素的数量，此处的数量指的是 [ContinuousSessionContainer] 的数量。
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
 * 基于 [ConcurrentMap] 的 [ContinuousSessionContainer] 实现。
 *
 */
internal class MapContinuousSessionContainer(
    override val group: String,
    private val map: ConcurrentMap<String, ContinuousSession<*>> = ConcurrentHashMap(),
) : ContinuousSessionContainer {
    override fun contains(id: String): Boolean = id in map

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: String): ContinuousSession<T>? = map[key] as? ContinuousSession<T>

    override val keys: Set<String>
        get() = map.keys

    override val size: Int
        get() = map.size

    fun remove(key: String): ContinuousSession<*>? = map.remove(key)

    fun isEmpty(): Boolean = map.isEmpty()

    fun merge(
        key: String,
        value: ContinuousSession<*>,
        mergeFunction: (old: ContinuousSession<*>, now: ContinuousSession<*>) -> ContinuousSession<*>
    ) {
        map.merge(key, value) { old, now ->
            mergeFunction(old, now)
        }
    }
}


internal fun <T> CancellableContinuation<T>.asContinuousSessionContinuation(): ContinuousSession<T> =
    ContinuousSessionImpl(this)


private class ContinuousSessionImpl<T>(
    private val continuation: CancellableContinuation<T>,
) :
    ContinuousSession<T> {
    @OptIn(InternalCoroutinesApi::class)
    override fun push(value: T) {
        continuation.resume(value)
    }

    override fun pushException(e: Throwable) {
        continuation.resumeWithException(e)
    }

    override fun cancel(e: Throwable?) {
        continuation.cancel(e)
    }
}

