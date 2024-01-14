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

import love.forte.simbot.ability.*
import love.forte.simbot.ability.StandardDeleteOption.*
import love.forte.simbot.ability.StandardDeleteOption.Companion.inStandardAnalysis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


/**
 *
 * @author ForteScarlet
 */
class StandardDeleteOptionTests {

    @Test
    fun standardOptionStandardAnalysisTest() {
        with(arrayOf<DeleteOption>()) {
            var each = 0
            inStandardAnalysis({
                each++
            }) {
                assertTrue(isEmpty)
                assertFalse(isFull)

                StandardDeleteOption.entries.forEach {
                    assertFalse(it in this)
                }
            }
            assertEquals(0, each)
        }

        with(arrayOf<DeleteOption>(IGNORE_ON_FAILURE)) {
            inStandardAnalysis({
                assertEquals(IGNORE_ON_FAILURE, it)
            }) {
                assertFalse(isEmpty)
                assertFalse(isFull)

                StandardDeleteOption.entries.forEach {
                    if (it == IGNORE_ON_FAILURE) {
                        assertTrue(it in this)
                    } else {
                        assertFalse(it in this)
                    }
                }
            }
        }

        with(arrayOf<DeleteOption>(IGNORE_ON_FAILURE, IGNORE_ON_NO_SUCH_TARGET)) {
            inStandardAnalysis {
                assertFalse(isEmpty)
                assertFalse(isFull)

                assertTrue(contains(IGNORE_ON_FAILURE))
                assertTrue(contains(IGNORE_ON_NO_SUCH_TARGET))
                assertFalse(contains(IGNORE_ON_ANY_FAILURE))
                assertFalse(contains(IGNORE_ON_UNSUPPORTED))

                assertTrue(isIgnoreOnFailure)
                assertTrue(isIgnoreOnNoSuchTarget)
                assertFalse(isIgnoreOnAnyFailure)
                assertFalse(isIgnoreOnUnsupported)
            }
        }

        with(arrayOf<DeleteOption>(IGNORE_ON_ANY_FAILURE, IGNORE_ON_UNSUPPORTED)) {
            inStandardAnalysis {
                assertFalse(isEmpty)
                assertFalse(isFull)

                assertFalse(contains(IGNORE_ON_FAILURE))
                assertFalse(contains(IGNORE_ON_NO_SUCH_TARGET))
                assertTrue(contains(IGNORE_ON_ANY_FAILURE))
                assertTrue(contains(IGNORE_ON_UNSUPPORTED))

                assertFalse(isIgnoreOnFailure)
                assertFalse(isIgnoreOnNoSuchTarget)
                assertTrue(isIgnoreOnAnyFailure)
                assertTrue(isIgnoreOnUnsupported)
            }
        }

        with(StandardDeleteOption.entries.toTypedArray()) {
            inStandardAnalysis {
                assertFalse(isEmpty)
                assertTrue(isFull)

                StandardDeleteOption.entries.forEach {
                    assertTrue(contains(it))
                }

                assertTrue(isIgnoreOnFailure)
                assertTrue(isIgnoreOnNoSuchTarget)
                assertTrue(isIgnoreOnAnyFailure)
                assertTrue(isIgnoreOnUnsupported)

            }
        }
    }

}
