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

package love.forte.simbot.timestamp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.time.MillisecondTimestamp
import love.forte.simbot.common.time.TimeUnit
import love.forte.simbot.common.time.Timestamp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


/**
 *
 * @author ForteScarlet
 */
class TimestampTests {

    @Test
    fun millisTests() {
        val millis = 1697822131902L
        assertEquals(Timestamp.ofMilliseconds(millis), Timestamp.ofMilliseconds(millis))

        val timestamp = MillisecondTimestamp(millis)
        assertEquals(timestamp.timeAs(TimeUnit.SECONDS), millis / 1000)
    }

    @OptIn(ExperimentalSimbotAPI::class)
    @Test
    fun nowTimestampTests() {
        val now = Timestamp.now()

        println(now)

        assertEquals(now.compareTo(now), 0)
        assertEquals(now.milliseconds.toString().length, 13)

        val timeOfNow = Timestamp.ofMilliseconds(now.milliseconds)

        assertEquals(now, timeOfNow)
        assertEquals(now.milliseconds, timeOfNow.milliseconds)
        assertEquals(now.compareTo(timeOfNow), 0)
    }

    @OptIn(ExperimentalSimbotAPI::class)
    @Test
    fun nowTimestampValueTest() = runTest {
        val now1 = Timestamp.now()
        withContext(Dispatchers.Default) {
            delay(200)
        }
        val now2 = Timestamp.now()
        assertTrue(now2.milliseconds - now1.milliseconds >= 200)
    }

}
