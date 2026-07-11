/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.common.collection

import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import kotlin.test.*


/**
 *
 * @author ForteScarlet
 */
class ConcurrentQueueTests {

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun concurrentModifyTest() {
        with(createConcurrentQueue<Int>()) {
            add(1)
            add(2)
            add(3)

            val iterator = iterator()
            assertTrue(iterator.hasNext())
            val n1 = iterator.next()
            assertEquals(1, n1)
            add(4)
            add(5)
            assertTrue(iterator.hasNext())
            assertEquals(2, iterator.next())
            remove(4)
            assertEquals(3, iterator.next())
            // 下一个4有没有都有可能
            println(iterator.hasNext())
        }

        with(createConcurrentQueue<Int>()) {
            add(1)
            add(2)
            add(3)

            var times = 0
            for (i in this) {
                if (i == 1) {
                    add(4)
                    add(5)
                }
                if (i == 2) {
                    remove(4)
                }
                times++
            }
            println(times)
            // 至少3次
            assertTrue(times >= 3)
        }
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun iteratorShouldSeeTailAddBeforeReachingTailTest() {
        val queue = createConcurrentQueue<Int>()
        queue.add(1)
        queue.add(2)

        val iterator = queue.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(1, iterator.next())

        queue.add(3)

        assertTrue(iterator.hasNext())
        assertEquals(2, iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals(3, iterator.next())
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun queueSizeTest() {
        with(createConcurrentQueue<Int>()) {
            assertEquals(0, size)
            repeat(10) {
                add(it)
            }
            assertEquals(10, size)
            repeat(10) {
                add(it)
            }
            assertEquals(20, size)
            clear()
            assertEquals(0, size)
        }
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun queueShouldKeepFifoOrderAndDuplicatesTest() {
        val queue = createConcurrentQueue<Int>()
        listOf(3, 1, 3, 2).forEach(queue::add)

        assertEquals(listOf(3, 1, 3, 2), queue.toList())
        assertEquals(4, queue.size)
        assertFalse(queue.isEmpty())
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun removeAndRemoveIfShouldKeepFifoOrderTest() {
        val queue = createConcurrentQueue<Int>()
        queue.add(1)
        queue.add(2)
        queue.add(2)
        queue.add(3)
        queue.add(4)

        queue.remove(2)
        assertEquals(listOf(1, 2, 3, 4), queue.toList())

        queue.removeIf { it % 2 == 0 }
        assertEquals(listOf(1, 3), queue.toList())
        assertEquals(2, queue.size)
        assertFalse(queue.isEmpty())

        queue.remove(100)
        queue.removeIf { it > 100 }
        assertEquals(listOf(1, 3), queue.toList())
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun removeIfPredicateFailureShouldKeepQueueUsableTest() {
        val queue = createConcurrentQueue<Int>()
        repeat(5) { queue.add(it) }

        assertFailsWith<IllegalStateException> {
            queue.removeIf {
                if (it == 3) error("predicate failure")
                it < 2
            }
        }

        assertEquals(listOf(2, 3, 4), queue.toList())
        queue.add(5)
        queue.remove(3)
        assertEquals(listOf(2, 4, 5), queue.toList())
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun removingHeadTailAndLastElementShouldKeepQueueReusableTest() {
        val queue = createConcurrentQueue<Int>()
        listOf(1, 2, 3).forEach(queue::add)

        queue.remove(1)
        assertEquals(listOf(2, 3), queue.toList())
        assertEquals(2, queue.size)

        queue.remove(3)
        assertEquals(listOf(2), queue.toList())
        assertEquals(1, queue.size)

        queue.remove(2)
        assertTrue(queue.isEmpty())
        assertEquals(0, queue.size)

        queue.add(4)
        assertEquals(listOf(4), queue.toList())
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun removeIfAllAndEmptyOperationsShouldBeIdempotentTest() {
        val queue = createConcurrentQueue<Int>()

        queue.remove(1)
        queue.removeIf { true }
        queue.clear()
        queue.clear()
        assertTrue(queue.isEmpty())

        repeat(5, queue::add)
        queue.removeIf { true }
        assertTrue(queue.isEmpty())
        assertEquals(0, queue.size)
        assertFalse(queue.iterator().hasNext())

        queue.add(10)
        assertEquals(listOf(10), queue.toList())
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun iteratorNextShouldRespectEmptyAndExhaustedBoundariesTest() {
        val empty = createConcurrentQueue<Int>().iterator()
        assertFalse(empty.hasNext())
        assertFailsWith<NoSuchElementException> { empty.next() }

        val queue = createConcurrentQueue<Int>()
        queue.add(1)
        queue.add(2)
        val iterator = queue.iterator()

        assertEquals(1, iterator.next())
        assertEquals(2, iterator.next())
        assertFalse(iterator.hasNext())
        assertFailsWith<NoSuchElementException> { iterator.next() }
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun emptyIteratorShouldRemainEmptyAfterLaterAddTest() {
        val queue = createConcurrentQueue<Int>()
        val iterator = queue.iterator()

        assertFalse(iterator.hasNext())
        queue.add(1)
        assertFalse(iterator.hasNext())
        assertEquals(listOf(1), queue.toList())
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun iteratorHasNextShouldBeIdempotentTest() {
        val queue = createConcurrentQueue<Int>()
        queue.add(1)
        queue.add(2)

        val iterator = queue.iterator()
        assertTrue(iterator.hasNext())
        assertTrue(iterator.hasNext())
        assertEquals(1, iterator.next())

        queue.add(3)

        assertTrue(iterator.hasNext())
        assertTrue(iterator.hasNext())
        assertEquals(2, iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals(3, iterator.next())
        assertFalse(iterator.hasNext())
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun clearShouldAllowReuseAndKeepOldIteratorSafeTest() {
        val queue = createConcurrentQueue<Int>()
        queue.add(1)
        queue.add(2)
        queue.add(3)

        val iterator = queue.iterator()
        assertTrue(iterator.hasNext())
        assertEquals(1, iterator.next())

        queue.clear()
        assertEquals(0, queue.size)
        assertTrue(queue.isEmpty())

        queue.add(4)
        assertEquals(listOf(4), queue.toList())

        while (iterator.hasNext()) {
            iterator.next()
        }
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun interleavedCoroutineReadAndWriteShouldKeepAllAddedValuesTest() = runTest {
        val queue = createConcurrentQueue<Int>()
        val jobs = mutableListOf<Job>()

        repeat(4) { worker ->
            jobs += launch {
                repeat(50) { index ->
                    queue.add(worker * 100 + index)
                    if (index % 10 == 0) {
                        yield()
                    }
                }
            }
        }

        repeat(4) {
            jobs += launch {
                repeat(20) {
                    queue.iterator().forEach { _ -> }
                    yield()
                }
            }
        }

        jobs.joinAll()

        val values = queue.toList()
        assertEquals(200, values.size)
        assertEquals(200, values.toSet().size)
    }

}
