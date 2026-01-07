/*
 *     Copyright (c) 2026. ForteScarlet.
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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKStringFromUtf8
import platform.posix.fgets
import platform.posix.pclose
import platform.posix.popen
import kotlin.test.Test

/*
 *     Copyright (c) 2025. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simbot-component-onebot
 *     Email      ForteScarlet@163.com
 *
 *     This project and this file are part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

/**
 *
 * @author ForteScarlet
 */
class CliTests {

    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun testCli() {
        val file = popen("java -version 2>&1", "r")
            ?: error("Failed to execute command.")

        try {
            val output = StringBuilder()
            val buffer = ByteArray(1024)
            while (true) {
                val line = fgets(buffer.refTo(0), buffer.size, file) ?: break
                output.append(line.toKStringFromUtf8())
            }

            println(output)
        } finally {
            val status = pclose(file)
            println("pclose status: $status")
        }
    }

}
