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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 *
 * @author ForteScarlet
 */
class FlowIteratorTests {

    @Test
    fun flowAsIteratorTest() = runTest {
        with(flowOf(1, 2, 3)) {
            val iterator = coroutineScope {
                withContext(Dispatchers.Default) {
                    asIterator(
                        this,
                        hasNext = {
                            runBlocking { hasNext() }
                        },
                        next = {
                            runBlocking { next() }
                        }
                    )
                }
            }

            assertTrue(iterator.hasNext())
            assertEquals(1, iterator.next())
            assertTrue(iterator.hasNext())
            assertEquals(2, iterator.next())
            assertTrue(iterator.hasNext())
            assertEquals(3, iterator.next())
            assertFalse(iterator.hasNext())
        }
    }
}
