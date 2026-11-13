/*
 *     Copyright (c) 2026. ForteScarlet.
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

package event

import kotlinx.coroutines.*
import love.forte.simbot.common.attribute.mutableAttributeMapOf
import love.forte.simbot.event.*
import org.junit.jupiter.api.Timeout
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.*

/**
 * 验证事件 Java 适配器的入口环境、阻塞桥接与独立 future 所有权。
 */
@Timeout(10)
class JEventInterceptorContextTests {
    /**
     * 用最小事件上下文隔离适配行为，不依赖实际事件分发器。
     */
    private fun source(
        configuredContext: CoroutineContext = EmptyCoroutineContext,
        target: suspend (EventListenerContext) -> EventResult
    ): EventInterceptor.Context {
        val eventContext = object : EventContext {
            override val coroutineContext: CoroutineContext = configuredContext
            override val event: Event get() = error("event is not needed")
            override val attributes = mutableAttributeMapOf()
        }
        val listenerContext = object : EventListenerContext {
            override val context: EventContext = eventContext
            override val listener: EventListener = EventListener { EventResult.empty() }
            override var plainText: String? = null
        }
        return object : EventInterceptor.Context {
            override val eventListenerContext: EventListenerContext = listenerContext
            override suspend fun invoke(): EventResult = target(listenerContext)
            override suspend fun invoke(eventListenerContext: EventListenerContext): EventResult =
                target(eventListenerContext)
        }
    }

    /**
     * 经由挂起入口调用适配器，模拟普通 Kotlin 拦截链的调用方式。
     */
    private suspend fun execute(interceptor: EventInterceptor, source: EventInterceptor.Context): EventResult =
        with(interceptor) { source.intercept() }

    /**
     * Java 阻塞回调离开入口线程，下游恢复入口环境，返回后 Java 回调仍在其原线程执行。
     */
    @Test
    fun blockingCallbackIsIsolatedAndDownstreamRestoresEntryEnvironment() = runBlocking {
        val local = ThreadLocal<String>()
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { dispatcher ->
            withContext(dispatcher + CoroutineName("entry") + local.asContextElement("entry")) {
                val entryThread = Thread.currentThread()
                val result = EventResult.empty()
                val interceptor = JBlockEventInterceptor {
                    val javaThread = Thread.currentThread()
                    assertNotSame(entryThread, javaThread)
                    assertEquals("entry", local.get())
                    invoke().also {
                        assertSame(javaThread, Thread.currentThread())
                        assertEquals("entry", local.get())
                    }
                }
                assertSame(result, execute(interceptor, source(CoroutineName("configured")) {
                    assertSame(dispatcher, currentCoroutineContext()[ContinuationInterceptor])
                    assertEquals("entry", currentCoroutineContext()[CoroutineName]?.name)
                    assertSame(entryThread, Thread.currentThread())
                    assertEquals("entry", local.get())
                    yield()
                    assertSame(entryThread, Thread.currentThread())
                    result
                }))
                assertSame(entryThread, Thread.currentThread())
                assertEquals("entry", local.get())
            }
        }
        assertNull(local.get())
    }

    /**
     * 取消调用协程会取消阻塞桥接中的挂起下游，并等待其清理完成。
     */
    @Test
    fun blockingBridgePropagatesCallerCancellation() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val interceptor = JBlockEventInterceptor { invoke() }
        val call = launch {
            execute(interceptor, source {
                try {
                    entered.complete(Unit)
                    awaitCancellation()
                } finally {
                    finished.complete(Unit)
                }
            })
        }
        try {
            entered.await()
            call.cancelAndJoin()
            assertTrue(finished.isCompleted)
        } finally {
            call.cancelAndJoin()
        }
    }

    /**
     * 下游失败保留类型和消息进入 Java 回调，处理后仍可重新调用下游。
     */
    @Test
    fun blockingBridgePreservesFailuresAndAllowsRecovery() = runBlocking {
        val failure = IllegalStateException("first attempt")
        val result = EventResult.empty()
        var attempts = 0
        val interceptor = JBlockEventInterceptor {
            try {
                invoke()
            } catch (actual: IllegalStateException) {
                assertEquals(failure::class, actual::class)
                assertEquals(failure.message, actual.message)
                invoke()
            }
        }
        assertSame(result, execute(interceptor, source {
            if (++attempts == 1) throw failure
            currentCoroutineContext().ensureActive()
            result
        }))
        assertEquals(2, attempts)
        currentCoroutineContext().ensureActive()
    }

    /**
     * 默认异步下游继承当前入口环境，而非 EventContext 中的静态调度配置。
     */
    @Test
    fun asyncBridgeCapturesCurrentEntryEnvironment() = runBlocking {
        val local = ThreadLocal<String>()
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { dispatcher ->
            withContext(dispatcher + CoroutineName("entry") + local.asContextElement("entry")) {
                val entryThread = Thread.currentThread()
                val interceptor = JAsyncEventInterceptor { it.invoke() }
                execute(interceptor, source(CoroutineName("configured")) {
                    assertSame(dispatcher, currentCoroutineContext()[ContinuationInterceptor])
                    assertSame(entryThread, Thread.currentThread())
                    assertEquals("entry", currentCoroutineContext()[CoroutineName]?.name)
                    assertEquals("entry", local.get())
                    EventResult.empty()
                })
            }
        }
        assertNull(local.get())
    }

    /**
     * 已有非挂起 createContext 扩展点仍可显式选择自定义执行作用域。
     */
    @Test
    fun asyncCreateContextOverrideKeepsItsCustomScope() = runBlocking(CoroutineName("entry")) {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { dispatcher ->
            val customScope = object : CoroutineScope {
                override val coroutineContext: CoroutineContext = dispatcher + CoroutineName("custom")
            }
            var customized = false
            val interceptor = object : JAsyncEventInterceptor {
                override fun EventInterceptor.Context.createContext(): JAsyncEventInterceptor.Context {
                    customized = true
                    return JAsyncEventInterceptor.Context(this, customScope)
                }

                override fun intercept(context: JAsyncEventInterceptor.Context): CompletableFuture<EventResult> =
                    context.invoke()
            }
            execute(interceptor, source {
                assertSame(dispatcher, currentCoroutineContext()[ContinuationInterceptor])
                assertEquals("custom", currentCoroutineContext()[CoroutineName]?.name)
                EventResult.empty()
            })
            assertTrue(customized)
        }
    }

    /**
     * 未返回给框架等待的独立 future 可在拦截结束后继续，由用户负责取消。
     */
    @Test
    fun independentAsyncFutureKeepsUserOwnedLifecycle() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val continueExecution = CompletableDeferred<Unit>()
        val continued = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        var future: CompletableFuture<EventResult>? = null
        val interceptor = JAsyncEventInterceptor {
            future = it.invoke()
            CompletableFuture.completedFuture(EventResult.empty())
        }
        try {
            execute(interceptor, source {
                try {
                    entered.complete(Unit)
                    continueExecution.await()
                    continued.complete(Unit)
                    awaitCancellation()
                } finally {
                    finished.complete(Unit)
                }
            })
            entered.await()
            assertFalse(finished.isCompleted)
            continueExecution.complete(Unit)
            continued.await()
            assertNotNull(future).cancel(true)
            finished.await()
        } finally {
            future?.cancel(true)
            if (entered.isCompleted) {
                withContext(NonCancellable) { withTimeout(5_000) { finished.await() } }
            }
        }
    }

    /**
     * 公开数据类的原构造、解构与 copy 调用保持可用。
     */
    @Test
    fun originalContextDataClassConstructorsRemainAvailable() {
        val source = source { EventResult.empty() }
        val scope = object : CoroutineScope {
            override val coroutineContext: CoroutineContext = EmptyCoroutineContext
        }
        val blocking = JBlockEventInterceptor.Context(source)
        assertSame(source, blocking.component1())
        assertEquals(blocking, blocking.copy())
        val async = JAsyncEventInterceptor.Context(source, scope)
        assertSame(source, async.component1())
        assertSame(scope, async.component2())
        assertEquals(async, async.copy())
    }

    /**
     * 取消事件调用会直接中断 Java 拦截回调中的同步等待，不需要先进入下游。
     */
    @Test
    fun callerCancellationInterruptsTheBlockingEventCallback() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val gate = CountDownLatch(1)
        val interceptor = JBlockEventInterceptor {
            try {
                entered.complete(Unit)
                gate.await()
                EventResult.empty()
            } finally {
                finished.complete(Unit)
            }
        }
        val call = launch {
            execute(interceptor, source { error("short-circuited") })
        }
        try {
            entered.await()
            withTimeout(5_000) {
                call.cancelAndJoin()
                finished.await()
            }
            assertTrue(call.isCancelled)
        } finally {
            gate.countDown()
            call.cancelAndJoin()
        }
    }

    /**
     * 阻塞事件回调将中断异常映射为取消，保留普通业务异常的类型和内容。
     */
    @Test
    fun blockingEventCallbackDistinguishesInterruptionFromBusinessFailure() = runBlocking {
        val interrupted = JBlockEventInterceptor { throw InterruptedException("interrupted") }
        val cancellation = assertFailsWith<CancellationException> {
            execute(interrupted, source { error("short-circuited") })
        }
        // 栈恢复可能复制取消异常，中断原因应仍保留在完整原因链中。
        assertTrue(generateSequence(cancellation as Throwable) { it.cause }.any {
            it is InterruptedException && it.message == "interrupted"
        })
        val business = JBlockEventInterceptor { throw IllegalArgumentException("business") }
        assertEquals("business", assertFailsWith<IllegalArgumentException> {
            execute(business, source { error("short-circuited") })
        }.message)
        currentCoroutineContext().ensureActive()
    }
}
