/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.core.bot.internal

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.overwriteWith
import love.forte.simbot.bot.BotRegisterFailureException
import love.forte.simbot.bot.NoSuchBotException
import love.forte.simbot.common.collection.computeValue
import love.forte.simbot.common.collection.concurrentMutableMap
import love.forte.simbot.common.collection.removeValue
import love.forte.simbot.common.coroutines.mergeWith
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.onebot.v11.core.OneBot11
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBot
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotConfiguration
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotManager
import love.forte.simbot.component.onebot.v11.core.component.OneBot11Component
import love.forte.simbot.component.onebot.v11.core.event.internal.stage.OneBotBotRegisteredEventImpl
import love.forte.simbot.component.onebot.v11.core.utils.onEachErrorLog
import love.forte.simbot.event.EventProcessor
import kotlin.coroutines.CoroutineContext


/**
 * [OneBotBotManager] 的实现
 * @author ForteScarlet
 */
internal class OneBotBotManagerImpl(
    override val job: Job,
    override val coroutineContext: CoroutineContext,
    private val component: OneBot11Component,
    private val eventProcessor: EventProcessor,
    private val serializersModule: SerializersModule,
) : OneBotBotManager(), CoroutineScope {
    private val decoderJson = Json(OneBot11.DefaultJson) {
        serializersModule = serializersModule overwriteWith this@OneBotBotManagerImpl.serializersModule
    }

    private val bots = concurrentMutableMap<String, OneBotBot>()

    override fun all(): Sequence<OneBotBot> =
        bots.values.asSequence()

    override fun register(configuration: OneBotBotConfiguration): OneBotBot {
        val uniqueId = requireNotNull(configuration.botUniqueId) {
            "Required `botUniqueId` is null"
        }

        val configContext = configuration.coroutineContext
        val mergedContext = configContext.mergeWith(coroutineContext)
        val mergedContextJob = mergedContext[Job]!!
        val job: CompletableJob = mergedContextJob as? CompletableJob ?: SupervisorJob(mergedContextJob)

        fun createBot(): OneBotBotImpl =
            OneBotBotImpl(
                uniqueId,
                mergedContext,
                job,
                configuration,
                component,
                eventProcessor,
                decoderJson
            )

        val created = bots.computeValue(
            uniqueId,
        ) { key, old ->
            if (old == null || !old.isActive) {
                createBot()
            } else {
                throw BotRegisterFailureException("Conflict bot with unique id $key")
            }
        }!!

        created.onCompletion {
            bots.removeValue(uniqueId) { created }
        }

        // push event
        launch {
            eventProcessor
                .push(OneBotBotRegisteredEventImpl(created))
                .onEachErrorLog(logger)
                .collect()
        }


        return created
    }

    override fun get(id: ID): OneBotBot =
        find(id) ?: throw NoSuchBotException(id.toString())

    override fun find(id: ID): OneBotBot? =
        bots[id.literal]


}
