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

import kotlinx.coroutines.test.runTest
import love.forte.simbot.interceptor.*
import kotlin.test.Test
import kotlin.test.assertEquals

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

/**
 *
 * @author Forte Scarlet
 */
class InterceptorTests {
    @Test
    fun passThroughInterceptorsInOrder() = runTest {
        val set = SimpleInterceptorSet()
        val events = mutableListOf<String>()

        set.add(
            SimpleInterceptor {
                events += "1-before"
                it.invoke().also { result ->
                    events += "1-after:$result"
                }
            }
        )
        set.add(
            SimpleInterceptor {
                events += "2-before"
                it.invoke().also { result ->
                    events += "2-after:$result"
                }
            }
        )
        set.add(
            SimpleInterceptor {
                events += "3-before"
                it.invoke().also { result ->
                    events += "3-after:$result"
                }
            }
        )

        val result = set.intercept<SimpleInterceptor, _, _> { interceptors ->
            SimpleInterceptorContext(interceptors.iterator()) {
                events += "id"
                "Id, Ego, Superego"
            }
        }

        assertEquals("Id, Ego, Superego", result)
        assertEquals(
            listOf(
                "1-before",
                "2-before",
                "3-before",
                "id",
                "3-after:Id, Ego, Superego",
                "2-after:Id, Ego, Superego",
                "1-after:Id, Ego, Superego",
            ),
            events
        )
    }

    @Test
    fun interceptorCanShortCircuit() = runTest {
        val set = SimpleInterceptorSet()
        val events = mutableListOf<String>()

        set.add(
            SimpleInterceptor {
                events += "1-before"
                it.invoke().also { result ->
                    events += "1-after:$result"
                }
            }
        )
        set.add(
            SimpleInterceptor {
                events += "2-before"
                it.invoke().also { result ->
                    events += "2-after:$result"
                }
            }
        )
        set.add(
            SimpleInterceptor {
                events += "3-short-circuit"
                "Intercepted"
            }
        )
        set.add(
            SimpleInterceptor {
                events += "4-unreachable"
                it.invoke()
            }
        )

        val result = set.intercept<SimpleInterceptor, _, _> { interceptors ->
            SimpleInterceptorContext(interceptors.iterator()) {
                events += "id"
                "Id, Ego, Superego"
            }
        }

        assertEquals("Intercepted", result)
        assertEquals(
            listOf(
                "1-before",
                "2-before",
                "3-short-circuit",
                "2-after:Intercepted",
                "1-after:Intercepted",
            ),
            events
        )
    }

    @Test
    fun emptyInterceptorSetInvokesIdDirectly() = runTest {
        val set = SimpleInterceptorSet()
        val events = mutableListOf<String>()

        val result = set.intercept<SimpleInterceptor, _, _> { interceptors ->
            SimpleInterceptorContext(interceptors.iterator()) {
                events += "id"
                "Id only"
            }
        }

        assertEquals("Id only", result)
        assertEquals(listOf("id"), events)
    }

    @Test
    fun consumedContextInvokesIdDirectlyOnFollowingInvoke() = runTest {
        val events = mutableListOf<String>()
        val context = SimpleInterceptorContext(
            listOf(
                SimpleInterceptor {
                    events += "1-before"
                    it.invoke().also { result ->
                        events += "1-after:$result"
                    }
                }
            ).iterator()
        ) {
            events += "id"
            "Id"
        }

        assertEquals("Id", context.invoke())
        assertEquals("Id", context.invoke())
        assertEquals(
            listOf(
                "1-before",
                "id",
                "1-after:Id",
                "id",
            ),
            events
        )
    }
}


private fun interface SimpleInterceptor : Interceptor<SimpleInterceptorContext, String> {
    override suspend fun intercept(context: SimpleInterceptorContext): String
}

private class SimpleInterceptorContext(
    override val interceptors: Iterator<SimpleInterceptor>,
    private val id: suspend () -> String
) : IteratorAggregationInterceptorContext<SimpleInterceptor, String>() {
    override suspend fun invokeId(): String = id()
    override suspend fun doIntercept(interceptor: SimpleInterceptor): String = interceptor.intercept(this)
}
