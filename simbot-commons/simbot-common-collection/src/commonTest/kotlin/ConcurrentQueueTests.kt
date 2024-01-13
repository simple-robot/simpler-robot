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

import love.forte.simbot.common.collection.ExperimentalSimbotCollectionApi
import love.forte.simbot.common.collection.createConcurrentQueue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


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

}
