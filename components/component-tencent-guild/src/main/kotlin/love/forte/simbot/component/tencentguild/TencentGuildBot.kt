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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.Bot
import love.forte.simbot.ComplexID
import love.forte.simbot.ID
import java.security.MessageDigest

/**
 * 一个tencent频道BOT的接口实例。
 * @author ForteScarlet
 */
public abstract class TencentGuildBot : Bot {

    abstract override val id: TencentGuildBotID

    public abstract val client: HttpClient


    /**
     * Bot的 [票据](https://bot.q.qq.com/wiki/develop/api/#%E7%A5%A8%E6%8D%AE%E8%AF%B4%E6%98%8E)。
     */
    @Serializable(with = Ticket.Serializer::class)
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
        internal val authorizationBotToken: String = "Bot $appId"
        internal val authorizationBearerToken: String = ""

        public object Serializer : KSerializer<Ticket> {
            override fun deserialize(decoder: Decoder): Ticket {
                val split = decoder.decodeString().split('.', limit = 3)
                require(split.size == 3) {
                    "Cannot resolve to ticket: pair size less than 3: " + split.size
                }

                return Ticket(
                    split[0], // appId
                    split[1], // appKey
                    split[0] + '.' + split[2]
                )
            }

            override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("TencentGuildBotTicket", PrimitiveKind.STRING)

            override fun serialize(encoder: Encoder, value: Ticket) {
                val str = buildString {
                    append(value.appId).append('.')
                    append(value.appKey).append('.')
                    val tokenRandomStr = value.token.split('.', limit = 2)
                    require(tokenRandomStr.size == 2) { "Token has no random str part." }
                    append(tokenRandomStr.last())
                }
                encoder.encodeString(str)
            }

        }
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
internal inline fun digest(algorithm: String, block: MessageDigest.() -> Unit): ByteArray =
    MessageDigest.getInstance(algorithm).also(block).digest()

internal fun ByteArray.toHex(): String {
    return buildString {
        this@toHex.forEach { b ->
            val str = (b.toInt() and 0xff).toString(16)
            if (str.length == 1) {
                append('0')
            }
            append(str)
        }
    }
}


@Suppress("EqualsOrHashCode")
@SerialName("TCG.BOT.ID")
@Serializable // TODO
public class TencentGuildBotID constructor(
    public val ticket: TencentGuildBot.Ticket
) : ComplexID() {

    private val ticketMD5: ByteArray by lazy(LazyThreadSafetyMode.NONE) {
        digest("md5") {
            update(ticket.appId.encodeToByteArray())
            update(ticket.appKey.encodeToByteArray())
            //update(ticket.token.encodeToByteArray())
        }
    }

    override fun compareTo(other: ID): Int {
        return if (other is TencentGuildBotID) ticketMD5.sum().compareTo(other.ticketMD5.sum())
        else -1
    }

    override fun hashCode(): Int = ticketMD5.hashCode()
    override fun toString(): String = ticketMD5.toHex()
}
