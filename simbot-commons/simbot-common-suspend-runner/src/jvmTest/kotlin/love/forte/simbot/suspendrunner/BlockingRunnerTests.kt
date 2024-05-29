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

package love.forte.simbot.suspendrunner

import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertIs


/**
 *
 * @author ForteScarlet
 */
class BlockingRunnerTests {
    @Test
    fun runBlockingExceptionallyTest() {
        runInNoScopeBlocking { runNormally() }

        assertIs<RunException>(
            assertFails {
                runInNoScopeBlocking { runExceptionally1() }
            }
        )

        assertIs<RunException>(
            assertFails {
                runInNoScopeBlocking { runExceptionally2() }
            }
        )
    }

    private suspend fun runNormally() = suspendCancellableCoroutine { continuation ->
        continuation.resume(0)
    }

    private suspend fun runExceptionally1() {
        delay(1)
        throw RunException()
    }

    private suspend fun runExceptionally2() = suspendCancellableCoroutine<Int> { continuation ->
        continuation.resumeWithException(RunException())
    }


    private class RunException : RuntimeException()
}
