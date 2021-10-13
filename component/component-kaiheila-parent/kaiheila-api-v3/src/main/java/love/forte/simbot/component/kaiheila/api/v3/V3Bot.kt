/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("V3BotUtil")

package love.forte.simbot.component.kaiheila.api.v3

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.common.collections.concurrentSortedQueueOf
import love.forte.simbot.AtomicRef
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.sender.BotSender
import love.forte.simbot.component.kaiheila.*
import love.forte.simbot.component.kaiheila.api.Api
import love.forte.simbot.component.kaiheila.api.ApiConfiguration
import love.forte.simbot.component.kaiheila.api.doRequestForData
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildListReq
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildUser
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildViewReq
import love.forte.simbot.component.kaiheila.api.v3.sender.KhlV3Getter
import love.forte.simbot.component.kaiheila.api.v3.sender.KhlV3Sender
import love.forte.simbot.component.kaiheila.api.v3.sender.KhlV3Setter
import love.forte.simbot.component.kaiheila.api.v3.user.Me
import love.forte.simbot.component.kaiheila.api.v3.user.MeReq
import love.forte.simbot.component.kaiheila.event.*
import love.forte.simbot.component.kaiheila.objects.Channel
import love.forte.simbot.component.kaiheila.objects.Guild
import love.forte.simbot.component.kaiheila.objects.User
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.InflaterInputStream
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume

internal val DefaultClient: HttpClient
    get() {
        val client = HttpClient(HttpClientConfig)
        Runtime.getRuntime().addShutdownHook(thread(start = false, isDaemon = true) {
            client.close()
        })
        return client
    }

private val HttpClientConfig: HttpClientConfig<*>.() -> Unit = {
    install(WebSockets)
    install(JsonFeature) {
        serializer = KotlinxSerializer(khlJson)
    }
    install(HttpTimeout) {
        this.connectTimeoutMillis = 6000
    }
}


@JvmOverloads
public fun v3WsBot(
    clientId: String,
    token: String,
    clientSecret: String,
    configuration: V3BotConfiguration = v3BotConfiguration {
        apiConfiguration {
            api = V3
        }
    },
    client: HttpClient = DefaultClient,
    wsClient: HttpClient = DefaultClient,
): KhlBot = V3WsBot(clientId, token, clientSecret, client, configuration, wsClient)


/**
 *
 * V3版本api下，ws协议的Bot实例。
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public class V3WsBot(
    override val clientId: String,
    @Volatile
    override var token: String,
    @Volatile
    override var clientSecret: String,
    override val client: HttpClient,
    val configuration: V3BotConfiguration,
    val wsClient: HttpClient = client,
    parentContext: CoroutineContext = EmptyCoroutineContext,
) : WebsocketBot, CoroutineScope {

    init {
        if (wsClient.feature(WebSockets) == null) {
            wsClient.config {
                install(WebSockets)
            }
        }
    }

    private class Listener(val priority: Int, private val f: suspend (Event<*>) -> Unit) {
        suspend operator fun invoke(event: Event<*>) = f(event)
    }

    private val listeners = concurrentSortedQueueOf<Listener>(Comparator.comparingInt(Listener::priority))
    private var listenersCached: List<Listener>? = null

    private val eventLocator = configuration.eventLocator

    override val api: Api get() = apiConfiguration.api
    override val apiConfiguration: ApiConfiguration = configuration.apiConfiguration

    /** 获取 [Gateway] 的请求体。 */
    private val gatewayReq = GatewayReq(configuration.compress)

    /** 网络日志相关的logger */
    override val networkLog: Logger =
        LoggerFactory.getLogger("love.forte.simbot.component.kaiheila.v3.network.$clientId")

    private val supervisorJob = SupervisorJob(parentContext[Job])

    private val me: Me = runBlocking { MeReq.doRequestForData(this@V3WsBot)!! }

    private val info = KhlBotInfo()

    override val botInfo: BotInfo
        get() = info

    private inner class KhlBotInfo : BotInfo {
        override val botCode: String get() = me.id
        override val botName: String get() = me.username
        override val botAvatar: String get() = me.avatar
    }

    override val sender: BotSender = BotSender(
        KhlV3Sender(
            this,
            configuration.senderFactories.defaultSenderFactory.getOnBotSender(this)
        ),
        KhlV3Setter(
            this,
            configuration.senderFactories.defaultSetterFactory.getOnBotSetter(this),
            this
        ),
        KhlV3Getter(
            this,
            configuration.senderFactories.defaultGetterFactory.getOnBotGetter(this)
        ),
        botInfo
    )

    /** 普通的logger */
    override val log: Logger =
        LoggerFactory.getLogger("love.forte.simbot.component.kaiheila.v3.bot.$clientId[$botName#$botCode]")

    override val coroutineContext: CoroutineContext =
        parentContext + supervisorJob +
                CoroutineName("simbot.khl.bot.$clientId") +
                CoroutineLogger(log)


    // ws session
    private lateinit var session: DefaultWebSocketSession
    private lateinit var sessionJob: Job

    private val sn = AtomicInteger(0)

    @Synchronized
    private suspend fun resetAll() {
        if (::session.isInitialized) {
            session.close()
        }
        if (::sessionJob.isInitialized) {
            sessionJob.cancelAndJoin()
        }
        sn.set(0)
    }

    /**
     * 启动bot
     */
    override suspend fun start() {
        resetAll()
        val maxRetry = configuration.connectRetryTimes
        var times = 0u
        var waitTime = 1000u
        var success = false

        while (!success) {
            log.debug("Try start bot $clientId")
            success = linkWs()
            log.debug("Success: $success")
            if (success) {
                break
            }
            times++
            if (times >= maxRetry) {
                error("Cannot start bot, retry $times times, give up.")
            }
            // val thisTimeWait = waitTime

            log.debug("Retry wait {} in {}", waitTime, times)
            delay(waitTime.toLong())
            waitTime *= 2u
        }

    }

    override fun listen(priority: Int, listener: suspend (Event<*>) -> Unit) {
        listeners.add(Listener(priority, listener)).also {
            resetListenersCache()
        }
    }

    private fun resetListenersCache() {
        synchronized(listeners) {
            listenersCached = null
        }
    }

    private fun listenersCache(): List<Listener> {
        return listenersCached ?: synchronized(listeners) {
            listenersCached ?: listeners.toList().also {
                listenersCached = it
            }
        }
    }

    private suspend fun getGateway(): Result<Gateway> =
        kotlin.runCatching { gatewayReq.doRequestForData(this)!! }


    private suspend fun linkWs(): Boolean {
        // for gateway

        val gateway: Gateway = getGateway().getOrElse { e ->
            log.error("Gateway failed.", e)
            null
        } ?: return false

        val url = gateway.url

        log.debug("Gateway url {}", url)

        val nowCoroutineContext = coroutineContext

        try {
            session = wsClient.webSocketSession() {
                this.url.takeFrom(url)
            }.apply {
                val newCoroutineName = nowCoroutineContext[CoroutineName]?.let {
                    CoroutineName("simbot.khl.bot.ws.$clientId." + it.name)
                } ?: CoroutineName("simbot.khl.bot.ws.$clientId")

                val nowLogger = nowCoroutineContext[CoroutineLogger] ?: CoroutineLogger(networkLog)

                val newScope = CoroutineScope(nowCoroutineContext + newCoroutineName + nowLogger)
                // sessionJob = newScope.launch {
                var firstWaitForHello: Job? = launch(start = CoroutineStart.LAZY) {
                    nowLogger.debug("Start firstWaitForHello Job")
                    delay(6000)
                    this@apply.close(CloseReason(CloseReason.Codes.NOT_CONSISTENT, "No Hello!"))
                }


                var pingSendJob: Job? = null
                var waitForPong: Job? = null
                val resumeContinuation = AtomicRef<CancellableContinuation<String>?>(null)

                suspend fun sendPing() {
                    send(Frame.Text(Signal.Ping.jsonValue(sn.get())))
                    waitForPong?.cancelAndJoin()
                    waitForPong = launch(coroutineContext) {
                        delay(6000)
                        // TODO 进入超时状态
                    }
                }

                suspend fun waitForResumeSession(): String = suspendCancellableCoroutine { continuation ->
                    resumeContinuation.updateAndGet { pre ->
                        pre?.cancel()
                        continuation
                    }
                }

                // send resume
                suspend fun resume() {
                    // Send resume, and wait for resumeAsk.
                    send(Frame.Text(Signal.Resume.jsonValue(sn.get())))

                    try {
                        val session = waitForResumeSession()
                        nowLogger.debug("ResumeAsk session: {}", session)
                    } catch (cancelled: CancellationException) {
                        nowLogger.debug("ResumeAsk waiting reset.")
                    }
                }


                /**
                 * Do when get text
                 */
                suspend fun doText(text: String): Event<*>? {
                    nowLogger.debug("[Do-Text]: {}", text)

                    val element = khlJson.parseToJsonElement(text)
                    val s = element.jsonObject["s"]?.jsonPrimitive?.int
                    val signal = when (s) {
                        0 -> khlJson.decodeFromJsonElement(Signal_0.serializer(), element)
                        1 -> khlJson.decodeFromJsonElement(Signal_1.serializer(), element)
                        // 2 -> khlJson.decodeFromJsonElement(Signal_2.serializer(), element) // Ping
                        3 -> khlJson.decodeFromJsonElement(Signal_3.serializer(), element)
                        4 -> khlJson.decodeFromJsonElement(Signal_4.serializer(), element)
                        5 -> khlJson.decodeFromJsonElement(Signal_5.serializer(), element)
                        6 -> khlJson.decodeFromJsonElement(Signal_6.serializer(), element)
                        else -> {
                            throw KhlSignalException("Unknown signal number: $s")
                        } // error(element)
                    }

                    nowLogger.debug("[Signal_{}]: {}", s, signal)

                    when (signal) {
                        // Hello
                        is Signal.Hello -> {
                            nowLogger.debug("Signal.Hello: {}", signal)
                            firstWaitForHello?.cancel()
                            firstWaitForHello = null

                            pingSendJob?.cancelAndJoin()

                            pingSendJob = launch(coroutineContext) {
                                while (this@apply.isActive) {
                                    val r = ThreadLocalRandom.current()
                                    val wait: Duration =
                                        if (r.nextBoolean()) Duration.ofSeconds(30) + Duration.ofSeconds(
                                            r.nextLong(5))
                                        else Duration.ofSeconds(30) - Duration.ofSeconds(r.nextLong(
                                            5))
                                    nowLogger.debug("Wait {} for next ping", wait)
                                    delay(wait.toMillis())
                                    nowLogger.debug("Send ping.")
                                    sendPing()
                                    // send(Frame.Text(Signal.Ping.jsonValue(sn.get())))

                                }
                            }

                        }
                        is Signal.Event -> {
                            /*
                                注意： 该消息会有 `sn`, 代表消息序号,
                                针对当前 `session` 的消息的序号,
                                客户端需记录该数字,
                                并按顺序接收消息，
                                `resume` 时需传入该参数才能完成。
                             */
                            val lastSn = this@V3WsBot.sn.get()
                            if (lastSn >= signal.sn) {
                                // 如果当前事件是已经处理过的事件，直接抛弃
                                nowLogger.debug("LastSn({}) > signal.sn({}), return null.", lastSn, signal.sn)
                                return null
                            }

                            if (lastSn - signal.sn > 1) {
                                // 如果事件差距超过1, 说明事件超前了
                                // 也直接抛弃，并发送一个resume.
                                nowLogger.debug("LastSn({}) - signal.sn({}) > 1, drop and resume.", lastSn, signal.sn)
                                resume()
                                return null
                            }

                            this@V3WsBot.sn.set(signal.sn)

                            val eventData = signal.d.jsonObject
                            val eventType = eventData["type"]?.jsonPrimitive?.int ?: kotlin.run {
                                nowLogger.debug("EventData[type] was null. {}", eventData)
                                return null
                            }
                            nowLogger.debug("Event type: {}", eventType)

                            val channelType = eventData["channel_type"]?.jsonPrimitive?.content?.let {
                                nowLogger.debug("Channel type primitive: {}", it)
                                when {
                                    it.equals("group", true) -> Channel.Type.GROUP
                                    it.equals("person", true) -> Channel.Type.PERSON
                                    else -> null
                                }
                            }
                            nowLogger.debug("Channel type: {}", channelType)


                            val extraType =
                                eventData["extra"]?.jsonObject?.get("type")?.jsonPrimitive?.content ?: kotlin.run {
                                    nowLogger.debug("EventData[extra][type] was null. {}", eventData)
                                    return null
                                }

                            val serializer =
                                eventLocator.locateAsEvent(eventType, channelType, extraType) ?: kotlin.run {
                                    nowLogger.debug("eventLocator.locateAsEvent({}, {}, {}) was null. {}",
                                        eventType,
                                        channelType,
                                        extraType,
                                        eventData)
                                    return null
                                }

                            return khlJson.decodeFromJsonElement(serializer, eventData)
                        }
                        is Signal.Reconnect -> {
                            nowLogger.debug("Received the reconnect signal. data: {}", signal.d)
                            // reconnect
                            close(CloseReason(CloseReason.Codes.TRY_AGAIN_LATER, "reconnect: ${signal.d.err}"))
                            this@V3WsBot.start()
                            // TODO reconnect

                        }

                        is Signal.Pong -> {
                            // cancel job
                            waitForPong?.cancelAndJoin()
                        }

                        is Signal.ResumeAck -> {
                            val sessionId = signal.d.sessionId
                            resumeContinuation.updateAndGet { pre ->
                                kotlin.runCatching { pre?.resume(sessionId) }
                                null
                            }
                        }
                        // is Signal.Ping -> {
                        // No way. maybe.
                        // }
                        else -> {
                            nowLogger.warn("Other unknown or client to server signal numbered {}: {}", signal.s, signal)
                        }
                    }


                    if (signal is Signal_0) {
                        nowLogger.debug("Signal_0: {}", signal.d)
                    }

                    return null
                }


                sessionJob = incoming.receiveAsFlow().mapNotNull { frame ->
                    nowLogger.debug("[Frame  ]: {}", frame)
                    try {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                nowLogger.debug("[Text  ]: {}", text)
                                doText(text)
                            }
                            is Frame.Binary -> {
                                val text =
                                    InflaterInputStream(frame.readBytes().inputStream()).reader(Charsets.UTF_8)
                                        .use { it.readText() }
                                doText(text)
                            }
                            is Frame.Close -> {
                                println("[Close ]: " + frame.readBytes().decodeToString())
                                this@apply.close()
                                null
                            }
                            is Frame.Pong -> {
                                println("[Pong: ]: " + frame.readBytes().decodeToString())
                                null
                            }
                            is Frame.Ping -> {
                                println("[Ping: ]: $frame")
                                null
                            }
                        }
                    } catch (e: ClosedReceiveChannelException) {
                        // closed
                        throw e
                    } catch (e: Throwable) {
                        throw e
                    }
                }.onStart {
                    firstWaitForHello?.start()
                }.onEach {
                    nowLogger.debug("Event: $it")

                    listenersCache().forEach { l ->
                        l.invoke(it)
                    }

                }.launchIn(newScope)
            }

        } catch (e: Throwable) {
            log.error("Ws link fail.", e)
            return false
        }
        return true
    }


    /**
     * 终止bot
     */
    override suspend fun close(cause: Throwable?) {
        sessionJob.cancel(cause?.let { CancellationException(it.localizedMessage, it) })
        session.close()
        supervisorJob.cancel(cause?.let { CancellationException(it.localizedMessage, it) })
        wsClient.close()
        client.close()
    }

    override suspend fun join() {
        supervisorJob.join()
    }


    override fun close() {
        closeBot()
    }

    override suspend fun guilds(): List<Guild> {


        return GuildListReq.SortById.Asc.doRequestForData(this, client).items
    }

    override suspend fun guild(guildId: String): Guild {
        return GuildViewReq(guildId).doRequestForData(this, client)!!
    }

    override suspend fun viewUser(guildId: String, userId: String): User {
        GuildUser
        TODO("Not yet implemented")
    }
}





