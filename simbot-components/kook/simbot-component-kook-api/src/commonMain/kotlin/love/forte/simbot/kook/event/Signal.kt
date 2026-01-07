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

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmStatic


/**
 * 信令基本格式
 *
 * ```
 * {
 *     "s" : 1,  // int, 信令，详情参照信令说明
 *     "d" : {}, // 数据字段mixed
 *     "sn" : 0, // int, 该字段并不一定有，只在s=0时有，与webhook一致。
 * }
 * ```
 *
 * @author ForteScarlet
 */
@Serializable
public sealed class Signal {

    /**
     * 信令类型。
     */
    public abstract val s: Int

    /**
     * 信令数据。
     */
    @Contextual
    public abstract val d: Any


    public companion object {
        //region signal `s` constants
        /**
         * 信令[1] HELLO
         * - 方向：server->client
         * - 说明：当我们成功连接 websocket 后，客户端应该在 6s 内收到该包，否则认为连接超时。
         */
        public const val S_EVENT: Int = 0

        /**
         * 信令[1] HELLO
         * - 方向：server->client
         * - 说明：当我们成功连接 websocket 后，客户端应该在 6s 内收到该包，否则认为连接超时。
         */
        public const val S_HELLO: Int = 1

        /**
         * 信令[2] PING
         * - 方向：client -> server
         * - 说明：每隔 30s(随机-5，+5),将当前的最大 sn 传给服务端,客户端应该在 6s 内收到 PONG, 否则心跳超时。
         */
        public const val S_PING: Int = 2

        /**
         * 信令[3] PONG
         * - 方向：server -> client
         * - 说明：回应客户端发出的 ping
         */
        public const val S_PONG: Int = 3

        /**
         * 信令[4] RESUME
         * - 当链接未断开时 客户端需传入 当前收到的最后一个 sn 序号
         */
        public const val S_RESUME: Int = 4

        /**
         * 信令[5] RECONNECT
         * - 方向：server->client
         * - 说明：服务端通知客户端, 代表该连接已失效, 请重新连接。客户端收到后应该主动断开当前连接。
         */
        public const val S_RECONNECT: Int = 5

        /**
         * 信令[6] RESUME ACK
         * - 方向：server->client
         * - 说明：服务端通知客户端 resume 动作成功，中间所有离线消息已经全部发送成功
         */
        public const val S_RESUME_ACK: Int = 6

        //endregion


    }

    /**
     * [信令[0] - Event](https://developer.kookapp.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[0]%20EVENT)
     * - 方向： server->client
     * - 说明： 在正常连接状态下，收到的消息事件等。
     * 参数列表： 具体参见 [Event](https://developer.kookapp.cn/doc/event/event-introduction)
     *
     * 注意：该消息会有 `sn`, 代表消息序号, 针对当前 `session` 的消息的序号,
     * 客户端需记录该数字,并按顺序接收消息， `resume` 时需传入该参数才能完成。
     *
     * 注意事项：
     *
     * - 收到消息时需要按照 `sn` 顺序处理, 服务端会尽可能保证 `sn` 的顺序性
     * - 假设收到消息的 sn 出现乱序, 需要先存入暂存区 (buffer) 等待正确的 `sn` 消息处理后再从暂存区顺序处理
     * - 假设收到了一条已处理过的 `sn` 的消息, 则直接抛弃不处理
     * - 客户端需要存储当前已处理成功的最大的 `sn`, 待心跳 `ping` 时回传服务端,
     *      如果服务端发现当前客户端最新处理成功的消息 sn 落后于最新消息 (丢包等异常情况), 服务端将会按照客户端指定的 `sn` 将之后所有最新的消息重传给客户端.
     * - 消息内容与 `webhook` 保持一致
     *
     * ```json
     * {
     *     "s": 0,
     *     "d": {
     *         // 参见event
     *     },
     *     "sn": 1000
     * }
     * ```
     *
     */
    @Serializable
    public data class Event<out EX : EventExtra>(
        override val d: love.forte.simbot.kook.event.Event<EX>,
        public val sn: Int
    ) : Signal() {
        override val s: Int
            get() = S_EVENT

        /**
         * [d] 的别名。
         *
         * @see d
         */
        public val data: love.forte.simbot.kook.event.Event<EX> get() = d
    }

    /**
     * [信令[1] HELLO](https://developer.kookapp.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[1]%20HELLO)
     * - 方向： server->client
     * - 说明： 当我们成功连接 websocket 后，客户端应该在 6s 内收到该包，否则认为连接超时。
     *
     * 成功示例：
     *
     * ```json
     * {
     *     "s": 1,
     *     "d": {
     *         "code": 0,
     *         "session_id": "xxxx"
     *     }
     * }
     * ```
     *
     * 失败：
     *
     * | 状态码 | 含义 | 备注 |
     * | ---- | ---- | --- |
     * | 40100 | 缺少参数 |  |
     * | 40101 | 无效的 token |  |
     * | 40102 | token 验证失败 |  |
     * | 40103 | token 过期 | 需要重新连接 |
     *
     * ```json
     * {
     *     "s": 1,
     *     "d": {
     *         "code": 40103
     *     }
     * }
     * ```
     *
     */
    @Serializable
    public data class Hello(
        override val d: Data
    ) : Signal() {
        override val s: Int
            get() = S_HELLO

        /**
         * [Hello] 中的数据。
         *
         * @param code 状态码
         * @param sessionId 会话 id
         */
        @Serializable
        public data class Data(val code: Int, @SerialName("session_id") val sessionId: String)

        public companion object {
            /**
             * 成功
             */
            public const val CODE_SUCCESS: Int = 0

            /**
             * 缺少参数
             */
            public const val CODE_MISSING_PARAMETER: Int = 40100

            /**
             * 无效的 token
             */
            public const val CODE_INVALID_TOKEN: Int = 40101

            /**
             * token 验证失败
             */
            public const val CODE_TOKEN_VERIFICATION_FAILED: Int = 40102

            /**
             * token 过期
             */
            public const val CODE_TOKEN_EXPIRED: Int = 40103
        }
    }


    /**
     * [信令[2] PING](https://developer.kookapp.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[2]%20PING)
     * - 方向：client->server
     * - 说明：每隔 30s(随机-5，+5),将当前的最大 `sn` 传给服务端,客户端应该在 `6s` 内收到 PONG, 否则心跳超时。
     *
     * ```json
     * {
     *     "s": 2,
     *     "sn": 6 // 客户端目前收到的最新的消息 sn
     * }
     * ```
     *
     * 注意事项：
     *
     * - 心跳间隔： 30 秒 + rand(-5,5)秒
     * - 如果发了 ping, 6 秒内没有收到 pong，我们应该进入到超时状态。
     *
     */
    @Serializable
    public data class Ping(
        /**
         * 客户端目前收到的最新的消息 sn
         */
        private val sn: Int
    ) : Signal() {
        override val s: Int
            get() = S_PING

        override val d: Unit
            get() = Unit

        public fun jsonValue(): String = """{"s":$S_PING,"sn":$sn}"""
    }

    /**
     * [信令[3] PONG](https://developer.kookapp.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[3]%20PONG)
     * - 方向：server->client
     * - 说明：回应客户端的 ping
     *
     * ```json
     * {
     *     "s": 3
     * }
     * ```
     *
     */
    @Serializable
    public object Pong : Signal() {
        override val s: Int
            get() = S_PONG

        override val d: Unit
            get() = Unit

        override fun toString(): String {
            return "Pong(s=$S_PONG)"
        }
    }

    /**
     * [信令[4] RESUME](https://developer.kookapp.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[4]%20RESUME)
     *
     * 当链接未断开时 客户端需传入 当前收到的最后一个 sn 序号
     *
     * ```json
     * {
     *     "s": 4,
     *     "sn": 100
     * }
     * ```
     *
     */
    @Serializable
    public data class Resume(val sn: Int) : Signal() {
        override val s: Int
            get() = S_RESUME

        override val d: Unit
            get() = Unit

        public companion object {

            /**
             * [Resume] 的 jsonValue
             */
            @JvmStatic
            public fun Resume.jsonValue(): String =
                """{""s":$S_RESUME,"sn":$sn}"""
        }
    }

    /**
     * [信令[5] RECONNECT](https://developer.kookapp.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[5]%20RECONNECT)
     *
     * - 方向： server->client
     * - 说明： 服务端通知客户端, 代表该连接已失效, 请重新连接。客户端收到后应该主动断开当前连接。
     *
     * 注意： 客户端收到该信令代表因为某些原因导致当前连接已失效, 需要进行以下操作以避免消息丢失.
     *
     * - 重新获取 `gateway`;
     * - 清空本地的 `sn` 计数;
     * - 清空本地消息队列.
     *
     * | 状态码 | 描述 |
     * | --- | --- |
     * | 40106 | resume 失败, 缺少参数 |
     * | 40107 | 当前 session 已过期 (resume 失败, PING 的 sn 无效) |
     * | 40108 | 无效的 sn , 或 sn 已经不存在 (resume 失败, PING 的 sn 无效) |
     *
     * 示例：
     * ```json
     * {
     *     "s": 5,
     *     "d": {
     *         "code": 41008,
     *         "err": "Missing params"
     *     }
     * }
     * ```
     */
    @Serializable
    public data class Reconnect(override val d: Data) : Signal() {
        override val s: Int
            get() = S_RECONNECT

        /**
         * [Reconnect] 中的数据。
         *
         * @property code 状态码
         * @property err 错误信息
         */
        @Serializable
        public data class Data(val code: Int, val err: String)
    }

    /**
     * [信令[6] RESUME ACK](https://developer.kookapp.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[6]%20RESUME%20ACK)
     *
     * - 方向：server->client
     * - 说明：服务端通知客户端 `resume` 动作成功，中间所有离线消息已经全部发送成功
     *
     * 示例：
     * ```json
     * {
     *     "s": 6,
     *     "d": {
     *         "session_id": "xxxx-xxxxxx-xxx-xxx"
     *     }
     * }
     * ```
     */
    @Serializable
    public data class ResumeAck(override val d: Data) : Signal() {
        override val s: Int
            get() = S_RESUME_ACK

        /**
         * [ResumeAck] 中的数据。
         *
         * @property sessionId 会话 id
         */
        @Serializable
        public data class Data(@SerialName("session_id") val sessionId: String)
    }

}
