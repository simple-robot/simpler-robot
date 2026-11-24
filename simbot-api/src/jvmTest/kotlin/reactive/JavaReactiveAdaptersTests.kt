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

package reactive

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.test.runTest
import love.forte.simbot.application.Application
import love.forte.simbot.event.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import kotlin.test.*

/**
 * Verifies how Java reactive callbacks behave when used by the coroutine APIs.
 */
class JavaReactiveAdaptersTests {
    /**
     * A typed listener returns an invalid result for an unmatched event and the emitted result for a match.
     * Empty publishers and publishers with multiple results fail.
     */
    @Test
    fun eventListenersFilterAndAwaitASingleResult() = runTest {
        val context = mockk<EventListenerContext>()
        val result = EventResult.empty()
        val typed = JavaReactiveAdapters.typedListener(MessageEvent::class.java, result)

        every { context.context.event } returns mockk<Event>()
        assertSame(EventResult.invalid(), typed.handleWith(context))
        every { context.context.event } returns mockk<MessageEvent>()
        assertSame(result, typed.handleWith(context))

        assertFailsWith<NoSuchElementException> {
            JavaReactiveAdapters.listener(Mono.empty()).handleWith(context)
        }
        assertFailsWith<IllegalArgumentException> {
            JavaReactiveAdapters.listener(Flux.just(result, result)).handleWith(context)
        }
    }

    /**
     * A Java interceptor that returns the next handler's publisher propagates its result.
     */
    @Test
    fun listenerInterceptorDelegatesToTheChain() = runTest {
        val result = EventResult.empty()
        val context = object : EventInterceptor.Context {
            override val eventListenerContext: EventListenerContext = mockk()
            override suspend fun invoke(): EventResult = result
            override suspend fun invoke(eventListenerContext: EventListenerContext): EventResult = error("unused")
        }

        with(JavaReactiveAdapters.interceptor()) {
            assertSame(result, context.intercept())
        }
    }

    /**
     * The Java dispatch interceptor replaces the downstream context while building the chain.
     * Collecting the flow runs the downstream handler and appends the interceptor's result.
     */
    @Test
    fun dispatchInterceptorComposesTheResultStream() = runTest {
        val replacement = mockk<EventContext>()
        var builds = 0
        var collections = 0
        val context = object : EventDispatchInterceptor.Context {
            override val eventContext: EventContext = mockk()
            override fun invoke(): Flow<EventResult> = error("unused")
            override fun invoke(eventContext: EventContext): Flow<EventResult> {
                assertSame(replacement, eventContext)
                builds++
                return flow {
                    collections++
                    emit(EventResult.invalid())
                }
            }
        }

        val results = with(JavaReactiveAdapters.dispatchInterceptor(replacement)) { context.intercept() }
        assertEquals(1, builds)
        assertEquals(0, collections)
        assertEquals(listOf(EventResult.invalid(), EventResult.empty()), results.toList())
        assertEquals(1, collections)
    }

    /**
     * Each launch subscription obtains one application from the Java launcher.
     * An empty publisher causes the launch to fail.
     */
    @Test
    fun applicationLauncherRunsOnEachSubscription() = runTest {
        val application = mockk<Application>()
        var launches = 0
        val launcher = JavaReactiveAdapters.launcher {
            launches++
            Mono.just(application)
        }
        val apps = JavaReactiveAdapters.launch(launcher)

        assertEquals(0, launches)
        repeat(2) { assertSame(application, apps.awaitSingle()) }
        assertEquals(2, launches)
        assertFailsWith<NoSuchElementException> { JavaReactiveAdapters.launcher { Mono.empty() }.launch() }
    }

    /**
     * A Java stage handler suspends until its publisher completes.
     * Cancelling the suspended handler cancels the subscription.
     */
    @Test
    fun applicationStageAwaitsCompletionAndPropagatesCancellation() = runTest {
        val application = mockk<Application>()
        val completion = Sinks.empty<Void>()
        val handler = JavaReactiveAdapters.launchHandler(completion.asMono())
        val running = launch(start = CoroutineStart.UNDISPATCHED) { handler.invoke(application) }
        assertFalse(running.isCompleted)
        assertEquals(Sinks.EmitResult.OK, completion.tryEmitEmpty())
        running.join()

        var cancelled = false
        val cancellable = JavaReactiveAdapters.launchHandler(Mono.never<Void>().doOnCancel { cancelled = true })
        val pending = launch(start = CoroutineStart.UNDISPATCHED) { cancellable.invoke(application) }
        pending.cancelAndJoin()
        assertTrue(cancelled)
    }
}
