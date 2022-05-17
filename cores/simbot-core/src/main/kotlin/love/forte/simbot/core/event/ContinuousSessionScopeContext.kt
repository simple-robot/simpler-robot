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
import love.forte.simbot.*
import love.forte.simbot.event.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resumeWithException

@ExperimentalSimbotApi
internal class SimpleContinuousSessionContext(
    override val coroutineScope: CoroutineScope,
    private val manager: ResumedListenerManager,
) : ContinuousSessionContext() {
    
    companion object {
        private val logger = LoggerFactory.getLogger(SimpleContinuousSessionContext::class)
    }
    
    private fun <T> waitingAsReceiver(
        continuation: CancellableContinuation<T>,
        id: ID,
        listener: ResumeListener<T>,
    ) {
        val deferred = CompletableDeferred<T>(coroutineScope.coroutineContext[Job])
        val receiver = deferred.asReceiver(continuation)
        val provider = continuation.asProvider(deferred)
        
        manager.set(id, listener, deferred, provider, receiver)
        
        continuation.invokeOnCancellation {
            val cause = if (it == null) null
            else it as? CancellationException ?: CancellationException(it.localizedMessage, it)
            logger.debug("Deferred cancel by cause: {}", cause.toString())
            deferred.cancel(cause)
        }
    }
    
    override suspend fun <T> waiting(id: ID, listener: ResumeListener<T>): T {
        return suspendCancellableCoroutine { c ->
            waitingAsReceiver(c, id, listener)
        }
        
        
    }
    
    
    override fun <T> getProvider(id: ID): ContinuousSessionProvider<T>? {
        return manager.getProvider(id)
    }
    
    override fun <T> getReceiver(id: ID): ContinuousSessionReceiver<T>? {
        return manager.getReceiver(id)
    }
}

internal fun <T> Deferred<T>.asReceiver(continuation: CancellableContinuation<T>? = null): CoreContinuousSessionReceiver<T> =
    CoreContinuousSessionReceiver(continuation, this)


internal class CoreContinuousSessionReceiver<T>(
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


internal fun <T> CancellableContinuation<T>.asProvider(deferred: CompletableDeferred<T>): CoreContinuousSessionProvider<T> =
    CoreContinuousSessionProvider(this, deferred)


internal class CoreContinuousSessionProvider<T>(
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


internal data class WaitingListener<T>(
    val listener: ResumeListener<T>,
    val listenerJob: Job,
    val provider: CoreContinuousSessionProvider<T>,
    val receiver: CoreContinuousSessionReceiver<T>,
) {
    suspend operator fun invoke(context: EventProcessingContext) {
        if (!provider.isCompleted) {
            withContext(listenerJob) {
                context.run {
                    listener.run { invoke(provider) }
                }
            }
        }
    }
    
    fun cancel(reason: Throwable? = null) {
        provider.cancel(reason)
        receiver.cancel(reason)
    }
}

internal class ResumedListenerManager {
    companion object {
        private val logger = LoggerFactory.getLogger(ResumedListenerManager::class)
    }
    
    private val listeners = ConcurrentHashMap<String, WaitingListener<*>>()
    
    /**
     * 会 cancel 被顶替的旧值。
     */
    fun <T> set(
        id: ID,
        listener: ResumeListener<T>,
        listenerJob: Job,
        provider: CoreContinuousSessionProvider<T>,
        receiver: CoreContinuousSessionReceiver<T>,
    ) {
        val cid = id.literal
        val current = WaitingListener(listener, listenerJob, provider, receiver)
        listeners.merge(cid, current) { old, now ->
            logger.debug("Merge waiting listener with save id {}", cid)
            old.cancel(SimbotIllegalStateException("Replaced by the same ID listener. id = $cid"))
            now
        }
        
        listenerJob.invokeOnCompletion {
            listeners.remove(cid)?.cancel()
            logger.debug("Remove completed resume listener. id: {}", cid)
        }
    }
    
    @Suppress("UNCHECKED_CAST")
    fun <T> getProvider(id: ID): ContinuousSessionProvider<T>? {
        return listeners[id.literal]?.provider as? ContinuousSessionProvider<T>
    }
    
    @Suppress("UNCHECKED_CAST")
    fun <T> getReceiver(id: ID): ContinuousSessionReceiver<T>? {
        return listeners[id.literal]?.receiver as? ContinuousSessionReceiver<T>
    }
    
    fun isEmpty(): Boolean = listeners.isEmpty()
    
    suspend fun process(context: CoreEventProcessingContext, scope: CoroutineScope) {
        listeners.forEach { (id, listener) ->
            scope.launch {
                try {
                    logger.trace("Launch resumed listener: {} of id {} by event {}", listener, id, context.event)
                    listener(context)
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
        }
    }
}

