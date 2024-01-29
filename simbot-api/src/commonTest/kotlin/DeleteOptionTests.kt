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

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.ability.StandardDeleteOption.Companion.standardAnalysis
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 *
 * @author ForteScarlet
 */
class DeleteOptionTests {

    @Test
    fun optionAnalysisTest() {
        val options = arrayOf<DeleteOption>(
            StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET,
            StandardDeleteOption.IGNORE_ON_UNSUPPORTED
        )

        val standardAnalysis = options.standardAnalysis()

        assertTrue(StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET in standardAnalysis)
        assertTrue(StandardDeleteOption.IGNORE_ON_UNSUPPORTED in standardAnalysis)
    }

    @Test
    fun optionAnalysisTest_NoOptions() {
        val options = arrayOf<DeleteOption>()

        val standardAnalysis = options.standardAnalysis()

        assertFalse(StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET in standardAnalysis)
        assertFalse(StandardDeleteOption.IGNORE_ON_UNSUPPORTED in standardAnalysis)
    }

    @Test
    fun optionAnalysisTest_FullOptions() {
        val options: Array<out DeleteOption> = StandardDeleteOption.entries.toTypedArray()

        val standardAnalysis = options.standardAnalysis()

        StandardDeleteOption.entries.forEach {
            assertTrue(it in standardAnalysis)
        }
    }

}
