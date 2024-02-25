/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import love.forte.simbot.common.collectable.collectBy
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import java.util.function.Function
import java.util.stream.Collector
import java.util.stream.Collectors
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

/**
 *
 * @author ForteScarlet
 */
class DispatcherTests {

    private val d1 = Executors.newFixedThreadPool(2) { rb ->
        Thread(rb, "D1t").apply {
            isDaemon = true
        }
    }.asCoroutineDispatcher()

    private val d2 = Executors.newFixedThreadPool(2) { rb ->
        Thread(rb, "D2t").apply {
            isDaemon = true
        }
    }.asCoroutineDispatcher()

    @Test
    fun test() = runTest {
        flow {
            println(Thread.currentThread())
            emit(1)
            val v = withContext(d2) {
                println(Thread.currentThread())
                2
            }
            println(Thread.currentThread())
            emit(v)
        }.flowOn(d1)
            .collect {
                println(Thread.currentThread())
            }

    }

    @Test
    fun flowCollectSetByTest() = runTest {
        val flow = flow {
            repeat(100) {
                emit(it)
                kotlinx.coroutines.delay(1)
            }
        }

        val set = flow.collectBy(Collectors.toSet())
        assertEquals(100, set.size)
        repeat(100) {
            assertContains(set, it)
        }
    }

    @Test
    fun flowCollectSetByWithScopeTest() = runTest {
        coroutineScope {
            val flow = flow {
                repeat(100) {
                    emit(it)
                    kotlinx.coroutines.delay(1)
                }
            }

            val concurrentMap =
                flow.collectBy(scope = this, collector = Collectors.toConcurrentMap(Function.identity(), Function.identity()))
            val keySet = concurrentMap.keys
            assertEquals(100, keySet.size)
            repeat(100) {
                assertContains(keySet, it)
            }
        }
    }

    @Test
    fun flowCollectListByTest() = runTest {
        val flow = flow {
            repeat(100) {
                emit(it)
                kotlinx.coroutines.delay(1)
            }
        }

        val list = flow.collectBy(Collectors.toList())
        assertEquals(100, list.size)
        repeat(100) {
            assertEquals(it, list[it])
        }
    }

    @Test
    fun flowCollectListByWithScopeTest() = runTest {
        coroutineScope {
            val flow = flow {
                repeat(100) {
                    emit(it)
                    kotlinx.coroutines.delay(1)
                }
            }

            val concurrentList =
                flow.collectBy(
                    scope = this,
                    collector = Collector.of(
                        { CopyOnWriteArrayList<Int>() },
                        { l, v -> l.add(v) },
                        { r1, r2 ->
                            r1.addAll(r2)
                            r1
                        },
                        Collector.Characteristics.IDENTITY_FINISH,
                        Collector.Characteristics.CONCURRENT
                    )
                )

            assertEquals(100, concurrentList.size)
            repeat(100) {
                assertEquals(it, concurrentList[it])
            }
        }
    }

}
