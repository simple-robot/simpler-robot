/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.common.ktor.inputfile

import io.ktor.utils.io.core.*
import kotlinx.coroutines.test.runTest
import love.forte.simbot.common.ktor.inputfile.InputFileTests.Companion.assertInputFileByMockClient
import java.io.File
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class InputFileJvmTests {

    @Test
    fun fileInputFile() = runTest {
        val realText = "Hello, World"
        val file = File.createTempFile("InputFileJvmTests-PREFIX", "InputFileJvmTests-SUFFIX")
            .also { it.deleteOnExit() }
        file.appendText(realText)

        val bytes = realText.toByteArray()

        assertInputFileByMockClient(bytes.size, realText, InputFile(file))
    }

    @Test
    fun pathInputFile() = runTest {
        val realText = "Hello, World"
        val file = File.createTempFile("InputFileJvmTests-PREFIX", "InputFileJvmTests-SUFFIX")
            .also { it.deleteOnExit() }
        file.appendText(realText)
        val bytes = realText.toByteArray()

        assertInputFileByMockClient(bytes.size, realText, InputFile(file.toPath()))
    }

}
