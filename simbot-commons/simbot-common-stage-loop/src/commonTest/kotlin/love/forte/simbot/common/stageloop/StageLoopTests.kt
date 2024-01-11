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
import kotlin.test.assertTrue


/**
 *
 * @author ForteScarlet
 */
class StageLoopTests {

    @Test
    fun stageLoopTest() = runTest {
        val loop = DefaultStageLoop<TestStage>()
        var l1 = 0
        var l2 = 0
        var d1Done = false
        var d2Done = false
        val st = Start(onLoop1 = { l1 = it }, onLoop2 = { l2 = it })
        loop.appendStage(st)
        loop.loop(condition = {
            if (it is Done1) {
                d1Done = true
            }
            if (it is Done2) {
                d2Done = true
            }
            it != null
        })
        assertEquals(l1, 3)
        assertEquals(l2, 3)
        assertTrue(d1Done)
        assertTrue(d2Done)
    }

    sealed class TestStage : Stage<TestStage>()

    data class Start(val onLoop1: (Int) -> Unit, val onLoop2: (Int) -> Unit) : TestStage() {
        override suspend fun invoke(loop: StageLoop<TestStage>) {
            loop.appendStage(Loop1(0, onLoop1))
            loop.appendStage(Loop2(0, onLoop2))
        }
    }

    data class Loop1(val time: Int, val on: (Int) -> Unit) : TestStage() {
        override suspend fun invoke(loop: StageLoop<TestStage>) {
            on(time)
            if (time < 3) {
                loop.appendStage(Loop1(time + 1, on))
            } else {
                loop.appendStage(Done1)
            }
        }
    }

    data class Loop2(val time: Int, val on: (Int) -> Unit) : TestStage() {
        override suspend fun invoke(loop: StageLoop<TestStage>) {
            on(time)
            if (time < 3) {
                loop.appendStage(Loop2(time + 1, on))
            } else {
                loop.appendStage(Done2)
            }
        }
    }

    data object Done1 : TestStage() {
        override suspend fun invoke(loop: StageLoop<TestStage>) {
        }
    }

    data object Done2 : TestStage() {
        override suspend fun invoke(loop: StageLoop<TestStage>) {
        }
    }

}

