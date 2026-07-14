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
@OptIn(ExperimentalSimbotCollectionApi::class)
class PriorityConcurrentQueueTests {

    @Test
    fun priorityConcurrentQueueIteratorTest_3_priority() {
        val queue = createPriorityConcurrentQueue<String>()

        val priorities = listOf(5, 10, 1)

        priorities.forEach { priority ->
            for (c in 'A'..'C') {
                queue.add(priority, "$priority-$c")
            }
        }

        val joinStr = queue.joinToString(", ")
        assertEquals("1-A, 1-B, 1-C, 5-A, 5-B, 5-C, 10-A, 10-B, 10-C", joinStr)

        queue.add(0, "0-A")

        val joinStr2 = queue.joinToString(", ")
        assertEquals("0-A, 1-A, 1-B, 1-C, 5-A, 5-B, 5-C, 10-A, 10-B, 10-C", joinStr2)
    }

    @Test
    fun concurrentAddsThenRemoveIfShouldKeepOnlyOtherPriorityTest() = runTest {
        val queue = createPriorityConcurrentQueue<String>()
        val jobs = List(100) { index ->
            launch {
                val priority = index % 2 + 1
                queue.add(priority, "$priority:$index")
            }
        }

        jobs.joinAll()
        queue.removeIf(1) { true }

        val values = queue.toList()
        assertEquals(50, values.size)
        assertTrue(values.all { it.startsWith("2:") })
        assertTrue(queue.isEmpty(1))
        assertFalse(queue.isEmpty(2))
    }


    @Test
    fun priorityConcurrentQueueRemoveNoMatchReturnsTest() {
        val queue = createPriorityConcurrentQueue<String>()
        queue.add(1, "A")
        queue.add(2, "B")

        queue.remove("C")
        queue.removeIf { it == "C" }

        assertEquals("A, B", queue.joinToString(", "))
        assertEquals(2, queue.size)
    }

    @Test
    fun priorityConcurrentQueueRemoveShouldAffectFirstMatchByPriorityTest() {
        val queue = createPriorityConcurrentQueue<String>()
        queue.add(3, "3-A")
        queue.add(1, "A")
        queue.add(2, "A")

        queue.remove("A")

        assertEquals("A, 3-A", queue.joinToString(", "))
        assertEquals(2, queue.size)
    }

    @Test
    fun removeAtPriorityShouldAffectOnlyFirstDuplicateTest() {
        val queue = createPriorityConcurrentQueue<String>()
        queue.add(1, "A")
        queue.add(1, "A")
        queue.add(1, "B")
        queue.add(2, "A")

        queue.remove(1, "A")

        assertEquals(listOf("A", "B", "A"), queue.toList())
        assertEquals(3, queue.size)
    }

    @Test
    fun priorityConcurrentQueueRemoveIfShouldAffectOnlySpecifiedPriorityTest() {
        val queue = createPriorityConcurrentQueue<String>()
        queue.add(1, "A")
        queue.add(2, "A")
        queue.add(2, "B")
        queue.add(3, "A")

        queue.removeIf(2) { it == "A" }

        assertEquals("A, B, A", queue.joinToString(", "))
        assertEquals(3, queue.size)
        assertFalse(queue.isEmpty(1))
        assertFalse(queue.isEmpty(2))
        assertFalse(queue.isEmpty(3))
    }

    @Test
    fun globalRemoveIfShouldRemoveEveryMatchAndKeepBucketFifoTest() {
        val queue = createPriorityConcurrentQueue<String>()
        queue.add(1, "remove")
        queue.add(1, "1-A")
        queue.add(2, "remove")
        queue.add(2, "2-A")
        queue.add(2, "remove")
        queue.add(3, "remove")

        queue.removeIf { it == "remove" }

        assertEquals(listOf("1-A", "2-A"), queue.toList())
        assertEquals(2, queue.size)
        assertTrue(queue.isEmpty(3))

        queue.add(3, "3-A")
        assertEquals(listOf("1-A", "2-A", "3-A"), queue.toList())
    }

    @Test
    fun removeIfAtPriorityShouldCloseAndRecreateEmptyBucketTest() {
        val queue = createPriorityConcurrentQueue<String>()
        queue.add(1, "keep")
        queue.add(2, "A")
        queue.add(2, "B")

        queue.removeIf(2) { true }
        assertTrue(queue.isEmpty(2))
        assertEquals(listOf("keep"), queue.toList())

        queue.add(2, "C")
        assertEquals(listOf("keep", "C"), queue.toList())
    }

    @Test
    fun removeIfPredicateFailureShouldKeepPriorityQueueUsableTest() {
        val queue = createPriorityConcurrentQueue<Int>()
        repeat(5) { queue.add(1, it) }

        assertFailsWith<IllegalStateException> {
            queue.removeIf(1) {
                if (it == 3) error("predicate failure")
                it < 2
            }
        }

        assertEquals(listOf(2, 3, 4), queue.toList())
        queue.add(1, 5)
        queue.remove(1, 3)
        assertEquals(listOf(2, 4, 5), queue.toList())
    }

    @Test
    fun priorityConcurrentQueueBucketShouldBeReusableAfterEmptyTest() {
        val queue = createPriorityConcurrentQueue<String>()
        queue.add(10, "10-A")
        queue.add(5, "5-A")

        queue.remove(5, "5-A")
        assertTrue(queue.isEmpty(5))
        assertEquals("10-A", queue.joinToString(", "))

        queue.add(5, "5-B")
        assertFalse(queue.isEmpty(5))
        assertEquals("5-B, 10-A", queue.joinToString(", "))
    }

    @Test
    fun priorityExtremesShouldSortWithoutOverflowTest() {
        val queue = createPriorityConcurrentQueue<String>()
        queue.add(Int.MAX_VALUE, "max")
        queue.add(0, "zero-A")
        queue.add(Int.MIN_VALUE, "min")
        queue.add(0, "zero-B")

        assertEquals(listOf("min", "zero-A", "zero-B", "max"), queue.toList())
        assertFalse(queue.isEmpty(Int.MIN_VALUE))
        assertTrue(queue.isEmpty(1))
    }

    @Test
    fun priorityConcurrentQueueIteratorShouldSeeTailAddInCurrentBucketTest() {
        val queue = createPriorityConcurrentQueue<String>()
        queue.add(1, "1-A")
        queue.add(1, "1-B")
        queue.add(2, "2-A")

        val iterator = queue.iterator()
        assertTrue(iterator.hasNext())
        assertEquals("1-A", iterator.next())

        queue.add(1, "1-C")

        assertTrue(iterator.hasNext())
        assertEquals("1-B", iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals("1-C", iterator.next())
        assertTrue(iterator.hasNext())
        assertEquals("2-A", iterator.next())
        assertFalse(iterator.hasNext())
    }

    @Test
    fun priorityConcurrentQueueClearShouldAllowReuseAndKeepOldIteratorSafeTest() {
        val queue = createPriorityConcurrentQueue<String>()
        queue.add(1, "1-A")
        queue.add(2, "2-A")

        val iterator = queue.iterator()
        assertTrue(iterator.hasNext())
        iterator.next()

        queue.clear()
        assertEquals(0, queue.size)
        assertTrue(queue.isEmpty())

        queue.add(1, "1-B")
        assertEquals("1-B", queue.joinToString(", "))

        while (iterator.hasNext()) {
            iterator.next()
        }
    }

    @Test
    fun priorityConcurrentQueueInterleavedReadAndWriteShouldKeepPriorityOrderTest() = runTest {
        val queue = createPriorityConcurrentQueue<String>()
        val jobs = mutableListOf<Job>()

        for (priority in 1..3) {
            jobs += launch {
                repeat(40) { index ->
                    queue.add(priority, "$priority:$index")
                    if (index % 10 == 0) {
                        yield()
                    }
                }
            }
        }

        repeat(3) {
            jobs += launch {
                repeat(20) {
                    queue.iterator().forEach { _ -> }
                    yield()
                }
            }
        }

        jobs.joinAll()

        val values = queue.toList()
        val priorities = values.map { it.first().code - '0'.code }
        assertEquals(120, values.size)
        assertEquals(120, values.toSet().size)
        assertEquals(priorities.sorted(), priorities)
    }

    @OptIn(ExperimentalSimbotCollectionApi::class)
    @Test
    fun priorityConcurrentQueueIteratorTest_iterator_next() {
        val queue = createPriorityConcurrentQueue<String>()
        val iterator = queue.iterator()
        assertFalse(iterator.hasNext())
        queue.add(1, "A")
        assertFalse(iterator.hasNext())
        val newIterator = queue.iterator()
        assertTrue(newIterator.hasNext())
        newIterator.next()
        assertFalse(newIterator.hasNext())
        assertFailsWith<NoSuchElementException> { newIterator.next() }
        assertFailsWith<NoSuchElementException> { iterator.next() }
    }

}
