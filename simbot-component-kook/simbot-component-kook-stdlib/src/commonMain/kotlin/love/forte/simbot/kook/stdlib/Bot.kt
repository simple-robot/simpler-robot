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

package love.forte.simbot.kook.stdlib

import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.sync.Mutex
import love.forte.simbot.kook.TokenType
import love.forte.simbot.kook.api.user.GetMeApi
import love.forte.simbot.kook.api.user.Me
import love.forte.simbot.kook.api.user.OfflineApi
import love.forte.simbot.kook.event.Event
import love.forte.simbot.kook.event.EventExtra
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmStatic

/**
 * 一个 KOOK Bot。
 *
 * [Bot] 承载了通过 `WebSocket` 的方式与 KOOK 服务器连接并订阅事件的能力，
 * 以及通过各种 [KOOK HTTP API][love.forte.simbot.kook.api.KookApi] 进行功能交互的能力。
 *
 * ### 日志
 *
 * [Bot] 中主要提供了两个日志命名：
 * - `love.forte.simbot.kook.bot.${ticket.clientId}`
 * - `love.forte.simbot.kook.event.${ticket.clientId}`
 *
 * (其中 `ticket.clientId` 对应了当前 bot 的 [Bot.ticket] 中的实际信息)
 *
 * 其中，前缀为 `love.forte.simbot.kook.event` 的日志命名会主要输出与事件有较大关系的信息，例如每次收到的事件原始JSON等。
 * 而其他的日志则主要由前缀为 `love.forte.simbot.kook.bot` 的日志命名输出。
 *
 * 大部分日志都是 `DEBUG` 或 `TRACE` 级别的。
 *
 * @author ForteScarlet
 */
public interface Bot : CoroutineScope {
    override val coroutineContext: CoroutineContext

    /**
     * 此 bot 的票据信息。
     */
    public val ticket: Ticket

    /**
     * 当前 bot 所使用的配置信息。
     */
    public val configuration: BotConfiguration

    /**
     * 当前bot所使用的 [HttpClient] 实例。
     *
     * 此实例代表的用于进行API请求（xxxRequest）的http client。
     */
    public val apiClient: HttpClient

    /**
     * Bot 是否处于活跃状态。
     *
     * @see kotlinx.coroutines.Job.isActive
     */
    public val isActive: Boolean

    /**
     * Bot 是否至少启动过一次。
     */
    public val isStarted: Boolean

    /**
     * 当前bot作为用户时的信息。
     *
     * bot必须至少启动一次或执行过一次 [me] 才能获取到此信息。
     *
     * @throws IllegalStateException 如果bot未启动或未执行过 [me] 则会抛出此异常。
     */
    public val botUserInfo: Me

    /**
     * 添加一个事件处理器。
     * 所有事件处理器会在每次触发的时候按照添加顺序依次进行处理。
     *
     * Deprecated: 重命名为了 [subscribe]
     *
     * @see subscribe
     * @param processorType 事件处理器类型。默认为 [ProcessorType.NORMAL]。
     * @param processor 事件处理器。
     */
    @Suppress("DEPRECATION")
    @Deprecated("Use `subscribe`", ReplaceWith("subscribe(subscribeSequence = processorType, processor = processor)"))
    public fun processor(
        processorType: ProcessorType,
        processor: EventProcessor
    ) {
        subscribe(
            subscribeSequence = when (processorType) {
                ProcessorType.PREPARE -> SubscribeSequence.PREPARE
                ProcessorType.NORMAL -> SubscribeSequence.NORMAL
            },
            processor = processor
        )
    }

    /**
     * 添加一个 [ProcessorType.NORMAL] 类型的事件处理器。
     * 所有事件处理器会在每次触发的时候按照添加顺序依次进行处理。
     *
     * Deprecated: 重命名为了 [subscribe]
     *
     * @see subscribe
     * @param processor 事件处理器。
     */
    @Suppress("DEPRECATION")
    @Deprecated("Use `subscribe`", ReplaceWith("subscribe(SubscribeSequence.NORMAL, processor = processor)"))
    public fun processor(processor: EventProcessor) {
        processor(ProcessorType.NORMAL, processor)
    }

    /**
     * 订阅事件并使用事件处理器 [processor] 进行处理。。
     * 所有事件处理器会在每次触发的时候按照添加顺序依次进行处理。
     *
     * @param subscribeSequence 事件订阅的序列类型。默认为 [SubscribeSequence.NORMAL]。
     * @param processor 事件处理器。
     */
    public fun subscribe(
        subscribeSequence: SubscribeSequence,
        processor: EventProcessor
    )

    /**
     * 添加一个 [SubscribeSequence.NORMAL] 类型的事件处理器。
     * 所有事件处理器会在每次触发的时候按照添加顺序依次进行处理。
     *
     * @param processor 事件处理器。
     */
    public fun subscribe(processor: EventProcessor) {
        subscribe(SubscribeSequence.NORMAL, processor)
    }

    /**
     * 查询 bot 作为用户的信息。
     *
     * @see GetMeApi
     */
    @STP
    public suspend fun me(): Me

    /**
     * 让 bot 下线。
     *
     * @see OfflineApi
     */
    @ST
    public suspend fun offline()

    /**
     * 启动此bot，即连接到 ws 服务器并订阅事件。
     *
     * 如果 bot 已经被启动，则关闭旧连接并重新连接。
     *
     * [start] 会由 [Mutex] 进行同步，同一时间只会有一个启动流程在执行。
     *
     * 如果启动过程中出现任何异常则会关闭当前 Bot。
     *
     * @throws kotlinx.coroutines.CancellationException Bot 已经被关闭时 ([isActive] == false)
     * @throws IllegalStateException bot 启动失败
     *
     */
    @ST
    public suspend fun start() {
        start(true)
    }

    /**
     * 启动此bot，即连接到 ws 服务器并订阅事件。
     *
     * 如果 bot 已经被启动，则关闭旧连接并重新连接。
     *
     * [start] 会由 [Mutex] 进行同步，同一时间只会有一个启动流程在执行。
     *
     * 当 [cancelBotOnFailure] 为 true 时，如果启动过程中出现任何异常则会终止当前 Bot。
     *
     * @see start
     * @param cancelBotOnFailure 为 true 时，如果启动过程中出现任何异常则会终止当前 Bot。
     * @throws kotlinx.coroutines.CancellationException Bot 已经被关闭时 ([isActive] == false)
     * @throws IllegalStateException bot 启动失败
     *
     */
    @ST
    public suspend fun start(cancelBotOnFailure: Boolean)

    /**
     * 挂起 bot 直到其被 [取消][cancel]。
     */
    @ST(asyncBaseName = "asFuture", asyncSuffix = "")
    public suspend fun join()

    /**
     * 取消此 bot 。
     *
     * @see kotlinx.coroutines.Job.cancel
     */
    public fun cancel(reason: Throwable?)

    /**
     * 取消此 bot 。
     *
     * @see kotlinx.coroutines.Job.cancel
     */
    public fun cancel() {
        cancel(null)
    }
}

/**
 * Start and return bot self.
 *
 * @see Bot.start
 */
public suspend fun Bot.alsoStart(): Bot = also { start() }

/**
 * Cancel bot and join.
 *
 * @see Bot.cancel
 * @see Bot.join
 */
public suspend fun Bot.cancelAndJoin() {
    cancel()
    join()
}

/**
 * Start bot and join.
 *
 * @see Bot.start
 * @see Bot.join
 */
public suspend fun Bot.startAndJoin() {
    start()
    join()
}

/**
 * Bot 所使用的部分权限"票据"。
 *
 * 这些信息可以在 [KOOK Bot 应用](https://developer.kookapp.cn/app/index)
 * 中选择某个具体的 Bot 进行查看、配置。
 *
 * @author ForteScarlet
 */
public interface Ticket {
    /**
     * Client Id.
     */
    public val clientId: String

    /**
     * Token.
     */
    public val token: String

    /**
     * Type of token.
     */
    public val type: TokenType

    public companion object {

        /**
         * 得到 [TokenType.BOT] 类型的、用于 WebSocket 连接的票据信息。
         */
        @JvmStatic
        public fun botWsTicket(clientId: String, token: String): Ticket = BotWsTicket(clientId, token)
    }
}


private data class BotWsTicket(override val clientId: String, override val token: String) : Ticket {
    override val type: TokenType
        get() = TokenType.BOT
}


/**
 * 针对一个具体的 [EventExtra] 类型进行监听。
 *
 * @param EX 事件内容 [Event.extra] 的具体类型。
 *
 */
@Suppress("DEPRECATION")
@Deprecated("Use `Bot.subscribe(...)`", ReplaceWith("this.subscribe<EX>(processor)"))
public inline fun <reified EX : EventExtra> Bot.processor(
    crossinline processor: suspend Event<EX>.(raw: String) -> Unit
) {
    processor { raw ->
        if (extra is EX) {
            @Suppress("UNCHECKED_CAST")
            processor.invoke(this as Event<EX>, raw)
        }
    }
}

/**
 * 针对一个 [Event.type] 目标进行监听。
 *
 * @param type 事件类型。
 */
@Suppress("DEPRECATION")
@Deprecated("Use `Bot.subscribe(...)`", ReplaceWith("this.subscribe(type, processor)"))
public inline fun Bot.processor(type: Event.Type, crossinline processor: suspend Event<*>.(raw: String) -> Unit) {
    processor { raw ->
        if (this.type == type) {
            processor(raw)
        }
    }
}

/**
 * [Bot] 中的事件处理器类型。
 *
 * Deprecated: 重命名为了 [SubscribeSequence]
 *
 * @see SubscribeSequence
 */
@Deprecated("Use `SubscribeSequence` with `Bot.subscribe`", ReplaceWith("SubscribeSequence"))
public enum class ProcessorType {
    /**
     * 前置类型。所有前置事件处理器会在每次触发的时候优先于 [普通类型][NORMAL] 的事件处理器进行处理，
     * 并且所有前置处理器**不会**异步执行。
     *
     * 前置类型的事件处理器应当尽可能的快速执行完毕。
     */
    PREPARE,

    /**
     * 普通类型。所有事件处理器会在每次触发的时候按照添加顺序依次进行处理，
     * 但是以事件为单位，整个流程可能是异步的。
     *
     * [NORMAL] 最终是否会异步处理会受配置项 [BotConfiguration.isNormalEventProcessAsync]
     * 的影响，默认为异步。如果此配置为 `false` 则 [NORMAL] 的实际表现效果将会与 [PREPARE] 类似，
     * 只是优先级低于 [PREPARE]。
     */
    NORMAL
}

/**
 * [Bot] 中订阅事件时的序列类型。
 */
public enum class SubscribeSequence {
    /**
     * 前置类型。所有前置事件处理器会在每次触发的时候优先于 [普通类型][NORMAL] 的事件处理器进行处理，
     * 并且所有前置处理器**不会**异步执行。
     *
     * 前置类型的事件处理器应当尽可能的快速执行完毕。
     */
    PREPARE,

    /**
     * 普通类型。所有事件处理器会在每次触发的时候按照添加顺序依次进行处理，
     * 但是以事件为单位，整个流程可能是异步的。
     *
     * [NORMAL] 最终是否会异步处理会受配置项 [BotConfiguration.isNormalEventProcessAsync]
     * 的影响，默认为异步。如果此配置为 `false` 则 [NORMAL] 的实际表现效果将会与 [PREPARE] 类似，
     * 只是优先级低于 [PREPARE]。
     */
    NORMAL
}
