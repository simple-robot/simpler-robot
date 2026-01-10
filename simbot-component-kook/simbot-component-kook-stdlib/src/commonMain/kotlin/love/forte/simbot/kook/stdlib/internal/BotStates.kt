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
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.common.atomic.AtomicLong
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.atomic.updateAndGet
import love.forte.simbot.kook.InternalKookApi
import love.forte.simbot.kook.api.Gateway
import love.forte.simbot.kook.api.GetGatewayApi
import love.forte.simbot.kook.event.EventExtra
import love.forte.simbot.kook.event.Signal
import love.forte.simbot.kook.event.UnknownExtra
import love.forte.simbot.kook.stdlib.KookBotClientCloseException
import love.forte.simbot.kook.stdlib.KookBotConnectException
import love.forte.simbot.kook.stdlib.requestDataBy
import love.forte.simbot.logger.Logger
import kotlin.math.max
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException as CoroutineCancellationException
import kotlinx.coroutines.CancellationException as CreateCancellationException


internal sealed class State : love.forte.simbot.common.stageloop.State<State>() {
    internal abstract val bot: BotImpl
    internal abstract val botLogger: Logger
    internal open val isReceiving: Boolean get() = false
}

internal data class ReconnectInfo(val sn: Long, val sessionId: String)

internal data class RetryInfo(val time: Int = 1, val exceptions: List<Throwable> = emptyList())

private fun RetryInfo?.incr(exception: Throwable? = null): RetryInfo {
    if (this == null) {
        return RetryInfo(time = 1, exceptions = exception?.let { listOf(it) } ?: emptyList())
    }

    return copy(
        time = time + 1,
        exceptions = buildList {
            addAll(exceptions)
            exception?.also(::add)
        }
    )
}

/**
 * state: 获取网关信息并创建连接（前的准备工作）
 *
 * @param isCompress 是否压缩
 * @param retryInfo 重连信息，如果为null则是第一次连接。如果重联次数 time > 3 则会整合异常并抛出。
 */
internal class Connect(
    override val bot: BotImpl,
    override val botLogger: Logger,
    val isCompress: Boolean,
    /**
     * 重连信息，如果为null则是第一次连接
     */
    val retryInfo: RetryInfo? = null,
    /**
     * 重连信息，如果存在则为重连。
     */
    private val reconnect: ReconnectInfo? = null,
) : State() {

    /**
     * @throws KookBotConnectException 连接失败
     */
    override suspend fun invoke(): State {
        // 获取 gateway
        botLogger.debug("Requesting for gateway info...")

        if (retryInfo != null) {
            botLogger.debug("Requesting for gateway info, Retry info: {}", retryInfo)
            if (retryInfo.time > 3) {
                // throw it.
                throw KookBotConnectException("Connect failed. Retry times: ${retryInfo.time}").also { e ->
                    retryInfo.exceptions.forEach(e::addSuppressed)
                }
            }
        }

        val gateway = GetGatewayApi.create(isCompress).requestDataBy(bot).info {
            if (reconnect != null) {
                botLogger.debug("Reconnect: sn: {}, sessionId: {}", reconnect.sn, reconnect.sessionId)
                parameters["session_id"] = reconnect.sessionId
                parameters["sn"] = reconnect.sn.toString()
                parameters["resume"] = "1"
            }
        }

        botLogger.debug("Gateway info: {}, next state: CreateWsSession", gateway)
        return CreateWsSession(bot, botLogger, RequestedGateway(this, gateway))
    }
}

private data class GatewayInfo(val url: String, val urlBuilder: URLBuilder.() -> Unit = {})

private fun Gateway.info(urlBuilder: URLBuilder.() -> Unit = {}): GatewayInfo = GatewayInfo(url, urlBuilder)

private class RequestedGateway(val connect: Connect, val gateway: GatewayInfo)

/**
 * 根据 gateway 信息创建 ws 会话
 */
private class CreateWsSession(
    override val bot: BotImpl,
    override val botLogger: Logger,
    val gateway: RequestedGateway
) : State() {
    override suspend fun invoke(): State {
        botLogger.debug("Creating ws session... gateway: {}", gateway)

        val session = try {
            val session = connectWs()
            botLogger.debug("Ws session created: {}", session)
            session
        } catch (e: Throwable) {
            botLogger.error("Connect to gateway {} failed, msg: {}, retry.", gateway, e.message)
            botLogger.debug("Connect to gateway {} failed, msg: {}, retry.", gateway, e.message, e)

            return Connect(
                bot = bot,
                botLogger = botLogger,
                isCompress = gateway.connect.isCompress,
                retryInfo = gateway.connect.retryInfo.incr(e)
            )
        }

        return WaitingHello(bot, botLogger, gateway, session)
    }

    private suspend fun connectWs(): DefaultClientWebSocketSession {
        return bot.wsClient.ws(gateway.gateway)
    }

    /**
     * 通过 [Gateway] 连接bot信息。
     */
    private suspend fun HttpClient.ws(gateway: GatewayInfo): DefaultClientWebSocketSession {
        return webSocketSession {
            url {
                takeFrom(gateway.url)
                gateway.apply {
                    urlBuilder()
                }
            }
        }
    }
}

/**
 * 等待 hello 消息
 */
private class WaitingHello(
    override val bot: BotImpl,
    override val botLogger: Logger,
    val gateway: RequestedGateway,
    val wsSession: DefaultClientWebSocketSession
) : State() {
    override suspend fun invoke(): State {
        val wsConnectTimeout = bot.configuration.wsConnectTimeout

        val hello = try {
            withTimeout(wsConnectTimeout) {
                wsSession.waitHello()
            }
        } catch (timeout: TimeoutCancellationException) {
            // TODO: timeout
            botLogger.error("Connect to gateway for waiting hello timeout in {}ms. retry.", wsConnectTimeout)
            botLogger.debug("Connect to gateway for waiting hello timeout in {}ms. retry.", wsConnectTimeout, timeout)
            wsSession.cancel("Connect to gateway for waiting hello timeout in ${wsConnectTimeout}ms.")
            // 回退到第一步
            return Connect(
                bot = bot,
                botLogger = botLogger,
                isCompress = gateway.connect.isCompress,
                retryInfo = gateway.connect.retryInfo.incr(timeout)
            )
        }

        botLogger.debug("Hello received: {}", hello)

        return CreateHeartbeatJob(bot, botLogger, gateway, wsSession, hello)
    }

    private suspend inline fun DefaultClientWebSocketSession.waitHello(): Signal.Hello {
        // receive Hello
        var hello: Signal.Hello? = null

        while (isActive) {
            val h = waitForHello(BotImpl.defaultApiDecoder, this)
            if (h != null) {
                hello = h
                break
            }
        }

        if (hello == null) {
            val reason = closeReason.await() // .err()
            throw KookBotClientCloseException(reason, "Receive hello signal failed: $reason")
        }

        return hello
    }

    private suspend fun waitForHello(decoder: Json, session: DefaultClientWebSocketSession): Signal.Hello? {
        var hello: Signal.Hello? = null
        // for hello
//        hello = session.receiveDeserialized<Signal.Hello>()
//        val json = session.receiveDeserialized<JsonElement>()
        val frame = session.incoming.receive()
        if (frame is Frame.Text || frame is Frame.Binary) {
            val json = decoder.parseToJsonElement(frame.readToText())
            if (json.jsonObject["s"]?.jsonPrimitive?.intOrNull == Signal.S_HELLO) {
                hello = decoder.decodeFromJsonElement(Signal.Hello.serializer(), json)
            }
        }

        return hello
    }
}

/**
 * 接收到 Hello 后，创建心跳任务
 */
private class CreateHeartbeatJob(
    override val bot: BotImpl,
    override val botLogger: Logger,
    val gateway: RequestedGateway,
    val session: DefaultClientWebSocketSession,
    val hello: Signal.Hello,
) : State() {
    override suspend fun invoke(): State {
        val sn = atomic(0L)
        botLogger.debug("Creating heartbeat job for session: {}", session)

        val heartbeatJob = session.heartbeatJob(sn)

        botLogger.debug("Heartbeat job created: {}", heartbeatJob)

        return CreateClient(bot, botLogger, session, hello, gateway, sn, heartbeatJob)
    }

    private fun DefaultClientWebSocketSession.heartbeatJob(sn: AtomicLong): HeartbeatJob {
        fun helloInterval(): Long {
            val r = kotlin.random.Random.nextLong(5000)
            return if (kotlin.random.Random.nextBoolean()) 30_000 + r else 30_000 - r
        }

        val pongChannel = Channel<Signal.Pong>(1, BufferOverflow.DROP_OLDEST)

        // heartbeat Job
        val heartbeatLaunchJob = launch {
            suspend fun waitForPong(timeout: Long): Signal.Pong {
                return withTimeout(timeout) {
                    pongChannel.receive()
                }
            }

            val pongTimeout = 6.seconds.inWholeMilliseconds

            hb@ while (isActive) {
                val ping = Signal.Ping(sn.value.toInt())
                botLogger.trace("Send 'Ping' {}", ping)
                send(ping.jsonValue())

                // wait for pong
                try {
                    // 如果超时:
                    // 在连接中，每隔 30 秒发一次心跳 ping 包，如果 6 秒内，没有收到心跳 pong 包，则超时。进入到指数回退，重试。
                    botLogger.trace("Waiting for 'Pong'")
                    val pong = waitForPong(pongTimeout)
                    botLogger.debug("Received pong {} in 6s", pong)
                } catch (timeout: TimeoutCancellationException) {
                    // timeout for waiting pong!
                    botLogger.trace("pong receive timeout: {}", timeout.message, timeout)

                    // retry twice
                    for (i in 1..2) {
                        try {
                            val pong = waitForPong(pongTimeout)
                            botLogger.debug("Received pong {} in 6s (timeout retry {})", pong, i)
                            continue@hb
                        } catch (timeout0: TimeoutCancellationException) {
                            botLogger.debug(
                                "Received pong timeout (timeout retry {}): {}",
                                i,
                                timeout0.message,
                                timeout0
                            )
                            delay((i * 2).seconds)
                            continue
                        }
                    }

                    botLogger.error("pong receive timeout: {}, Cancel session", timeout.message, timeout)
                    // close exceptionally
                    // closeExceptionally()
                    close()
                    // cancel("pong receive timeout: ${timeout.localizedMessage}", timeout)
                    break
                }

                delay(helloInterval())
            }
        }

        heartbeatLaunchJob.invokeOnCompletion {
            pongChannel.close()
        }

        return HeartbeatJob(heartbeatLaunchJob, pongChannel)
    }
}

private class HeartbeatJob(val job: Job, val pongChannel: Channel<Signal.Pong>) {
    fun pong(pong: Signal.Pong) {
        pongChannel.trySend(pong)
    }

    fun cancel() {
        job.cancel()
    }

    override fun toString(): String {
        return "HeartbeatJob(job=$job)"
    }
}

/**
 * 创建事件处理链接
 */
private class CreateClient(
    override val bot: BotImpl,
    override val botLogger: Logger,
    private val session: DefaultClientWebSocketSession,
    private val hello: Signal.Hello,
    private val gateway: RequestedGateway,
    private val sn: AtomicLong,
    private val heartbeatJob: HeartbeatJob
) : State() {

    override suspend fun invoke(): State {
        // TODO Array of Channel and hash by guild id
        val eventProcessChannel = Channel<EventData>(capacity = Channel.BUFFERED)
        eventProcessChannel.invokeOnClose { cause ->
            botLogger.debug("Event process closed, cause: {}", cause?.message, cause)
        }
        botLogger.debug("Creating event process channel: {}", eventProcessChannel)

        val eventProcessJob = eventProcessJob(eventProcessChannel)

        return Receiving(
            bot,
            botLogger,
            hello,
            Client(gateway.connect.isCompress, session, sn, heartbeatJob, eventProcessJob)
        )
    }

    private fun eventProcessJob(channel: Channel<EventData>): EventProcessJob {
        val job = channel
            .receiveAsFlow()
            .cancellable()
            .buffer()
            .onEach { (event, raw) -> bot.processEvent(event, raw) }
            .catch { e ->
                bot.eventLogger.error("Event process flow on error. Cause: {}", e.message, e)
            }.onCompletion { e ->
                if (e == null) {
                    bot.eventLogger.debug("Event process flow is completed. No exception.")
                } else {
                    bot.eventLogger.debug(
                        "Event process flow is completed. Cause: {}",
                        e.message,
                        e
                    )
                }
            }.launchIn(bot)

        job.invokeOnCompletion { channel.cancel() }

        return EventProcessJob(job, channel)
    }
}

private class EventProcessJob(
    val job: Job,
    val eventChannel: Channel<EventData>,
) {
    suspend fun sendEvent(eventData: EventData) {
        eventChannel.send(eventData)
    }

    fun trySendEvent(eventData: EventData): ChannelResult<Unit> {
        return eventChannel.trySend(eventData)
    }

    fun cancel() {
        job.cancel()
    }

    override fun toString(): String {
        return "EventProcessJob(job=$job, eventChannel=$eventChannel)"
    }
}

private class Client(
    val isCompress: Boolean,
    val session: DefaultClientWebSocketSession,
    val sn: AtomicLong,
    val heartbeatJob: HeartbeatJob,
    val eventProcessJob: EventProcessJob
) {
    fun cancel(sessionCause: CoroutineCancellationException? = null) {
        session.cancel(sessionCause)
        heartbeatJob.cancel()
        eventProcessJob.cancel()
    }
}

/**
 * 事件循环接收状态
 */
private class Receiving(
    override val bot: BotImpl,
    override val botLogger: Logger,
    private val hello: Signal.Hello,
    private val client: Client
) : State() {

    override val isReceiving: Boolean
        get() = true

    private inline val eventLogger
        get() = bot.eventLogger

    @Suppress("ReturnCount")
    override suspend fun invoke(): State? {
        val session = client.session
        if (!session.isActive) {
            // 会话不再活跃。
            val reason = session.closeReason.await()
            return if (!bot.isActive) {
                botLogger.error("The session [{}] (and bot) is no longer active. reason: {}", session, reason)
                client.cancel()
                null
            } else {
                botLogger.error("The session [{}] is no longer active. reason: {}, try to reconnect", session, reason)
                client.cancel()
                Reconnect(bot, botLogger, client.isCompress, client.sn.value, hello.d.sessionId)
            }
        }

        try {
            eventLogger.trace("Receiving next frame...")
            val frameCatching = session.incoming.receiveCatching()
            frameCatching.onFailure { cause ->
                eventLogger.error(
                    "Receiving next frame on failure: {}, close current session and try to reconnect",
                    cause?.message,
                    cause
                )
                val cancelException = CreateCancellationException(
                    "Receiving next frame on failure,, close current session and try to reconnect",
                    cause
                )
                client.cancel(sessionCause = cancelException)
                return Reconnect(bot, botLogger, client.isCompress, client.sn.value, hello.d.sessionId)
            }
            frameCatching.onClosed { cause ->
                eventLogger.error(
                    "Receiving next frame on closed: {}, close current session and try to reconnect",
                    cause?.message,
                    cause
                )
                val cancelException = CreateCancellationException(
                    "Receiving next frame on failure,, close current session and try to reconnect",
                    cause
                )
                client.cancel(sessionCause = cancelException)
                return Reconnect(bot, botLogger, client.isCompress, client.sn.value, hello.d.sessionId)
            }

            val frame = frameCatching.getOrThrow()
//            val frame = session.incoming.receive()
            eventLogger.trace("Next frame: {}", frame)

            try {
                when (frame) {
                    is Frame.Text -> return processSignalString(frame.readText())
                    is Frame.Binary -> return processSignalString(frame.readToText())
                    else -> {
                        eventLogger.trace("Other frame: {}", frame)
                    }
                }
            } catch (processException: Throwable) {
                eventLogger.error("An exception was thrown while processing the event frame", processException)
            }

            return this
        } catch (cancellation: CreateCancellationException) {
            // 似乎不会到这儿来？
            client.cancel()
            return if (!bot.isActive) {
                botLogger.warn("Session (and bot) is cancelled: {}", cancellation.message)
                botLogger.debug("Session (and bot) is cancelled: {}", cancellation.message, cancellation)
                null
            } else {
                botLogger.warn("Session is cancelled: {}, try to reconnect", cancellation.message)
                botLogger.debug("Session is cancelled: {}, try to reconnect", cancellation.message, cancellation)
                // reconnect?
                Reconnect(bot, botLogger, client.isCompress, client.sn.value, hello.d.sessionId)
            }

        } catch (e: Throwable) {
            botLogger.error("Session received frame failed: {}, cancel current session and try reconnect", e.message, e)
            client.cancel(sessionCause = CreateCancellationException("unexpected exception was received", e))
            // next: Reconnect
            return Reconnect(bot, botLogger, client.isCompress, client.sn.value, hello.d.sessionId)
        }
    }

    @Suppress("ReturnCount")
    @OptIn(InternalKookApi::class, FragileSimbotAPI::class)
    private suspend fun processSignalString(eventString: String): State {
        eventLogger.trace("Signal: {}", eventString)

        val json = BotImpl.defaultApiDecoder
        val jsonElement = json.parseToJsonElement(eventString)
        val propertyS = jsonElement.jsonObject["s"]
        val s = propertyS?.jsonPrimitive?.intOrNull
        if (s == null) {
            // Event data 's' 不是数字类型或不是 JsonPrimitive
            eventLogger.warn("Event data property [s] ('{}') is not of numeric type or not JsonPrimitive", propertyS)
            return this
        }

        when (s) {
            Signal.S_PONG -> {
                eventLogger.trace("Pong signal: {}", eventString)
                client.heartbeatJob.pong(json.decodeFromJsonElement(Signal.Pong.serializer(), jsonElement))
                return this
            }

            Signal.S_RECONNECT -> {
                eventLogger.debug("Reconnect signal: {}", eventString)
                client.cancel()
                return Reconnect(bot, botLogger, client.isCompress, client.sn.value, hello.d.sessionId)
            }

            Signal.S_EVENT -> {
                eventLogger.debug("Event signal raw: {}", eventString)

                val jsonD = jsonElement.jsonObject["d"]
                val jsonDObj = jsonElement.jsonObject["d"] as? JsonObject
                if (jsonDObj == null) {
                    eventLogger.error("Event data property [d] ('{}') is not exists or not JsonObject", jsonD)
                    return this
                }

                val event: Signal.Event<EventExtra>

                val deserializer = EventExtra.eventSerializerOrNull(jsonDObj)
                if (deserializer == null) {
                    eventLogger.warn("Cannot resolve event deserialization strategy via json property 'd' {}", jsonDObj)
                    return this
                } else {
                    event = try {
                        json.decodeFromJsonElement(deserializer, jsonElement)
                    } catch (se: SerializationException) {
                        var throwIt = false

                        val event0: Signal.Event<EventExtra>? = try {
                            json.decodeFromJsonElement(Signal.Event.serializer(UnknownExtra.serializer()), jsonElement)
                                .also {
                                    it.data.extra.initSource(eventString)
                                }
                        } catch (e1: Exception) {
                            se.addSuppressed(e1)
                            throwIt = true
                            null
                        }

                        if (event0 == null) {
                            // try to get sn and update
                            val sn = ((jsonElement as? JsonObject)?.get("sn") as? JsonPrimitive)?.longOrNull
                            if (sn != null) {
                                updateSn(sn)
                            } else {
                                val snEx =
                                    IllegalStateException("'sn' not found in event json, can not update sn for it")
                                se.addSuppressed(snEx)
                            }
                        }

                        if (throwIt) {
                            throw se
                        } else {
                            // 无法解析 extra，降级为 UnknownExtra
                            eventLogger.warn(
                                "Cannot resolve event deserialization strategy " +
                                    "via json property 'd', use UnknownExtra instead. " +
                                    "Enable `love.forte.simbot.kook.event.\${bot.clientId}`'s debug logger " +
                                    "for more details and stacktrace."
                            )

                            eventLogger.debug(
                                "Cannot resolve event deserialization strategy " +
                                    "via json property 'd', use UnknownExtra instead. extra raw event string: {}",
                                eventString,
                                se
                            )
                        }

                        event0!!
                    }
                }


                eventLogger.debug("Event signal value: {}", event)

                // 更新 sn, 保留最大值
                val eventSn = event.sn.toLong()
                val currentSn = updateSn(eventSn)
                if (eventSn < currentSn) {
                    eventLogger.debug("Event sn {} < current sn {}, ignore and skip this event.", eventSn, currentSn)
                    return this
                } else {
                    val eventData = EventData(event, eventString)
                    try {
                        // push event
                        client.eventProcessJob.apply {
                            for (i in 1..3) {
                                val sendResult = trySendEvent(eventData)
                                if (sendResult.isSuccess) {
                                    return@apply
                                }

                                if (sendResult.isFailure && !sendResult.isClosed) {
                                    // retry
                                    continue
                                }

                                if (sendResult.isClosed) {
                                    return@apply
                                }
                            }

                            sendEvent(eventData)
                        }
                    } catch (e: Throwable) {
                        eventLogger.error("Event push on error: {}", e.message, e)
                    }
                }

                return this
            }
        }

        return this
    }

    private fun updateSn(eventSn: Long): Long = client.sn.updateSn(eventSn)

    private fun AtomicLong.updateSn(newSn: Long): Long {
        return updateAndGet { v -> max(newSn, v) }
    }
}

private data class Reconnect(
    override val bot: BotImpl,
    override val botLogger: Logger,
    val isCompress: Boolean,
    val sn: Long,
    val sessionId: String,
) : State() {
    override suspend fun invoke(): State {
        return Connect(bot, botLogger, isCompress, null, ReconnectInfo(sn, sessionId))
    }
}


@OptIn(InternalSimbotAPI::class)
private suspend fun Frame.readToText(): String {
    return when (this) {
        is Frame.Text -> readText()
        is Frame.Binary -> readToTextWithDeflated()

        else -> throw IllegalArgumentException("Frame is not text or binary type.")
    }
}


private data class EventData(val event: Signal.Event<*>, val raw: String)

/**
 * 由平台实现对二进制 `deflate` 压缩数据进行解压缩并转为字符串数据。
 *
 * @throws UnsupportedOperationException 当不支持解析二进制数据时
 */
@InternalSimbotAPI
public expect suspend fun Frame.Binary.readToTextWithDeflated(): String
