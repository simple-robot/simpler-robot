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

@file:Suppress("unused")
@file:JvmName("KaiheilaBots")

package love.forte.simbot.component.kaiheila

import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import love.forte.simbot.LogAble
import love.forte.simbot.bot.Bot
import love.forte.simbot.component.kaiheila.api.Api
import love.forte.simbot.component.kaiheila.api.ApiConfiguration
import love.forte.simbot.component.kaiheila.event.Event
import love.forte.simbot.component.kaiheila.objects.Guild
import love.forte.simbot.component.kaiheila.objects.User
import love.forte.simbot.constant.PriorityConstant
import org.slf4j.Logger
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


/**
 * 一个 khlBot所应有的基础API接口。
 *
 * @see KhlBot
 */
public interface KhlBotApi {

    /**
     * 得到此BOT下所有的[服务器][Guild]
     */
    @JvmSynthetic
    suspend fun guilds(): List<Guild>
    fun getGuilds() = runBlocking { guilds() }

    /**
     * 根据 [guildId] 寻找一个加入 [服务器][Guild]。
     * @throws love.forte.simbot.component.kaiheila.api.KhlApiException Api返回错误的时候。
     */
    @JvmSynthetic
    suspend fun guild(guildId: String): Guild
    fun getGuild(guildId: String) = runBlocking { guild(guildId) }

    /**
     * 根据 [服务器][guildId] 和 [用户ID][userId] 查询一个人的信息。
     * @throws love.forte.simbot.component.kaiheila.api.KhlApiException Api返回错误的时候。
     */
    @JvmSynthetic
    suspend fun viewUser(guildId: String, userId: String): User
    fun getUserView(guildId: String, userId: String) = runBlocking { viewUser(guildId, userId) }


}


/**
 * 一个开黑啦Bot的信息实例。
 *
 * 参考 [开黑啦 - 机器人](https://developer.kaiheila.cn/bot)
 *
 *
 * 机器人连接模式有两种：webSocket 和 webhook.
 *
 *
 * @author ForteScarlet
 */
public interface KhlBot : LogAble, Bot, KhlBotApi, CoroutineScope {

    val api: Api

    /**
     * bot所属的logger
     */
    override val log: Logger

    /**
     * 与网络日志相关的logger
     */
    val networkLog: Logger

    /**
     * 此bot所使用的api配置信息。
     */
    val apiConfiguration: ApiConfiguration

    /**
     * Client id.
     */
    val clientId: String


    val client: HttpClient

    /**
     * token info. 可以重新生成，因此也允许运行时更新。
     */
    var token: String


    /**
     * client secret. 可以重新生成，因此也允许运行时更新。
     */
    var clientSecret: String


    /**
     * 连接模式。
     */
    val connectionMode: ConnectionMode


    /**
     * 启用这个bot
     */
    @JvmSynthetic
    suspend fun start()
    fun startBot() = runBlocking { start() }


    /**
     * 终止这个bot.
     */
    @JvmSynthetic
    suspend fun close(cause: Throwable? = null)
    fun closeBot(cause: Throwable) = runBlocking { close(cause) }
    fun closeBot() = runBlocking { close() }

    @JvmSynthetic
    suspend fun join()
    fun joinBot() = runBlocking { join() }


    fun listen(priority: Int = PriorityConstant.LAST, listener: suspend (Event<*>) -> Unit)

}


public val KhlBot.botCode: String get() = botInfo.botCode
public val KhlBot.botName: String get() = botInfo.botName
public val KhlBot.botAvatar: String? get() = botInfo.botAvatar


public inline fun <reified E> KhlBot.listenPrecise(
    priority: Int = PriorityConstant.LAST,
    crossinline listener: suspend (E) -> Unit,
) = listen(priority) {
    if (it is E) listener(it)
}


/**
 * 使用Websocket协议通讯的bot。
 */
public interface WebsocketBot : KhlBot {
    override val connectionMode: ConnectionMode
        get() = ConnectionMode.WEBSOCKET
}

/**
 * 使用Webhook方式的bot。
 */
public interface WebhookBot : KhlBot {
    override val connectionMode: ConnectionMode
        get() = ConnectionMode.WEBHOOK
    // 他还需要一些额外的参数。

    /**
     * Verify Token. 可以重新生成。
     */
    var verifyToken: String


    /**
     * Encrypt Key. 可以重新生成，可以为空。
     */
    var encryptKey: String?
}


public enum class ConnectionMode {
    WEBSOCKET, WEBHOOK
}


public inline fun KhlBot.requireMode(
    mode: ConnectionMode,
    msg: () -> String = { "KaiheilaBot's connection mode require $mode, but not." },
) {
    if (this.connectionMode != mode) {
        throw IllegalStateException(msg())
    }
}

public inline fun KhlBot.requireWebsocketMode(msg: () -> String = { "KaiheilaBot's connection mode require Websocket, but not." }): WebsocketBot {
    if (this.isWebsocketBot()) return this else throw IllegalStateException(msg())
}

public inline fun KhlBot.requireWebhookMode(msg: () -> String = { "KaiheilaBot's connection mode require Webhook, but not." }): WebhookBot {
    if (this.isWebhookBot()) return this else throw IllegalStateException(msg())
}


public fun KhlBot.checkMode(mode: ConnectionMode): Boolean = connectionMode == mode


@OptIn(ExperimentalContracts::class)
public fun KhlBot.isWebsocketBot(): Boolean {
    contract {
        returns(true) implies (this@isWebsocketBot is WebsocketBot)
    }
    return checkMode(ConnectionMode.WEBSOCKET)
}


@OptIn(ExperimentalContracts::class)
public fun KhlBot.isWebhookBot(): Boolean {
    contract {
        returns(true) implies (this@isWebhookBot is WebhookBot)
    }
    return checkMode(ConnectionMode.WEBSOCKET)
}

