/*
 * Copyright (c) 2022-2025. ForteScarlet.
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
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import love.forte.simbot.common.atomic.AtomicLong
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.common.collection.ConcurrentQueue
import love.forte.simbot.common.collection.ExperimentalSimbotCollectionApi
import love.forte.simbot.common.collection.createConcurrentQueue
import love.forte.simbot.common.stageloop.loop
import love.forte.simbot.common.weak.WeakRef
import love.forte.simbot.common.weak.weakRef
import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.logger.isDebugEnabled
import love.forte.simbot.qguild.QQGuildResultSerializationException
import love.forte.simbot.qguild.api.GatewayApis
import love.forte.simbot.qguild.api.GatewayInfo
import love.forte.simbot.qguild.api.app.AppAccessToken
import love.forte.simbot.qguild.api.app.GetAppAccessTokenApi
import love.forte.simbot.qguild.api.requestData
import love.forte.simbot.qguild.api.user.GetBotInfoApi
import love.forte.simbot.qguild.ed25519.*
import love.forte.simbot.qguild.ed25519.annotations.InternalEd25519Api
import love.forte.simbot.qguild.event.*
import love.forte.simbot.qguild.model.User
import love.forte.simbot.qguild.stdlib.*
import love.forte.simbot.qguild.stdlib.DisposableHandle
import kotlin.concurrent.Volatile
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


/**
 * implementation for [Bot].
 * @author ForteScarlet
 */
@OptIn(ExperimentalSimbotCollectionApi::class, InternalEd25519Api::class)
internal class BotImpl(
    override val ticket: Bot.Ticket,
    override val configuration: BotConfiguration,
) : Bot {
    internal val logger = LoggerFactory.getLogger("love.forte.simbot.qguild.bot.${ticket.appId}")

    // private val parentJob: Job
    override val coroutineContext: CoroutineContext
    private val job: Job

    init {
        configCheck()

        val configContext = configuration.coroutineContext
        val parentJob = configContext[Job]
        job = SupervisorJob(parentJob)
        coroutineContext = configContext.minusKey(Job) + job + CoroutineName("QGBot.${ticket.appId}")
    }

    private fun configCheck() {
        if (configuration.intents.value == 0) {
            logger.warn("Bot(appId={}) intents value is ZERO", ticket.appId)
        }

        checkTicketSecret()
    }

    private fun checkTicketSecret() {
        if (ticket.secret.isEmpty()) {
            logger.error(
                "The `ticket.secret` is empty. " +
                        "Since v4.0.0-beta6, the authentication logic within component " +
                        "has been migrated to new logic that requires the use of `secret`. " +
                        "If you do not configure the `ticket.secret`, " +
                        "it will most likely fail to start and throw an exception. " +
                        "See also: https://github.com/simple-robot/simbot-component-qq-guild/pull/163"
            )
        }
    }

    private lateinit var _ed25519PrivateKey: Ed25519PrivateKey
    private lateinit var _ed25519PublicKey: Ed25519PublicKey

    private val ed25519KeyPair: Deferred<Ed25519KeyPair> = async(start = CoroutineStart.LAZY) {
        genEd25519Keypair(ticket.secret.paddingEd25519Seed().toByteArray()).also { (pri, pub) ->
            _ed25519PrivateKey = pri
            _ed25519PublicKey = pub
        }
    }

    private suspend fun ed25519PrivateKey() =
        if (::_ed25519PrivateKey.isInitialized) _ed25519PrivateKey
        else ed25519KeyPair.await().privateKey

    private suspend fun ed25519PublicKey() =
        if (::_ed25519PublicKey.isInitialized) _ed25519PublicKey
        else ed25519KeyPair.await().publicKey

    internal val eventDecoder = Signal.Dispatch.dispatchJson {
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val wsClient: HttpClient? = configuration.let {
        if (it.disableWs) return@let null

        val wsClientEngine = it.wsClientEngine
        val wsClientEngineFactory = it.wsClientEngineFactory

        when {
            wsClientEngine != null -> HttpClient(wsClientEngine) {
                configWsHttpClient()
            }

            wsClientEngineFactory != null -> HttpClient(wsClientEngineFactory) {
                configWsHttpClient()
            }

            else -> HttpClient {
                configWsHttpClient()
            }
        }
    }

    private fun HttpClientConfig<*>.configWsHttpClient() {
        WebSockets {
        }
    }

    override val apiClient: HttpClient = configuration.let {
        val apiClientEngine = it.apiClientEngine
        val apiClientEngineFactory = it.apiClientEngineFactory

        when {
            apiClientEngine != null -> HttpClient(apiClientEngine) {
                configApiHttpClient()
            }

            apiClientEngineFactory != null -> HttpClient(apiClientEngineFactory) {
                configApiHttpClient()
            }

            else -> HttpClient {
                configApiHttpClient()
            }
        }
    }

    private fun HttpClientConfig<*>.configApiHttpClient() {
        install(ContentNegotiation) {
            json(configuration.apiDecoder)
        }

        val apiHttpRequestTimeoutMillis = configuration.apiHttpRequestTimeoutMillis
        val apiHttpConnectTimeoutMillis = configuration.apiHttpConnectTimeoutMillis
        val apiHttpSocketTimeoutMillis = configuration.apiHttpSocketTimeoutMillis

        if (apiHttpRequestTimeoutMillis != null || apiHttpConnectTimeoutMillis != null || apiHttpSocketTimeoutMillis != null) {
            install(HttpTimeout) {
                apiHttpRequestTimeoutMillis?.also { requestTimeoutMillis = it }
                apiHttpConnectTimeoutMillis?.also { connectTimeoutMillis = it }
                apiHttpSocketTimeoutMillis?.also { socketTimeoutMillis = it }
            }
        }
    }

    /**
     * 下一次刷新token需要等待事件，最小1秒
     */
    private fun flushWaitingDuration(expireIn: Int): Duration =
        max(1, (expireIn.toFloat() * 0.9f).toInt()).seconds

    /**
     * `access_token`。
     * 在使用了 [start] 后会定期刷新，
     * 周期为上一个 [accessToken] 有效期的 90%。
     * 比如如果有效期是 7200，那么下次刷新会在 7200*0.9=6480 秒后刷新。
     *
     * 刷新此值的应当只有一个 Job，一般不会产生竞争的情况。
     */
    @Volatile
    override var accessToken: String = ""

    override val apiServer: Url = configuration.serverUrl

    @Deprecated("Use qqBotToken", replaceWith = ReplaceWith("qqBotToken"))
    internal val botToken = "Bot ${ticket.appId}.${ticket.token}"

    internal val qqBotToken
        get() = "QQBot $accessToken"

    override val shard: Shard = configuration.shard
    override val apiDecoder: Json = configuration.apiDecoder

    internal val processorQueue: ConcurrentQueue<EventProcessor> = createConcurrentQueue()
    internal val preProcessorQueue: ConcurrentQueue<EventProcessor> = createConcurrentQueue()

    override fun subscribe(sequence: SubscribeSequence, processor: EventProcessor): DisposableHandle {
        return when (sequence) {
            SubscribeSequence.PRE -> {
                preProcessorQueue.add(processor)
                DisposableHandleImpl(preProcessorQueue, processor)
            }

            SubscribeSequence.NORMAL -> {

                processorQueue.add(processor)
                DisposableHandleImpl(processorQueue, processor)
            }
        }
    }

    private class DisposableHandleImpl(queue: ConcurrentQueue<EventProcessor>, subject: EventProcessor) :
        DisposableHandle {

        @Suppress("unused")
        private val disposed = atomic(false) // 0

        @Volatile
        private var queueRef: WeakRef<ConcurrentQueue<EventProcessor>>? = weakRef(queue)

        @Volatile
        private var subjectRef: WeakRef<EventProcessor>? = weakRef(subject)

        override fun dispose() {
            if (!disposed.compareAndSet(expect = false, value = true)) {
                return
            }

            val queue = queueRef?.let { ref ->
                ref.value.also {
                    ref.clear()
                    queueRef = null
                }
            }
            val subject = subjectRef?.let { ref ->
                ref.value.also {
                    ref.clear()
                    subjectRef = null
                }
            }

            if (queue == null || subject == null) {
                return
            }

            queue.remove(subject)
        }

        private companion object
    }

    /**
     * 用于 [start] 或内部重新连接的锁。
     */
    internal val startLock = Mutex()

    @Volatile
    private var stageLoopJob: Job? = null

    @Volatile
    private var flushAccessTokenJob: Job? = null

    private val clientLock = Mutex()

    @Volatile
    private var _client: ClientImpl? = null
    override val client: Bot.Client? get() = _client

    internal suspend inline fun updateClient(new: ClientImpl?) {
        clientLock.withLock {
            _client?.cancel()
            _client = new
        }
    }

    override suspend fun start() {
        start0 { requestData(GatewayApis.Normal) }
    }

    override suspend fun start(gateway: GatewayInfo) {
        start0 { gateway }
    }

    private suspend fun start0(gatewayFactory: suspend () -> GatewayInfo) {
        startLock.withLock {
            stageLoopJob?.cancel()
            stageLoopJob = null

            if (flushAccessTokenJob == null) {
                logger.debug("Initializing flush accessToken job")
                flushAccessTokenJob = initFlushAccessTokenJob()
            }

            if (wsClient != null) {
                val gateway = gatewayFactory()

                logger.debug("Request gateway {} by shard {}", gateway, shard)

                this.stageLoopJob = launchWsLoop(wsClient, gateway)
            } else {
                logger.debug("WsClient is null, will not connect to ws server.")
            }
        }
    }

    private suspend fun launchWsLoop(wsClient: HttpClient, gateway: GatewayInfo): Job {
        val state = Connect(this, shard, gateway, wsClient)

        logger.debug("Create state loop {}", state)

        var st: State? = state
        do {
            logger.debug("Current state: {}", st)
            st = st?.invoke()
        } while (st != null && st !is ReceiveEvent)

        if (st == null) {
            // 当前状态为空且尚未进入事件接收状态
            throw IllegalStateException("The current state is null and not yet in the event receiving state")
        }

        val stageLoopJob: Job = launch {
            st.loop()
        }

        stageLoopJob.invokeOnCompletion { reason ->
            reason?.also {
                logger.debug("StageLoopJob is on completion: {}", it.message, it)
            }
        }

        return stageLoopJob
    }

    private suspend fun initFlushAccessTokenJob(): Job {
        val api = GetAppAccessTokenApi.create(ticket.appId, ticket.secret)

        val initialToken = getNewAccessToken(api)
        val firstToken = initialToken.accessToken
        this.accessToken = firstToken
        val initialDelay = flushWaitingDuration(initialToken.expiresIn)

        logger.debug(
            "Initialized token: {}{}***{}{}, next delay: {}",
            firstToken[0],
            firstToken[1],
            firstToken[firstToken.lastIndex - 1],
            firstToken.last(),
            initialDelay.toString()
        )

        return launch {
            var delay = initialDelay
            while (isActive) {
                delay(delay)
                val appToken = getNewAccessToken(api)
                this@BotImpl.accessToken = appToken.accessToken
                delay = this@BotImpl.flushWaitingDuration(appToken.expiresIn)
                logger.debug(
                    "Flushed token: {}{}********{}{}, next delay: {}",
                    firstToken[0],
                    firstToken[1],
                    firstToken[firstToken.lastIndex - 1],
                    firstToken.last(),
                    delay.toString()
                )
            }
        }
    }

    private suspend fun getNewAccessToken(api: GetAppAccessTokenApi): AppAccessToken =
        runCatching {
            api.requestData(
                client = apiClient,
                token = null,
                server = null,
                appId = null
            )
        }.getOrElse { err ->
            when (err) {
                is SerializationException, is QQGuildResultSerializationException -> {
                    var message = "Deserialize the result of GetAppAccessToken API failed."
                    if (ticket.secret.isEmpty()) {
                        message += " Did you forget to configure `ticket.secret`? It's required!"
                    }
                    throw IllegalStateException(message, err)
                }

                else -> throw err
            }
        }

    @OptIn(ExperimentalStdlibApi::class, InternalEd25519Api::class)
    override suspend fun emitEvent(
        payload: String,
        options: EmitEventOptions?,
    ): EmitResult {
        // CODE	名称	客户端行为	描述
        //  0	Dispatch	Receive	服务端进行消息推送
        //  13	回调地址验证	Receive	开放平台对机器人服务端进行验证
        logger.debug("Emit raw event with payload: {}", payload)

        suspend fun verifyIfNecessary() {
            val (signature, signatureTimestamp) = options?.ed25519SignatureVerification ?: return
            logger.debug("`ed25519SignatureVerification` exists, verity the payload")

            val signatureBytes = signature.hexToByteArray()

            check(Ed25519KeyPair.CRYPTO_SIGN_BYTES == signatureBytes.size) {
                "Invalid signature hex size, " +
                        "expect ${Ed25519KeyPair.CRYPTO_SIGN_BYTES}, " +
                        "actual ${signatureBytes.size}"
            }

            val and: UByte = signatureBytes[63].toUByte().and(224u)

            check(and == 0u.toUByte()) {
                "Invalid signature hex, " +
                        "expect signatureBytes[63] && 224 == 0, " +
                        "actual $and (0x${and.toHexString()})"
            }

            val msg = "$signatureTimestamp$payload"

            check(ed25519PublicKey().verify(signature = Signature(signatureBytes), data = msg.toByteArray())) {
                "Ed25519 verify failed"
            }
        }

        suspend fun signatureIfNecessary(verify: Signal.CallbackVerify): EmitResult {
            val (plainToken, eventTs) = verify.data
            val msg = "$eventTs$plainToken"

            val signature = ed25519PrivateKey().sign(msg.toByteArray())

            val verified = Signal.CallbackVerify.Verified(
                plainToken,
                signature.data.toHexString()
            )

            return EmitResult.Verified(verified)
        }

        // Verify payload
        verifyIfNecessary()

        val json = eventDecoder.parseToJsonElement(payload)

        when (val opcode = json.getOpcode()) {
            null -> {
                if (options?.ignoreMissingOpcode == true) return EmitResult.Nothing

                throw IllegalArgumentException("Required attribute `$.op` is missing")
            }

            Opcodes.Dispatch -> {
                val serializer = resolveDispatchSerializer(
                    json = json.jsonObject,
                    allowNameMissing = true
                )

                val dispatch = if (serializer != null) {
                    eventDecoder.decodeFromJsonElement(serializer, json)
                } else {
                    // Unknown
                    val disSeq = -1L
                    val id = json.tryGetId()

                    Signal.Dispatch.Unknown(id, disSeq, json, payload).also {
                        val t =
                            kotlin.runCatching {
                                json.jsonObject[Signal.Dispatch.DISPATCH_CLASS_DISCRIMINATOR]
                                    ?.jsonPrimitive
                                    ?.content
                            }.getOrNull()

                        logger.warn("Unknown event type {}, decode it as Unknown event: {}", t, it)
                    }
                }

                emitEvent(dispatch, payload)
                return EmitResult.Dispatched
            }

            Opcodes.CallbackVerify -> {
                val verify = eventDecoder.decodeFromJsonElement(Signal.CallbackVerify.serializer(), json)
                logger.debug("On Signal.CallbackVerify: {}", verify)
                return signatureIfNecessary(verify)
            }

            else -> {
                if (options?.ignoreUnknownOpcode == true) return EmitResult.Nothing

                throw IllegalArgumentException("Unknown opcode: $opcode, emitEvent can only support opcode in [0, 13]")
            }
        }
    }

    override fun cancel(reason: Throwable?) {
        if (!job.isActive) return

        job.cancel(reason?.let { CancellationException(it.message, it) })
    }

    override suspend fun join() {
        job.join()
    }


    internal inner class ClientImpl(
        val readyData: Ready.Data,
        val sessionInfo: SessionInfo,
        val wsSession: DefaultClientWebSocketSession,
    ) : Bot.Client {
        override val seq: Long get() = sessionInfo.seq.value
        override val isActive: Boolean get() = wsSession.isActive

        suspend fun cancelAndJoin(reason: Throwable? = null) {
            val cancel = reason?.let { CancellationException(it.message, it) }
            val sessionJob = wsSession.coroutineContext[Job]!!
            sessionJob.cancel(cancel)
            sessionJob.join()
        }

        fun cancel(reason: Throwable? = null) {
            val cancel = reason?.let { CancellationException(it.message, it) }
            val sessionJob = wsSession.coroutineContext[Job]!!
            sessionJob.cancel(cancel)
        }

        override fun toString(): String {
            return "ClientImpl(shard=$shard, session=$readyData, seq=$seq)"
        }
    }

    //// self api

    override suspend fun me(): User {
        return GetBotInfoApi.requestDataBy(this)
    }

}


internal class AtomicLongRef(initValue: Long = 0) {
    private val atomicValue: AtomicLong = atomic(initValue)
    var value: Long
        get() = atomicValue.value
        set(value) {
            atomicValue.value = value
        }

    fun updateAndGet(function: (Long) -> Long): Long {
        while (true) {
            val current = value
            val new = function(current)
            if (atomicValue.compareAndSet(current, new)) {
                return new
            }
        }
    }
}

internal data class SessionInfo(
    val session: DefaultClientWebSocketSession,
    val seq: AtomicLongRef,
    val heartbeatJob: Job,
    val logger: Logger,
    val readyData: Ready.Data,
)

@OptIn(ExperimentalSimbotCollectionApi::class)
internal suspend fun BotImpl.emitEvent(dispatch: Signal.Dispatch, raw: String) {
    logger.debug("Emit event {} from raw {}", dispatch, raw)
    // 先顺序地使用 preProcessor 处理
    preProcessorQueue.forEach { processor ->
        runCatching {
            processor.doInvoke(dispatch, raw)
        }.onFailure { e ->
            if (logger.isDebugEnabled) {
                logger.debug(
                    "Event pre-precess failure. raw: {}, event: {}", raw, dispatch
                )
            }
            logger.error("Event pre-precess failure.", e)
        }
    }

    // 然后异步地正常处理
    // TODO 同步异步 configurable?
    // bot launch or session launch?
    launch {
        processorQueue.forEach { processor ->
            runCatching {
                processor.doInvoke(dispatch, raw)
            }.onFailure { e ->
                if (logger.isDebugEnabled) {
                    logger.debug(
                        "Event precess failure. raw: {}, event: {}", raw, dispatch
                    )
                }
                logger.error("Event precess failure.", e)
            }
        }
    }
}
