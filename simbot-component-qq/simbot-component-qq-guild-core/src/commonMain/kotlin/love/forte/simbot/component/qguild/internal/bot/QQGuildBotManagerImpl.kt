/*
 * Copyright (c) 2021-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.internal.bot

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import love.forte.simbot.bot.ConflictBotException
import love.forte.simbot.bot.JobBasedBotManager
import love.forte.simbot.bot.NoSuchBotException
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.collection.ExperimentalSimbotCollectionApi
import love.forte.simbot.common.collection.createConcurrentQueue
import love.forte.simbot.common.coroutines.linkTo
import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.common.function.invokeBy
import love.forte.simbot.common.function.invokeWith
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.qguild.QQGuildComponent
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.bot.QQGuildBotManager
import love.forte.simbot.component.qguild.bot.QQGuildBotManagerConfiguration
import love.forte.simbot.component.qguild.bot.config.QGBotComponentConfiguration
import love.forte.simbot.component.qguild.internal.event.QGBotRegisteredEventImpl
import love.forte.simbot.event.EventDispatcher
import love.forte.simbot.event.onEachError
import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.logger
import love.forte.simbot.qguild.stdlib.Bot
import love.forte.simbot.qguild.stdlib.BotFactory
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotCollectionApi::class)
internal class QQGuildBotManagerImpl(
    override val eventDispatcher: EventDispatcher,
    override val configuration: QQGuildBotManagerConfiguration,
    private val component: QQGuildComponent,
    override val job: Job,
    private val coroutineContext: CoroutineContext // 不应包含 Job
) : QQGuildBotManager, JobBasedBotManager() {
    companion object {
        private val LOGGER = LoggerFactory.logger<QQGuildBotManagerImpl>()
    }

    private val logger: Logger
        get() = LOGGER

    /**
     * 允许重复的 id 的bot。
     *
     */
    private var bots = createConcurrentQueue<QGBotImpl>()

    init {
        job.invokeOnCompletion {
            bots.clear()
        }
    }

    override fun get(id: ID): QGBot {
        if (!isActive) throw IllegalStateException("This manager has been completed")

        val all = all(id).take(2).toList()
        when {
            all.size > 1 -> throw ConflictBotException("id $id with $all")
            all.isEmpty() -> throw NoSuchBotException("id=$id")
            else -> return all.first()
        }
    }

    override fun all(): Sequence<QGBot> {
        if (!isActive) return emptySequence()
        return bots.asSequence()
    }

    override fun all(id: ID): Sequence<QGBot> =
        all().filter { it.isMe(id) }

    private val onRegister = atomic(false)

    override fun register(ticket: Bot.Ticket, block: ConfigurerFunction<QGBotComponentConfiguration>?): QGBot {
        while (true) {
            if (!isActive) throw IllegalStateException("This manager has been completed")
            if (onRegister.compareAndSet(expect = false, value = true)) {
                break
            }
        }

        try {
            return doRegister(ticket, block)
        } finally {
            onRegister.value = false
        }
    }

    override fun register(
        appId: String,
        secret: String,
        token: String,
        block: ConfigurerFunction<QGBotComponentConfiguration>?,
    ): QGBot = register(Bot.Ticket(appId, secret, token), block)

    private fun doRegister(
        ticket: Bot.Ticket,
        block: ConfigurerFunction<QGBotComponentConfiguration>?,
    ): QGBot {
        val configure = configuration.botConfigure
        val config = QGBotComponentConfiguration().invokeBy(block)
        val sourceBot = BotFactory.create(ticket) {
            // init context
            coroutineContext = if (coroutineContext == EmptyCoroutineContext) {
                this@QQGuildBotManagerImpl.coroutineContext
            } else {
                this@QQGuildBotManagerImpl.coroutineContext + coroutineContext
            }

            configure(ticket.appId, ticket.secret, ticket.token)
            config.botConfigure?.invokeWith(this)

            val customJob = coroutineContext[Job]
            if (customJob == null) {
                coroutineContext += SupervisorJob(job)
            } else {
                customJob.linkTo(job)
            }
        }

        // check botInfo
        logger.info("Registered bot appId: {}", sourceBot.ticket.appId)
        val newBot = QGBotImpl(
            source = sourceBot,
            component = component,
            eventDispatcher = eventDispatcher,
            configuration = config
        )

        bots.add(newBot)

        newBot.onCompletion {
            // remove self on completion
            bots.remove(newBot)
        }

        newBot.launch {
            val event = QGBotRegisteredEventImpl(newBot)
            eventDispatcher
                .push(event)
                .onEachError { e ->
                    logger.error("Event {} process failed: {}", event, e, e.content)
                }
                .collect()
        }

        return newBot
    }

}
