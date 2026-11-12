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
package interception

import kotlinx.coroutines.*
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.common.attribute.mutableAttributeMapOf
import love.forte.simbot.interception.InterceptionPoint
import love.forte.simbot.interception.Interceptor
import love.forte.simbot.interception.InterceptorInvoker
import love.forte.simbot.interception.async
import org.junit.jupiter.api.Timeout
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.*

@OptIn(Api4J::class)
@Timeout(10)
class AsyncInterceptorTests {
    private object Point : InterceptionPoint<String, String>
    private object NullablePoint : InterceptionPoint<String, String?>

    @Test
    fun repeatedProceedPreservesAttributesContextAndSuffix() = runBlocking(CoroutineName("async-caller")) {
        val attributes = mutableAttributeMapOf()
        val calls = mutableListOf<String>()
        val first = async<String, String, Point> { _, c, chain ->
            assertSame(attributes, chain.attributes)
            chain.proceed(c).thenCompose { first -> chain.proceed("second").thenApply { first + it } }
        }
        val second = Interceptor<String, String, Point> { _, c, chain ->
            assertSame(attributes, chain.attributes)
            assertEquals("async-caller", currentCoroutineContext()[CoroutineName]?.name)
            calls += c
            chain.proceed(c)
        }
        assertEquals("firstsecond", InterceptorInvoker(Point, listOf(first, second), attributes) {
            delay(1)
            it
        }.invoke("first"))
        assertEquals(listOf("first", "second"), calls)
    }

    @Test
    fun downstreamFailureCanBeRecoveredAndRetried() = runBlocking {
        var attempts = 0
        val interceptor = async<String, String, Point> { _, c, chain ->
            chain.proceed(c).handle { result, failure ->
                if (failure == null) CompletableFuture.completedFuture(result) else chain.proceed(c)
            }.thenCompose { it }
        }
        assertEquals("ok", InterceptorInvoker(Point, listOf(interceptor)) {
            if (++attempts == 1) error("first attempt")
            currentCoroutineContext().ensureActive()
            "ok"
        }.invoke("request"))
        assertEquals(2, attempts)
    }

    /**
     * 下游失败可由 Java 提供备用结果，不能提前取消父调用。
     */
    @Test
    fun downstreamFailureCanBeRecoveredWithFallback() = runBlocking {
        val interceptor = async<String, String, Point> { _, c, chain ->
            chain.proceed(c).exceptionally { "fallback" }
        }
        assertEquals("fallback", InterceptorInvoker(Point, listOf(interceptor)) {
            error("downstream failed")
        }.invoke("request"))
        currentCoroutineContext().ensureActive()
    }

    @Test
    fun nullableResultIsSupported() = runBlocking {
        val interceptor = async<String, String?, NullablePoint> { _, _, _ ->
            CompletableFuture.completedFuture(null)
        }
        assertNull(InterceptorInvoker(NullablePoint, listOf(interceptor)) { "unused" }.invoke("request"))
    }

    @Test
    fun callerCancellationCancelsFutureAndDownstream() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val returned = AtomicReference<CompletableFuture<String>>()
        val interceptor = async<String, String, Point> { _, c, chain ->
            chain.proceed(c).also { returned.set(it) }
        }
        val call = launch {
            InterceptorInvoker(Point, listOf(interceptor)) {
                entered.complete(Unit)
                try {
                    awaitCancellation()
                } finally {
                    finished.complete(Unit)
                }
            }.invoke("request")
        }
        withTimeout(5_000) {
            entered.await()
            call.cancelAndJoin()
            finished.await()
        }
        assertTrue(returned.get().isCancelled)
    }

    @Test
    fun callerCancellationCancelsExternalStage() = runBlocking {
        val returned = CompletableFuture<String>()
        val entered = CompletableDeferred<Unit>()
        val interceptor = async<String, String, Point> { _, _, _ ->
            entered.complete(Unit)
            returned
        }
        val call = launch { InterceptorInvoker(Point, listOf(interceptor)) { it }.invoke("request") }
        entered.await()
        call.cancelAndJoin()
        assertTrue(returned.isCancelled)
    }

    /**
     * 未组合的下游在拦截返回后继续执行，由用户取消其 future。
     */
    @Test
    fun independentFutureRunsUntilUserCancelsIt() = runBlocking {
        val entered = CompletableFuture<Unit>()
        val continueExecution = CompletableDeferred<Unit>()
        val continued = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val source = AtomicReference<CompletableFuture<String>>()
        val interceptor = async<String, String, Point> { _, c, chain ->
            chain.proceed(c).also(source::set)
            entered.thenApply { "short" }
        }
        try {
            withTimeout(5_000) {
                assertEquals("short", InterceptorInvoker(Point, listOf(interceptor)) {
                    try {
                        entered.complete(Unit)
                        continueExecution.await()
                        continued.complete(Unit)
                        awaitCancellation()
                    } finally {
                        finished.complete(Unit)
                    }
                }.invoke("request"))
                // 仅在拦截返回后放行，证明源 future 没有被框架等待或取消。
                assertFalse(finished.isCompleted)
                continueExecution.complete(Unit)
                continued.await()
                assertFalse(source.get().isDone)
                assertTrue(source.get().cancel(false))
                finished.await()
            }
        } finally {
            source.get()?.cancel(false)
        }
    }

    /**
     * 父调用仅取消返回的组合 stage，源 future 仍由用户管理。
     */
    @Test
    fun callerCancellationDoesNotCancelSourceOfDependentStage() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val continueExecution = CompletableDeferred<Unit>()
        val continued = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val source = AtomicReference<CompletableFuture<String>>()
        val dependent = AtomicReference<CompletableFuture<String>>()
        val interceptor = async<String, String, Point> { _, c, chain ->
            chain.proceed(c).also(source::set).thenApply { it }.also(dependent::set)
        }
        val call = launch {
            InterceptorInvoker(Point, listOf(interceptor)) {
                try {
                    entered.complete(Unit)
                    continueExecution.await()
                    continued.complete(Unit)
                    awaitCancellation()
                } finally {
                    finished.complete(Unit)
                }
            }.invoke("request")
        }
        try {
            withTimeout(5_000) {
                entered.await()
                call.cancelAndJoin()
                assertTrue(dependent.get().isCancelled)
                assertFalse(source.get().isDone)
                continueExecution.complete(Unit)
                continued.await()
                assertFalse(finished.isCompleted)
                assertTrue(source.get().cancel(false))
                finished.await()
            }
        } finally {
            call.cancel()
            source.get()?.cancel(false)
        }
    }

    /**
     * 直接返回源 future 时，父超时经 await 取消对应下游。
     */
    @Test
    fun callerTimeoutCancelsDirectDownstream() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val source = AtomicReference<CompletableFuture<String>>()
        val interceptor = async<String, String, Point> { _, c, chain ->
            chain.proceed(c).also(source::set)
        }
        assertFailsWith<TimeoutCancellationException> {
            withTimeout(250) {
                InterceptorInvoker(Point, listOf(interceptor)) {
                    try {
                        entered.complete(Unit)
                        awaitCancellation()
                    } finally {
                        finished.complete(Unit)
                    }
                }.invoke("request")
            }
        }
        assertTrue(entered.isCompleted)
        assertTrue(source.get().isCancelled)
        withTimeout(5_000) { finished.await() }
    }

    /**
     * 单次 future 的取消只影响对应下游，不能取消再次继续执行的能力。
     */
    @Test
    fun cancellingOneFutureAllowsAnotherAttempt() = runBlocking {
        val entered = CompletableFuture<Unit>()
        val finished = CompletableDeferred<Unit>()
        val calls = mutableListOf<String>()
        val interceptor = async<String, String, Point> { _, c, chain ->
            val first = chain.proceed(c)
            entered.thenCompose {
                assertTrue(first.cancel(false))
                chain.proceed("retry")
            }
        }
        assertEquals("ok", InterceptorInvoker(Point, listOf(interceptor)) { context ->
            calls += context
            if (context == "request") {
                try {
                    entered.complete(Unit)
                    awaitCancellation()
                } finally {
                    finished.complete(Unit)
                }
            }
            "ok"
        }.invoke("request"))
        withTimeout(5_000) { finished.await() }
        assertEquals(listOf("request", "retry"), calls)
    }

    /**
     * Java 配置仅作用于回调，下游恢复入口上下文且不继承新增元素。
     */
    @Test
    fun additionalContextIsIsolatedFromDownstream() {
        val local = ThreadLocal<String>()
        val javaOnly = ThreadLocal<String>()
        runBlocking(CoroutineName("caller") + local.asContextElement("caller")) {
            assertEquals("caller", local.get())
            assertNull(javaOnly.get())
            val interceptor =
                async<String, String, Point>(
                    CoroutineName("adapter") + local.asContextElement("adapter") + javaOnly.asContextElement("java-only")
                ) { _, c, chain ->
                    assertEquals("adapter", local.get())
                    assertEquals("java-only", javaOnly.get())
                    chain.proceed(c)
                }
            assertEquals("value", InterceptorInvoker(Point, listOf(interceptor)) {
                assertEquals("caller", currentCoroutineContext()[CoroutineName]?.name)
                assertEquals("caller", local.get())
                assertNull(javaOnly.get())
                it
            }.invoke("value"))
            assertEquals("caller", currentCoroutineContext()[CoroutineName]?.name)
            assertEquals("caller", local.get())
            assertNull(javaOnly.get())
        }
        assertNull(local.get())
        assertNull(javaOnly.get())
    }

    /**
     * Java 使用独立线程时，下游仍回到入口线程并恢复入口上下文。
     */
    @Test
    fun downstreamRestoresCallerDispatcherAcrossJavaThread() = runBlocking {
        val local = ThreadLocal<String>()
        val javaOnly = ThreadLocal<String>()
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { callerDispatcher ->
            Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { javaDispatcher ->
                withContext(callerDispatcher + CoroutineName("caller") + local.asContextElement("caller")) {
                    val callerThread = Thread.currentThread()
                    val javaThread = AtomicReference<Thread>()
                    val interceptor = async<String, String, Point>(
                        javaDispatcher + CoroutineName("java") + local.asContextElement("java") +
                            javaOnly.asContextElement("java-only")
                    ) { _, c, chain ->
                        javaThread.set(Thread.currentThread())
                        assertNotSame(callerThread, Thread.currentThread())
                        assertEquals("java", local.get())
                        assertEquals("java-only", javaOnly.get())
                        chain.proceed(c)
                    }
                    assertEquals("value", InterceptorInvoker(Point, listOf(interceptor)) {
                        assertSame(callerThread, Thread.currentThread())
                        assertNotSame(javaThread.get(), Thread.currentThread())
                        assertEquals("caller", currentCoroutineContext()[CoroutineName]?.name)
                        assertEquals("caller", local.get())
                        assertNull(javaOnly.get())
                        "value"
                    }.invoke("request"))
                    assertSame(callerThread, Thread.currentThread())
                    assertEquals("caller", currentCoroutineContext()[CoroutineName]?.name)
                    assertEquals("caller", local.get())
                    assertNull(javaOnly.get())
                }
            }
        }
    }

    /**
     * 附加上下文不能替换调用方的取消关系。
     */
    @Test
    fun adapterContextCannotReplaceCallerJob() {
        val job = Job()
        try {
            assertFailsWith<IllegalArgumentException> {
                async<String, String, Point>(job) { _, c, chain -> chain.proceed(c) }
            }
        } finally {
            job.cancel()
        }
    }
}
