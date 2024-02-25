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

package love.forte.simbot.suspendrunner

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.suspendrunner.reserve.flux
import love.forte.simbot.suspendrunner.reserve.list
import love.forte.simbot.suspendrunner.reserve.mono
import love.forte.simbot.suspendrunner.reserve.suspendReserve
import reactor.test.StepVerifier
import kotlin.test.Test
import kotlin.test.assertEquals


/**
 *
 * @author ForteScarlet
 */
class JvmReserveTests {

    @OptIn(InternalSimbotAPI::class)
    @Test
    fun jvmReserveMonoTest() = runTest {
        val reserve = suspendReserve(this, Dispatchers.Default) { run() }
        val mono = reserve.transform(mono())

        StepVerifier.create(mono)
            .expectNext(1)
            .verifyComplete()
    }

    private suspend fun run(): Int {
        delay(1)
        return 1
    }


    @OptIn(InternalSimbotAPI::class)
    @Test
    fun jvmReserveFluxTest() = runTest {
        val reserve = suspendReserve(this, Dispatchers.Default) { flowOf(1, 2, 3) }
        val flux = reserve.transform(flux())

        StepVerifier.create(flux)
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .verifyComplete()
    }

    @OptIn(InternalSimbotAPI::class)
    @Test
    fun jvmReserveListTest() = runTest {
        val reserve = suspendReserve(this, Dispatchers.Default) { flowOf(1, 2, 3) }
        val list = reserve.transform(list())

        assertEquals(3, list.size)
        assertEquals(1, list[0])
        assertEquals(2, list[1])
        assertEquals(3, list[2])
    }
}
