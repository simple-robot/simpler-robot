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

package love.forte.simbot.bot

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.selects.SelectClause0
import kotlinx.coroutines.selects.select
import love.forte.simbot.ability.OnCompletion
import kotlin.concurrent.Volatile
import kotlin.jvm.JvmSynthetic


/**
 *
 * 一个 [Bot] 的基础能力实现的抽象类型。
 *
 * 在基于 [Job] 的前提下提供部分功能的默认实现。
 *
 * @author ForteScarlet
 */
public abstract class JobBasedBot : Bot {
    /**
     * 当前 bot 持有的 job.
     */
    protected abstract val job: Job

    /**
     * 是否已经启动。
     *
     * 此属性为 `volatile` 的可变属性，在内部修改其值时，
     * 应当只可能将其设置为 `true`，且基于此不需要 `atomic` 实现或加锁。
     * 当然，[start] 逻辑本身还是因该有锁的。
     * 需要由实现者手动修改其值。
     */
    @Volatile
    override var isStarted: Boolean = false
        protected set

    override fun onCompletion(handle: OnCompletion) {
        job.invokeOnCompletion { cause -> handle.invoke(cause) }
    }

    override val isActive: Boolean
        get() = job.isActive

    override val isCompleted: Boolean
        get() = job.isCompleted

    @JvmSynthetic
    override suspend fun join() {
        job.join()
    }

    override fun cancel(reason: Throwable?) {
        job.cancel(reason?.let { CancellationException(it.message, it) })
    }

    /**
     * Clause for [select] expression of [join] suspending function that selects when the job is complete.
     * This clause never fails, even if the job completes exceptionally.
     *
     * @see Job.onJoin
     */
    public val onJoin: SelectClause0
        get() = job.onJoin
}

