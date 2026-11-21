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

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.attribute.Attribute
import love.forte.simbot.common.attribute.MutableAttributeMap
import love.forte.simbot.common.attribute.mutableAttributeMapOf
import love.forte.simbot.common.attribute.set
import love.forte.simbot.interception.InterceptionPoint
import love.forte.simbot.interception.Interceptor
import love.forte.simbot.interception.InterceptorInvoker
import kotlin.test.*

/**
 * 验证执行器的固定后继、按需读取及调用生命周期。
 */
class InterceptorInvokerTests {
    private class Context(val value: Int)

    private object Point : InterceptionPoint<Context, Int>

    @Test
    fun repeatedProceedReexecutesTheEntireSuffixWithItsOwnContext() = runTest {
        val calls = mutableListOf<String>()
        val chain = mutableListOf<Interceptor<Context, Int, Point>>(
            Interceptor { _, context, chain ->
                chain.proceed(context) + chain.proceed(Context(context.value + 1))
            },
            Interceptor { _, context, chain ->
                calls += "B${context.value}"
                chain.proceed(context)
            },
            Interceptor { _, context, chain ->
                calls += "C${context.value}"
                chain.proceed(context)
            }
        )
        val invoker = InterceptorInvoker(Point, chain) { context ->
            calls += "target${context.value}"
            context.value
        }
        assertEquals(3, invoker.invoke(Context(1)))
        assertEquals(listOf("B1", "C1", "target1", "B2", "C2", "target2"), calls)
        assertFailsWith<IllegalStateException> { invoker.invoke(Context(1)) }
    }

    @Test
    fun downstreamInterceptorCanAlsoRepeatProceed() = runTest {
        var targetCalls = 0
        val chain = listOf<Interceptor<Context, Int, Point>>(
            Interceptor { _, c, chain -> chain.proceed(c) + chain.proceed(c) },
            Interceptor { _, c, chain -> chain.proceed(c) + chain.proceed(c) },
            Interceptor { _, c, chain -> chain.proceed(c) }
        )
        val invoker = InterceptorInvoker(Point, chain) { ++targetCalls }
        assertEquals(10, invoker.invoke(Context(0)))
        assertEquals(4, targetCalls)
    }

    @Test
    fun attributesAreSharedAcrossLayersAndRepeatedProceedDespiteContextReplacement() = runTest {
        val key = Attribute.of<Int>("test.attempt")
        val attributes = mutableAttributeMapOf()
        val chain = listOf<Interceptor<Context, Int, Point>>(
            Interceptor { _, c, next ->
                assertSame(attributes, next.attributes)
                next.attributes[key] = 0
                next.proceed(c) + next.proceed(Context(2))
            },
            Interceptor { _, c, next ->
                assertSame(attributes, next.attributes)
                next.attributes[key] = next.attributes[key]!! + 1
                next.proceed(c)
            }
        )
        assertEquals(3, InterceptorInvoker(Point, chain, attributes) { it.value }.invoke(Context(1)))
        assertEquals(2, attributes[key])
    }

    @Test
    fun separateAndNestedInvocationsGetIndependentAttributes() = runTest {
        val maps = mutableListOf<MutableAttributeMap>()
        val inner = Interceptor<Context, Int, Point> { _, c, next ->
            maps += next.attributes
            next.proceed(c)
        }
        val outer = Interceptor<Context, Int, Point> { _, c, next ->
            maps += next.attributes
            InterceptorInvoker(Point, listOf(inner)) { it.value }.invoke(c)
            next.proceed(c)
        }
        repeat(2) { InterceptorInvoker(Point, listOf(outer)) { it.value }.invoke(Context(1)) }
        assertEquals(4, maps.size)
        for (i in maps.indices) for (j in 0 until i) assertNotSame(maps[i], maps[j])
    }

    /**
     * 集合入口也不能预先扫描未执行的后续链，短路时只读取当前拦截器。
     */
    @Test
    fun collectionIsNotCopiedOrReadBeforeInvocation() = runTest {
        var reads = 0
        var checks = 0
        val source = object : AbstractCollection<Interceptor<Context, Int, Point>>() {
            override val size: Int get() = error("Should not inspect collection size")

            override fun iterator(): Iterator<Interceptor<Context, Int, Point>> =
                object : Iterator<Interceptor<Context, Int, Point>> {
                    override fun hasNext(): Boolean {
                        checks++
                        return true
                    }

                    override fun next(): Interceptor<Context, Int, Point> {
                        check(++reads == 1) { "Should not read past a short-circuiting interceptor" }
                        return Interceptor { _, _, _ -> 42 }
                    }
                }
        }
        val invoker = InterceptorInvoker(Point, source) { error("Should not reach target") }
        assertEquals(0, reads)
        assertEquals(0, checks)
        assertEquals(42, invoker.invoke(Context(1)))
        assertEquals(1, reads)
        assertEquals(1, checks)
    }

    /**
     * 已耗尽的后继不会因来源随后新增元素而改变。
     */
    @Test
    fun exhaustedSuccessorRemainsTheTargetOnRepeatedProceed() = runTest {
        var lateCalls = 0
        var targetCalls = 0
        var checks = 0
        val source = mutableListOf<Interceptor<Context, Int, Point>>(
            Interceptor { _, c, chain -> chain.proceed(c) + chain.proceed(c) }
        )
        val iterator = object : Iterator<Interceptor<Context, Int, Point>> {
            private var index = 0

            override fun hasNext(): Boolean {
                checks++
                return index < source.size
            }

            override fun next(): Interceptor<Context, Int, Point> = source[index++]
        }
        val invoker = InterceptorInvoker(Point, iterator) {
            if (++targetCalls == 1) {
                source += Interceptor { _, c, chain -> lateCalls++; chain.proceed(c) }
            }
            1
        }
        assertEquals(2, invoker.invoke(Context(0)))
        assertEquals(2, targetCalls)
        assertEquals(0, lateCalls)
        assertEquals(2, checks)
    }

    /**
     * 后继读取失败也固定在当前位置，不能在重试时跳过失败元素。
     */
    @Test
    fun iteratorFailureIsCachedAcrossRepeatedProceed() = runTest {
        for (failDuringHasNext in listOf(false, true)) {
            val failure = IllegalStateException("Failed to read successor")
            var reads = 0
            var checks = 0
            val iterator = object : Iterator<Interceptor<Context, Int, Point>> {
                override fun hasNext(): Boolean {
                    if (++checks == 2 && failDuringHasNext) throw failure
                    return true
                }

                override fun next(): Interceptor<Context, Int, Point> {
                    if (++reads != 1) throw failure
                    return Interceptor { _, c, chain ->
                        repeat(2) {
                            assertSame(failure, assertFailsWith<IllegalStateException> { chain.proceed(c) })
                        }
                        42
                    }
                }
            }
            val invoker = InterceptorInvoker(Point, iterator) { error("Should not reach target") }
            assertEquals(42, invoker.invoke(Context(0)))
            assertEquals(2, checks)
            assertEquals(if (failDuringHasNext) 1 else 2, reads)
        }
    }

    /**
     * 用户逻辑失败不能缓存执行结果；重试仍需重新执行同一段后继。
     */
    @Test
    fun downstreamFailureCanBeRetriedWithoutChangingTheSuffix() = runTest {
        val failure = IllegalArgumentException("First attempt failed")
        val calls = mutableListOf<String>()
        val interceptors = listOf<Interceptor<Context, Int, Point>>(
            Interceptor { _, context, chain ->
                assertSame(failure, assertFailsWith<IllegalArgumentException> { chain.proceed(context) })
                chain.proceed(Context(2))
            },
            Interceptor { _, context, chain ->
                calls += "interceptor${context.value}"
                chain.proceed(context)
            }
        )
        val invoker = InterceptorInvoker(Point, interceptors) { context ->
            calls += "target${context.value}"
            if (context.value == 1) throw failure
            context.value
        }

        assertEquals(2, invoker.invoke(Context(1)))
        assertEquals(listOf("interceptor1", "target1", "interceptor2", "target2"), calls)
    }

    /**
     * 拦截器及目标抛出的异常或取消应原样传播，失败也不能重新使用执行器。
     */
    @Test
    fun failuresAndCancellationPropagateAndConsumeTheInvoker() = runTest {
        val failures = listOf(IllegalArgumentException("Operation failed"), CancellationException("Cancelled"))
        for (failure in failures) {
            for (failInInterceptor in listOf(false, true)) {
                var targetCalls = 0
                val interceptors = listOf(Interceptor<Context, Int, Point> { point, context, chain ->
                    assertSame(Point, point)
                    if (failInInterceptor) throw failure
                    chain.proceed(context)
                })
                val invoker = InterceptorInvoker(Point, interceptors) {
                    targetCalls++
                    throw failure
                }

                assertSame(failure, assertFails { invoker.invoke(Context(1)) })
                assertFailsWith<IllegalStateException> { invoker.invoke(Context(1)) }
                assertEquals(if (failInInterceptor) 0 else 1, targetCalls)
            }
        }
    }

    /**
     * 可空结果既可以由最终目标返回，也可以由拦截器短路产生。
     */
    @Test
    fun nullableResultsArePreservedForTargetAndShortCircuit() = runTest {
        val point = object : InterceptionPoint<Context, Int?> {}
        var targetCalls = 0
        val passthrough = Interceptor<Context, Int?, InterceptionPoint<Context, Int?>> { _, context, chain ->
            chain.proceed(context)
        }
        val targetInvoker = InterceptorInvoker(point, listOf(passthrough)) {
            targetCalls++
            null
        }
        assertNull(targetInvoker.invoke(Context(1)))
        assertEquals(1, targetCalls)

        val shortCircuit = Interceptor<Context, Int?, InterceptionPoint<Context, Int?>> { _, _, _ -> null }
        val shortCircuitInvoker = InterceptorInvoker(point, listOf(shortCircuit)) {
            error("Should not reach target")
        }
        assertNull(shortCircuitInvoker.invoke(Context(1)))
    }

    /**
     * 空链同样按需读取，并保留执行器只能调用一次的约束。
     */
    @Test
    fun emptyIteratorIsLazyAndInvokerRemainsSingleUse() = runTest {
        var checks = 0
        var calls = 0
        val iterator = object : Iterator<Interceptor<Context, Int, Point>> {
            override fun hasNext(): Boolean {
                checks++
                return false
            }

            override fun next(): Interceptor<Context, Int, Point> = error("Empty iterator")
        }
        val invoker = InterceptorInvoker(Point, iterator) { ++calls }
        assertEquals(0, checks)
        assertEquals(1, invoker.invoke(Context(0)))
        assertFailsWith<IllegalStateException> { invoker.invoke(Context(0)) }
        assertEquals(1, checks)
        assertEquals(1, calls)
    }
}
