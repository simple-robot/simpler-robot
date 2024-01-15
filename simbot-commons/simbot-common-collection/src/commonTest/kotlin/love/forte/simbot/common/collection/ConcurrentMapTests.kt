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

package love.forte.simbot.common.collection

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.*

/**
 *
 * @author ForteScarlet
 */
class ConcurrentMapTests {
    @Test
    fun concurrentMapComputeTest() = runTest {
        data class Value(val int: Int)

        val map = concurrentMutableMap<Int, Value>()

        coroutineScope {
            repeat(10) { n ->
                launch(Dispatchers.Default) {
                    repeat(10) { _ ->
                        map.computeValue(n) { _, old ->
                            old?.copy(int = old.int + 1) ?: Value(1)
                        }
                    }
                }
            }
        }

        assertEquals(10, map.size)
        map.values.forEach { assertEquals(10, it.int) }
    }

    @Test
    fun mapSetTest() {
        with(concurrentMutableMap<Int, Int>()) {
            put(1, 1)
            assertEquals(1, get(1))
            assertNull(get(2))
            assertEquals(1, computeValueIfAbsent(1) { 2 })
            assertEquals(2, computeValueIfAbsent(2) { 2 })
            assertEquals(3, computeValueIfPresent(2) { _, _ -> 3 })
            assertEquals(5, computeValue(3) { _, v ->
                assertNull(v)
                5
            })
            clear()
            assertTrue(isEmpty())
            assertEquals(0, size)
            this.putAll(mapOf(1 to 1, 2 to 2, 3 to 3))
            assertFalse(isEmpty())
            assertEquals(3, size)
            assertEquals(keys, setOf(1, 2, 3))
            assertContentEquals(values, listOf(1, 2, 3))
            remove(3)
            assertFalse(isEmpty())
            assertEquals(2, size)
            assertEquals(keys, setOf(1, 2))
            assertContentEquals(values, listOf(1, 2))
            assertTrue(containsKey(1))
            assertTrue(containsKey(2))
            assertFalse(containsKey(3))
            assertTrue(containsValue(1))
            assertTrue(containsValue(2))
            assertFalse(containsValue(3))
            assertEquals(2, entries.size)
            assertEquals(size, entries.size)
            val iterator = iterator()
            assertTrue(iterator.hasNext())
            val n1 = iterator.next()
            assertContains(setOf(1, 2), n1.key)
            assertContains(setOf(1, 2), n1.value)
            assertTrue(iterator.hasNext())
            val n2 = iterator.next()
            assertContains(setOf(1, 2), n2.key)
            assertContains(setOf(1, 2), n2.value)
            assertFalse(iterator.hasNext())
        }

    }

}
