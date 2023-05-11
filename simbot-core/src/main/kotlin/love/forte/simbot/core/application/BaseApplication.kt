/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.core.application

import kotlinx.coroutines.Job
import love.forte.simbot.application.Application
import love.forte.simbot.application.BotManagers
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.BotManager
import love.forte.simbot.bot.BotVerifyInfo
import love.forte.simbot.bot.ComponentMismatchException
import love.forte.simbot.logger.LoggerFactory
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException


/**
 *
 * @author ForteScarlet
 */
public abstract class BaseApplication : Application {
    abstract override val coroutineContext: CoroutineContext
    protected abstract val logger: Logger

    override val botManagers: BotManagers by lazy { BotManagersImpl(providers.filterIsInstance<BotManager<*>>()) }


    private val job: Job? get() = coroutineContext[Job]

    override suspend fun join() {
        job?.join()
    }

    override suspend fun shutdown(reason: Throwable?) {
        job?.cancel(reason?.let { CancellationException(reason) })
        stopAll(reason)
    }

    protected open suspend fun stopAll(reason: Throwable?) {
        providers.forEach {
            kotlin.runCatching {
                it.cancel(reason)
            }.getOrElse { e ->
                logger.error("Event provider $it cancel failure.", e)
            }
        }
    }
}


private class BotManagersImpl(private val botManagers: List<BotManager<*>>) : BotManagers,
    List<BotManager<*>> by botManagers {

    override fun register(botVerifyInfo: BotVerifyInfo): Bot? {
        logger.info("Registering bot with verify info [{}]", botVerifyInfo)
        for (manager in this) {
            try {
                return manager.register(botVerifyInfo).also { bot ->
                    logger.debug(
                        "Bot verify info [{}] is registered as [{}] via manager [{}]",
                        botVerifyInfo,
                        bot,
                        manager
                    )
                }
            } catch (ignore: ComponentMismatchException) {
                logger.debug("Bot verify info [{}] is not matched by manager {}, try next.", botVerifyInfo, manager)

            }
        }

        return null
    }

    override fun toString(): String {
        return "BotManagersImpl(managers=$botManagers)"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BotManagersImpl::class)
    }
}
