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

import kotlinx.coroutines.Job
import love.forte.simbot.ability.OnCompletion
import kotlin.jvm.JvmSynthetic
import kotlinx.coroutines.CancellationException as CreateCancellationException


/**
 * 一个基于 [Job] 并提供部分基础能力实现的 [BotManager] 抽象。
 *
 * @author ForteScarlet
 */
public abstract class JobBasedBotManager : BotManager {
    protected abstract val job: Job

    override val isActive: Boolean
        get() = job.isActive

    override val isCompleted: Boolean
        get() = job.isCompleted


    @JvmSynthetic
    override suspend fun join() {
        job.join()
    }

    override fun cancel(cause: Throwable?) {
        job.cancel(cause?.let { CreateCancellationException(it.message, it) })
    }

    override fun onCompletion(handle: OnCompletion) {
        job.invokeOnCompletion { cause -> handle.invoke(cause) }
    }


}
