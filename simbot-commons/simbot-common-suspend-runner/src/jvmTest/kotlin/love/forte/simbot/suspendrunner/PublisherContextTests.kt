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

@file:OptIn(love.forte.simbot.annotations.InternalSimbotAPI::class)

package love.forte.simbot.suspendrunner

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.test.runTest
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import reactor.core.publisher.Flux
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.*

/**
 * Verifies execution environments and subscription ownership of generated Publishers.
 */
class PublisherContextTests {
    /**
     * Explicit environments survive suspension without inheriting their supplied Job.
     */
    @Test
    fun explicitContextPreservesExecutionEnvironment() = runTest {
        val local = ThreadLocal<String>()
        val sourceJob = currentCoroutineContext()[Job]
        val context = currentCoroutineContext() + Dispatchers.Unconfined +
            CoroutineName("requested") + local.asContextElement("requested")
        val publisher = asPublisher(context) {
            assertEquals("requested", currentCoroutineContext()[CoroutineName]?.name)
            assertEquals("requested", local.get())
            assertSame(Dispatchers.Unconfined, currentCoroutineContext()[ContinuationInterceptor])
            assertNotSame(sourceJob, currentCoroutineContext()[Job])
            yield()
            assertEquals("requested", local.get())
            "value"
        }

        withContext(CoroutineName("caller") + local.asContextElement("caller")) {
            assertEquals("value", publisher.asFlow().single())
            assertEquals("caller", local.get())
        }
        assertNull(local.get())
        assertTrue(sourceJob!!.isActive)
    }

    /**
     * The compiler bridge forwards its scope environment to each subscription.
     */
    @Test
    fun compilerBridgePreservesScopeEnvironment() = runTest {
        val local = ThreadLocal<String>()
        val scopeContext = currentCoroutineContext() + Dispatchers.Unconfined +
            CoroutineName("scope") + local.asContextElement("scope")
        val scope = object : CoroutineScope {
            /**
             * Uses the caller Job to detect accidental subscription parenting.
             */
            override val coroutineContext = scopeContext
        }
        val publisher = compilerPublisher(scope) {
            assertEquals("scope", currentCoroutineContext()[CoroutineName]?.name)
            assertEquals("scope", local.get())
            assertNotSame(scope.coroutineContext[Job], currentCoroutineContext()[Job])
            yield()
            assertEquals("scope", local.get())
            "value"
        }

        assertEquals("value", publisher.asFlow().single())
        assertNull(local.get())
        assertTrue(scope.coroutineContext[Job]!!.isActive)
    }

    /**
     * A null context retains publish's defaults instead of inheriting the collector environment.
     */
    @Test
    fun nullContextUsesPublisherDefaults() = runTest {
        val publisher = asPublisher(context = null) {
            assertNull(currentCoroutineContext()[CoroutineName])
            assertSame(Dispatchers.Default, currentCoroutineContext()[ContinuationInterceptor])
            "value"
        }

        withContext(CoroutineName("collector")) {
            assertEquals("value", publisher.asFlow().single())
        }
    }

    /**
     * Null operation results still complete without a Reactive Streams element.
     */
    @Test
    fun nullResultCompletesWithoutElements() = runTest {
        assertEquals(emptyList(), asPublisher<String?> { null }.asFlow().toList())
    }

    /**
     * Cancelling a subscription stops its operation without cancelling the supplied context Job.
     */
    @Test
    fun subscriptionCancellationStopsOnlyItsOperation() = runTest {
        val sourceJob = currentCoroutineContext()[Job]!!
        val started = CompletableDeferred<Job>()
        val subscription = CompletableDeferred<Subscription>()
        val publisher = asPublisher(currentCoroutineContext() + Dispatchers.Unconfined) {
            started.complete(currentCoroutineContext()[Job]!!)
            awaitCancellation()
        }
        publisher.subscribe(object : Subscriber<Nothing> {
            /**
             * Retains the subscription so cancellation follows the public Reactive Streams API.
             */
            override fun onSubscribe(value: Subscription) {
                subscription.complete(value)
                value.request(1)
            }

            /**
             * Rejects values from the intentionally suspended operation.
             */
            override fun onNext(value: Nothing) = error("The operation must remain suspended")

            /**
             * Fails on errors instead of allowing an uncaught asynchronous failure to pass unnoticed.
             */
            override fun onError(error: Throwable) = throw AssertionError("Unexpected terminal error", error)

            /**
             * Rejects normal completion before subscription cancellation.
             */
            override fun onComplete() = error("The operation must remain suspended")
        })

        val operationJob = started.await()
        subscription.await().cancel()
        operationJob.join()

        assertTrue(operationJob.isCancelled)
        assertTrue(sourceJob.isActive)
    }

    /**
     * Business failures can retry with a fresh coroutine and the same execution environment.
     */
    @Test
    fun retryPreservesEnvironmentWithoutFailingSourceJob() = runTest {
        val local = ThreadLocal<String>()
        val sourceJob = currentCoroutineContext()[Job]!!
        var attempts = 0
        val publisher = asPublisher(
            currentCoroutineContext() + Dispatchers.Unconfined +
                CoroutineName("retry") + local.asContextElement("retry")
        ) {
            assertEquals("retry", currentCoroutineContext()[CoroutineName]?.name)
            assertEquals("retry", local.get())
            if (++attempts == 1) {
                error("retryable")
            }
            "recovered"
        }

        assertEquals("recovered", Flux.from(publisher).retry(1).asFlow().single())
        assertEquals(2, attempts)
        assertTrue(sourceJob.isActive)
        assertNull(local.get())
    }

    /**
     * Calls the hidden compiler entry point without enabling compiler transforms in this runtime module.
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> compilerPublisher(
        scope: CoroutineScope,
        block: suspend () -> T
    ): Publisher<T> = Class.forName("love.forte.simbot.suspendrunner.BlockingRunnerKt")
        .getDeclaredMethod("\$\$asPublisher", Function1::class.java, CoroutineScope::class.java)
        .invoke(null, block, scope) as Publisher<T>
}
