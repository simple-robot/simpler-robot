/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.websocket.*
import kotlinx.serialization.Serializable
import love.forte.simbot.Bot
import love.forte.simbot.CharSequenceID

/**
 * 一个tencent频道BOT的接口实例。
 * @author ForteScarlet
 */
public abstract class TencentGuildBot : Bot {
    /**
     * Bot的appID
     */
    abstract override val id: CharSequenceID

    public abstract val client: HttpClient


    /**
     * Bot的 [票据](https://bot.q.qq.com/wiki/develop/api/#%E7%A5%A8%E6%8D%AE%E8%AF%B4%E6%98%8E)。
     */
    @Serializable
    public data class Ticket(
        /**
         * app_id	用于识别一个机器人的 id
         */
        public var appId: String,

        /**
         * app_key	用于在 oauth 场景进行请求签名的密钥，在一些描述中也叫做 app_secret
         */
        public var appKey: String,

        /**
         * token	机器人token，用于以机器人身份调用 openapi，格式为 ${app_id}.${random_str}
         */
        public var token: String,
    ) {
        public val authorizationBotToken: String = "Bot $appId"

    }
}

@TencentGuildBotConfDSL
public class TicketBuilder {
    /**
     * app_id	用于识别一个机器人的 id
     */
    public lateinit var appId: String

    /**
     * app_key	用于在 oauth 场景进行请求签名的密钥，在一些描述中也叫做 app_secret
     */
    public lateinit var appKey: String

    /**
     * token	机器人token，用于以机器人身份调用 openapi，格式为 ${app_id}.${random_str}
     */
    public lateinit var token: String
    public fun build(): TencentGuildBot.Ticket {
        require(token.startsWith(appId)) { "'token' verify failed: not from 'appId'" }
        return TencentGuildBot.Ticket(appId, appKey, token)
    }
}

/**
 * QQ频道Bot配置类。
 */
public class TencentGuildBotConfiguration {

    /**
     * Bot的 [票据]()
     */
    public var ticket: TencentGuildBot.Ticket? = null

    //region 票据
    @TencentGuildBotConfDSL
    public inline fun ticket(block: TicketBuilder.() -> Unit) {
        ticket = TicketBuilder().also(block).build()
    }

    public var client: HttpClient = HttpClient {
        install(WebSockets)
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json) // TODO
        }
        install(HttpTimeout) {
            this.connectTimeoutMillis = 6000
        }
    }

    //endregion

    // 其他的

}

@Retention(AnnotationRetention.BINARY)
@DslMarker
public annotation class TencentGuildBotConfDSL


@TencentGuildBotConfDSL
public inline fun tencentGuildBotConfiguration(block: TencentGuildBotConfiguration.() -> Unit): TencentGuildBotConfiguration {
    return TencentGuildBotConfiguration().also(block)
}


// TODO in api module




// @Suppress("EqualsOrHashCode")
// @SerialName("TCG.BOT.ID")
// @Serializable // TODO
// public class TencentGuildBotID constructor(
//     public val ticket: TencentGuildBot.Ticket
// ) : ComplexID() {
//
//     private val ticketMD5: ByteArray by lazy(LazyThreadSafetyMode.NONE) {
//         digest("md5") {
//             update(ticket.appId.encodeToByteArray())
//             update(ticket.appKey.encodeToByteArray())
//             //update(ticket.token.encodeToByteArray())
//         }
//     }
//
//     override fun compareTo(other: ID): Int {
//         return if (other is TencentGuildBotID) ticketMD5.sum().compareTo(other.ticketMD5.sum())
//         else -1
//     }
//
//     override fun hashCode(): Int = ticketMD5.hashCode()
//     override fun toString(): String = ticketMD5.toHex()
// }
