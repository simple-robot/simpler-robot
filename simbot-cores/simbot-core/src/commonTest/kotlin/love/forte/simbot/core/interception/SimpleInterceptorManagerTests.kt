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
package love.forte.simbot.core.interception

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import love.forte.simbot.interception.InterceptionPoint
import love.forte.simbot.interception.Interceptor
import love.forte.simbot.interception.InterceptorInvoker
import love.forte.simbot.interception.InterceptorRegistration
import kotlin.test.*

class SimpleInterceptorManagerTests {
    private class Context
    private object Point : InterceptionPoint<Context, Int>
    private object OtherPoint : InterceptionPoint<String, String>

    /**
     * 同类型的等值拦截点用于验证查询按值匹配，而非按对象身份匹配。
     */
    private data class NamedPoint(val name: String) : InterceptionPoint<Context, Int>

    /**
     * 未注册的拦截点不应产生查询结果。
     */
    @Test
    fun emptyManagerHasNoRegistrations() {
        val manager = SimpleInterceptorManager()
        assertFalse(manager.hasInterceptors(Point))
        assertTrue(manager.interceptors(Point).none())
        assertTrue(manager.interceptors.none())
        assertNull(manager.findById("missing", Point))
    }

    /**
     * 带类型的 ID 查询只接受匹配的拦截点，并过滤已注销的注册。
     */
    @Test
    fun typedLookupMatchesEquivalentPointsAndRejectsMismatches() {
        val manager = SimpleInterceptorManager()
        val point = NamedPoint("message")
        val equivalent = NamedPoint("message")
        val registration = manager.register(point) { _, context, chain -> chain.proceed(context) }

        assertNotSame(point, equivalent)
        assertSame(registration, manager.findById(registration.id, point))
        assertSame(registration, manager.findById(registration.id, equivalent))
        assertSame(registration, manager.interceptors(equivalent).single())
        assertTrue(manager.hasInterceptors(equivalent))
        assertNull(manager.findById(registration.id, NamedPoint("other")))
        assertNull(manager.findById(registration.id, OtherPoint))

        registration.dispose()
        assertNull(manager.findById(registration.id, equivalent))
        assertFalse(manager.hasInterceptors(equivalent))
        assertTrue(manager.interceptors(equivalent).none())
    }

    @Test
    fun registrationMetadataAndQueriesReferToTheSameObject() {
        val manager = SimpleInterceptorManager()
        val registration = manager.register(Point, 42) { _, c, chain -> chain.proceed(c) }
        assertTrue(registration.id.isNotBlank())
        assertSame(Point, registration.point)
        assertEquals(42, registration.priority)
        assertFalse(registration.isDisposed)
        assertSame(registration, manager.findById(registration.id))
        assertSame(registration, manager.interceptors(Point).single())
        assertSame(registration, manager.interceptors.single())
        assertTrue(manager.hasInterceptors(Point))
    }

    @Test
    fun disposalInvalidatesSavedExecutionViewsAndRemovesRegistration() = runTest {
        val manager = SimpleInterceptorManager()
        var calls = 0
        val registration = manager.register(Point) { _, c, chain ->
            calls++
            chain.proceed(c)
        }
        val chain = manager.interceptors(Point).map { it.interceptor }.toList()
        val saved = manager.interceptors.single()
        assertSame(registration.interceptor, saved.interceptor)
        registration.dispose()
        registration.dispose()
        assertTrue(registration.isDisposed)
        assertNull(manager.findById(registration.id))
        assertFalse(manager.disposeById(registration.id))
        assertFalse(manager.hasInterceptors(Point))
        assertTrue(manager.interceptors(Point).none())
        assertTrue(manager.interceptors.none())
        assertEquals(1, InterceptorInvoker(Point, chain) { 1 }.invoke(Context()))
        assertEquals(0, calls)
    }

    @Test
    fun disposalByIdAlsoInvalidatesPreviouslySavedExecutionViews() = runTest {
        val manager = SimpleInterceptorManager()
        var calls = 0
        val registration = manager.register(Point) { _, c, chain -> calls++; chain.proceed(c) }
        val savedInterceptor = registration.interceptor
        assertTrue(manager.disposeById(registration.id))
        assertTrue(registration.isDisposed)
        assertFalse(manager.disposeById(registration.id))
        registration.dispose()
        assertNull(manager.findById(registration.id))
        assertEquals(7, InterceptorInvoker(Point, listOf(savedInterceptor)) { 7 }.invoke(Context()))
        assertEquals(0, calls)
    }

    @Test
    fun duplicateInterceptorInstancesHaveIndependentIdsAndLifecycles() {
        val manager = SimpleInterceptorManager()
        val interceptor = Interceptor<Context, Int, Point> { _, c, chain -> chain.proceed(c) }
        val first = manager.register(Point, interceptor = interceptor)
        val second = manager.register(Point, interceptor = interceptor)
        assertNotEquals(first.id, second.id)
        assertNotSame(interceptor, first.interceptor)
        assertTrue(manager.disposeById(first.id))
        assertTrue(first.isDisposed)
        assertFalse(second.isDisposed)
        assertSame(second, manager.interceptors(Point).single())
        assertSame(second, manager.findById(second.id))
        assertEquals(1, manager.interceptors.count())
    }

    /**
     * 完整 Int 优先级范围内仍应稳定排序，全局查询不能交错不同点的注册。
     */
    @Test
    fun prioritiesAndRegistrationOrderArePreservedForEachPoint() {
        val manager = SimpleInterceptorManager()
        val middle1 = manager.register(Point, 0) { _, c, chain -> chain.proceed(c) }
        val other = manager.register(OtherPoint, 0) { _, c, chain -> chain.proceed(c) }
        val last = manager.register(Point, Int.MAX_VALUE) { _, c, chain -> chain.proceed(c) }
        val middle2 = manager.register(Point, 0) { _, c, chain -> chain.proceed(c) }
        val first = manager.register(Point, Int.MIN_VALUE) { _, c, chain -> chain.proceed(c) }
        val expected = listOf(first, middle1, middle2, last)
        assertEquals(expected, manager.interceptors(Point).toList())
        val all = manager.interceptors.toList()
        assertTrue(all == expected + other || all == listOf(other) + expected)
        assertSame(other, manager.findById(other.id))
        middle1.dispose()
        assertEquals(listOf(first, middle2, last), manager.interceptors(Point).toList())
        assertSame(other, manager.interceptors(OtherPoint).single())
    }

    @Test
    fun sequencesFilterAtIterationTimeAndIdsAreScopedToTheManager() {
        val manager = SimpleInterceptorManager()
        val another = SimpleInterceptorManager()
        val registration = manager.register(Point) { _, c, chain -> chain.proceed(c) }
        val byPoint = manager.interceptors(Point)
        val all = manager.interceptors
        assertNull(another.findById(registration.id))
        assertFalse(another.disposeById(registration.id))
        assertFalse(registration.isDisposed)
        registration.dispose()
        assertTrue(byPoint.none())
        assertTrue(all.none())
        assertNull(manager.findById("missing"))
        assertFalse(manager.disposeById("missing"))
        assertNull(manager.findById(""))
        assertFalse(manager.disposeById(""))
        val replacement = manager.register(Point) { _, c, chain -> chain.proceed(c) }
        assertNotEquals(registration.id, replacement.id)
        assertSame(replacement, byPoint.single())
        assertSame(replacement, all.single())
    }

    /**
     * 同一点的并发注册必须全部进入查询索引，且每个 ID 只对应一次注册。
     */
    @Test
    fun concurrentRegistrationsRemainQueryableAcrossPriorities() = runTest {
        val manager = SimpleInterceptorManager()
        val start = CompletableDeferred<Unit>()
        val additions = List(32) { index ->
            async(Dispatchers.Default) {
                start.await()
                manager.register(Point, index % 3) { _, context, chain -> chain.proceed(context) }
            }
        }
        start.complete(Unit)
        val registrations = additions.awaitAll()
        val visible = manager.interceptors(Point).toList()

        assertEquals(registrations.size, registrations.map { it.id }.toSet().size)
        assertEquals(registrations.toSet(), visible.toSet())
        assertEquals(registrations.size, visible.size)
        assertEquals(visible.map { it.priority }.sorted(), visible.map { it.priority })
        registrations.forEach { assertSame(it, manager.findById(it.id, Point)) }

        registrations.map { async(Dispatchers.Default) { it.dispose() } }.awaitAll()
        assertFalse(manager.hasInterceptors(Point))
        assertTrue(manager.interceptors.none())
        registrations.forEach { assertNull(manager.findById(it.id)) }
    }

    @Test
    fun concurrentDisposalByIdHasExactlyOneWinner() = runTest {
        val manager = SimpleInterceptorManager()
        val registration = manager.register(Point) { _, c, chain -> chain.proceed(c) }
        val start = CompletableDeferred<Unit>()
        val attempts = List(16) {
            async(Dispatchers.Default) { start.await(); manager.disposeById(registration.id) }
        }
        start.complete(Unit)
        assertEquals(1, attempts.awaitAll().count { it })
        assertTrue(registration.isDisposed)
        assertNull(manager.findById(registration.id))
        assertTrue(manager.interceptors.none())
        assertFalse(manager.hasInterceptors(Point))
    }

    /**
     * 覆盖最后一个点容器或仅优先级桶变空时，与同优先级和不同优先级添加的竞争。
     */
    @Test
    fun concurrentDisposalDoesNotLoseNewRegistrationsWhenBucketsBecomeEmpty() = runTest {
        val manager = SimpleInterceptorManager()
        repeat(500) { iteration ->
            val old = manager.register(Point) { _, c, chain -> chain.proceed(c) }
            val keeper = if (iteration % 2 == 0) {
                manager.register(Point, 100) { _, c, chain -> chain.proceed(c) }
            } else {
                null
            }
            val priority = if (iteration % 4 < 2) 0 else 10
            val start = CompletableDeferred<Unit>()
            val removal = async(Dispatchers.Default) { start.await(); manager.disposeById(old.id) }
            val addition = async(Dispatchers.Default) {
                start.await()
                manager.register(Point, priority) { _, c, chain -> chain.proceed(c) }
            }
            start.complete(Unit)
            assertTrue(removal.await())
            val current = addition.await()
            assertNull(manager.findById(old.id))
            assertTrue(manager.hasInterceptors(Point))
            assertSame(current, manager.findById(current.id))
            val expected = listOfNotNull(current, keeper)
            assertEquals(expected, manager.interceptors(Point).toList())
            assertEquals(expected, manager.interceptors.toList())
            keeper?.let { assertSame(it, manager.findById(it.id)) }
            current.dispose()
            keeper?.dispose()
            assertTrue(manager.interceptors.none())
        }
    }

    @Test
    fun disposalDoesNotInterruptAnAlreadyEnteredInterceptor() = runTest {
        val manager = SimpleInterceptorManager()
        val entered = CompletableDeferred<Unit>()
        val proceed = CompletableDeferred<Unit>()
        val registration = manager.register(Point) { _, c, chain ->
            entered.complete(Unit)
            proceed.await()
            chain.proceed(c)
        }
        val call = async { InterceptorInvoker(Point, listOf(registration.interceptor)) { 9 }.invoke(Context()) }
        entered.await()
        assertTrue(manager.disposeById(registration.id))
        proceed.complete(Unit)
        assertEquals(9, call.await())
    }

    /**
     * 未读取的注册在注销后应被查询过滤，重复继续执行应复用剩余后继。
     */
    @Test
    fun iteratorSkipsAnUnreadDisposedRegistrationAndReusesTheRemainingSuffix() = runTest {
        val manager = SimpleInterceptorManager()
        val materializedIds = mutableListOf<String>()
        lateinit var skipped: InterceptorRegistration<Context, Int, Point>
        var skippedCalls = 0
        var remainingCalls = 0
        var targetCalls = 0
        val outer = manager.register(Point) { _, context, chain ->
            assertEquals(1, materializedIds.size)
            skipped.dispose()
            chain.proceed(context) + chain.proceed(context)
        }
        skipped = manager.register(Point) { _, context, chain ->
            skippedCalls++
            chain.proceed(context)
        }
        val remaining = manager.register(Point) { _, context, chain ->
            remainingCalls++
            chain.proceed(context)
        }
        // 在 map 中计数，区分跳过尚未读取的注册与执行已缓存的失效包装器。
        val iterator = manager.interceptors(Point).map {
            materializedIds += it.id
            it.interceptor
        }.iterator()
        val invoker = InterceptorInvoker(Point, iterator) { ++targetCalls }

        assertTrue(materializedIds.isEmpty())
        assertEquals(3, invoker.invoke(Context()))
        assertEquals(listOf(outer.id, remaining.id), materializedIds)
        assertEquals(0, skippedCalls)
        assertEquals(2, remainingCalls)
        assertEquals(2, targetCalls)
    }

    /**
     * 缓存后继只固定执行位置，不应冻结其注册有效性。
     */
    @Test
    fun cachedSuccessorStillChecksDisposalBetweenRepeatedProceed() = runTest {
        val manager = SimpleInterceptorManager()
        val materializedIds = mutableListOf<String>()
        lateinit var successor: InterceptorRegistration<Context, Int, Point>
        var successorCalls = 0
        var targetCalls = 0
        val outer = manager.register(Point) { _, context, chain ->
            val first = chain.proceed(context)
            successor.dispose()
            first + chain.proceed(context)
        }
        successor = manager.register(Point) { _, context, chain ->
            successorCalls++
            chain.proceed(context)
        }
        val iterator = manager.interceptors(Point).map {
            materializedIds += it.id
            it.interceptor
        }.iterator()
        val invoker = InterceptorInvoker(Point, iterator) { ++targetCalls }

        assertEquals(3, invoker.invoke(Context()))
        assertEquals(listOf(outer.id, successor.id), materializedIds)
        assertEquals(1, successorCalls)
        assertEquals(2, targetCalls)
        assertTrue(successor.isDisposed)
    }
}
