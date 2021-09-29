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

package love.forte.simbot.component.kaiheila.api.v3

import io.ktor.client.*
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
import love.forte.simbot.component.kaiheila.WebsocketBot
import love.forte.simbot.component.kaiheila.api.ApiConfiguration
import love.forte.simbot.component.kaiheila.api.doRequestForData
import love.forte.simbot.component.kaiheila.event.*
import love.forte.simbot.component.kaiheila.khlJson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom
import java.util.zip.InflaterInputStream
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 *
 *
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public class V3WsBot(
    override val clientId: String,
    @Volatile override var token: String,
    override var clientSecret: String,
    val client: HttpClient,
    val configuration: V3BotConfiguration,
    val wsClient: HttpClient = client,
    parentContext: CoroutineContext = EmptyCoroutineContext,
    compress: Int = 1,
) : WebsocketBot, CoroutineScope {
    override val apiConfiguration: ApiConfiguration = configuration.apiConfiguration

    /** 获取 [Gateway] 的请求体。 */
    private val gatewayReq = GatewayReq(compress)

    /** 普通的logger */
    override val log: Logger = LoggerFactory.getLogger("love.forte.simbot.component.kaiheila.v3.bot.$clientId")

    /** 网络日志相关的logger */
    override val networkLog: Logger =
        LoggerFactory.getLogger("love.forte.simbot.component.kaiheila.v3.network.$clientId")

    private val supervisorJob = SupervisorJob(parentContext[Job])

    override val coroutineContext: CoroutineContext =
        parentContext + supervisorJob + CoroutineName("simbot.khl.bot.$clientId") + CoroutineLogger(log)

    override val botAvatar: String?
        get() = null

    override val botName: String
        get() = TODO()


    // ws session
    private lateinit var session: DefaultWebSocketSession
    private lateinit var sessionJob: Job

    private val sn = 0 // TODO

    /**
     * 启动bot
     */
    override suspend fun start() {
        if (::session.isInitialized) {
            session.close()
        }
        val maxRetry = configuration.connectRetryTimes
        var times = 0u
        var success = false

        while (!success) {
            log.debug("Try start bot $clientId")
            success = linkWs()
            println("Success: $success")
            if (success) {
                break
            }
            times++
            if (times >= maxRetry) {
                error("Cannot start bot, retry $times times, give up.")
            }
            val thisTimeWait = if (times > 60u || times shl 1 > 60u) 60u else times shl 1
            log.debug("Retry wait {}", thisTimeWait.toLong() * 2)
            delay(thisTimeWait.toLong())
        }
    }


    private suspend fun getGateway(): Result<Gateway> =
        kotlin.runCatching { gatewayReq.doRequestForData(apiConfiguration.api, client, token)!! }


    private suspend fun linkWs(): Boolean {
        // for gateway

        val gateway: Gateway = getGateway().getOrElse { e ->
            log.error("Gateway failed.", e)
            null
        } ?: return false

        // do {
        //     log.debug("Try get gateway by {}", GatewayReq.id)
        //     val result = getGateway()
        //     gateway = result.getOrNull()
        // } while (gateway == null)

        val url = gateway.url

        log.debug("Gateway url {}", url)

        val nowCoroutineContext = coroutineContext

        try {
            session = wsClient.webSocketSession {
                this.url.takeFrom(url)
            }.apply {
                val newCoroutineName = nowCoroutineContext[CoroutineName]?.let {
                    CoroutineName("simbot.khl.bot.ws.$clientId." + it.name)
                } ?: CoroutineName("simbot.khl.bot.ws.$clientId")

                val newCoroutineLogger = nowCoroutineContext[CoroutineLogger] ?: CoroutineLogger(networkLog)

                withContext(nowCoroutineContext + newCoroutineName + newCoroutineLogger) {
                    sessionJob = launch {
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
        // TODO
        session.close()
        supervisorJob.cancel(cause?.let { CancellationException(it.localizedMessage, it) })
    }


}





