/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.spring2.application.internal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import love.forte.simbot.ability.OnCompletion
import love.forte.simbot.application.ApplicationConfiguration
import love.forte.simbot.application.ApplicationLaunchStage
import love.forte.simbot.application.ApplicationLaunchStages
import love.forte.simbot.application.NormalApplicationEventHandler
import love.forte.simbot.bot.BotManagers
import love.forte.simbot.component.Components
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.plugin.Plugins
import love.forte.simbot.spring.common.application.SpringApplication
import kotlin.coroutines.CoroutineContext


/**
 * @author ForteScarlet
 */
internal class SpringApplicationImpl(
    override val configuration: ApplicationConfiguration,
    override val eventDispatcher: EventDispatcher,
    override val components: Components,
    override val plugins: Plugins,
    override val botManagers: BotManagers,
    val events: ApplicationLaunchStages
) : SpringApplication {
    private val job: Job
    override val coroutineContext: CoroutineContext

    init {
        val newJob = SupervisorJob(configuration.coroutineContext[Job])
        val newCoroutineContext = configuration.coroutineContext.minusKey(Job) + newJob

        this.job = newJob
        this.coroutineContext = newCoroutineContext
    }

    private inline fun <C : Any, reified H : NormalApplicationEventHandler<C>> invokeNormalHandler(
        stage: ApplicationLaunchStage<H>, block: H.() -> Unit
    ) {
        events[stage]?.forEach { handler ->
            (handler as? H)?.also { handler0 ->
                block(handler0)
            }
        }
    }

    override fun onCompletion(handle: OnCompletion) {
        job.invokeOnCompletion { handle.invoke(it) }
    }

    override val isActive: Boolean
        get() = job.isActive

    override val isCompleted: Boolean
        get() = job.isCompleted

    override fun cancel(reason: Throwable?) {
        invokeNormalHandler(ApplicationLaunchStage.RequestCancel) {
            invoke(this@SpringApplicationImpl)
        }

        job.cancel(reason?.let { CancellationException(reason.message, it) })

        invokeNormalHandler(ApplicationLaunchStage.Cancelled) {
            invoke(this@SpringApplicationImpl)
        }
    }

    override suspend fun join() {
        job.join()
    }

    override fun toString(): String {
        return "SpringApplication(" +
            "isActive=$isActive, " +
            "isCompleted=$isCompleted, " +
            "eventDispatcher=$eventDispatcher, " +
            "components=$components, " +
            "plugins=$plugins)"
    }


}
