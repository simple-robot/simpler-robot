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
@file:JvmName("CoroutineContexts")
@file:JvmMultifileClass

package love.forte.simbot.common.coroutines

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * 以 [parentContext] 作为基准、以当前 `receiver` 为主要结果合并两个 [CoroutineContext]，
 * （也就是使用当前 context 覆盖 [parentContext]）
 *
 * 例如：
 * ```
 * CoroutineName("name1").mergeWith(CoroutineName("name2"))
 * // 结果：CoroutineName("name1")
 * ```
 *
 *
 * 在这过程中同样“合并”二者的 [Job]:
 * - 如果都没有 [Job]，则创建一个 [SupervisorJob] 添加
 * - 如果 [parentContext] 中有 [Job]，则将其作为创建的 [SupervisorJob] 的 parent。
 * - 如果当前 `receiver` 有 [Job]，则将其作为创建的 [SupervisorJob] 的 parent。
 * - 如果二者都有 [Job]，则将当前 `receiver` 作为 parent，并通过 [linkTo] 链接到 [parentContext] 中的 [Job] 上。
 *
 * @see linkTo
 */
public fun CoroutineContext.mergeWith(parentContext: CoroutineContext): CoroutineContext {
    val mergedContext = parentContext.minusKey(Job) + this.minusKey(Job)
    val currentJob = this[Job]
    val parentJob = parentContext[Job]

    return when {
        currentJob == null && parentJob == null -> mergedContext + SupervisorJob()
        currentJob == null -> mergedContext + SupervisorJob(parentJob)
        parentJob == null -> mergedContext + SupervisorJob(currentJob)
        else -> mergedContext + SupervisorJob(currentJob).apply { linkTo(parentJob) }
    }
}
