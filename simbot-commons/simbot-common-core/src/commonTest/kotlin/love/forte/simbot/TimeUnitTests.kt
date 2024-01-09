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

package love.forte.simbot

import love.forte.simbot.common.time.TimeUnit
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for [TimeUnit].
 *
 */
class TimeUnitTests {

    @Test
    fun timeUnitConvertToSelfTest() {
        fun TimeUnit.doConvert(duration: Long, block: TimeUnit.(Long) -> Long) {
            assertEquals(block(duration), duration, "${this}.doConvert(duration, $block) not return it self value")
        }

        TimeUnit.MICROSECONDS.doConvert(Random.nextLong(), TimeUnit::toMicros)
        TimeUnit.NANOSECONDS.doConvert(Random.nextLong(), TimeUnit::toNanos)
        TimeUnit.MILLISECONDS.doConvert(Random.nextLong(), TimeUnit::toMillis)
        TimeUnit.SECONDS.doConvert(Random.nextLong(), TimeUnit::toSeconds)
        TimeUnit.MINUTES.doConvert(Random.nextLong(), TimeUnit::toMinutes)
        TimeUnit.HOURS.doConvert(Random.nextLong(), TimeUnit::toHours)
        TimeUnit.DAYS.doConvert(Random.nextLong(), TimeUnit::toDays)

        fun TimeUnit.doConvert(duration: Long) {
            assertEquals(duration, convert(duration, this))
        }

        TimeUnit.NANOSECONDS.doConvert(Random.nextLong())
        TimeUnit.MICROSECONDS.doConvert(Random.nextLong())
        TimeUnit.MILLISECONDS.doConvert(Random.nextLong())
        TimeUnit.SECONDS.doConvert(Random.nextLong())
        TimeUnit.MINUTES.doConvert(Random.nextLong())
        TimeUnit.HOURS.doConvert(Random.nextLong())
        TimeUnit.DAYS.doConvert(Random.nextLong())
    }

    // todo convert test, to xxx test


}
