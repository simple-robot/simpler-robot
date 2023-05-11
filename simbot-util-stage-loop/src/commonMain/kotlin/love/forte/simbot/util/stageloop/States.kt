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
 * 通过指定的 [State] 开启状态循环并直到状态消失。
 *
 * @param onEach 每一次循环的状态控制，如果手动中断则 [loop] 的返回值即为此函数用于判断的参数值。
 * ```kotlin
 * var state = ...
 * while(onEach(state)) {
 *     ...
 * }
 *
 * return state
 * ```
 *
 * 默认永远为 `true` 。
 * @param onNext 对中间每一次的新状态拦截处理，默认返回被拦截的结果自身。
 * @return 当循环结束时的最后一个有效状态，如果没有任何新的状态产生则得到 receiver 自身。
 *
 * @author ForteScarlet
 */
public suspend inline fun <S : State<S>> S.loop(
    onEach: (S) -> Boolean = { true },
    onNext: (S?) -> S? = { it }
): S {
    var state = this
    while (onEach(state)) {
        state().let(onNext)?.also { state = it } ?: return state
    }

    return state
}

