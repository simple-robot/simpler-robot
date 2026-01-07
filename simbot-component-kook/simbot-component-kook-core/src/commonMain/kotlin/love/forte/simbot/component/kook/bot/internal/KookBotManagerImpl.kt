/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.bot.internal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import love.forte.simbot.bot.ConflictBotException
import love.forte.simbot.bot.NoSuchBotException
import love.forte.simbot.common.collection.computeValue
import love.forte.simbot.common.collection.concurrentMutableMap
import love.forte.simbot.common.collection.removeValue
import love.forte.simbot.common.coroutines.mergeWith
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.KookComponent
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.component.kook.bot.KookBotConfiguration
import love.forte.simbot.component.kook.bot.KookBotManager
import love.forte.simbot.component.kook.bot.KookBotManagerConfiguration
import love.forte.simbot.component.kook.event.internal.KookBotRegisteredEventImpl
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.event.onEachError
import love.forte.simbot.kook.stdlib.BotFactory
import love.forte.simbot.kook.stdlib.Ticket
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author ForteScarlet
 */
internal class KookBotManagerImpl(
    private val eventProcessor: EventProcessor,
    override val configuration: KookBotManagerConfiguration,
    override val component: KookComponent
) : KookBotManager(), CoroutineScope {
    override val job: Job = configuration.coroutineContext[Job]!!
    override val coroutineContext: CoroutineContext = configuration.coroutineContext

    /**
     * 记录所有注册进来的 [KookBot] .
     */
    private val botMap = concurrentMutableMap<String, KookBotImpl>()

    override fun all(): Sequence<KookBot> = botMap.values.asSequence()


    override fun get(id: ID): KookBot =
        botMap[id.literal] ?: throw NoSuchBotException("id=$id")

    override fun find(id: ID): KookBot? = botMap[id.literal]

    override fun register(ticket: Ticket, configuration: KookBotConfiguration): KookBot {
        val clientId = ticket.clientId
        fun createBot(): KookBotImpl {
            val botConfiguration = configuration.botConfiguration
            botConfiguration.coroutineContext = botConfiguration.coroutineContext.mergeWith(this.coroutineContext)

            val sourceBot = BotFactory.create(ticket, botConfiguration)
            return KookBotImpl(eventProcessor, sourceBot, component, configuration)
        }

        return botMap.computeValue(clientId) { id, old ->
            if (old != null) {
                throw ConflictBotException("clientId=$id")
            }

            createBot()
        }!!.also { newBot ->
            newBot.onCompletion {
                botMap.removeValue(clientId) { newBot }
            }

            // Publish BotRegisteredEvent
            launch {
                val event = KookBotRegisteredEventImpl(newBot)
                eventProcessor.push(event)
                    .onEachError { er ->
                        newBot.logger.error(
                            "Event {} process on failure: {}",
                            event,
                            er.content.message,
                            er.content
                        )
                    }
                    .collect()
            }

        }
    }

}
