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
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers
import java.util.concurrent.Executors
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.*

@OptIn(Api4J::class)
@Timeout(10)
class ReactiveInterceptorTests {
    private object Point : InterceptionPoint<String, String>
    private object NullablePoint : InterceptionPoint<String, String?>
    private object ListPoint : InterceptionPoint<String, List<String>?>

    /**
     * Empty and populated publishers represent the two nullable result states.
     */
    @Test
    fun nullableResultsRoundTripThroughTheChain() = runBlocking {
        val interceptor = reactiveNullable<String, String, NullablePoint> { _, c, chain -> chain.proceed(c) }
        assertNull(InterceptorInvoker(NullablePoint, listOf(interceptor)) { null }.invoke("request"))
        assertEquals("value", InterceptorInvoker(NullablePoint, listOf(interceptor)) { "value" }.invoke("request"))

        val empty = reactiveNullable<String, String, NullablePoint> { _, _, _ -> Mono.empty() }
        assertNull(InterceptorInvoker(NullablePoint, listOf(empty)) { error("short-circuited") }.invoke("request"))
    }

    /**
     * A nullable result still requires exactly one successful completion signal.
     */
    @Test
    fun nullableResultsRejectMultipleValuesAndPreserveLateErrors() = runBlocking {
        val multiple = reactiveNullable<String, String, NullablePoint> { _, _, _ -> Flux.just("one", "two") }
        assertFailsWith<IllegalArgumentException> {
            InterceptorInvoker(NullablePoint, listOf(multiple)) { null }.invoke("request")
        }
        val failure = IllegalStateException("after value")
        val lateError = reactiveNullable<String, String, NullablePoint> { _, _, _ ->
            Flux.concat(Mono.just("value"), Mono.error(failure))
        }
        assertEquals(failure.message, assertFailsWith<IllegalStateException> {
            InterceptorInvoker(NullablePoint, listOf(lateError)) { null }.invoke("request")
        }.message)
    }

    /**
     * Collection contents do not change the publisher's result cardinality.
     */
    @Test
    fun listsRemainSingleResultsAndDistinguishEmptyFromNull(): Unit = runBlocking {
        val passThrough = reactiveNullable<String, List<String>, ListPoint> { _, c, chain -> chain.proceed(c) }
        assertNull(InterceptorInvoker(ListPoint, listOf(passThrough)) { null }.invoke("request"))
        assertEquals(emptyList(), InterceptorInvoker(ListPoint, listOf(passThrough)) { emptyList() }.invoke("request"))
        val values = listOf("one", "two")
        assertSame(values, InterceptorInvoker(ListPoint, listOf(passThrough)) { values }.invoke("request"))

        val collected =
            reactiveNullable<String, List<String>, ListPoint> { _, _, _ -> Flux.just("one", "two").collectList() }
        assertEquals(values, InterceptorInvoker(ListPoint, listOf(collected)) { null }.invoke("request"))
        val multiple = reactiveNullable<String, List<String>, ListPoint> { _, _, _ -> Flux.just(values, emptyList()) }
        assertFailsWith<IllegalArgumentException> {
            InterceptorInvoker(ListPoint, listOf(multiple)) { null }.invoke("request")
        }
    }

    /**
     * 独立订阅在拦截返回后仍可继续执行，由订阅者显式取消并等待清理。
     */
    @Test
    fun independentSubscriptionContinuesAfterInterceptionReturnsUntilDisposed() = runBlocking {
        val result = Sinks.one<String>()
        val entered = CompletableDeferred<Unit>()
        val continueExecution = CompletableDeferred<Unit>()
        val continued = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        var subscription: Disposable? = null
        val interceptor = reactive<String, String, Point> { _, c, chain ->
            subscription = Mono.from(chain.proceed(c)).subscribe({}, {})
            result.asMono()
        }
        val call = async {
            InterceptorInvoker(Point, listOf(interceptor)) {
                try {
                    entered.complete(Unit)
                    assertEquals(Sinks.EmitResult.OK, result.tryEmitValue("short"))
                    continueExecution.await()
                    continued.complete(Unit)
                    awaitCancellation()
                } finally {
                    finished.complete(Unit)
                }
            }.invoke("request")
        }
        try {
            assertEquals("short", call.await())
            assertFalse(finished.isCompleted)
            // 仅在拦截返回后放行，证明独立订阅仍能继续执行。
            continueExecution.complete(Unit)
            continued.await()
            assertFalse(finished.isCompleted)
            assertNotNull(subscription).dispose()
            finished.await()
        } finally {
            subscription?.dispose()
            call.cancelAndJoin()
            if (entered.isCompleted) {
                withContext(NonCancellable) { withTimeout(5_000) { finished.await() } }
            }
        }
    }

    /**
     * Callback, initial subscription and downstream execution inherit the caller context.
     */
    @Test
    fun nullableExecutionInheritsCallerContext() = runBlocking {
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { dispatcher ->
            withContext(dispatcher + CoroutineName("nullable-caller")) {
                val callerThread = Thread.currentThread()
                var callbackEntered = false
                var subscribed = false
                val interceptor = reactiveNullable<String, String, NullablePoint> { _, c, chain ->
                    callbackEntered = true
                    assertSame(callerThread, Thread.currentThread())
                    Mono.defer {
                        subscribed = true
                        assertSame(callerThread, Thread.currentThread())
                        Mono.from(chain.proceed(c))
                    }
                }
                assertEquals("value", InterceptorInvoker(NullablePoint, listOf(interceptor)) {
                    assertSame(dispatcher, currentCoroutineContext()[ContinuationInterceptor])
                    assertEquals("nullable-caller", currentCoroutineContext()[CoroutineName]?.name)
                    assertSame(callerThread, Thread.currentThread())
                    "value"
                }.invoke("request"))
                assertTrue(callbackEntered)
                assertTrue(subscribed)
                assertEquals("nullable-caller", currentCoroutineContext()[CoroutineName]?.name)
            }
        }
    }

    /**
     * 即使在 Reactor 工作线程订阅，下游仍使用构建 chain 时的完整入口环境。
     */
    @Test
    fun subscriptionOnAnotherThreadRestoresEntryEnvironment() = runBlocking {
        val local = ThreadLocal<String>()
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { dispatcher ->
            withContext(dispatcher + CoroutineName("entry") + local.asContextElement("entry")) {
                val entryThread = Thread.currentThread()
                val subscribedThread = CompletableDeferred<Thread>()
                val interceptor = reactive<String, String, Point> { _, c, chain ->
                    Mono.from(chain.proceed(c))
                        .doOnSubscribe { subscribedThread.complete(Thread.currentThread()) }
                        .subscribeOn(Schedulers.parallel())
                }
                assertEquals("value", InterceptorInvoker(Point, listOf(interceptor)) {
                    assertSame(dispatcher, currentCoroutineContext()[ContinuationInterceptor])
                    assertEquals("entry", currentCoroutineContext()[CoroutineName]?.name)
                    assertSame(entryThread, Thread.currentThread())
                    assertEquals("entry", local.get())
                    "value"
                }.invoke("request"))
                assertNotSame(entryThread, subscribedThread.await())
                assertEquals("entry", local.get())
            }
        }
    }

    /**
     * 外层 Java 回调的特殊上下文不会覆盖嵌套 reactive 和 Kotlin 下游的入口环境。
     */
    @Test
    fun nestedAsyncCallbackPreservesReactiveEntryEnvironment() = runBlocking {
        val local = ThreadLocal<String>()
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { entryDispatcher ->
            Executors.newSingleThreadExecutor().asCoroutineDispatcher().use { javaDispatcher ->
                withContext(entryDispatcher + CoroutineName("entry") + local.asContextElement("entry")) {
                    val entryThread = Thread.currentThread()
                    val calls = mutableListOf<String>()
                    val javaContext = javaDispatcher + CoroutineName("java") + local.asContextElement("java")
                    val javaInterceptor = async<String, String, Point>(javaContext) { _, c, chain ->
                        assertEquals("java", local.get())
                        assertNotSame(entryThread, Thread.currentThread())
                        chain.proceed(c)
                    }
                    val reactiveInterceptor = reactive<String, String, Point> { _, c, chain ->
                        assertSame(entryThread, Thread.currentThread())
                        assertEquals("entry", local.get())
                        Mono.from(chain.proceed(c))
                    }
                    val observer = Interceptor<String, String, Point> { _, c, chain ->
                        assertSame(entryDispatcher, currentCoroutineContext()[ContinuationInterceptor])
                        assertEquals("entry", currentCoroutineContext()[CoroutineName]?.name)
                        assertSame(entryThread, Thread.currentThread())
                        assertEquals("entry", local.get())
                        calls += "before"
                        chain.proceed(c).also {
                            assertSame(entryThread, Thread.currentThread())
                            assertEquals("entry", local.get())
                            calls += "after"
                        }
                    }
                    assertEquals("value", InterceptorInvoker(Point, listOf(javaInterceptor, reactiveInterceptor, observer)) {
                        assertSame(entryDispatcher, currentCoroutineContext()[ContinuationInterceptor])
                        assertEquals("entry", currentCoroutineContext()[CoroutineName]?.name)
                        assertEquals("entry", local.get())
                        "value"
                    }.invoke("request"))
                    assertEquals(listOf("before", "after"), calls)
                    assertSame(entryThread, Thread.currentThread())
                    assertEquals("entry", local.get())
                }
            }
        }
        assertNull(local.get())
    }

    /**
     * 可空下游失败可被响应式恢复，恢复后的空完成仍表示 null。
     */
    @Test
    fun nullableDownstreamFailureCanRecoverWithAnotherAttempt() = runBlocking {
        var attempts = 0
        val interceptor = reactiveNullable<String, String, NullablePoint> { _, c, chain ->
            Mono.from(chain.proceed(c)).onErrorResume { Mono.from(chain.proceed(c)) }
        }
        assertNull(InterceptorInvoker(NullablePoint, listOf(interceptor)) {
            if (++attempts == 1) error("first attempt")
            null
        }.invoke("request"))
        assertEquals(2, attempts)
        assertTrue(currentCoroutineContext().isActive)
    }

    /**
     * 可空适配器同样向订阅和挂起下游传播父调用的取消。
     */
    @Test
    fun nullableCallerCancellationCancelsSubscriptionAndDownstream() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val cancelled = CompletableDeferred<Unit>()
        val interceptor = reactiveNullable<String, String, NullablePoint> { _, c, chain ->
            Mono.from(chain.proceed(c)).doOnCancel { cancelled.complete(Unit) }
        }
        val call = launch {
            InterceptorInvoker(NullablePoint, listOf(interceptor)) {
                entered.complete(Unit)
                try {
                    awaitCancellation()
                } finally {
                    finished.complete(Unit)
                }
            }.invoke("request")
        }
        entered.await()
        call.cancelAndJoin()
        finished.await()
        cancelled.await()
    }

    @Test
    fun resubscriptionReexecutesSuffixAndSharesAttributes() = runBlocking(CoroutineName("reactive-caller")) {
        var attempts = 0
        val attributes = mutableAttributeMapOf()
        val calls = mutableListOf<String>()
        val interceptor = reactive<String, String, Point> { _, c, chain ->
            assertSame(attributes, chain.attributes)
            val publisher = chain.proceed(c)
            assertEquals(0, attempts)
            Mono.from(publisher).retry(1)
        }
        val observer = Interceptor<String, String, Point> { _, c, chain ->
            assertSame(attributes, chain.attributes)
            assertEquals("reactive-caller", currentCoroutineContext()[CoroutineName]?.name)
            calls += c
            chain.proceed(c)
        }
        assertEquals("ok", InterceptorInvoker(Point, listOf(interceptor, observer), attributes) {
            delay(1)
            if (++attempts == 1) error("first attempt")
            "ok"
        }.invoke("request"))
        assertEquals(listOf("request", "request"), calls)
        assertEquals(2, attempts)
    }

    @Test
    fun rejectsEmptyAndMultipleResultsAndPropagatesErrors() = runBlocking {
        val empty = reactive<String, String, Point> { _, _, _ -> Mono.empty() }
        assertFailsWith<NoSuchElementException> {
            InterceptorInvoker(Point, listOf(empty)) { it }.invoke("request")
        }
        val multiple = reactive<String, String, Point> { _, _, _ -> Flux.just("one", "two") }
        assertFailsWith<IllegalArgumentException> {
            InterceptorInvoker(Point, listOf(multiple)) { it }.invoke("request")
        }
        val failure = IllegalStateException("rejected")
        val error = reactive<String, String, Point> { _, _, _ -> Mono.error(failure) }
        assertEquals(failure.message, assertFailsWith<IllegalStateException> {
            InterceptorInvoker(Point, listOf(error)) { it }.invoke("request")
        }.message)
        val lateError = reactive<String, String, Point> { _, _, _ ->
            Flux.concat(Mono.just("value"), Mono.error(failure))
        }
        assertEquals(failure.message, assertFailsWith<IllegalStateException> {
            InterceptorInvoker(Point, listOf(lateError)) { it }.invoke("request")
        }.message)
    }

    /**
     * 收到一个值不代表执行结束，仍需等待 publisher 的完成信号。
     */
    @Test
    fun singleValueDoesNotCompleteBeforeThePublisher() = runBlocking {
        val subscribed = CompletableDeferred<Unit>()
        val publisher = Sinks.many().unicast().onBackpressureBuffer<String>()
        val interceptor = reactive<String, String, Point> { _, _, _ ->
            publisher.asFlux().doOnSubscribe { subscribed.complete(Unit) }
        }
        val call = async { InterceptorInvoker(Point, listOf(interceptor)) { it }.invoke("request") }
        subscribed.await()
        assertEquals(Sinks.EmitResult.OK, publisher.tryEmitNext("value"))
        yield()
        assertFalse(call.isCompleted)
        assertEquals(Sinks.EmitResult.OK, publisher.tryEmitComplete())
        assertEquals("value", call.await())
    }

    @Test
    fun callerCancellationCancelsSubscriptionAndDownstream() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        val cancelled = CompletableDeferred<Unit>()
        val interceptor = reactive<String, String, Point> { _, c, chain ->
            Mono.from(chain.proceed(c)).doOnCancel { cancelled.complete(Unit) }
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
            cancelled.await()
        }
    }

    /**
     * 父取消仅影响返回的 publisher，独立订阅仍由订阅者负责取消。
     */
    @Test
    fun callerCancellationLeavesIndependentSubscriptionForUserToDispose() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val continueExecution = CompletableDeferred<Unit>()
        val continued = CompletableDeferred<Unit>()
        val finished = CompletableDeferred<Unit>()
        var subscription: Disposable? = null
        val interceptor = reactive<String, String, Point> { _, c, chain ->
            subscription = Mono.from(chain.proceed(c)).subscribe({}, {})
            Mono.never()
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
            entered.await()
            call.cancelAndJoin()
            assertFalse(finished.isCompleted)
            // 父调用已结束，再次执行握手后才由用户取消该订阅。
            continueExecution.complete(Unit)
            continued.await()
            assertFalse(finished.isCompleted)
            assertNotNull(subscription).dispose()
            finished.await()
        } finally {
            subscription?.dispose()
            call.cancelAndJoin()
            if (entered.isCompleted) {
                withContext(NonCancellable) { withTimeout(5_000) { finished.await() } }
            }
        }
    }

    /**
     * 明确取消已启动的订阅后，仍能继续执行；不依赖真实时间触发竞争。
     */
    @Test
    fun cancellingOneSubscriptionAllowsAnotherAttempt() = runBlocking {
        var attempts = 0
        val firstEntered = Sinks.one<String>()
        val firstFinished = CompletableDeferred<Unit>()
        val interceptor = reactive<String, String, Point> { _, c, chain ->
            Mono.from(chain.proceed(c)).takeUntilOther(firstEntered.asMono())
                .switchIfEmpty(Mono.defer { Mono.from(chain.proceed(c)) })
        }
        assertEquals("ok", withTimeout(5_000) {
            InterceptorInvoker(Point, listOf(interceptor)) {
                if (++attempts == 1) {
                    try {
                        assertEquals(Sinks.EmitResult.OK, firstEntered.tryEmitValue("entered"))
                        awaitCancellation()
                    } finally {
                        firstFinished.complete(Unit)
                    }
                }
                "ok"
            }.invoke("request")
        })
        firstFinished.await()
        assertEquals(2, attempts)
    }
}
