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
import love.forte.simbot.interception.*
import org.junit.jupiter.api.Timeout
import java.util.concurrent.Executors
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(Api4J::class)
@Timeout(10)
class BlockingInterceptorTests {
    private class Context

    private object Point : InterceptionPoint<Context, Int>
    private object NullablePoint : InterceptionPoint<Context, Int?>

    private suspend fun BlockingInterceptor<Context, Int, Point>.invoke(target: suspend (Context) -> Int): Int =
        intercept(Point, Context(), Interceptor.Chain.create(mutableAttributeMapOf(), target))

    @Test
    fun singleThreadDispatcherPreservesContextAndAllowsSuspendingProceed() {
        Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "blocking-interceptor-test").apply { isDaemon = true }
        }.asCoroutineDispatcher().use { dispatcher ->
            val interceptor = object : BlockingInterceptor<Context, Int, Point>(dispatcher) {
                override fun interceptBlocking(
                    point: Point,
                    context: Context,
                    chain: BlockingInterceptor.Chain<Context, Int>
                ): Int {
                    assertTrue(Thread.currentThread().name.startsWith("blocking-interceptor-test"))
                    return chain.proceed(context) + chain.proceed(context)
                }
            }
            runBlocking(CoroutineName("caller")) {
                withTimeout(5.seconds) {
                    assertEquals(6, interceptor.invoke {
                        delay(1.milliseconds)
                        assertEquals("caller", currentCoroutineContext()[CoroutineName]?.name)
                        3
                    })
                }
            }
        }
    }

    @Test
    fun blockingInterceptorCanRecoverFromDownstreamFailure() = runBlocking {
        val interceptor = object : BlockingInterceptor<Context, Int, Point>() {
            override fun interceptBlocking(
                point: Point,
                context: Context,
                chain: BlockingInterceptor.Chain<Context, Int>
            ): Int {
                return try {
                    chain.proceed(context)
                } catch (_: IllegalStateException) {
                    chain.proceed(context)
                }
            }
        }
        var attempts = 0
        assertEquals(42, interceptor.invoke {
            if (++attempts == 1) error("first attempt")
            currentCoroutineContext().ensureActive()
            42
        })
        currentCoroutineContext().ensureActive()
        assertEquals(2, attempts)
    }

    @Test
    fun callerCancellationCancelsSuspendingProceed() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val interceptor = object : BlockingInterceptor<Context, Int, Point>() {
            override fun interceptBlocking(
                point: Point,
                context: Context,
                chain: BlockingInterceptor.Chain<Context, Int>
            ): Int =
                chain.proceed(context)
        }
        val call = async {
            interceptor.invoke {
                entered.complete(Unit)
                try {
                    awaitCancellation()
                } finally {
                    finished.complete(Unit)
                }
            }
        }
        withTimeout(5_000) {
            entered.await()
            call.cancelAndJoin()
            finished.await()
        }
        assertTrue(call.isCancelled)
    }

    /**
     * 空附加上下文使用调用线程，并保留属性与替换后的业务上下文。
     */
    @Test
    fun nullContextUsesCallerThreadAndPreservesChainData() = runBlocking {
        val callerThread = Thread.currentThread()
        val original = Context()
        val replacement = Context()
        val attributes = mutableAttributeMapOf()
        val interceptor = blocking<Context, Int, Point>(null) { point, context, chain ->
            assertSame(Point, point)
            assertSame(original, context)
            assertSame(callerThread, Thread.currentThread())
            assertSame(attributes, chain.attributes)
            chain.proceed(replacement)
        }
        assertEquals(7, InterceptorInvoker(Point, listOf(interceptor), attributes) {
            assertSame(replacement, it)
            7
        }.invoke(original))
    }

    /**
     * 阻塞适配器允许可空结果短路，不应额外执行目标。
     */
    @Test
    fun nullableResultCanShortCircuitTheTarget() = runBlocking {
        val interceptor = blocking<Context, Int?, NullablePoint>(null) { _, _, _ -> null }
        assertNull(InterceptorInvoker(NullablePoint, listOf(interceptor)) {
            error("short-circuited")
        }.invoke(Context()))
    }

    /**
     * 阻塞适配器也必须保留调用方的取消关系。
     */
    @Test
    fun adapterContextCannotReplaceCallerJob() {
        val job = Job()
        try {
            assertFailsWith<IllegalArgumentException> {
                blocking<Context, Int, Point>(job) { _, c, chain -> chain.proceed(c) }
            }
        } finally {
            job.cancel()
        }
    }

    /**
     * Java 回调覆盖或新增的上下文元素不泄漏到下游，proceed 返回后仍恢复 Java 环境。
     */
    @Test
    fun separateDispatchersRestoreFullEntryEnvironmentAndJavaCallbackEnvironment() = runBlocking {
        val inheritedLocal = ThreadLocal<String>()
        val javaOnlyLocal = ThreadLocal<String>()
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { entryDispatcher ->
            Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { javaDispatcher ->
                val javaContext = javaDispatcher + CoroutineName("java") +
                    inheritedLocal.asContextElement("java") + javaOnlyLocal.asContextElement("java-only")
                val interceptor = blocking<Context, Int, Point>(javaContext) { _, c, chain ->
                    val javaThread = Thread.currentThread()
                    assertEquals("java", inheritedLocal.get())
                    assertEquals("java-only", javaOnlyLocal.get())
                    val result = chain.proceed(c)
                    assertSame(javaThread, Thread.currentThread())
                    assertEquals("java", inheritedLocal.get())
                    assertEquals("java-only", javaOnlyLocal.get())
                    result + 1
                }
                withContext(entryDispatcher + CoroutineName("entry") + inheritedLocal.asContextElement("entry")) {
                    val entryThread = Thread.currentThread()
                    assertEquals(42, InterceptorInvoker(Point, listOf(interceptor)) {
                        assertSame(entryDispatcher, currentCoroutineContext()[ContinuationInterceptor])
                        assertEquals("entry", currentCoroutineContext()[CoroutineName]?.name)
                        assertSame(entryThread, Thread.currentThread())
                        assertEquals("entry", inheritedLocal.get())
                        assertNull(javaOnlyLocal.get())
                        yield()
                        assertSame(entryThread, Thread.currentThread())
                        assertEquals("entry", inheritedLocal.get())
                        assertNull(javaOnlyLocal.get())
                        41
                    }.invoke(Context()))
                    assertSame(entryThread, Thread.currentThread())
                    assertEquals("entry", currentCoroutineContext()[CoroutineName]?.name)
                    assertEquals("entry", inheritedLocal.get())
                    assertNull(javaOnlyLocal.get())
                }
            }
        }
        assertNull(inheritedLocal.get())
        assertNull(javaOnlyLocal.get())
    }

    /**
     * 未被 Java 回调处理的下游异常保留类型和消息，调用方捕获后仍可继续运行。
     */
    @Test
    fun unhandledDownstreamFailurePropagatesToCaller() = runBlocking {
        val failure = IllegalStateException("downstream failure")
        val interceptor = blocking<Context, Int, Point> { _, c, chain -> chain.proceed(c) }
        val thrown = assertFailsWith<IllegalStateException> {
            InterceptorInvoker(Point, listOf(interceptor)) { throw failure }.invoke(Context())
        }
        // 协程调试的栈恢复可能复制异常，不约束异常对象身份。
        assertEquals(failure::class, thrown::class)
        assertEquals(failure.message, thrown.message)
        currentCoroutineContext().ensureActive()
    }

    /**
     * Executor 重载仅调度 Java 回调，下游仍恢复调用入口的线程和上下文。
     */
    @Test
    fun executorOverloadDispatchesOnlyTheBlockingCallback() = runBlocking(CoroutineName("entry")) {
        val entryThread = Thread.currentThread()
        val executor = Executors.newSingleThreadExecutor { runnable -> Thread(runnable, "blocking-executor-test") }
        try {
            val interceptor = blocking<Context, Int, Point>(executor) { _, c, chain ->
                assertNotSame(entryThread, Thread.currentThread())
                assertTrue(Thread.currentThread().name.startsWith("blocking-executor-test"))
                chain.proceed(c)
            }
            assertEquals(42, InterceptorInvoker(Point, listOf(interceptor)) {
                assertSame(entryThread, Thread.currentThread())
                assertEquals("entry", currentCoroutineContext()[CoroutineName]?.name)
                42
            }.invoke(Context()))
        } finally {
            executor.shutdownNow()
        }
    }

    /**
     * 直接返回非空结果可短路目标，不需要创建或管理额外的下游任务。
     */
    @Test
    fun nonNullResultCanShortCircuitTheTarget() = runBlocking {
        val interceptor = blocking<Context, Int, Point> { _, _, _ -> 42 }
        assertEquals(42, InterceptorInvoker(Point, listOf(interceptor)) {
            error("short-circuited")
        }.invoke(Context()))
    }
}
