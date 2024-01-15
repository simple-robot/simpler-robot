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

package love.forte.simbot.common

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

    @Test
    fun timeUnitConvertTest() {
        val td = 1L
        val th = td * 24L
        val tm = th * 60L
        val ts = tm * 60L
        val tms = ts * 1000L
        val tus = tms * 1000L
        val tns = tus * 1000L

        fun doConvert(duration: Long, unit: TimeUnit) {
            assertEquals(td, TimeUnit.DAYS.convert(duration, unit))
            assertEquals(th, TimeUnit.HOURS.convert(duration, unit))
            assertEquals(tm, TimeUnit.MINUTES.convert(duration, unit))
            assertEquals(ts, TimeUnit.SECONDS.convert(duration, unit))
            assertEquals(tms, TimeUnit.MILLISECONDS.convert(duration, unit))
            assertEquals(tus, TimeUnit.MICROSECONDS.convert(duration, unit))
            assertEquals(tns, TimeUnit.NANOSECONDS.convert(duration, unit))
        }

        doConvert(td, TimeUnit.DAYS)
        doConvert(th, TimeUnit.HOURS)
        doConvert(tm, TimeUnit.MINUTES)
        doConvert(ts, TimeUnit.SECONDS)
        doConvert(tms, TimeUnit.MILLISECONDS)
        doConvert(tus, TimeUnit.MICROSECONDS)
        doConvert(tns, TimeUnit.NANOSECONDS)
    }

}
