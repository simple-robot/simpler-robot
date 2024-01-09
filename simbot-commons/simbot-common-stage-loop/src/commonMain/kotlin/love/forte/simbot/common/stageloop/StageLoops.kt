/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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


/**
 * 通过提供的 [StageLoop] 进行循环。
 *
 * @param condition 每次循环时的条件判断。默认为 `next != null`。
 * 可以配合其他条件实现声明周期控制，例如 `Job`
 * ```kotlin
 * val job: Job = ...
 * stageLoop.loop(condition = { next -> job.isActive && next != null })
 * ```
 *
 * @param exceptionHandle 每个阶段循环到并执行时的异常处理器
 */
public suspend inline fun <S : Stage<S>> StageLoop<S>.loop(
    crossinline condition: (next: S?) -> Boolean = { next -> next != null },
    crossinline exceptionHandle: (Throwable) -> Unit = { e ->
        throw e
    },
) {
    var next: S? = poll()
    while (condition(next)) {
        try {
            invoke(next)
        } catch (e: Throwable) {
            exceptionHandle(e)
        }
        next = poll()
    }
    invoke(null)
}

