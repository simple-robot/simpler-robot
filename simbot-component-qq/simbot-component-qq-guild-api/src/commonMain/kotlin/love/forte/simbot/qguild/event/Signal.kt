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

package love.forte.simbot.qguild.event

import kotlinx.serialization.*
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import love.forte.simbot.qguild.QGInternalApi
import love.forte.simbot.qguild.event.Signal.Dispatch.Unknown
import love.forte.simbot.qguild.event.Signal.Resume.Data
import kotlin.jvm.JvmField


/** @suppress */
public sealed interface ReceivedSignal // 接收的，下行

/** @suppress */
public sealed interface SendingSignal // 发送的，上行

/**
 * [负载](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html)
 *
 * 上下行数据。
 *
 * @author ForteScarlet
 */
@Serializable
public sealed class Signal<D>(@Serializable(Opcode.SerializerByCode::class) public val op: Opcode) {
    @SerialName("d")
    public abstract val data: D

    /**
     * [1.连接到 Gateway](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_1-%E8%BF%9E%E6%8E%A5%E5%88%B0-gateway)
     * 第一步先调用 [/gateway][love.forte.simbot.qguild.api.GatewayApis.Normal]
     * 或 [/gateway/bot][love.forte.simbot.qguild.api.GatewayApis.Shared]
     * 接口获取网关地址
     *
     * 然后进行 `websocket` 连接，一旦连接成功，就会返回 [OpCode 10 Hello][Hello] 消息。这个消息主要的内容是心跳周期，单位毫秒(milliseconds)
     */
    @Serializable
    public data class Hello(@SerialName("d") override val data: Data) : Signal<Hello.Data>(Opcode.Hello),
        ReceivedSignal {
        /**
         * @property heartbeatInterval 心跳周期，单位毫秒(milliseconds)
         */
        @Serializable
        public data class Data(@SerialName("heartbeat_interval") public val heartbeatInterval: Long)
    }

    /**
     * [2.鉴权连接](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_2-%E9%89%B4%E6%9D%83%E8%BF%9E%E6%8E%A5)
     *
     * 建立 `websocket` 连接之后，就需要进行鉴权了，需要发送一个 [OpCode 2 Identify](https://bot.q.qq.com/wiki/develop/api/gateway/opcode.html) 消息
     */
    @Serializable
    public data class Identify(@SerialName("d") override val data: Data) : Signal<Identify.Data>(Opcode.Identify),
        SendingSignal {

        /**
         *
         * @property token 创建机器人的时候分配的，
         * 格式为 `QQBot {ACCESS_TOKEN}`
         * @property intents 此次连接所需要接收的事件，具体可参考 [Intents]
         * @property shard 该参数是用来进行水平分片的。该参数是个拥有两个元素的数组。
         * 例如：`[0,4]`，代表分为四个片，当前链接是第 0 个片，业务稍后应该继续建立 shard 为 `[1,4]`, `[2,4]`, `[3,4]` 的链接，才能完整接收事件。更多详细的内容可以参考 [Shard]。
         * @property properties 目前无实际作用，可以按照自己的实际情况填写，也可以留空
         */
        @Serializable
        public data class Data(
            public val token: String,
            public val intents: Intents,
            public val shard: Shard,
            public val properties: Map<String, String>
        )
    }


    /**
     * [3.发送心跳](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_3-%E5%8F%91%E9%80%81%E5%BF%83%E8%B7%B3)
     *
     * 鉴权成功之后，就需要按照周期进行心跳发送。[d][data] 为客户端收到的最新的消息的 `s`，如果是第一次连接，传 `null`。
     *
     * 心跳发送成功之后会收到 [OpCode 11 Heartbeat ACK][HeartbeatACK] 消息
     *
     */
    @Serializable
    public data class Heartbeat(@SerialName("d") override val data: Long?) : Signal<Long?>(Opcode.Heartbeat),
        SendingSignal

    /**
     * [Heartbeat ACK](https://bot.q.qq.com/wiki/develop/api/gateway/opcode.html)
     *
     * 当发送心跳成功之后，就会收到该消息
     */
    @Serializable
    public data object HeartbeatACK : Signal<Unit>(Opcode.HeartbeatACK), ReceivedSignal {
        /**
         * 没有data信息
         */
        @SerialName("d")
        override val data: Unit
            get() = Unit
    }

    /**
     * [4.恢复连接](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html#_4-%E6%81%A2%E5%A4%8D%E8%BF%9E%E6%8E%A5)
     *
     * 有很多原因都会导致连接断开，断开之后短时间内重连会补发中间遗漏的事件，以保障业务逻辑的正确性。
     * 断开重连不需要发送 `Identify` 请求。在连接到 Gateway 之后，需要发送 [Opcode 6 Resume][Resume] 消息
     *
     * 其中 [seq][Data.seq] 指的是在接收事件时候的 `s` 字段，我们推荐开发者在处理过事件之后记录下 `s` 这样可以在 `resume` 的时候传递给 websocket，websocket 会自动补发这个 seq 之后的事件。
     *
     * 恢复成功之后，就开始补发遗漏事件，所有事件补发完成之后，会下发一个 [Resumed Event][Dispatch]
     *
     */
    @Serializable
    public data class Resume(@SerialName("d") override val data: Data) : Signal<Data>(Opcode.Resume),
        SendingSignal {

        /**
         * @property token token
         * @property seq 在接收事件时候的 `s` 字段
         */
        @Serializable
        public data class Data(
            public val token: String, @SerialName("session_id") public val sessionId: String, public val seq: Long
        )
    }

    /**
     * 推送的事件。
     *
     * 具体的事件类型参考各实现类。
     *
     * 当一个事件的解析出现异常或存在未知的 `type` 时，可被解析为 [Unknown] 并仅携带原始信息。
     *
     * ## 序列化器
     * 建议使用 [resolveDispatchSerializer] 通过 JSON 中
     * [Dispatch.DISPATCH_CLASS_DISCRIMINATOR] 的值来获取对应的序列化器，
     * 而不是使用多态。
     *
     * [Dispatch] 从 `v4.1.0` 开始不再是 `sealed` ，不能直接使用多态序列化。
     *
     * @see love.forte.simbot.qguild.event
     *
     */
    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonClassDiscriminator(Dispatch.DISPATCH_CLASS_DISCRIMINATOR)
    public abstract class Dispatch : Signal<@Contextual Any>(Opcode.Dispatch) {

        /**
         * 事件序列
         */
        protected abstract val s: Long

        public abstract val id: String?

        /**
         * 事件序列
         */
        public val seq: Long get() = s

        /**
         * 此事件的实际本体
         */
        @SerialName("d")
        public abstract override val data: Any


        public companion object {

            internal const val DEFAULT_SEQ: Long = -1L

            /**
             * [Dispatch] 使用 [Json] 进行多态解析时的类鉴别器属性名。
             *
             */
            public const val DISPATCH_CLASS_DISCRIMINATOR: String = "t"

            /**
             * 将 [json] 的 [classDiscriminator][JsonConfiguration.classDiscriminator] 调整为 [`t`][DISPATCH_CLASS_DISCRIMINATOR]
             *
             */
            public fun toDispatchJson(json: Json): Json = Json(json) {
                this.classDiscriminator = DISPATCH_CLASS_DISCRIMINATOR
            }

            /**
             * 构建一个 [Json], 并且保证 [classDiscriminator][JsonConfiguration.classDiscriminator] 为 [`t`][DISPATCH_CLASS_DISCRIMINATOR]
             *
             */
            public inline fun dispatchJson(crossinline block: JsonBuilder.() -> Unit): Json = Json {
                block()
                this.classDiscriminator = DISPATCH_CLASS_DISCRIMINATOR
            }
        }


        /**
         * 用于承载未知类型事件的事件类型。
         *
         * 当bot接收到了一个事件但是其类型未知时将会被包装为 [Unknown]。
         *
         * [Unknown] 的构建仅由内部完成。
         *
         */
        public data class Unknown @QGInternalApi constructor(
            override val id: String? = null,
            override val s: Long = DEFAULT_SEQ,
            override val data: JsonElement,
            val raw: String
        ) : Dispatch()

    }

    /**
     * 回调地址验证。开放平台对机器人服务端进行验证
     *
     * @see Opcode.CallbackVerify
     */
    @Serializable
    public data class CallbackVerify(
        @SerialName("d") override val data: Data
    ) : Signal<CallbackVerify.Data>(Opcode.CallbackVerify) {


        /**
         * 请求结构(`Payload.d`)
         *
         * | 字段 | 描述 |
         * | --- | --- |
         * | plain_token | 需要计算签名的字符串 |
         * | event_ts | 计算签名使用时间戳 |
         */
        @Serializable
        public data class Data(
            @SerialName("plain_token")
            val plainToken: String,
            @SerialName("event_ts")
            val eventTs: String
        )

        /**
         * 回调地址验证返回结果
         *
         *  | 字段 | 描述 |
         *  | --- | --- |
         *  | plain_token | 需要计算签名的字符串 |
         *  | signature | 签名 |
         *
         */
        @Serializable
        public data class Verified(
            @SerialName("plain_token")
            val plainToken: String,
            val signature: String,
        )
    }
}


/**
 * 获取当前json结构体中的 `op` 字段。
 *
 */
public fun JsonElement.getOpcode(): Int? = jsonObject["op"]?.jsonPrimitive?.int

@QGInternalApi
public fun JsonElement.tryGetId(): String? = kotlin.runCatching {
    jsonObject["id"]?.jsonPrimitive?.contentOrNull
}.getOrNull()

@QGInternalApi
public fun JsonElement.tryGetSeq(): Long? = kotlin.runCatching {
    jsonObject["s"]?.jsonPrimitive?.longOrNull
}.getOrNull()

/**
 * [Shared](https://bot.q.qq.com/wiki/develop/api/gateway/shard.html)
 *
 * 随着 bot 的增长并被添加到越来越多的频道中，事件越来越多，业务有必要对事件进行水平分割，实现负载均衡。机器人网关实现了一种用户可控制的分片方法，该方法允许跨多个网关连接拆分事件。
 * 分片完全由用户控制，并且不需要在单独的连接之间进行状态共享。
 *
 * 要在连接上启用分片，需要在建立连接的时候指定分片参数，具体参考 [gateway](https://bot.q.qq.com/wiki/develop/api/gateway/reference.html)
 *
 */
@Serializable(SharedSerializer::class)
public data class Shard(val value: Int, val total: Int) {
    public constructor(range: IntRange) : this(range.first, range.last)

    init {
        require(value < total) { "Shared value must less than total, but value $value >= total $total" }
    }

    override fun toString(): String = "[$value, $total]"

    public companion object {
        /**
         * 代表总1个分片的第0个片，也就是独一个的完整片。
         */
        @JvmField
        public val FULL: Shard = Shard(0, 1)
    }
}


/**
 * `Range(1, 2)` -> `[1, 2]`
 */
internal object SharedSerializer : KSerializer<Shard> {
    private val arraySerializer = IntArraySerializer()
    override fun deserialize(decoder: Decoder): Shard {
        val array = arraySerializer.deserialize(decoder)
        return when {
            array.isEmpty() -> Shard(0, 1)
            array.size == 1 -> Shard(array[0], array[0])
            else -> Shard(array[0], array[1])
        }
    }

    override val descriptor: SerialDescriptor get() = arraySerializer.descriptor

    override fun serialize(encoder: Encoder, value: Shard) {
        arraySerializer.serialize(encoder, intArrayOf(value.value, value.total))
    }
}

/**
 * @suppress Used by symbol processor.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
public annotation class DispatchTypeName(val value: String)

/**
 * 解析 [json] 并寻找匹配的 [KSerializer]<out [Signal.Dispatch]>,
 * 如果找不到则得到 `null`。
 *
 * @see resolveDispatchSerializer
 */
public fun resolveDispatchSerializer(
    json: JsonObject,
    allowNameMissing: Boolean = false,
): KSerializer<out Signal.Dispatch>? {
    val eventName = json[Signal.Dispatch.DISPATCH_CLASS_DISCRIMINATOR]?.jsonPrimitive?.contentOrNull
    require(allowNameMissing || eventName != null) {
        "Required json attribute $.${Signal.Dispatch.DISPATCH_CLASS_DISCRIMINATOR} is missing"
    }

    eventName ?: return null

    return resolveDispatchSerializer(eventName)
}
