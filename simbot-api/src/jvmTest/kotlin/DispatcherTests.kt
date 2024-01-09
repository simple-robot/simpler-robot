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

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class DispatcherTests {

    private val d1 = Executors.newFixedThreadPool(2) { rb ->
        Thread(rb, "D1t").apply {
            isDaemon = true
        }
    }.asCoroutineDispatcher()

    private val d2 = Executors.newFixedThreadPool(2) { rb ->
        Thread(rb, "D2t").apply {
            isDaemon = true
        }
    }.asCoroutineDispatcher()

    @Test
    fun test() = runBlocking {
        flow {
            println(Thread.currentThread())
            emit(1)
            val v = withContext(d2) {
                println(Thread.currentThread())
                2
            }
            println(Thread.currentThread())
            emit(v)
        }.flowOn(d1)
            .collect {
                println(Thread.currentThread())
            }

    }

}
