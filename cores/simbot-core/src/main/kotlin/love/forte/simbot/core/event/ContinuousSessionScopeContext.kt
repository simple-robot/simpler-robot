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
 *
 */

package love.forte.simbot.core.event

import kotlinx.coroutines.*
import kotlinx.coroutines.future.asCompletableFuture
import love.forte.simbot.ID
import love.forte.simbot.LoggerFactory
import love.forte.simbot.event.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.milliseconds


internal class CoreContinuousSessionContext(
    override val coroutineScope: CoroutineScope,
    private val manager: ResumedListenerManager
) : ContinuousSessionContext() {

    companion object {
        private val logger = LoggerFactory.getLogger(CoreContinuousSessionContext::class)
    }

    private fun <T> waitingAsReceiver(
        continuation: CancellableContinuation<T>,
        id: ID,
        timeout: Long,
        listener: ResumeListener<T>
    ) {
        val deferred = CompletableDeferred<T>() // use this for hook
        val receiver = deferred.asSession(continuation)
        continuation.invokeOnCancellation(receiver::cancel) // continuation.invokeOnCancellation must only here
        val provider = continuation.asSession(deferred)

        manager.set(id, listener, provider, receiver)

        if (timeout > 0) {
            val timeoutJob = coroutineScope.launch(start = CoroutineStart.LAZY) {
                delay(timeout)
                logger.trace("Session {} timeout", id)
                @Suppress("ThrowableNotThrown")
                // session [abc] 因5秒的时限超时了。
                provider.pushException(ContinuousSessionTimeoutException("Session [$id] has timed out due to the ${timeout.milliseconds} time limit."))
            }
            deferred.invokeOnCompletion { timeoutJob.cancel() }
            timeoutJob.start()
        }
    }

    override suspend fun <T> waitingFor(id: ID, timeout: Long, listener: ResumeListener<T>): T =
        suspendCancellableCoroutine { c ->
            waitingAsReceiver(c, id, timeout, listener)
        }


    override suspend fun <E : Event, T> waitingFor(
        id: ID,
        timeout: Long,
        eventKey: Event.Key<E>,
        listener: ClearTargetResumeListener<E, T>
    ): T = waitingFor(id, timeout) { c, p ->
        if (c.event.key isSubFrom eventKey) {
            eventKey.safeCast(c.event)?.also { event -> listener(event, c, p) }
        }
    }


    override fun <T> waiting(id: ID, timeout: Long, listener: ResumeListener<T>): ContinuousSessionReceiver<T> {
        return coroutineScope.async {
            waitingFor(id, timeout, listener)
        }.asSession()
    }


    override fun <E : Event, T> waiting(
        id: ID,
        timeout: Long,
        eventKey: Event.Key<E>,
        listener: ClearTargetResumeListener<E, T>
    ): ContinuousSessionReceiver<T> = waiting(id, timeout) { c, p ->
        if (c.event.key isSubFrom eventKey) {
            eventKey.safeCast(c.event)?.also { event ->
                listener(event, c, p)
            }
        }
    }

    override fun <T> getProvider(id: ID): ContinuousSessionProvider<T>? {
        return manager.getProvider(id)
    }

    override fun <T> getReceiver(id: ID): ContinuousSessionReceiver<T>? {
        return manager.getReceiver(id)
    }
}

internal fun <T> Deferred<T>.asSession(continuation: CancellableContinuation<T>? = null): CoreContinuousSessionReceiver<T> =
    CoreContinuousSessionReceiver(continuation, this)


internal class CoreContinuousSessionReceiver<T>(
    private val continuation: CancellableContinuation<T>?,
    private val deferred: Deferred<T>
) : ContinuousSessionReceiver<T> {
    override suspend fun await(): T = deferred.await()

    override fun cancel(reason: Throwable?) {
        continuation?.cancel(reason)
        deferred.cancel(reason?.let { CancellationException(it.localizedMessage, it) })
    }

    override fun tryCancel(reason: Throwable?): Boolean {
        if (continuation != null && (continuation.isCompleted || continuation.isCancelled)) return false
        if (deferred.isCompleted || deferred.isCancelled) return false
        cancel(reason)
        return true
    }

    override fun asFuture(): Future<T> = deferred.asCompletableFuture()
}


internal fun <T> CancellableContinuation<T>.asSession(deferred: CompletableDeferred<T>): CoreContinuousSessionProvider<T> =
    CoreContinuousSessionProvider(this, deferred)

internal class CoreContinuousSessionProvider<T>(
    private val continuation: CancellableContinuation<T>,
    private val deferred: CompletableDeferred<T>
) : ContinuousSessionProvider<T> {
    override fun push(value: T) {
        continuation.resume(value)
        deferred.complete(value)
    }

    override fun pushException(e: Throwable) {
        continuation.resumeWithException(e)
        deferred.completeExceptionally(e)
    }

    override fun cancel(reason: Throwable?) {
        continuation.cancel(reason)
        deferred.cancel(reason?.let { CancellationException(reason.localizedMessage, reason) })
    }

    override fun invokeOnCompletion(handler: CompletionHandler) {
        deferred.invokeOnCompletion(handler)
    }

    override val isCompleted: Boolean
        get() = continuation.isCompleted
}


internal data class WaitingListener<T>(
    val listener: ResumeListener<T>,
    val provider: CoreContinuousSessionProvider<T>,
    val receiver: CoreContinuousSessionReceiver<T>
) {
    suspend operator fun invoke(context: EventProcessingContext) {
        listener(context, provider)
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
        provider: CoreContinuousSessionProvider<T>,
        receiver: CoreContinuousSessionReceiver<T>
    ) {
        val cid = id.toString()
        val current = WaitingListener(listener, provider, receiver)
        listeners.merge(cid, current) { old, now ->
            old.provider.cancel(CancellationException("Replaced by the same ID listener. id = $cid"))
            now
        }
        provider.invokeOnCompletion {
            listeners.remove(cid)
            logger.debug("Remove resumed listener: {}", cid)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getProvider(id: ID): ContinuousSessionProvider<T>? {
        return listeners[id.toString()]?.provider as? ContinuousSessionProvider<T>
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getReceiver(id: ID): ContinuousSessionReceiver<T>? {
        return listeners[id.toString()]?.receiver as? ContinuousSessionReceiver<T>
    }

    fun isEmpty(): Boolean = listeners.isEmpty()

    suspend fun process(context: CoreEventProcessingContext, scope: CoroutineScope) {
        listeners.forEach { (id, listener) ->
            scope.launch {
                try {
                    logger.debug("Launch resumed listener: {} of id {}", listener, id)
                    listener(context)
                } catch (e: Throwable) {
                    // TODO process exception?
                    logger.error("ResumedListener(id=$id) invoke failed: ${e.localizedMessage}", e)
                }
            }
        }
    }
}

