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

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


/**
 * 用于标记 [Opcode] 中对应的往来类型为可接收类型。
 *
 * _参考 [文档](https://bot.q.qq.com/wiki/develop/api/gateway/opcode.html) 中的 "客户端操作含义"_
 */
public sealed interface ReceiveAble

/**
 * 用于标记 [Opcode] 中对应的往来类型为可发送类型。
 *
 * _参考 [文档](https://bot.q.qq.com/wiki/develop/api/gateway/opcode.html) 中的 "客户端操作含义"_
 */
public sealed interface SendAble

/**
 * [`opcode`](https://bot.q.qq.com/wiki/develop/api/gateway/opcode.html) 常量类。
 *
 */
@Suppress("ConstPropertyName")
public object Opcodes {
    /** 服务端进行消息推送 */
    public const val Dispatch: Int = 0
    /** 客户端或服务端发送心跳 */
    public const val Heartbeat: Int = 1
    /** 客户端发送鉴权 */
    public const val Identify: Int = 2
    /** 客户端恢复连接 */
    public const val Resume: Int = 6
    /** 服务端通知客户端重新连接 */
    public const val Reconnect: Int = 7
    /** 当identify或resume的时候，如果参数有错，服务端会返回该消息 */
    public const val InvalidSession: Int = 9

    /**
     * 当客户端与网关建立ws连接之后，网关下发的第一条消息.
     *
     * CODE: 10
     */
    public const val Hello: Int = 10

    /** 当发送心跳成功之后，就会收到该消息 */
    public const val HeartbeatACK: Int = 11

    /**
     * 回调地址验证。开放平台对机器人服务端进行验证
     *
     * 参考 [官方webhook文档](https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/interface-framework/event-emit.html)
     */
    public const val CallbackVerify: Int = 13
}

/**
 * 针对 [Opcodes] 中各 `opcode` 的实例类型。
 */
@Serializable(Opcode.SerializerByCode::class)
public sealed class Opcode(public val code: Int) {
    public val isReceive: Boolean get() = this is ReceiveAble
    public val isSend: Boolean get() = this is SendAble

    // 0
    public object Dispatch : Opcode(Opcodes.Dispatch), ReceiveAble

    // 1
    public object Heartbeat : Opcode(Opcodes.Heartbeat), ReceiveAble, SendAble

    // 2
    public object Identify : Opcode(Opcodes.Identify), SendAble

    // 6
    public object Resume : Opcode(Opcodes.Resume), SendAble

    // 7
    public object Reconnect : Opcode(Opcodes.Reconnect), ReceiveAble

    // 9
    public object InvalidSession : Opcode(Opcodes.InvalidSession), ReceiveAble

    /**
     * 当客户端与网关建立ws连接之后，网关下发的第一条消息.
     *
     * CODE: 10
     */
    public object Hello : Opcode(Opcodes.Hello), ReceiveAble

    // 11
    public object HeartbeatACK : Opcode(Opcodes.HeartbeatACK), ReceiveAble

    /**
     * Callback地址验证
     *
     * 参考 [官方webhook文档](https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/interface-framework/event-emit.html)
     *
     * CODE: 13
     */
    public object CallbackVerify : Opcode(Opcodes.CallbackVerify), ReceiveAble

    public object SerializerByCode : KSerializer<Opcode> {
        override fun deserialize(decoder: Decoder): Opcode {
            return when (val code = decoder.decodeInt()) {
                Opcodes.Dispatch -> Dispatch
                Opcodes.Heartbeat -> Heartbeat
                Opcodes.Identify -> Identify
                Opcodes.Resume -> Resume
                Opcodes.Reconnect -> Reconnect
                Opcodes.InvalidSession -> InvalidSession
                Opcodes.Hello -> Hello
                Opcodes.HeartbeatACK -> HeartbeatACK
                Opcodes.CallbackVerify -> CallbackVerify
                else -> throw NoSuchElementException("opcode: $code")
            }
        }

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("opcode", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: Opcode) {
            encoder.encodeInt(value.code)
        }

    }
}


