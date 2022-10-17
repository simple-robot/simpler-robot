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

package love.forte.simbot.core.event

import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.event.*
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resumeWithException

@ExperimentalSimbotApi
internal class SimpleContinuousSessionContext(
    private val coroutineScope: CoroutineScope,
    private val manager: ContinuousSessionListenerManager,
) : ContinuousSessionContext() {
    
    companion object {
        private val logger = LoggerFactory.getLogger(SimpleContinuousSessionContext::class)
    }
    
    private fun <T> waiting0(
        continuation: CancellableContinuation<T>,
        id: String,
        listener: ContinuousSessionSelector<T>,
    ) {
        val deferred = CompletableDeferred<T>(coroutineScope.coroutineContext[Job])
        val receiver = deferred.asReceiver(continuation)
        val provider = continuation.asProvider(deferred)
        
        manager[id, deferred, provider, receiver] = listener
        
        continuation.invokeOnCancellation {
            val cause = if (it == null) null
            else it as? CancellationException ?: CancellationException(it.localizedMessage, it)
            if (logger.isDebugEnabled) {
                logger.debug("ContinuousSessionSelector continuation cancelled by cause: {}", cause.toString())
            }
            deferred.cancel(cause)
        }
    }
    
    override suspend fun <T> waiting(id: String, listener: ContinuousSessionSelector<T>): T {
        return suspendCancellableCoroutine { c ->
            waiting0(c, id, listener)
        }
    }
    
    
    override fun <T> getProvider(id: String): ContinuousSessionProvider<T>? {
        return manager.getProvider(id)
    }
    
    override fun <T> getReceiver(id: String): ContinuousSessionReceiver<T>? {
        return manager.getReceiver(id)
    }
}

internal fun <T> Deferred<T>.asReceiver(continuation: CancellableContinuation<T>? = null): SimpleContinuousSessionReceiver<T> =
    SimpleContinuousSessionReceiver(continuation, this)


internal class SimpleContinuousSessionReceiver<T>(
    private val continuation: CancellableContinuation<T>?,
    private val deferred: Deferred<T>,
) : ContinuousSessionReceiver<T> {
    private val compiled = AtomicBoolean(deferred.isCompleted)
    
    init {
        deferred.invokeOnCompletion {
            compiled.compareAndSet(false, true)
        }
    }
    
    override suspend fun await(): T = deferred.await()
    
    override fun cancel(reason: Throwable?) {
        if (compiled.compareAndSet(false, true)) {
            if (continuation != null && !continuation.isCompleted) {
                continuation.cancel(reason)
            }
            deferred.cancel(reason?.let { CancellationException(it.localizedMessage, it) })
        }
    }
    
    override fun asFuture(): Future<T> = deferred.asCompletableFuture()
}


internal fun <T> CancellableContinuation<T>.asProvider(deferred: CompletableDeferred<T>): SimpleContinuousSessionProvider<T> =
    SimpleContinuousSessionProvider(this, deferred)


internal class SimpleContinuousSessionProvider<T>(
    private val continuation: CancellableContinuation<T>,
    private val deferred: CompletableDeferred<T>,
) : ContinuousSessionProvider<T> {
    
    private val completed = AtomicBoolean(continuation.isCompleted)
    
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun push(value: T) {
        if (completed.compareAndSet(false, true)) {
            continuation.resume(value) {
                // nothing now.
            }
            deferred.complete(value)
        }
    }
    
    override fun pushException(e: Throwable) {
        if (completed.compareAndSet(false, true)) {
            continuation.resumeWithException(e)
            deferred.completeExceptionally(e)
        }
        
    }
    
    override fun cancel(reason: Throwable?) {
        if (completed.compareAndSet(false, true)) {
            if (!continuation.isCompleted) {
                continuation.cancel(reason)
            }
            deferred.cancel(reason?.let { CancellationException(reason.localizedMessage, reason) })
        }
    }
    
    override fun invokeOnCompletion(handler: CompletionHandler) {
        deferred.invokeOnCompletion(handler)
    }
    
    override val isCompleted: Boolean
        get() = completed.get() // continuation.isCompleted
}

/**
 * 持续会话监听函数。
 */
internal data class ContinuousSessionListener<T>(
    val selector: ContinuousSessionSelector<T>,
    val listenerDeferred: CompletableDeferred<T>,
    val provider: SimpleContinuousSessionProvider<T>,
    val receiver: SimpleContinuousSessionReceiver<T>,
) {
    suspend operator fun invoke(context: EventProcessingContext) {
        if (!provider.isCompleted) {
            withContext(listenerDeferred) {
                context.run {
                    selector.run { invoke(provider) }
                }
            }
        }
    }
    
    fun cancel(reason: Throwable? = null) {
        provider.cancel(reason)
        receiver.cancel(reason)
    }
}

internal class ContinuousSessionListenerManager {
    companion object {
        private val logger = LoggerFactory.logger<ContinuousSessionListenerManager>()
    }
    
    private val listeners = ConcurrentHashMap<String, ContinuousSessionListener<*>>()
    
    /**
     * 会 cancel 被顶替的旧值。
     */
    operator fun <T> set(
        id: String,
        listenerDeferred: CompletableDeferred<T>,
        provider: SimpleContinuousSessionProvider<T>,
        receiver: SimpleContinuousSessionReceiver<T>,
        listener: ContinuousSessionSelector<T>,
    ) {
        val current = ContinuousSessionListener(listener, listenerDeferred, provider, receiver)
        listeners.merge(id, current) { old, now ->
            logger.debug("Merge waiting listener with save id {}", id)
            old.cancel(SimbotIllegalStateException("Replaced by the same ID listener. id = $id"))
            now
        }
        
        listenerDeferred.invokeOnCompletion {
            listeners.remove(id)?.cancel()
            logger.debug("Remove completed resume listener. id: {}", id)
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    fun <T> getProvider(id: String): ContinuousSessionProvider<T>? {
        return listeners[id]?.provider as? ContinuousSessionProvider<T>
    }
    
    @Suppress("UNCHECKED_CAST")
    fun <T> getReceiver(id: String): ContinuousSessionReceiver<T>? {
        return listeners[id]?.receiver as? ContinuousSessionReceiver<T>
    }
    
    fun isEmpty(): Boolean = listeners.isEmpty()
    
    /**
     *
     * @return [context] 是否被使用
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun process(context: SimpleEventProcessingContext): Boolean {
        for ((id, listener) in listeners) {
            try {
                val deferred = listener.listenerDeferred
                logger.trace("Launch resumed listener: {} of id {} by event {}", listener, id, context.event)
                listener(context)
                if (deferred.isCompleted && deferred.getCompletionExceptionOrNull() == null) {
                    logger.debug("Event context {} is used by ResumeListener(id={})", context, id)
                    return true
                }
            } catch (e: CancellationException) {
                if (logger.isDebugEnabled) {
                    logger.debug("ResumeListener(id=$id) invoke failed: ${e.localizedMessage}", e)
                }
            } catch (e: Throwable) {
                if (logger.isErrorEnabled) {
                    logger.error("ResumedListener(id=$id) invoke failed: ${e.localizedMessage}", e)
                }
            }
        }
        return false
    }
}

