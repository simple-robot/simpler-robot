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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.gettimeofday
import platform.posix.time
import platform.posix.timeval
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class TimeTest {

    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun nowTest() {
        // time
        val nowUnixtime = time(null)
        println(nowUnixtime)
    }


    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun nowTest2() {
        val time = memScoped {
            val timeVal = alloc<timeval>()
            gettimeofday(timeVal.ptr, null)
            timeVal.tv_sec * 1_000L + timeVal.tv_usec / 1_000L
        }

        println(time)
    }
}
