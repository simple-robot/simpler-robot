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
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.component.kaiheila.CoroutineLogger
import love.forte.simbot.component.kaiheila.KhlBot
import love.forte.simbot.component.kaiheila.WebsocketBot
import love.forte.simbot.component.kaiheila.api.Api
import love.forte.simbot.component.kaiheila.api.ApiConfiguration
import love.forte.simbot.component.kaiheila.api.doRequestForData
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildListReq
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildUser
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildViewReq
import love.forte.simbot.component.kaiheila.api.v3.user.Me
import love.forte.simbot.component.kaiheila.api.v3.user.MeReq
import love.forte.simbot.component.kaiheila.event.*
import love.forte.simbot.component.kaiheila.khlJson
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

internal val DefaultClient: HttpClient get() {
    val client = HttpClient(block = Block)
    Runtime.getRuntime().addShutdownHook(thread(start = false, isDaemon = true) {
        client.close()
    })
    return client
}

private val Block: HttpClientConfig<*>.() -> Unit = {
    install(WebSockets)
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
    compress: Int = 1,
) : WebsocketBot, CoroutineScope {

    init {
        if (wsClient.feature(WebSockets) == null) {
            wsClient.config {
                install(WebSockets)
            }
        }
    }

    override val api: Api get() = apiConfiguration.api

    override val apiConfiguration: ApiConfiguration = configuration.apiConfiguration

    /** 获取 [Gateway] 的请求体。 */
    private val gatewayReq = GatewayReq(compress)
    /** 网络日志相关的logger */
    override val networkLog: Logger =
        LoggerFactory.getLogger("love.forte.simbot.component.kaiheila.v3.network.$clientId")

    private val supervisorJob = SupervisorJob(parentContext[Job])

    private val me: Me = runBlocking { MeReq.doRequestForData(this@V3WsBot)!! }

    override val botAvatar: String get() = me.avatar
    override val botName: String get() = me.username

    /** 普通的logger */
    override val log: Logger = LoggerFactory.getLogger("love.forte.simbot.component.kaiheila.v3.bot.$clientId[$botName]")

    override val coroutineContext: CoroutineContext =
        parentContext + supervisorJob +
                CoroutineName("simbot.khl.bot.$clientId") +
                CoroutineLogger(log)


    // ws session
    private lateinit var session: DefaultWebSocketSession
    private lateinit var sessionJob: Job

    private val sn = AtomicInteger(0) // TODO

    /**
     * 启动bot
     */
    override suspend fun start() {
        if (::session.isInitialized) {
            session.close()
        }
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
            val thisTimeWait = waitTime

            log.debug("Retry wait {} in {}", waitTime, times)
            delay(waitTime.toLong())
            waitTime *= 2u
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

                val newCoroutineLogger = nowCoroutineContext[CoroutineLogger] ?: CoroutineLogger(networkLog)

                val newScope = CoroutineScope(nowCoroutineContext + newCoroutineName + newCoroutineLogger)
                sessionJob = newScope.launch {
                    var firstWaitForHello: Job? = launch {
                        println("start firstWaitForHello Job")
                        delay(6000)
                        this@apply.close(CloseReason(CloseReason.Codes.NOT_CONSISTENT, "No Hello!"))
                    }

                    var pingSendJob: Job? = null
                    var waitForPong: Job? = null

                    /**
                     * Do when get text
                     */
                    suspend fun doText(text: String) {
                        println("[Binary-text]: $text")

                        val element = khlJson.parseToJsonElement(text)
                        val s = element.jsonObject["s"]?.jsonPrimitive?.int
                        val signal = when (s) {
                            0 -> khlJson.decodeFromJsonElement<Signal_0>(element)
                            1 -> khlJson.decodeFromJsonElement<Signal_1>(element)
                            // 2 -> khlJson.decodeFromJsonElement<Signal_2>(element) // Ping
                            3 -> khlJson.decodeFromJsonElement<Signal_3>(element)
                            5 -> khlJson.decodeFromJsonElement<Signal_5>(element)
                            6 -> khlJson.decodeFromJsonElement<Signal_6>(element)
                            else -> error(element)
                        }

                        println("[Signal_$s]: $signal")

                        when (signal) {
                            // Hello
                            is Signal.Hello -> {
                                println("Signal.Hello: $signal")
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
                                        log.debug("Wait {} for next ping", wait.toString())
                                        delay(wait.toMillis())
                                        log.debug("Send ping.")
                                        send(Frame.Text(Signal.Ping.jsonValue(0)))
                                        waitForPong = launch(coroutineContext) {
                                            delay(6000)
                                            // 进入超时状态
                                        }
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
                                this@V3WsBot.sn.set(signal.sn)

                                // TODO event

                            }
                            is Signal.Reconnect -> {
                                // reconnect
                                close(CloseReason(CloseReason.Codes.TRY_AGAIN_LATER, "reconnect"))
                            }
                            // is Signal.Ping -> {
                            //
                            // }
                            is Signal.Pong -> {
                                // cancel job
                                waitForPong?.cancelAndJoin()
                            }
                            is Signal.ResumeAck -> {

                            }
                            is Signal.Ping -> {

                            }
                            else -> {
                                println("other $signal")
                            }
                        }


                        if (signal is Signal_0) {
                            println(signal.d)
                        }
                    }

                    while (this@apply.isActive) {
                        try {
                            when (val frame: Frame = incoming.receive()) {
                                is Frame.Text -> {
                                    val text = frame.readText()
                                    println("[Text  ]: $text")
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
                                }
                                is Frame.Pong -> {
                                    println("[Pong: ]: " + frame.readBytes().decodeToString())
                                }
                            }
                        } catch (e: ClosedReceiveChannelException) {
                            // closed
                            throw e
                        } catch (e: Throwable) {
                            throw e
                        }
                    }
                }
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





