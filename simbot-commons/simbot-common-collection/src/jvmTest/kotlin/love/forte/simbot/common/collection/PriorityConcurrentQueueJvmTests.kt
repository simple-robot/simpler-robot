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

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalSimbotCollectionApi::class)
class PriorityConcurrentQueueJvmTests {
    @Test
    fun priorityAddAndBucketRemovalStressTest() {
        StressOptions()
            .iterations(20)
            .invocationsPerIteration(100)
            .check(PriorityAddAndBucketRemovalScenario::class)
    }

    @Test
    fun priorityAddAndBucketRemovalModelCheckingTest() {
        ModelCheckingOptions()
            .iterations(5)
            .invocationsPerIteration(100)
            .check(PriorityAddAndBucketRemovalScenario::class)
    }

    @Test
    fun priorityAddAndScopedRemoveIfStressTest() {
        StressOptions()
            .iterations(5)
            .invocationsPerIteration(50)
            .check(PriorityAddAndScopedRemoveIfScenario::class)
    }

    @Test
    fun priorityAddAndGlobalRemoveStressTest() {
        StressOptions()
            .iterations(5)
            .invocationsPerIteration(50)
            .check(PriorityAddAndGlobalRemoveScenario::class)
    }

    @Test
    fun priorityAddAndGlobalRemoveIfStressTest() {
        StressOptions()
            .iterations(5)
            .invocationsPerIteration(50)
            .check(PriorityAddAndGlobalRemoveIfScenario::class)
    }

    @Test
    fun highCardinalityBucketChurnShouldNotLoseValuesTest() {
        val queue = createPriorityConcurrentQueue<Int>()
        repeat(PRIORITY_COUNT) { queue.add(it, it) }
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)

        try {
            val addFuture = executor.submit {
                start.await()
                repeat(PRIORITY_COUNT) { queue.add(it, it + PRIORITY_COUNT) }
            }
            val removeFuture = executor.submit {
                start.await()
                repeat(PRIORITY_COUNT) { queue.remove(it, it) }
            }

            start.countDown()
            addFuture.get(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            removeFuture.get(TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        } finally {
            executor.shutdownNow()
        }

        val durableValues = (PRIORITY_COUNT until PRIORITY_COUNT * 2).toList()
        assertEquals(durableValues, queue.toList())

        queue.removeIf { true }
        assertTrue(queue.isEmpty())
        durableValues.forEachIndexed { priority, value -> queue.add(priority, value) }
        assertEquals(durableValues, queue.toList())
    }

    private companion object {
        const val PRIORITY_COUNT = 2_000
        const val TEST_TIMEOUT_SECONDS = 30L
    }
}

@OptIn(ExperimentalSimbotCollectionApi::class)
@Suppress("unused")
internal class PriorityAddAndBucketRemovalScenario {
    private val queue = createPriorityConcurrentQueue<Int>().also {
        it.add(PRIORITY, INITIAL_VALUE)
    }

    @Operation
    fun addAndContainsAddedValue(): Boolean {
        queue.add(PRIORITY, ADDED_VALUE)
        return ADDED_VALUE in queue
    }

    @Operation
    fun removeInitial() {
        queue.remove(PRIORITY, INITIAL_VALUE)
    }

    private companion object {
        const val PRIORITY = 1
        const val INITIAL_VALUE = 0
        const val ADDED_VALUE = 1
    }
}

@OptIn(ExperimentalSimbotCollectionApi::class)
@Suppress("unused")
internal class PriorityAddAndScopedRemoveIfScenario {
    private val queue = priorityQueueWithInitialValue()

    @Operation
    fun addAndContainsAddedValue(): Boolean = addAndContainsAddedValue(queue)

    @Operation
    fun removeInitial() {
        queue.removeIf(SCENARIO_PRIORITY) { it == SCENARIO_INITIAL_VALUE }
    }
}

@OptIn(ExperimentalSimbotCollectionApi::class)
@Suppress("unused")
internal class PriorityAddAndGlobalRemoveScenario {
    private val queue = priorityQueueWithInitialValue()

    @Operation
    fun addAndContainsAddedValue(): Boolean = addAndContainsAddedValue(queue)

    @Operation
    fun removeInitial() {
        queue.remove(SCENARIO_INITIAL_VALUE)
    }
}

@OptIn(ExperimentalSimbotCollectionApi::class)
@Suppress("unused")
internal class PriorityAddAndGlobalRemoveIfScenario {
    private val queue = priorityQueueWithInitialValue()

    @Operation
    fun addAndContainsAddedValue(): Boolean = addAndContainsAddedValue(queue)

    @Operation
    fun removeInitial() {
        queue.removeIf { it == SCENARIO_INITIAL_VALUE }
    }
}

@OptIn(ExperimentalSimbotCollectionApi::class)
private fun priorityQueueWithInitialValue(): PriorityConcurrentQueue<Int> =
    createPriorityConcurrentQueue<Int>().also {
        it.add(SCENARIO_PRIORITY, SCENARIO_INITIAL_VALUE)
    }

@OptIn(ExperimentalSimbotCollectionApi::class)
private fun addAndContainsAddedValue(queue: PriorityConcurrentQueue<Int>): Boolean {
    queue.add(SCENARIO_PRIORITY, SCENARIO_ADDED_VALUE)
    return SCENARIO_ADDED_VALUE in queue
}

private const val SCENARIO_PRIORITY = 1
private const val SCENARIO_INITIAL_VALUE = 0
private const val SCENARIO_ADDED_VALUE = 1
