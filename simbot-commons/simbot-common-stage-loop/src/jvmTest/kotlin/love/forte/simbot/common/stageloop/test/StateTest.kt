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

package love.forte.simbot.common.stageloop.test

import love.forte.simbot.common.stageloop.State
import love.forte.simbot.common.stageloop.loop


suspend fun main() {
    MyState.Start.loop { it.also(::println) }.also(::println)
}

sealed class MyState : State<MyState>() {

    /** 一个用于启动状态循环的状态 */
    data object Start : MyState() {
        override suspend fun invoke(): MyState = Process(0)
    }

    /** 中间的处理状态，可以直接隐藏 */
    private data class Process(val value: Int) : MyState() {
        override suspend fun invoke(): MyState {
            return if (value < 10) Process(value + 1) else End(value)
        }
    }

    /** 最后一个状态，会被 `State.loop()` 返回 */
    data class End(val value: Int): MyState() {
        override suspend fun invoke(): MyState? = null
    }
}
