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

@file:JvmName("Events")
@file:Suppress("unused")

package love.forte.simbot.component.kaiheila.event

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import love.forte.simbot.component.kaiheila.KhlBot
import love.forte.simbot.component.kaiheila.api.BaseRespData
import love.forte.simbot.component.kaiheila.objects.Channel
import love.forte.simbot.component.kaiheila.objects.Role
import love.forte.simbot.component.kaiheila.objects.User
import java.util.*


/**
 *
 * 开黑啦Event - [事件Event](https://developer.kaiheila.cn/doc/event)
 *
 *
 * 当 websocket 或 webhook 收到 s=0 的消息时，代表当前收到的消息是事件(包含用户的聊天消息及系统的通知消息等)。
 *
 * @author ForteScarlet
 */
public interface Event<E : Event.Extra> {

    /**
     * 每个事件的出现，都会是对应的某个BOT所触发的。
     *
     */
    val bot: KhlBot

    /**
     * 消息频道类型.
     */
    @SerialName("channel_type")
    val channelType: Channel.Type

    /**
     * 事件的类型。
     * 1:文字消息, 2:图片消息，3:视频消息，4:文件消息， 8:音频消息，9:KMarkdown，10:card消息，255:系统消息, 其它的暂未开放
     * @see Type
     */
    val type: Type


    /**
     * 事件基本类型。详见 [事件 - 事件主要格式](https://developer.kaiheila.cn/doc/event)
     *
     * - 1:文字消息,
     * - 2:图片消息，
     * - 3:视频消息，
     * - 4:文件消息，
     * - 8:音频消息，
     * - 9:KMarkdown，
     * - 10:card消息，
     * - 255:系统消息,
     * - 其它的暂未开放
     *
     */
    @Serializable(with = EventTypeSerializer::class)
    @Suppress("unused")
    public enum class Type(val type: Int) {
        UNKNOWN(-999999),

        TEXT(EventTypeConstant.T_TEXT),
        IMAGE(EventTypeConstant.T_IMAGE),
        VIDEO(EventTypeConstant.T_VIDEO),
        FILE(EventTypeConstant.T_FILE),
        VOICE(EventTypeConstant.T_VOICE),
        KMD(EventTypeConstant.T_KMD),
        CARD(EventTypeConstant.T_CARD),
        SYS(EventTypeConstant.T_SYS),
        ;

        companion object {
            @JvmStatic
            fun byType(type: Int): Type {
                if (type == UNKNOWN.type) {
                    return UNKNOWN
                }
                if (!EventTypeConstant.containsType(type)) {
                    throw IndexOutOfBoundsException("Type $type")
                }

                val values = values()
                for (i in 1..values.size) {
                    val v = values[i]
                    if (v.type == type) {
                        return v
                    }
                }

                throw NoSuchElementException("Type $type")
            }

            /**
             * Get instance of [Event.Type] by [type], or default value (like null).
             */
            @JvmStatic
            @JvmOverloads
            fun byTypeOr(type: Int, default: Type? = null): Type? {
                if (type == UNKNOWN.type) {
                    return UNKNOWN
                }
                if (!EventTypeConstant.containsType(type)) {
                    return default
                }

                val values = values()
                for (i in 1..values.size) {
                    val v = values[i]
                    if (v.type == type) {
                        return v
                    }
                }

                return default
            }
        }

    }


    /**
     * 发送目的 id，如果为是 GROUP 消息，则 target_id 代表频道 id
     */
    @SerialName("target_id")
    val targetId: String

    /**
     * 发送者 id, `1` 代表系统
     */
    @SerialName("author_id")
    val authorId: String

    /**
     * 消息内容, 文件，图片，视频时，content 为 url
     */
    val content: String

    /**
     * msgId
     */
    @SerialName("msg_id")
    val msgId: String


    /**
     * 消息发送时间的**毫秒**时间戳.
     */
    @SerialName("msg_timestamp")
    // @TimeType(TimeUnit.MILLISECONDS)
    val msgTimestamp: Long

    /**
     * 随机串，与用户消息发送 api 中传的 nonce 保持一致
     */
    val nonce: String

    /**
     * 不同的消息类型，结构不一致。
     */
    val extra: E


    /**
     * 事件中的额外消息结构。
     *
     * 分为两种情况：[Event.type] == `255` 的时候与相反的时候。
     *
     *
     * 等于 `255` 的时候即代表为 *系统事件消息*，否则是 *文字频道消息*
     *
     * @see Event.extra
     */
    public sealed interface Extra {
        /**
         * Type.
         */
        val type: Any


        /**
         * 当 [Event.type] == `255` 时的 [结构](https://developer.kaiheila.cn/doc/event/event-introduction#)
         */
        public interface Sys<B : Sys.Body> : Extra {
            override val type: String
            val body: B
            interface Body
        }

        /**
         * 当 [Event.type] != `255` 时的 [结构](https://developer.kaiheila.cn/doc/event/event-introduction#)
         */
        public interface Text : Extra {
            override val type: Int

            /**
             * 服务器 id
             */
            val guildId: String

            /**
             * 频道名
             */
            val channelName: String

            /**
             * 提及到的用户 id 的列表
             */
            val mention: List<String>

            /**
             * 是否 mention 所有用户
             */
            val mentionAll: Boolean

            /**
             * mention 用户角色的数组
             */
            val mentionRoles: List<Role>

            /**
             * 是否 mention 在线用户
             */
            val mentionHere: Boolean

            /**
             * 用户信息, 见 [对象-用户User](https://developer.kaiheila.cn/doc/objects#%E7%94%A8%E6%88%B7User) ([User])
             */
            val author: User

        }

    }

}


public interface BotInitialized {
    /**
     * bot应当只能初始化一次。
     */
    var bot: KhlBot
}


@Serializable
public data class SimpleEvent<E : Event.Extra>(
    @SerialName("channel_type")
    override val channelType: Channel.Type,
    override val type: Event.Type,
    @SerialName("target_id")
    override val targetId: String,
    @SerialName("author_id")
    override val authorId: String,
    override val content: String,
    @SerialName("msg_id")
    override val msgId: String,
    @SerialName("msg_timestamp")
    override val msgTimestamp: Long,
    override val nonce: String,
    override val extra: E,
) : Event<E>, BotInitialized, BaseRespData()






@Serializable
public data class SimpleText(
    override val type: Int,
    @SerialName("guild_id")
    override val guildId: String = "",
    @SerialName("channel_name")
    override val channelName: String = "",
    override val mention: List<String> = emptyList(),
    @SerialName("mention_all")
    override val mentionAll: Boolean = false,
    @SerialName("mention_roles")
    override val mentionRoles: List<Role> = emptyList(),
    @SerialName("mention_here")
    override val mentionHere: Boolean = false,
    override val author: User
) : Event.Extra.Text



/**
 * 判断 [Event.authorId] 是否等于 `"1"`
 */
public fun Event<*>.isFromSys(): Boolean = authorId == "1"


/**
 * 类型枚举 [Event.Type] 的类型常量类。
 */
internal object EventTypeConstant {
    internal const val T_TEXT = 1
    internal const val T_IMAGE = 2
    internal const val T_VIDEO = 3
    internal const val T_FILE = 4
    internal const val T_VOICE = 8
    internal const val T_KMD = 9
    internal const val T_CARD = 10

    /** sys目前与上述几种类型的关联性/连续性差距较大，暂时用于单独判断。 */
    internal const val T_SYS = 255

    /** all types */
    private val types = BitSet(16).apply {
        set(T_TEXT)
        set(T_IMAGE)
        set(T_VIDEO)
        set(T_FILE)
        set(T_VOICE)
        set(T_KMD)
        set(T_CARD)
    }

    /** 判断是否存在某个类型。 */
    public fun containsType(type: Int): Boolean = types[type] || type == T_SYS
}


/**
 * [Event.Type] 序列化器。
 */
public object EventTypeSerializer : KSerializer<Event.Type> {
    /** descriptor for Int */
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EventType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Event.Type {
        val value = decoder.decodeInt()
        return Event.Type.byType(value)
    }

    override fun serialize(encoder: Encoder, value: Event.Type) {
        encoder.encodeInt(value.type)
    }
}
