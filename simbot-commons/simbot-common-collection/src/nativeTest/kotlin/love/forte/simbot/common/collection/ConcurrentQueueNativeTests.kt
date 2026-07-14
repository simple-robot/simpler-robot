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

package love.forte.simbot.common.collection

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalSimbotCollectionApi::class)
class ConcurrentQueueNativeTests {
    @Test
    fun concurrentAddsFromMultipleWorkersShouldKeepEveryValueTest() = runTest {
        val queue = createConcurrentQueue<Int>()

        withContext(Dispatchers.Default) {
            List(WORKER_COUNT) { worker ->
                launch {
                    repeat(VALUES_PER_WORKER) { index ->
                        queue.add(worker * VALUES_PER_WORKER + index)
                    }
                }
            }.joinAll()
        }

        val values = queue.toList()
        assertEquals(WORKER_COUNT * VALUES_PER_WORKER, values.size)
        assertEquals(values.size, values.toSet().size)
        assertEquals((0 until values.size).toSet(), values.toSet())
        repeat(WORKER_COUNT) { worker ->
            val start = worker * VALUES_PER_WORKER
            val workerValues = values.filter { it / VALUES_PER_WORKER == worker }
            assertEquals((start until start + VALUES_PER_WORKER).toList(), workerValues)
        }
    }

    @Test
    fun concurrentRemovesShouldEmptyQueueAndAllowReuseTest() = runTest {
        val queue = createConcurrentQueue<Int>()
        val valueCount = WORKER_COUNT * VALUES_PER_WORKER
        repeat(valueCount, queue::add)

        withContext(Dispatchers.Default) {
            List(WORKER_COUNT) { worker ->
                launch {
                    for (value in worker until valueCount step WORKER_COUNT) {
                        queue.remove(value)
                    }
                }
            }.joinAll()
        }

        assertTrue(queue.isEmpty())
        assertEquals(0, queue.size)
        assertEquals(emptyList(), queue.toList())
        queue.add(valueCount)
        assertEquals(listOf(valueCount), queue.toList())
    }

    @Test
    fun concurrentPriorityAddsShouldKeepEveryValueAndPriorityOrderTest() = runTest {
        val queue = createPriorityConcurrentQueue<Int>()
        val valueCount = WORKER_COUNT * VALUES_PER_WORKER

        withContext(Dispatchers.Default) {
            List(WORKER_COUNT) { worker ->
                launch {
                    repeat(VALUES_PER_WORKER) { index ->
                        queue.add(index % PRIORITY_COUNT, worker * VALUES_PER_WORKER + index)
                    }
                }
            }.joinAll()
        }

        val values = queue.toList()
        assertEquals(valueCount, queue.size)
        assertEquals((0 until valueCount).toSet(), values.toSet())
        val priorities = values.map { it % VALUES_PER_WORKER % PRIORITY_COUNT }
        assertEquals(priorities.sorted(), priorities)
    }

    @Test
    fun closingBucketAndAddingShouldNeverLoseDurableValueTest() = runTest {
        repeat(BUCKET_RACE_REPETITIONS) { iteration ->
            val queue = createPriorityConcurrentQueue<Int>()
            val sentinel = iteration
            val durable = iteration + BUCKET_RACE_REPETITIONS
            queue.add(RACE_PRIORITY, sentinel)
            val start = CompletableDeferred<Unit>()

            withContext(Dispatchers.Default) {
                val removeJob = launch {
                    start.await()
                    queue.remove(RACE_PRIORITY, sentinel)
                }
                val addJob = launch {
                    start.await()
                    queue.add(RACE_PRIORITY, durable)
                }
                start.complete(Unit)
                joinAll(removeJob, addJob)
            }

            assertEquals(listOf(durable), queue.toList())
            assertEquals(1, queue.size)
        }
    }

    @Test
    fun priorityBucketsShouldRemainReusableAfterConcurrentChurnTest() = runTest {
        val queue = createPriorityConcurrentQueue<Int>()

        withContext(Dispatchers.Default) {
            List(WORKER_COUNT) { worker ->
                launch {
                    repeat(VALUES_PER_WORKER) { index ->
                        val value = worker * VALUES_PER_WORKER + index
                        val priority = index % PRIORITY_COUNT
                        queue.add(priority, value)
                        queue.remove(priority, value)
                    }
                }
            }.joinAll()
        }

        val expected = (0 until PRIORITY_COUNT).map { priority -> priority to -priority - 1 }
        expected.forEach { (priority, value) -> queue.add(priority, value) }

        assertEquals(expected.map { it.second }, queue.toList())
    }

    private companion object {
        const val WORKER_COUNT = 4
        const val VALUES_PER_WORKER = 500
        const val PRIORITY_COUNT = 8
        const val BUCKET_RACE_REPETITIONS = 200
        const val RACE_PRIORITY = 1
    }
}
