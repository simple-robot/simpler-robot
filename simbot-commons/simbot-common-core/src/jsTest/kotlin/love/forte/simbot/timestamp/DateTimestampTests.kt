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

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.time.DateTimestamp
import love.forte.simbot.common.time.Timestamp
import kotlin.js.Date
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 *
 * @author ForteScarlet
 */
class DateTimestampTests {

    @Test
    fun jsDateTimestampTest() {
        val timestamp = DateTimestamp(Date())

        // is milliseconds (13位)
        assertTrue { timestamp.milliseconds.toString().length >= 13 }

        Date().let { d ->
            assertEquals(DateTimestamp(d), DateTimestamp(d))
        }
    }

    @OptIn(ExperimentalSimbotAPI::class)
    @Test
    fun jsTimestampNowIsDateTimestampTest() {
        assertTrue("JS Timestamp.now is not DateTimestamp instance") { Timestamp.now() is DateTimestamp }
    }

}
