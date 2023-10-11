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

package love.forte.simbot.utils

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job

/**
 * 将 [Job] "链接"到一个虚拟的 [parent] Job 上。
 *
 * 所谓"链接"，即当 [parent] 关闭或完成时，
 * 会同时关闭当前Job（通过 [Job.invokeOnCompletion]）。
 *
 * [parent] 不会被作为真正的父Job，[Job] 可以"链接"多个虚拟的父Job。
 *
 */
public fun Job.linkTo(parent: Job): DisposableHandle {
    val thisJob = this
    return parent.invokeOnCompletion { cause ->
        if (thisJob.isActive) {
            thisJob.cancel(CancellationException("Linked virtual parent Job $parent is completed.", cause))
        }
    }
}
