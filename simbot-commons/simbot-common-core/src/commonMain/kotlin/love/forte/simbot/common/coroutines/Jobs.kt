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

package love.forte.simbot.common.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads

/**
 * 将 [Job] "链接"到一个虚拟的 [parent] Job 上。
 *
 * 所谓"链接"，即当 [parent] 关闭或完成时，
 * 会同时关闭当前Job（通过 [Job.invokeOnCompletion]）。
 *
 * [parent] 不会被作为真正的父Job，[Job] 可以"链接"多个虚拟的父Job。
 *
 * @param cancelChecker 关闭当前Job前的检查。只有当 [cancelChecker] 返回 `true` 才会真正的执行 [thisJob.cancel][Job.cancel]。
 * 默认情况下 [cancelChecker] 会使用 [{ !thisJob.isCompleted }][Job.isCompleted]
 *
 */
@JvmName("linkTo")
@JvmOverloads
public inline fun Job.linkTo(
    parent: Job,
    crossinline cancelChecker: (cause: Throwable?) -> Boolean = { !this.isCompleted },
): DisposableHandle {
    val thisJob = this
    return parent.invokeOnCompletion { cause ->
        if (cancelChecker(cause)) {
            thisJob.cancel(
                LinkedParentJobCancellationException(
                    "Linked virtual parent Job $parent is completed.",
                    cause
                )
            )
        }
    }
}


/**
 * @see linkTo
 */
public class LinkedParentJobCancellationException(override val message: String?, override val cause: Throwable?) :
    CancellationException(message)
