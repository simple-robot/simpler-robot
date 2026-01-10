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

package love.forte.simbot.kook.event

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import love.forte.simbot.annotations.FragileSimbotAPI
import love.forte.simbot.kook.ExperimentalKookApi
import love.forte.simbot.kook.InternalKookApi
import love.forte.simbot.kook.objects.SimpleAttachments
import love.forte.simbot.kook.objects.SimpleUser
import love.forte.simbot.kook.objects.kmd.RawValueKMarkdown
import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic


/**
 * [事件 Event](https://developer.kookapp.cn/doc/event/event-introduction)
 *
 * > 当 `websocket` 或 `webhook` 收到 `s=0` 的消息时，
 * 代表当前收到的消息是事件(包含用户的聊天消息及系统的通知消息等)。
 *
 */
@Serializable
public data class Event<out E : EventExtra>(

    /**
     * 消息通道类型, `GROUP` 为组播消息, `PERSON` 为单播消息, `BROADCAST` 为广播消息
     */
    @SerialName("channel_type")
    public val channelTypeValue: String,

    /**
     * 事件的类型。
     *
     * - 1:文字消息
     * - 2:图片消息
     * - 3:视频消息
     * - 4:文件消息
     * - 8:音频消息
     * - 9:KMarkdown
     * - 10:card消息
     * - 255:系统消息
     * - 其它的暂未开放
     */
    @SerialName("type")
    public val typeValue: Int,

    /**
     * 发送目的, 频道消息类时, 代表的是频道 `channel_id`，如果 `channel_type` 为 `GROUP` 组播且 `type` 为 `255` 系统消息时，则代表服务器 `guild_id`
     */
    @SerialName("target_id")
    public val targetId: String,

    /**
     * 发送者 id, `1` 代表系统
     */
    @SerialName("author_id")
    public val authorId: String,

    /**
     * 消息内容, 文件，图片，视频时，content 为 url
     */
    public val content: String,

    /**
     * 消息的 id
     */
    @SerialName("msg_id")
    public val msgId: String,

    /**
     * 消息发送时间的毫秒时间戳
     */
    @SerialName("msg_timestamp")
    public val msgTimestamp: Long,

    /**
     * 随机串，与用户消息发送 api 中传的 nonce 保持一致
     */
    public val nonce: String,

    /**
     * 事件的数据内容。
     * 不同的事件类型，结构不一致
     */
    public val extra: E
) {

    /**
     * 事件的类型。
     *
     * 基于 [typeValue] 的值映射的 [Type] 元素。
     * 当类型值未知时得到 `null` 。
     *
     */
    public val type: Type?
        get() = when (typeValue) {
            Type.TEXT.value -> Type.TEXT
            Type.IMAGE.value -> Type.IMAGE
            Type.VIDEO.value -> Type.VIDEO
            Type.FILE.value -> Type.FILE
            Type.AUDIO.value -> Type.AUDIO
            Type.KMARKDOWN.value -> Type.KMARKDOWN
            Type.CARD.value -> Type.CARD
            Type.SYSTEM.value -> Type.SYSTEM
            else -> null
        }

    /**
     * 事件的类型枚举。
     */
    public enum class Type(public val value: Int) {
        /**
         * 文字消息
         */
        TEXT(1),

        /**
         * 图片消息
         */
        IMAGE(2),

        /**
         * 视频消息
         */
        VIDEO(3),

        /**
         * 文件消息
         */
        FILE(4),

        /**
         * 音频消息
         */
        AUDIO(8),

        /**
         * KMarkdown
         */
        KMARKDOWN(9),

        /**
         * card消息
         */
        CARD(10),

        /**
         * 系统消息
         */
        SYSTEM(255),
        ;

    }

    /**
     * 消息通道类型。
     *
     * 基于 [channelTypeValue] 的值映射的 [ChannelType] 元素。
     * 当类型值未知时得到 `null` 。
     */
    public val channelType: ChannelType?
        get() = when (channelTypeValue) {
            ChannelType.GROUP.value -> ChannelType.GROUP
            ChannelType.PERSON.value -> ChannelType.PERSON
            ChannelType.BROADCAST.value -> ChannelType.BROADCAST
            else -> null
        }

    /**
     * 消息通道类型, `GROUP` 为组播消息, `PERSON` 为单播消息, `BROADCAST` 为广播消息
     */
    public enum class ChannelType(public val value: String) {
        GROUP("GROUP"),
        PERSON("PERSON"),
        BROADCAST("BROADCAST"),

    }

}

/**
 * 事件的消息 `extra`。
 *
 * ### [UnknownExtra]
 *
 * [UnknownExtra] 与其他子类型有所不同。[UnknownExtra] 是由框架定义并实现的特殊类型，
 * 它用来承载那些接收后无法被解析或尚未支持的事件类型。
 *
 * @see Event.extra
 * @see TextExtra
 * @see SystemExtra
 * @see UnknownExtra
 *
 */
@Serializable
public sealed class EventExtra {
    public companion object {

        /**
         * 通过 [json] 中的 `type` 和 `extra.type` 来获取对应的 [DeserializationStrategy] 。
         * 当无法获取时得到 `null`。
         *
         * @param json 一个事件 JSON 中的 `d` 属性值。
         *
         * @throws IllegalArgumentException 当 [json] 中某个节点的属性格式不符合预期 （例如 [json] 并非 [JsonObject] 类型）
         *
         */
        @JvmStatic
        public fun serializerOrNull(json: JsonElement): KSerializer<out EventExtra>? {
            val type = json.jsonObject["type"]?.jsonPrimitive?.intOrNull ?: return null
            return serializerOrNull(type)
        }

        /**
         * 通过 [type] 来获取对应的 [DeserializationStrategy] 。
         * 当无法获取时得到 `null`。
         */
        @JvmStatic
        public fun serializerOrNull(type: Int): KSerializer<out EventExtra>? {
            return when (type) {
                Event.Type.TEXT.value -> TextEventExtra.serializer()
                Event.Type.IMAGE.value -> ImageEventExtra.serializer()
                Event.Type.VIDEO.value -> VideoEventExtra.serializer()
                // 文件消息已转为卡片消息，详情请直接参考卡片消息
//                Event.Type.FILE.value -> FileEventExtra.serializer()
                // Unknown?
//                Event.Type.AUDIO.value -> AudioEventExtra.serializer()
                Event.Type.KMARKDOWN.value -> KMarkdownEventExtra.serializer()
                Event.Type.CARD.value -> CardEventExtra.serializer()
                Event.Type.SYSTEM.value -> SystemExtra.serializer()
                else -> null
            }
        }

        /**
         * 通过 [json] 中的 `type` 来获取对应的 [KSerializer] 。
         * 当无法获取时得到 `null`。
         *
         * @param json 一个事件 JSON 中的 `d` 属性值。
         *
         * @throws IllegalArgumentException 当 [json] 中某个节点的属性格式不符合预期 （例如 [json] 并非 [JsonObject] 类型）
         *
         */
        @JvmStatic
        public fun eventSerializerOrNull(json: JsonElement): KSerializer<out Signal.Event<EventExtra>>? {
            val type = json.jsonObject["type"]?.jsonPrimitive?.intOrNull ?: return null
            return eventSerializerOrNull(type)
        }

        /**
         * 通过 [type] 来获取对应的 [KSerializer] 。
         * 当无法获取时得到 `null`。
         */
        @JvmStatic
        public fun eventSerializerOrNull(type: Int): KSerializer<out Signal.Event<EventExtra>>? {
            return when (type) {
                Event.Type.TEXT.value -> TEXT_EVENT_SERIALIZER
                Event.Type.IMAGE.value -> IMAGE_EVENT_SERIALIZER
                Event.Type.VIDEO.value -> VIDEO_EVENT_SERIALIZER
                Event.Type.KMARKDOWN.value -> KMARKDOWN_EVENT_SERIALIZER
                Event.Type.CARD.value -> CARD_EVENT_SERIALIZER
                Event.Type.SYSTEM.value -> SYSTEM_EVENT_SERIALIZER
                else -> null
            }
        }

        private val TEXT_EVENT_SERIALIZER by lazy { Signal.Event.serializer(TextEventExtra.serializer()) }
        private val IMAGE_EVENT_SERIALIZER by lazy { Signal.Event.serializer(ImageEventExtra.serializer()) }
        private val VIDEO_EVENT_SERIALIZER by lazy { Signal.Event.serializer(VideoEventExtra.serializer()) }
        private val KMARKDOWN_EVENT_SERIALIZER by lazy { Signal.Event.serializer(KMarkdownEventExtra.serializer()) }
        private val CARD_EVENT_SERIALIZER by lazy { Signal.Event.serializer(CardEventExtra.serializer()) }
        private val SYSTEM_EVENT_SERIALIZER by lazy { Signal.Event.serializer(SystemExtra.serializer()) }
    }
}

/**
 * 文字频道消息 `extra`
 *
 * > 当 [`type`][Event.typeValue] 非系统消息(255)时
 *
 * 当此事件的频道类型 [Event.channelType] 为 [Event.ChannelType.PERSON] 时，例如 [guildId] 等频道才有的属性可能会使用空内容填充。
 *
 *
 * @see TextEventExtra
 * @see ImageEventExtra
 * @see VideoEventExtra
 * @see KMarkdownEventExtra
 * @see CardEventExtra
 *
 */
@Serializable
@SerialName("text")
public sealed class TextExtra : EventExtra() {
    /**
     * 类型。同 [Event.type][Event.typeValue]
     */
    public abstract val type: Int

    /**
     * 服务器 id
     */
    public abstract val guildId: String?

    /**
     * 频道名
     */
    public abstract val channelName: String?

    /**
     * 提及到的用户 id 的列表
     */
    public abstract val mention: List<String>

    /**
     * 是否 mention 所有用户
     */
    public abstract val isMentionAll: Boolean

    /**
     * mention 用户角色的数组
     */
    public abstract val mentionRoles: List<Int>

    /**
     * 是否 mention 在线用户
     */
    public abstract val isMentionHere: Boolean

    /**
     * 用户信息
     */
    public abstract val author: SimpleUser
}

/**
 * [文字消息事件 extra](https://developer.kookapp.cn/doc/event/message#%E6%96%87%E5%AD%97%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
@Serializable
public data class TextEventExtra(
    override val type: Int,
    @SerialName("guild_id")
    override val guildId: String? = null,
    @SerialName("channel_name")
    override val channelName: String? = null,
    override val mention: List<String> = emptyList(),
    @SerialName("mention_all")
    override val isMentionAll: Boolean = false,
    @SerialName("mention_roles")
    override val mentionRoles: List<Int> = emptyList(),
    @SerialName("mention_here")
    override val isMentionHere: Boolean = false,
    override val author: SimpleUser
) : TextExtra()

/**
 * [图片消息事件 extra](https://developer.kookapp.cn/doc/event/message#%E5%9B%BE%E7%89%87%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
@Serializable
public data class ImageEventExtra(
    override val type: Int,
    @SerialName("guild_id")
    override val guildId: String? = null,
    @SerialName("channel_name")
    override val channelName: String? = null,
    override val mention: List<String> = emptyList(),
    @SerialName("mention_all")
    override val isMentionAll: Boolean = false,
    @SerialName("mention_roles")
    override val mentionRoles: List<Int> = emptyList(),
    @SerialName("mention_here")
    override val isMentionHere: Boolean = false,
    override val author: SimpleUser,

    /**
     * 附件
     */
    public val attachments: SimpleAttachments
) : TextExtra()

/**
 * [视频消息事件 extra](https://developer.kookapp.cn/doc/event/message#%E8%A7%86%E9%A2%91%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
@Serializable
public data class VideoEventExtra(
    override val type: Int,
    @SerialName("guild_id")
    override val guildId: String? = null,
    @SerialName("channel_name")
    override val channelName: String? = null,
    override val mention: List<String> = emptyList(),
    @SerialName("mention_all")
    override val isMentionAll: Boolean = false,
    @SerialName("mention_roles")
    override val mentionRoles: List<Int> = emptyList(),
    @SerialName("mention_here")
    override val isMentionHere: Boolean = false,
    override val author: SimpleUser,

    /**
     * 附件
     */
    public val attachments: SimpleAttachments
) : TextExtra()

/**
 * [KMarkdown 消息事件 extra](https://developer.kookapp.cn/doc/event/message#KMarkdown%20%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
@Serializable
public data class KMarkdownEventExtra(
    override val type: Int,
    @SerialName("guild_id")
    override val guildId: String? = null,
    @SerialName("channel_name")
    override val channelName: String? = null,
    override val mention: List<String> = emptyList(),
    @SerialName("mention_all")
    override val isMentionAll: Boolean = false,
    @SerialName("mention_roles")
    override val mentionRoles: List<Int> = emptyList(),
    @SerialName("mention_here")
    override val isMentionHere: Boolean = false,
    override val author: SimpleUser,

    /**
     * `nav_channels`
     */
    @property:ExperimentalKookApi
    public val navChannels: List<String>? = null,

    /**
     * `code`
     */
    @property:ExperimentalKookApi
    public val code: String? = null,

    /**
     * KMarkdown data
     */
    @property:ExperimentalKookApi
    public val kmarkdown: RawValueKMarkdown
) : TextExtra()

/**
 * [Card 消息事件 extra](https://developer.kookapp.cn/doc/event/message#Card%20%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
@Serializable
public data class CardEventExtra(
    override val type: Int,
    @SerialName("guild_id")
    override val guildId: String? = null,
    @SerialName("channel_name")
    override val channelName: String? = null,
    override val mention: List<String> = emptyList(),
    @SerialName("mention_all")
    override val isMentionAll: Boolean = false,
    @SerialName("mention_roles")
    override val mentionRoles: List<Int> = emptyList(),
    @SerialName("mention_here")
    override val isMentionHere: Boolean = false,
    override val author: SimpleUser,

    /**
     * `nav_channels`
     */
    @property:ExperimentalKookApi
    public val navChannels: List<String>? = null,

    /**
     * `code`
     */
    @property:ExperimentalKookApi
    public val code: String? = null,
) : TextExtra()


/**
 * 系统事件消息 `extra`
 * > 当 [`type`][Event.typeValue] 为系统消息(255)时
 */
@Serializable
@SerialName("sys")
public sealed class SystemExtra : EventExtra() {
//    /**
//     * 标识该事件的类型
//     */
//    public  val type: String,

    /**
     * 该事件关联的具体数据
     */
    @Contextual
    public abstract val body: Any?
}


/**
 * 当一个事件反序列化失败的时候，会被**尝试**使用 [UnknownExtra] 作为 `extra` 的序列化目标。
 * 如果是因为一个未知的事件导致的这次失败，则 [UnknownExtra] 便会反序列化成功并被推送。
 *
 *  [UnknownExtra] 不会提供任何可反序列化的属性，
 *  取而代之的是提供了 [source] 来获取本次反序列化失败的的原始JSON字符串信息。
 *  你可以通过 [source] 来做一些临时性处理，例如解析并获取其中的信息。
 *
 *
 *  ### FragileSimbotApi
 *
 *  [UnknownExtra] 类型的事件会随着支持的事件类型的增多而减少。
 *  对可能造成 [UnknownExtra] 出现概率降低的更新不会做专门的提示。
 *  因此使用 [UnknownExtra] 时应当明确了解其可能出现的内容，同时不可过分依赖它。
 */
@FragileSimbotAPI
@Serializable
@SerialName("$\$UNKNOWN")
public class UnknownExtra : EventExtra() {
    @Transient
    private lateinit var _source: String

    /**
     * 接收到的完整的原始事件JSON字符串
     */
    public val source: String
        get() = _source

    /**
     * @suppress 内部使用，用于初始化 [source] 值
     */
    @InternalKookApi
    @JvmSynthetic
    public fun initSource(source: String) {
        if (sourceInitialized) {
            throw IllegalStateException("'source' has already initialized")
        }

        _source = source
    }

    private val sourceInitialized get() = ::_source.isInitialized

    override fun toString(): String {
        if (!sourceInitialized) {
            return "UnknownExtra(source=<UNINITIALIZED>)"
        }

        return "UnknownExtra(source=$source)"
    }
}
