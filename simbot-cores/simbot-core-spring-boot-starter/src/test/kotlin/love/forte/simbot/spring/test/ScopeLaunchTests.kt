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

package love.forte.simbot.spring.test

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


/**
 *
 * @author ForteScarlet
 */
class ScopeLaunchTests {

    private class InternalErr(message: String) : RuntimeException(message)

    @Test
    fun scopeLaunchAndThrowTest() = runTest {
        try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    repeat(20) { i ->
                        launch {
                            delay(10)
                            throw InternalErr("ERROR in $i")
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            Assertions.assertInstanceOf(InternalErr::class.java, e)
        }
    }

}
