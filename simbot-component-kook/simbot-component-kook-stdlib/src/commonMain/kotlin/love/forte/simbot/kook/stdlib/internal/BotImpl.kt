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

package love.forte.simbot.kook.stdlib.internal

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import love.forte.simbot.common.collection.ConcurrentQueue
import love.forte.simbot.common.collection.ExperimentalSimbotCollectionApi
import love.forte.simbot.common.collection.createConcurrentQueue
import love.forte.simbot.common.stageloop.loop
import love.forte.simbot.kook.api.user.GetMeApi
import love.forte.simbot.kook.api.user.Me
import love.forte.simbot.kook.api.user.OfflineApi
import love.forte.simbot.kook.event.Signal
import love.forte.simbot.kook.stdlib.*
import love.forte.simbot.logger.LoggerFactory
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

/**
 *
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotCollectionApi::class)
internal class BotImpl(
    override val ticket: Ticket, override val configuration: BotConfiguration
) : Bot {
    private val botLogger = LoggerFactory.getLogger("love.forte.simbot.kook.bot.${ticket.clientId}")
    internal val eventLogger = LoggerFactory.getLogger("love.forte.simbot.kook.event.${ticket.clientId}")

    // TODO 异常处理器?

    internal val authorization: String = "${ticket.type.prefix} ${ticket.token}"

    private val queueMap = ActualEnumMap.create<SubscribeSequence, ConcurrentQueue<EventProcessor>> {
        createConcurrentQueue()
    }

    override fun subscribe(subscribeSequence: SubscribeSequence, processor: EventProcessor) {
        queueMap[subscribeSequence].add(processor)
    }

    private val job = SupervisorJob(configuration.coroutineContext[Job])
    override val coroutineContext: CoroutineContext = configuration.coroutineContext.minusKey(Job) + job

    override val apiClient: HttpClient = resolveHttpClient(
        configuration,
        configuration.clientEngine,
        configuration.clientEngineFactory,
        configuration.clientEngineConfig
    ).also(::closeOnBotClosed)

    internal val wsClient: HttpClient = resolveWsClient(
        configuration.wsEngine,
        configuration.wsEngineFactory,
        configuration.wsEngineConfig
    ).also(::closeOnBotClosed)

    private fun resolveHttpClient(
        configuration: BotConfiguration,
        engine: HttpClientEngine?,
        engineFactory: HttpClientEngineFactory<*>?,
        engineConfig: BotConfiguration.EngineConfiguration?,
    ): HttpClient = when {
        engine != null -> HttpClient(engine) {
            configApiHttpClient(configuration, engineConfig)
        }

        engineFactory != null -> HttpClient(engineFactory) {
            configApiHttpClient(configuration, engineConfig)
        }

        else -> HttpClient {
            configApiHttpClient(configuration, engineConfig)
        }
    }

    private fun HttpClientConfig<*>.configApiHttpClient(
        configuration: BotConfiguration, engineConfiguration: BotConfiguration.EngineConfiguration?
    ) {
        val apiHttpRequestTimeoutMillis = configuration.timeout?.requestTimeoutMillis
        val apiHttpConnectTimeoutMillis = configuration.timeout?.connectTimeoutMillis
        val apiHttpSocketTimeoutMillis = configuration.timeout?.socketTimeoutMillis

        if (
            apiHttpRequestTimeoutMillis != null ||
            apiHttpConnectTimeoutMillis != null ||
            apiHttpSocketTimeoutMillis != null
        ) {
            install(HttpTimeout) {
                apiHttpRequestTimeoutMillis?.also { requestTimeoutMillis = it }
                apiHttpConnectTimeoutMillis?.also { connectTimeoutMillis = it }
                apiHttpSocketTimeoutMillis?.also { socketTimeoutMillis = it }
            }
        }

        install(HttpRequestRetry) {
            maxRetries = 3
        }

        engineConfiguration?.also { ec ->
            engine {
                ec.pipelining?.also { pipelining = it }
            }
        }
    }

    private fun resolveWsClient(
        engine: HttpClientEngine?,
        engineFactory: HttpClientEngineFactory<*>?,
        engineConfig: BotConfiguration.EngineConfiguration?,
    ): HttpClient = when {
        engine != null -> HttpClient(engine) {
            configWsClient(engineConfig)
        }

        engineFactory != null -> HttpClient(engineFactory) {
            configWsClient(engineConfig)
        }

        else -> HttpClient {
            configWsClient(engineConfig)
        }
    }


    private fun HttpClientConfig<*>.configWsClient(
        engineConfiguration: BotConfiguration.EngineConfiguration?
    ) {
        install(HttpRequestRetry) {
            maxRetries = 3
        }

        WebSockets {
            pingInterval = 30.seconds
            // TODO for JVM:
            //  https://ktor.io/docs/server-websocket-deflate.html#installation
            // extensions {
            //    install(WebSocketDeflateExtension)
            // }
        }

        engineConfiguration?.also { ec ->
            engine {
                ec.pipelining?.also { pipelining = it }
            }
        }
    }


    private fun closeOnBotClosed(client: HttpClient) {
        job.invokeOnCompletion { client.close() }
    }

    override val isActive: Boolean
        get() = job.isActive

    @Volatile
    override var isStarted: Boolean = false

    @Volatile
    private lateinit var _me: Me

    override suspend fun me(): Me {
        return GetMeApi.requestDataBy(this).also {
            _me = it
        }
    }

    override val botUserInfo: Me
        get() = if (::_me.isInitialized) _me else error("Bot is not initialized.")

    override suspend fun offline() {
        OfflineApi.requestBy(this)
    }

    private val startLock = Mutex()

    @Volatile
    private var currentClientJob: Job? = null

    override suspend fun start(closeBotOnFailure: Boolean) {
        try {
            startLock.withLock {
                if (job.isCancelled) {
                    throw CancellationException("Bot has bean cancelled.")
                }
                // close current client if exist
                if (currentClientJob != null) {
                    botLogger.debug("Cancel current client: {}", currentClientJob)
                    currentClientJob?.cancel()
                    currentClientJob = null
                }


                val connect = Connect(this, botLogger, configuration.isCompress)
                botLogger.debug("Create connect: {}", connect)

                var currentState: State? = connect
                while (currentState?.isReceiving == false) {
                    currentState = currentState()
                }

                if (currentState == null) {
                    error("Bot start failed.")
                }

                currentClientJob = launch { currentState.loop() }

                isStarted = true

                val me = me() // init bot user info

                botLogger.info("Bot(id={}, name={}) started", me.id, me.username)
            }
        } catch (e: Throwable) {
            // close this bot
            if (closeBotOnFailure) {
                botLogger.error("Close bot on start failed", e)
                close()
            }
            throw e
        }
    }


    override suspend fun join() {
        job.join()
    }

    override fun close() {
        job.cancel()
        currentClientJob = null
    }

    internal suspend fun processEvent(event: Signal.Event<*>, raw: String) {
        val prepareProcessors = queueMap[SubscribeSequence.PREPARE]
        val normalProcessors = queueMap[SubscribeSequence.NORMAL]
        if (prepareProcessors.size == 0 && normalProcessors.size == 0) {
            eventLogger.trace("prepare processors and normal processors are both empty, skip.")
            return
        }

        val eventData = event.d
        prepareProcessors.forEach { processor ->
            try {
                processor.doInvoke(eventData, raw)
            } catch (e: Throwable) {
                eventLogger.error("Event prepare process failed. Enable debug level log for more information.", e)
                eventLogger.debug(
                    "Event prepare processor {} invoke failed. Event: {}, event.data: {}",
                    processor,
                    event,
                    eventData,
                    e
                )
            }
        }

        suspend fun doNormalProcess() {
            normalProcessors.forEach { processor ->
                try {
                    processor.doInvoke(eventData, raw)
                } catch (e: Throwable) {
                    eventLogger.error("Event normal process failed. Enable debug level log for more information.", e)
                    eventLogger.debug(
                        "Event normal processor {} invoke failed. Event: {}, event.data: {}",
                        processor,
                        event,
                        eventData,
                        e
                    )
                }
            }
        }

        if (configuration.isNormalEventProcessAsync) {
            launch {
                doNormalProcess()
            }
        } else {
            doNormalProcess()
        }


    }

    companion object {
        internal val defaultApiDecoder = Json {
            encodeDefaults = true
            isLenient = true
            allowSpecialFloatingPointValues = true
            prettyPrint = false
            useArrayPolymorphism = false
            ignoreUnknownKeys = true
        }
    }
}
