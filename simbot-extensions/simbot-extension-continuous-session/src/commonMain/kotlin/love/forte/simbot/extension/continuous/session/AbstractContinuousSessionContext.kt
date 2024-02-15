/*
 *     Copyright (c) 2024. ForteScarlet.
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ClosedSendChannelException
import love.forte.simbot.ability.OnCompletion
import love.forte.simbot.common.collection.computeValue
import love.forte.simbot.common.collection.computeValueIfAbsent
import love.forte.simbot.common.collection.concurrentMutableMap
import love.forte.simbot.common.collection.removeValue
import love.forte.simbot.extension.continuous.session.ContinuousSessionContext.ConflictStrategy.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName


/**
 *
 * @author ForteScarlet
 */
public abstract class AbstractContinuousSessionContext<T, R>(coroutineContext: CoroutineContext) :
    ContinuousSessionContext<T, R> {
    protected val sessions: MutableMap<Any, ContinuousSessionProvider<T, R>> = concurrentMutableMap()
    protected val launchScope: CoroutineScope = CoroutineScope(coroutineContext)
    protected val subScope: CoroutineScope =
        if (coroutineContext[Job] == null) launchScope else CoroutineScope(coroutineContext.minusKey(Job))

    protected abstract fun computeSession(key: Any, inSession: InSession<T, R>): ContinuousSessionProvider<T, R>

    override fun session(
        key: Any,
        strategy: ContinuousSessionContext.ConflictStrategy,
        inSession: InSession<T, R>
    ): ContinuousSessionProvider<T, R> {
        return when (strategy) {
            FAILURE -> {
                sessions.computeValue(key) { k, old ->
                    if (old != null) throw IllegalStateException("Session with key $key already exists")

                    computeSession(k, inSession)
                }!!
            }

            REPLACE -> {
                sessions.computeValue(key) { k, old ->
                    old?.cancel(ReplacedBecauseOfConflictSessionKeyException("conflict key $k"))
                    computeSession(k, inSession)
                }!!
            }

            EXISTING -> {
                sessions.computeValueIfAbsent(key) { k -> computeSession(k, inSession) }
            }
        }
    }

    // 是否检测 isActive?

    override fun get(key: Any): ContinuousSessionProvider<T, R>? = sessions[key]
    override fun contains(key: Any): Boolean = sessions.containsKey(key)
    override fun remove(key: Any): ContinuousSessionProvider<T, R>? = sessions[key]
}

/**
 * 创建一个 [ContinuousSessionContext] 的基础实现类型。
 */
@JvmName("createContinuousSessionContext")
public fun <T, R> ContinuousSessionContext(coroutineContext: CoroutineContext): ContinuousSessionContext<T, R> =
    SimpleContinuousSessionContext(coroutineContext)

private class SimpleContinuousSessionContext<T, R>(coroutineContext: CoroutineContext) :
    AbstractContinuousSessionContext<T, R>(coroutineContext) {
    private val parentJob = coroutineContext[Job]

    override fun computeSession(key: Any, inSession: InSession<T, R>): SimpleSessionImpl<T, R> {
        val job = Job(parentJob)
        val channel = Channel<SessionData<T, R>>()
        val session = SimpleSessionImpl(key, job, channel, subScope)

        job.invokeOnCompletion {
            sessions.removeValue(key) { session }
        }
        job.invokeOnCompletion {
            channel.close(it)
        }

        launchScope.launch {
            kotlin.runCatching {
                inSession.run { session.invoke() }
            }.onFailure { e ->
                job.completeExceptionally(e)
            }.onSuccess {
                job.complete()
            }
        }

        return session
    }
}

private data class SessionData<T, R>(val value: T, val continuation: CancellableContinuation<R>)

private class SimpleSessionImpl<T, R>(
    private val key: Any,
    private val job: CompletableJob,
    private val channel: Channel<SessionData<T, R>>,
    private val launchScope: CoroutineScope
) : ContinuousSession<T, R> {
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

    override suspend fun push(value: T): R = suspendCancellableCoroutine { continuation ->
        val data = SessionData(value, continuation)
        launchScope.launch {
            kotlin.runCatching {
                channel.send(data)
            }.onFailure { e ->
                when (e) {
                    is ClosedSendChannelException -> {
                        data.continuation.resumeWithException(
                            SessionPushOnFailureException("Push to session channel (key=$key) failed: ${e.message}", e)
                        )
                    }

                    is ClosedReceiveChannelException -> {
                        data.continuation.resumeWithException(
                            SessionPushOnFailureException("Push to session channel (key=$key) failed: ${e.message}", e)
                        )
                    }

                    is CancellationException -> {
                        data.continuation.cancel(
                            CancellationException(
                                "Push to session channel (key=$key) failed: ${e.message}",
                                e.cause?.let { SessionPushOnFailureException(e.message, it) }
                            )
                        )
                    }

                    else -> {
                        data.continuation.resumeWithException(
                            SessionPushOnFailureException("Push to session channel (key=$key) failed: ${e.message}", e)
                        )
                    }
                }
            }
        }
    }

    override fun cancel(cause: Throwable?) {
        job.cancel(cause?.let { CancellationException("Cancelled: ${it.message}", it) })
    }

    private fun checkJob() {
        if (!job.isActive) {
            throw CancellationException("Session (key=$key) is not active")
        }
    }

    private suspend fun receive() = channel.receive()

    override suspend fun await(result: R): T {
        checkJob()

        val (value, continuation) = receive()
        continuation.resume(result)
        return value
    }

    override suspend fun await(result: (T) -> R): T {
        checkJob()

        val (value, continuation) = receive()
        try {
            continuation.resume(result(value))
        } catch (e: Throwable) {
            continuation.resumeWithException(SessionAwaitOnFailureException(e.message, e))
            throw e
        }
        return value
    }

    override suspend fun await(): SessionContinuation<T, R> {
        checkJob()

        val (value, continuation) = receive()
        val handle =
            job.invokeOnCompletion { cause -> continuation.resumeWithException(SessionCompletedWithoutResumeException(cause)) }

        return createSimpleSessionContinuation(value, continuation, handle)
    }
}

