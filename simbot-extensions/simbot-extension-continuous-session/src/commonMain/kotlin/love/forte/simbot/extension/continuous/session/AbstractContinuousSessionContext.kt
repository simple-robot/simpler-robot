/*
 *     Copyright (c) 2024-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

@file:JvmName("ContinuousSessionContexts")
@file:JvmMultifileClass

package love.forte.simbot.extension.continuous.session

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import love.forte.simbot.ability.OnCompletion
import love.forte.simbot.common.collection.computeValue
import love.forte.simbot.common.collection.computeValueIfAbsent
import love.forte.simbot.common.collection.concurrentMutableMap
import love.forte.simbot.common.collection.removeValue
import love.forte.simbot.extension.continuous.session.ContinuousSessionContext.ConflictStrategy.*
import love.forte.simbot.extension.continuous.session.ContinuousSessionReceiver.Received
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


/**
 * 针对 [ContinuousSessionContext] 的基础抽象实现类。
 *
 * @author ForteScarlet
 */
@ExperimentalContinuousSessionAPI
public abstract class AbstractContinuousSessionContext<T, R>(coroutineContext: CoroutineContext) :
    ContinuousSessionContext<T, R> {
    protected val sessions: MutableMap<Any, ContinuousSessionProvider<*, T, R>> = concurrentMutableMap()
    protected val launchScope: CoroutineScope = CoroutineScope(coroutineContext)
    protected val subScope: CoroutineScope =
        if (coroutineContext[Job] == null) launchScope else CoroutineScope(coroutineContext.minusKey(Job))

    protected abstract fun <C> computeSession(
        key: ContinuousSessionKey<C>,
        inSession: InSession<C, T, R>
    ): ContinuousSessionProvider<C, T, R>

    @Suppress("UNCHECKED_CAST")
    override fun <C> session(
        key: ContinuousSessionKey<C>,
        strategy: ContinuousSessionContext.ConflictStrategy,
        inSession: InSession<C, T, R>
    ): ContinuousSessionProvider<C, T, R> {
        val p = when (strategy) {
            FAILURE -> {
                sessions.computeValue(key) { k, old ->
                    if (old != null && old.isActive) {
                        throw ConflictSessionKeyException("Session with key $k already exists")
                    }

                    computeSession(key, inSession)
                }!!
            }

            REPLACE -> {
                sessions.computeValue(key) { k, old ->
                    old?.cancel(ReplacedBecauseOfConflictSessionKeyException("conflict key $k"))
                    computeSession(key, inSession)
                }!!
            }

            EXISTING -> {
                sessions.computeValueIfAbsent(key) { _ -> computeSession(key, inSession) }
            }
        }

        return p as ContinuousSessionProvider<C, T, R>
    }

    // 是否检测 isActive?

    @Suppress("UNCHECKED_CAST")
    override fun <C> get(key: ContinuousSessionKey<C>): ContinuousSessionProvider<C, T, R>? =
        sessions[key] as? ContinuousSessionProvider<C, T, R>?

    override fun contains(key: ContinuousSessionKey<*>): Boolean = sessions.containsKey(key)

    @Suppress("UNCHECKED_CAST")
    override fun <C> remove(key: ContinuousSessionKey<C>): ContinuousSessionProvider<C, T, R>? =
        sessions.remove(key) as? ContinuousSessionProvider<C, T, R>?
}

/**
 * 创建一个 [ContinuousSessionContext] 的基础实现类型。
 */
@JvmName("createContinuousSessionContext")
@Suppress("FunctionNaming")
@ExperimentalContinuousSessionAPI
public fun <T, R> ContinuousSessionContext(coroutineContext: CoroutineContext): ContinuousSessionContext<T, R> =
    SimpleContinuousSessionContext(coroutineContext)

private class SimpleContinuousSessionContext<T, R>(coroutineContext: CoroutineContext) :
    AbstractContinuousSessionContext<T, R>(coroutineContext) {
    private val parentJob = coroutineContext[Job]

    override fun <C> computeSession(
        key: ContinuousSessionKey<C>,
        inSession: InSession<C, T, R>
    ): SimpleSessionImpl<C, T, R> {
        val job = SupervisorJob(parentJob)
        val channel = Channel<SessionData<C, T, R>>(
            capacity = Channel.RENDEZVOUS,
            onBufferOverflow = BufferOverflow.SUSPEND,
            onUndeliveredElement = { (_, value, continuation) ->
                continuation.resumeWithException(SessionPushOnFailureException("Undelivered value: $value"))
            }
        )

        val session = SimpleSessionImpl(key, job, channel, subScope)

        job.invokeOnCompletion {
            sessions.removeValue(key) { session }
            channel.cancel(it?.let { e -> CancellationException(e.message, e) })
        }

        launchScope.launch {
            try {
                inSession.run { session.invoke() }
                job.complete()
            } catch (e: CancellationException) {
                job.completeExceptionally(e)
                throw e
            } catch (e: Throwable) {
                job.completeExceptionally(e)
            }
        }

        return session
    }
}

private data class SessionData<C, T, R>(
    val context: C,
    val value: T,
    val continuation: CancellableContinuation<R>
)

private class SimpleSessionImpl<C, T, R>(
    private val key: ContinuousSessionKey<C>,
    private val job: CompletableJob,
    private val channel: Channel<SessionData<C, T, R>>,
    private val launchScope: CoroutineScope
) : ContinuousSession<C, T, R> {
    override val coroutineContext: CoroutineContext
        get() = launchScope.coroutineContext

    override val isActive: Boolean
        get() = job.isActive

    override val isCompleted: Boolean
        get() = job.isCompleted

    override val isCancelled: Boolean
        get() = job.isCancelled

    override fun onCompletion(handle: OnCompletion) {
        job.invokeOnCompletion(handle::invoke)
    }

    override suspend fun join() {
        job.join()
    }

    override suspend fun push(value: T, context: C): R {
        checkJob()

        return suspendCancellableCoroutine { continuation ->
            val data = SessionData(context, value, continuation)
            launchScope.launch {
                try {
                    channel.send(data)
                } catch (e: ClosedSendChannelException) {
                    data.continuation.resumeWithException(
                        SessionPushOnFailureException(
                            "Push to session channel (key=$key) failed: ${e.message}",
                            e
                        )
                    )
                } catch (e: ClosedReceiveChannelException) {
                    data.continuation.resumeWithException(
                        SessionPushOnFailureException(
                            "Push to session channel (key=$key) failed: ${e.message}",
                            e
                        )
                    )
                } catch (e: CancellationException) {
                    data.continuation.cancel(
                        CancellationException(
                            "Push to session channel (key=$key) failed: ${e.message}",
                            e.cause?.let { SessionPushOnFailureException(e.message, it) }
                        )
                    )
                    throw e
                } catch (e: Throwable) {
                    data.continuation.resumeWithException(
                        SessionPushOnFailureException(
                            "Push to session channel (key=$key) failed: ${e.message}",
                            e
                        )
                    )
                }
            }
        }
    }

    override fun cancel(cause: Throwable?) {
        job.cancel(cause?.let { CancellationException("Cancelled: ${it.message}", it) })
    }

    private fun checkJob() {
        if (!job.isActive) {
            throw CancellationException("Session with key [$key] is not active")
        }
    }

    private suspend fun receive(): SessionData<C, T, R> {
        checkJob()

        return channel.receive()
    }

    private data class ReceivedImpl<C, T>(
        override val context: C,
        override val value: T
    ) : Received<C, T>

    override suspend fun await(result: R): Received<C, T> {
        val (context, value, continuation) = receive()
        continuation.resume(result)
        return ReceivedImpl(context, value)
    }

    override suspend fun await(result: (Received<C, T>) -> R): Received<C, T> {
        val (context, value, continuation) = receive()
        val received = ReceivedImpl(context, value)
        try {
            continuation.resume(result(received))
        } catch (e: Throwable) {
            continuation.resumeWithException(SessionAwaitOnFailureException(e.message, e))
            throw e
        }
        return received
    }

    override suspend fun await(): SessionContinuation<C, T, R> {
        val (context, value, continuation) = receive()
        val handle =
            job.invokeOnCompletion { cause ->
                continuation.resumeWithException(
                    SessionCompletedWithoutResumeException(
                        cause
                    )
                )
            }

        return createSimpleSessionContinuation(context, value, continuation, handle)
    }
}
