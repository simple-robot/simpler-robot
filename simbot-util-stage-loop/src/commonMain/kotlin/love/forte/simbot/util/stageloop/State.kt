/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.util.stageloop

/**
 * 一个单向流转可挂起的 **"状态机"** 。
 *
 * 状态 [State] 是一个简易且状态单一单向的 _"状态机"_ 。
 * 与 [StageLoop] 类似，但是与其不同的是 [State] 没有外置的循环器，
 * 也没有内置的队列，仅使用其本身循环。
 *
 * 建议通过抽象或密封类提供类型约束并参考如下使用：
 *
 * ```kotlin
 * sealed class MyState : State<MyState>() {
 *
 *     /** 一个用于启动状态循环的状态 */
 *     object Start : MyState() {
 *         override suspend fun invoke(): MyState = Process(0)
 *     }
 *
 *     /** 中间的处理状态，可以直接隐藏 */
 *     private data class Process(val value: Int) : MyState() {
 *         override suspend fun invoke(): MyState? {
 *             return if (value < 10) Process(value + 1) else End
 *         }
 *     }
 *
 *      /** 最后一个状态，会被 `State.loop()` 返回 */
 *     data class End(val value: Int): MyState() {
 *         override suspend fun invoke(): MyState? = null
 *     }
 * }
 *
 * val myState: MyState = ...
 *
 * val lastState = myState.loop()  // 开始循环直到结束
 *
 * ```
 *
 * @see loop
 * @author ForteScarlet
 *
 */
public abstract class State<S : State<S>> {
    /*
        性能略高于 [StageLoop], 使用更简单
     */

    /**
     * 执行当前状态的逻辑，并返回下一个要进入的状态。
     *
     * @return 下一个状态或得到null来终止状态循环。
     */
    public abstract suspend operator fun invoke(): S?
}


