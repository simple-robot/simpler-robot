/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.qguild.stdlib.internal

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.isDebugEnabled
import love.forte.simbot.logger.isTraceEnabled
import love.forte.simbot.qguild.api.GatewayApis
import love.forte.simbot.qguild.api.GatewayInfo
import love.forte.simbot.qguild.err
import love.forte.simbot.qguild.event.*
import love.forte.simbot.qguild.stdlib.internal.BotImpl.ClientImpl
import love.forte.simbot.qguild.stdlib.requestDataBy
import kotlin.math.max
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


/**
 * 是否可以重新连接
 *
 * [参考](https://bot.q.qq.com/wiki/develop/api/gateway/error/error.html)
 */
private fun canBeResumed(code: Short): Boolean {
    return when (code.toInt()) {
        // 4008  发送 payload 过快，请重新连接，并遵守连接后返回的频控信息
        // 4009  连接过期，请重连
        4008, 4009 -> true
        else -> false
    }
}

private fun canBeIdentified(code: Short): Boolean {
    return when (code.toInt()) {
        // 4007 seq 错误
        // 4006 无效的 session id，无法继续 resume，请 identify
        // 4008 发送 payload 过快，请重新连接，并遵守连接后返回的频控信息
        // 4009 连接过期，请重连并执行 resume 进行重新连接
        4007, 4006 -> true
        // 4900~4913 内部错误，请重连
        else -> code in 4900..4913
    }
}


/**
 * 流转的状态
 */
internal abstract class State : love.forte.simbot.common.stageloop.State<State>() {
    internal abstract val bot: BotImpl
    internal val logger: Logger get() = bot.logger
}

internal class Connect(
    override val bot: BotImpl,
    private val shard: Shard,
    private val gateway: GatewayInfo,
    private val wsClient: HttpClient,
) : State() {
    override suspend fun invoke(): State {
        val intents = bot.configuration.intents
        val properties = bot.configuration.clientProperties

        if (logger.isDebugEnabled) {
            var bitString = intents.value.toString(2)
            if (bitString.startsWith("-")) {
                bitString = bitString.substring(1)
            }
            if (bitString.length < 32) {
                bitString = "0".repeat(32 - bitString.length) + bitString
            }

            logger.debug("Connect intents   : {} (0x{})", intents, bitString)
            logger.debug("Connect properties: {}", properties)
        }

        val identify = Signal.Identify(
            Signal.Identify.Data(
                token = bot.qqBotToken,
                intents = intents,
                shard = shard,
                properties = properties,
            )
        )

        bot.logger.debug("Connect to ws with gateway {}", gateway)
        val session = wsClient.ws { gateway }

        // next: receive Hello
        return WaitingHello(bot, session) { hello ->
            WaitingReadyEvent(bot, identify, hello.data, session, wsClient)
        }
    }
}

internal class WaitingHello(
    override val bot: BotImpl,
    val session: DefaultClientWebSocketSession,
    val next: WaitingHello.(Signal.Hello) -> State?
) : State() {
    override suspend fun invoke(): State? {
        var hello: Signal.Hello? = null
        while (session.isActive) {
            val frame = session.incoming.receive() as? Frame.Text ?: continue
            val text = frame.readText()
            logger.debug("Waiting hello : received frame {}", text)
            val json = bot.eventDecoder.parseToJsonElement(text)
            if (json.jsonObject["op"]?.jsonPrimitive?.int == Opcodes.Hello) {
                hello = bot.eventDecoder.decodeFromJsonElement(Signal.Hello.serializer(), json)
                break
            }
        }

        val h = hello
        if (h == null) {
            logger.error("Received Hello failed, nothing received. Awaiting close reason and throw")
            session.closeReason.await().err()
        }

        logger.debug("Received Hello: {}", h)

        // 下一个阶段
        return next(this, h)
    }
}

/**
 * 等待并接收 [Ready Event][Ready]
 *
 * [2.鉴权连接](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_2-%E9%89%B4%E6%9D%83%E8%BF%9E%E6%8E%A5)
 */
internal class WaitingReadyEvent(
    override val bot: BotImpl,
    private val identify: Signal.Identify,
    private val hello: Signal.Hello.Data,
    private val session: DefaultClientWebSocketSession,
    private val wsClient: HttpClient,
) : State() {
    override suspend fun invoke(): State {
        // 发送 identify
        val identifyJson = bot.eventDecoder.encodeToString(Signal.Identify.serializer(), identify)
        logger.debug("Send identify {}, JSON: {}", identify, identifyJson)
        session.send(identifyJson)
        logger.debug("Send identify Successfully.")

        // 等待ready
        var ready: Ready? = null
        logger.debug("Waiting for Signal ready...")
        while (session.isActive) {
            val frameResult = session.incoming.receiveCatching()
            if (!frameResult.isSuccess) {
                val reason = frameResult.exceptionOrNull()
                val closeReason = session.closeReason.await()
                throw IllegalStateException("Session closed: $closeReason", reason)
            }

            val frame = frameResult.getOrThrow() as? Frame.Text ?: continue
            val text = frame.readText()
            logger.debug("Waiting ready event : received frame {}", text)
            val json = bot.eventDecoder.parseToJsonElement(text)
            if (json.getOpcode() == Opcodes.Dispatch) {
                val dispatchSerializer = resolveDispatchSerializer(json.jsonObject)
                    ?: continue

                val dispatch = bot.eventDecoder.decodeFromJsonElement(dispatchSerializer, json)

                if (dispatch is Ready) {
                    ready = dispatch
                    break
                }
            }
        }

        val r = ready
        if (r == null) {
            logger.error("Received Ready event failed, nothing received. Awaiting close reason and throw")
            session.closeReason.await().err()
        }

        logger.debug("Received Ready event: {}", r)

        // next: session
        return HeartbeatJob(bot, hello, r.data, session, wsClient)
    }
}


internal class HeartbeatJob(
    override val bot: BotImpl,
    private val hello: Signal.Hello.Data,
    private val readyData: Ready.Data,
    private val session: DefaultClientWebSocketSession,
    private val wsClient: HttpClient,
) : State() {
    override suspend fun invoke(): State {
        val seq = AtomicLongRef(-1L)

        // 创建心跳任务
        val heartbeatJob = createHeartbeatJob(seq)

        val sessionInfo = SessionInfo(session, seq, heartbeatJob, logger, readyData)

        // next: process event
        return CreateClient(bot, sessionInfo, session, wsClient)
    }


    private fun createHeartbeatJob(seq: AtomicLongRef): Job {
        val heartbeatInterval = hello.heartbeatInterval

        fun getHelloInterval(): Long {
            val r = Random.nextLong(5000)
            return if (r > heartbeatInterval) 0 else heartbeatInterval - r
        }

        // heartbeat Job
        return session.launch(CoroutineName("bot.${bot.ticket.appId}.heartbeat")) {
            val serializer = Signal.Heartbeat.serializer()
            while (isActive) {
                val timeMillis = getHelloInterval()
                if (logger.isTraceEnabled) {
                    logger.trace("Heartbeat next delay interval: {}", timeMillis.milliseconds.toString())
                }
                delay(timeMillis)
                val hb = Signal.Heartbeat(seq.value.takeIf { it >= 0 })
                session.send(bot.eventDecoder.encodeToString(serializer, hb))
            }
        }
    }
}


/**
 * 创建 [ClientImpl]
 */
internal class CreateClient(
    override val bot: BotImpl,
    private val botClientSession: SessionInfo,
    private val session: DefaultClientWebSocketSession,
    private val wsClient: HttpClient,
) : State() {
    override suspend fun invoke(): State {
        val client = bot.ClientImpl(
            botClientSession.readyData,
            botClientSession,
            session
        )

        logger.debug("Current client: {}", client)

        // update client
        bot.updateClient(client)

        // next: receive events
        return ReceiveEvent(bot, client, wsClient)
    }
}

/**
 * 从 [session][BotImpl.ClientImpl.wsSession] 中尝试接收并处理下一个事件。
 */
internal class ReceiveEvent(
    override val bot: BotImpl,
    private val client: ClientImpl,
    private val wsClient: HttpClient,
) : State() {
    override suspend fun invoke(): State? {
        val session = client.wsSession
        val seq = client.sessionInfo.seq
        if (!session.isActive) {
            val reason = session.closeReason.await()
            logger.error("Session is closed. reason: {}. Try to resume", reason)
            return Resume(bot, client, wsClient)
        }

        suspend fun onCatchErr(e: Throwable?): State? {
            val reason = session.closeReason.await()
            if (reason == null) {
                logger.debug("Session closed and reason is null, try to resume", e)
                // try resume
                return Resume(bot, client, wsClient)
            }

            suspend fun doIdentify(): State {
                val gatewayInfo: GatewayInfo = GatewayApis.Normal.requestDataBy(bot)
                logger.debug("Reconnect gateway {} by shard {}", gatewayInfo, bot.shard)
                return Connect(bot, bot.shard, gatewayInfo, wsClient)
            }

            val reasonCode = reason.code
            when {
                canBeResumed(reasonCode) -> {
                    logger.debug("Session closed({}), try to resume", reason, e)
                    // try resume
                    return Resume(bot, client, wsClient)
                }

                canBeIdentified(reasonCode) -> {
                    logger.debug("Session closed({}), try to reconnect", reason, e)
                    // try resume
                    return doIdentify()
                }

                else -> {
                    // 4914，4915 不可以连接，请联系官方解封
                    when (reasonCode.toInt()) {
                        4914 -> logger.error(
                            "机器人已下架,只允许连接沙箱环境,请断开连接,检验当前连接环境 (reason={})",
                            reason,
                            e
                        )

                        4915 -> logger.error(
                            "机器人已封禁,不允许连接,请断开连接,申请解封后再连接 (reason={})",
                            reason,
                            e
                        )

                        else -> {
                            logger.warn("Unknown reason({}), try to IDENTIFY", reason, e)
                            return doIdentify()
                        }
                    }

                    bot.cancel(e)
                    if (session.isActive) {
                        session.cancel(e?.message ?: e.toString(), e)
                    }
                    bot.updateClient(null)
                }
            }

            // stop
            return null
        }

        logger.trace("Receiving next frame ...")
        val frameResult = session.incoming.receiveCatching()
        if (!frameResult.isSuccess) {
            return onCatchErr(frameResult.exceptionOrNull())
        }

        val frame = frameResult.getOrThrow()

        try {
            logger.trace("Received next frame: {}", frame)
            val raw = (frame as? Frame.Text)?.readText() ?: run {
                logger.debug("Not Text frame {}, skip.", frame)
                return this
            }
            logger.debug("Received text frame raw: {}", raw)
            val json = bot.eventDecoder.parseToJsonElement(raw)
            when (val opcode = json.getOpcode()) {
                Opcodes.Dispatch -> {
                    // event
                    val serializer = resolveDispatchSerializer(
                        json = json.jsonObject,
                        allowNameMissing = true
                    )

                    val dispatch = if (serializer != null) {
                        bot.eventDecoder.decodeFromJsonElement(serializer, json)
                    } else {
                        // 未知的事件类型
                        val disSeq = json.tryGetSeq() ?: seq.value
                        val id = json.tryGetId()

                        Signal.Dispatch.Unknown(id, disSeq, json, raw).also {
                            val t =
                                kotlin.runCatching { 
                                    json.jsonObject[Signal.Dispatch.DISPATCH_CLASS_DISCRIMINATOR]
                                        ?.jsonPrimitive
                                        ?.content 
                                }.getOrNull()
                            
                            logger.warn("Unknown event type {}, decode it as Unknown event: {}", t, it)
                        }
                    }

                    logger.debug("Received dispatch: {}", dispatch)
                    val dispatchSeq = dispatch.seq

                    // 推送事件
                    bot.emitEvent(dispatch, raw)

                    // seq留下最大值
                    val currentSeq = seq.updateAndGet { pref -> max(pref, dispatchSeq) }
                    logger.trace("Current seq: {}", currentSeq)
                }

                Opcodes.Reconnect -> {
                    // 重新连接
                    logger.debug("Received reconnect signal. Do Resume.")
                    return Resume(bot, client, wsClient)
                }

                else -> {
                    logger.debug("Received other signal with opcode: {}, raw: {}", opcode, raw)
                }
            }
        } catch (serEx: SerializationException) {
            logger.error("Serialization exception: {}", serEx.message, serEx)
        } catch (other: Throwable) {
            logger.error("Exception: {}", other.message, other)
        }

        // next: self
        return this
    }
}

@Deprecated("Deprecated")
internal fun tryCheckIsPolymorphicException(exception: SerializationException): Boolean {
    // 似乎是某个版本的错误提示，但是至少在 JSON v1.6.3 已经不适用了
    if (exception.message?.startsWith("Polymorphic serializer was not found for") == true) {
        return true
    }

    // kotlinx.serialization.json.internal.JsonDecodingException: Serializer for subclass 'MESSAGE_REACTION_ADD' is not found in the polymorphic scope of 'Dispatch'
    if (exception.message?.contains("is not found in the polymorphic scope of") == true) {
        return true
    }

    return false
}


/**
 * [4. 恢复连接](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_4-%E6%81%A2%E5%A4%8D%E8%BF%9E%E6%8E%A5)
 *
 * 有很多原因都会导致连接断开，断开之后短时间内重连会补发中间遗漏的事件，以保障业务逻辑的正确性。
 * 断开重连不需要发送Identify请求。在连接到 Gateway 之后，需要发送 Opcode 6 Resume消息
 *
 */
internal class Resume(
    override val bot: BotImpl,
    private val client: ClientImpl,
    private val wsClient: HttpClient,
) : State() {
    override suspend fun invoke(): State {
        val newSession = bot.startLock.withLock {
            // 关闭当前连接
            client.cancelAndJoin()
            val gateway = GatewayApis.Normal.requestDataBy(bot)
            wsClient.ws { gateway }.apply {
                // 发送 Opcode6
                val resumeSignal =
                    Signal.Resume(Signal.Resume.Data(bot.qqBotToken, client.readyData.sessionId, client.seq))
                send(bot.eventDecoder.encodeToString(Signal.Resume.serializer(), resumeSignal))
            }
        }

        return WaitingHello(bot, newSession) { hello ->
            // 跳过 wait ready, 直接HeartbeatJob
            HeartbeatJob(bot, hello.data, client.readyData, session, wsClient)
        }
    }
}

private suspend inline fun HttpClient.ws(crossinline gatewayInfo: () -> GatewayInfo): DefaultClientWebSocketSession {
    return webSocketSession { url(gatewayInfo().url) }
}



