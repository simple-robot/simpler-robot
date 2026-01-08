/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.qguild.stdlib

import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.serialization.json.Json
import love.forte.simbot.qguild.api.GatewayInfo
import love.forte.simbot.qguild.api.user.GetBotInfoApi
import love.forte.simbot.qguild.event.Shard
import love.forte.simbot.qguild.event.Signal
import love.forte.simbot.qguild.model.User
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP
import kotlin.coroutines.CoroutineContext

/**
 * 一个 QQ频道Bot。
 *
 * 在Java中，可以通过扩展工具类 `Bots.xxx` 得到更多兼容函数。
 *
 * @author ForteScarlet
 */
public interface Bot : CoroutineScope {
    override val coroutineContext: CoroutineContext

    /**
     * 当前bot的 [Ticket].
     */
    public val ticket: Ticket

    /**
     * 用于API请求的 `access_token`。
     * 当执行了 [start] 之后会周期性刷新此值，
     * 直到bot被终止。
     *
     * 如果bot尚未被启动，则会始终得到空字符串。
     *
     * 这属于敏感信息，请注意保护。
     */
    public val accessToken: String

    /**
     * Bot当前的配置信息。
     */
    public val configuration: BotConfiguration

    /**
     * bot用于api请求的 [HttpClient].
     */
    public val apiClient: HttpClient

    /**
     * 用于api请求并反序列化的 [Json]
     */
    public val apiDecoder: Json

    /**
     * 当前bot所使用的
     */
    public val apiServer: Url

    /**
     * 添加一个事件**预处理器**。
     *
     * [registerPreProcessor] 与 [processor] 不同的是，
     * [registerPreProcessor] 所注册的所有事件处理器会在接收到事件的时候以**同步顺序**的方式依次执行，
     * 而后再交由下游的 [processor] 处理。
     *
     * 也同样因此，尽可能避免在 [registerPreProcessor] 中执行任何耗时逻辑，这可能会对事件处理流程造成严重阻塞。
     *
     * @see EventProcessor
     * @suppress use [subscribe]
     *
     * @param processor 用于处理事件的函数类型
     */
    @Deprecated("Use `subscribe(PRE, processor)`", ReplaceWith("subscribe(SubscribeSequence.PRE, processor)"))
    public fun registerPreProcessor(processor: EventProcessor): DisposableHandle =
        subscribe(SubscribeSequence.PRE, processor)

    /**
     * 添加一个事件处理器。
     * @suppress use [subscribe]
     * @see EventProcessor
     */
    @Deprecated("Use `subscribe(processor)`", ReplaceWith("subscribe(processor)"))
    public fun registerProcessor(processor: EventProcessor): DisposableHandle =
        subscribe(SubscribeSequence.NORMAL, processor)

    /**
     * 订阅事件并使用 [processor] 对其进行处理。
     *
     * @see EventProcessor
     * @see SubscribeSequence
     */
    public fun subscribe(sequence: SubscribeSequence, processor: EventProcessor): DisposableHandle

    /**
     * 订阅事件并使用 [processor] 对其进行处理。
     * 默认使用 [SubscribeSequence.NORMAL] 级别。
     *
     * @see EventProcessor
     * @see SubscribeSequence
     */
    public fun subscribe(processor: EventProcessor): DisposableHandle =
        subscribe(SubscribeSequence.NORMAL, processor)

    /**
     * [票据](https://bot.q.qq.com/wiki/develop/api/#%E7%A5%A8%E6%8D%AE)
     *
     * 申请机器人通过后，平台将会下发三个票据。
     *
     */
    public data class Ticket(
        /**
         * 用于识别一个机器人的 id
         */
        val appId: String,

        /**
         * 用于在 oauth 场景进行请求签名的密钥
         */
        val secret: String,

        /**
         * 机器人token，用于以机器人身份调用 openapi，格式为 `${app_id}.${random_str}`
         */
        val token: String
    )

    /**
     * 启动当前BOT。如果已经存在 [client], 则会关闭已存连接并重新连接。
     *
     * @throws CancellationException 如果当前 bot 已经被关闭
     */
    @ST
    public suspend fun start()

    /**
     * 启动当前BOT。如果已经存在 [client], 则会关闭已存连接并重新连接。
     *
     * 更建议使用 `start(() -> GatewayInfo)`
     *
     * @throws CancellationException 如果当前 bot 已经被关闭
     *
     * @param gateway 提供准备好的路由信息。
     */
    @ST
    public suspend fun start(gateway: GatewayInfo)

    /**
     * 主动推送一个事件原文。
     * 可用于在 webhook 模式下推送事件。
     *
     * @param payload 接收到的事件推送的JSON格式正文字符串。
     * @param options 额外提供的属性或配置。默认为 `null`。
     *
     * @throws IllegalArgumentException 参考:
     * - [EmitEventOptions.ignoreUnknownOpcode]
     * - [EmitEventOptions.ignoreMissingOpcode]
     *
     * @since 4.1.0
     */
    @ST
    public suspend fun emitEvent(
        payload: String,
        options: EmitEventOptions? = null,
    ): EmitResult

    /**
     * 主动推送一个事件原文。
     * 可用于在 webhook 模式下推送事件。
     *
     * @param payload 接收到的事件推送的JSON格式正文字符串。
     *
     * @since 4.1.0
     */
    @ST
    public suspend fun emitEvent(payload: String): EmitResult {
        return emitEvent(payload, null)
    }

    /**
     * 终止当前BOT。
     */
    public fun cancel(reason: Throwable?)

    /**
     * 终止当前BOT。
     */
    public fun cancel() {
        cancel(null)
    }

    /**
     * 挂起直到此 bot 被 [cancel] 或因其他原因终止（例如收到了昭示着无法重连的事件）
     */
    @ST(asyncBaseName = "asFuture", asyncSuffix = "")
    public suspend fun join()

    /**
     * 此bot使用的 [shard] 信息。
     *
     */
    public val shard: Shard

    /**
     * 此Bot中的连接信息。
     *
     * 如果当前处于重连、重启的状态，得到的 [client] 中 [client.isActive][Client.isActive] 可能为 `false`，
     * [client] 本身也可能不存在。
     *
     * @return 当前bot持有的连接。如果当前正处于连接中、重连中、尚未启动或未启用ws连接，
     * 则可能得到null
     */
    public val client: Client?

    /**
     * Bot的连接信息。一般来讲代表一个ws连接。
     */
    public interface Client {
        /**
         * 事件推送所携带的 `s`.
         */
        public val seq: Long

        /**
         * 当前 client 是否处于运行状态。
         */
        public val isActive: Boolean
    }

    //// some self api

    /**
     * 通过 api [GetBotInfoApi] 查询bot自身信息。
     */
    @STP
    public suspend fun me(): User
}

/**
 * 订阅事件用的事件处理器的顺序模式
 */
public enum class SubscribeSequence {
    /**
     * 预处理级别。
     * 使用 [PRE]
     * 所注册的所有事件处理器会在接收到事件的时候以**同步顺序**的方式依次执行，
     * 而后再交由下游的 processor 处理。
     *
     * 尽可能避免在 [PRE] 中执行任何耗时逻辑，
     * 这可能会对事件处理流程造成严重阻塞。
     */
    PRE,

    /**
     * 普通级别。始终在所有 [PRE] 之后执行，
     * 且会作为一个整体在异步中处理。
     */
    NORMAL;
}

/**
 * 使用 [Bot.emitEvent] 推送一个外部事件，并且在 [block] 中配置 [EmitEventOptions]。
 * @see Bot.emitEvent
 * @since 4.1.0
 */
public suspend inline fun Bot.emitEvent(
    payload: String,
    block: EmitEventOptions.() -> Unit
): EmitResult {
    return emitEvent(payload, EmitEventOptions().apply(block))
}

/**
 * 使用 [Bot.emitEvent] 推送事件后的结果，
 * 会根据配置的不同和推送事件的 `opcode` 的不同得到不同的结果.
 *
 * @since 4.1.0
 */
public sealed class EmitResult {
    /**
     * 不属于任何可处理的 `opcode`, 但在 [EmitEventOptions]
     * 中配置了跳过而得到的无效返回值。
     */
    public data object Nothing : EmitResult()

    /**
     * `opcode` 为 `0`，
     * 普通的执行了事件调度、结束并返回。
     */
    public data object Dispatched : EmitResult()

    /**
     * `opcode` 为 `13`，
     * 对内容进行校验后得到了 [verified] 签名结果。
     */
    public data class Verified(val verified: Signal.CallbackVerify.Verified) : EmitResult()
}

/**
 * 在使用 [Bot.emitEvent] 时可选的一些额外属性或选项信息。
 * @since 4.1.0
 */
public class EmitEventOptions {
    /**
     * 如果为 `true`,
     * 则 payload 中解析出 [0, 13] 以外的 `op` 值不会抛出异常。
     * 默认为 `false`
     */
    public var ignoreUnknownOpcode: Boolean = false

    /**
     * 如果为 `true`,
     * 则 payload 中不存在 `op` 值时不会抛出异常。
     * 默认为 `false`
     */
    public var ignoreMissingOpcode: Boolean = false

    /**
     * 如果需要对此回调事件进行 ed25519 签名校验，
     * 则配置它所需的请求头透传参数。
     *
     * 如果你想要以自己的逻辑提前校验则可设为 `null`，
     * 如果为 `null` 则不会进行校验。
     *
     * 默认为 `null`。
     *
     * 更多参考 [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/interface-framework/sign.html)
     */
    public var ed25519SignatureVerification: Ed25519SignatureVerification? = null
}

/**
 * 进行 Ed25519 签名校验所需的参数。
 *
 * 更多参考 [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/interface-framework/sign.html)
 *
 * @since 4.1.0
 */
public data class Ed25519SignatureVerification(
    /**
     * 来自请求头中的 `X-Signature-Ed25519`.
     */
    val signatureEd25519: String,
    /**
     * 来自请求头中的 `X-Signature-Timestamp`.
     */
    val signatureTimestamp: String,
)
