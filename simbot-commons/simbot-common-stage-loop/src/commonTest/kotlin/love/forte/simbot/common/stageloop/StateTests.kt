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

package love.forte.simbot.common.stageloop

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class StateTests {
    @Test
    fun stateLoopTest() = runTest {
        var time = 0
        val last = Start.loop(onEach = {
            if (it is Loop) {
                time = it.time
            }
            true
        })

        assertEquals(3, time)
        assertEquals(Done, last)
    }

    sealed class TestState : State<TestState>()

    data object Start : TestState() {
        override suspend fun invoke(): TestState = Loop(1)
    }

    data class Loop(val time: Int) : TestState() {
        override suspend fun invoke(): TestState {
            if (time < 3) return Loop(time + 1)
            return Done
        }
    }

    data object Done : TestState() {
        override suspend fun invoke(): TestState? = null
    }
}


