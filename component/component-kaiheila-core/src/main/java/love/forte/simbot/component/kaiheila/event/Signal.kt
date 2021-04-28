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

package love.forte.simbot.component.kaiheila.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.KaiheilaRuntimeException
import love.forte.simbot.component.kaiheila.event.KaiheilaSignalReconnectException.Companion.reconnectException
import love.forte.simbot.component.kaiheila.event.Event as KhlEvent


/**
 * 提供一个参数，得到一个json字符串的工厂。
 */
public interface JsonValueFactory<P> {
    fun jsonValue(p: P): String
}


/**
 * 开黑啦 websocket概述 - [信令](https://developer.kaiheila.cn/doc/websocket#%E4%BF%A1%E4%BB%A4%E6%A0%BC%E5%BC%8F)
 *
 * 信令基本格式：
 * ```json
 * {
 *     "s" : 1,  // int, 信令，详情参照信令说明
 *     "d" : [], // 数据字段mixed
 *     "sn" : 0, // int, 该字段并不一定有，只在s=0时有，与webhook一致。
 * }
 * ```
 *
 */
@Serializable
public sealed class Signal<T> {

    abstract val s: Int
    abstract val d: T?
    abstract val sn: Int?


    //region 信令[0] - EVENT
    /**
     * 信令0 - [EVENT](https://developer.kaiheila.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[0]%20EVENT)
     *
     * 方向： server->client
     *  说明： 在正常连接状态下，收到的消息事件等。
     *  参数列表：
     *
     *  具体参见 [Event](https://developer.kaiheila.cn/doc/event)
     *
     *  注意： 该消息会有 `sn`, 代表消息序号, 针对当前 `session` 的消息的序号, 客户端需记录该数字,并按顺序接收消息， `resume` 时需传入该参数才能完成。
     *
     */
    @Serializable
    public data class Event<E : KhlEvent>(override val s: Int, override val d: E?, override val sn: Int?) : Signal<E>()
    //endregion


    //region 信令[1] - HELLO
    /**
     * 信令1 - [HELLO](https://developer.kaiheila.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[1]%20HELLO)
     * 方向： server->client
     * 说明： 当我们成功连接websocket后，客户端应该在6s内收到该包，否则认为连接超时。
     * 成功示例：
     * ```json
     * {
     *    "s": 1,
     *    "d": {
     *        "code": 0,
     *        "session_id": "xxxx"
     *    }
     * }
     *
     * ```
     * 失败：
     *
     * 状态码	含义	            备注
     * 40100	缺少参数
     * 40101	无效的token
     * 40102	token验证失败
     * 40103	token过期	需要重新连接
     *
     * 示例：
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
    public data class Hello(override val s: Int, override val d: HelloPack) : Signal<HelloPack>() {
        override val sn: Int? get() = null
    }

    @Serializable
    public data class HelloPack(val code: Int, @SerialName("session_id") val sessionId: String?)
    //endregion


    //region 信令[2] - PING
    /**
     * 信令2 - [PING](https://developer.kaiheila.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[2]%20PING)
     *
     * 方向： client -> server
     * 说明： 每隔30s(随机-5，+5),将当前的最大 sn 传给服务端,客户端应该在6s内收到PONG, 否则心跳超时。
     *
     */
    @Serializable
    public data class Ping(override val s: Int = 2, override val sn: Int) : Signal<String>() {
        override val d: String? get() = null

        companion object : JsonValueFactory<Int> {
            @JvmStatic
            override fun jsonValue(p: Int): String = """{"s":2,"sn":$p}"""

        }
    }
    //endregion


    //region 信令[3] - PONG
    /**
     * 信令3 - [PONG](https://developer.kaiheila.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[3]%20PONG)
     *
     * 方向： server -> client
     *
     * 说明： 回应客户端发出的ping
     *
     * 示例：
     *
     * ```json
     *  {
     *      "s": 3
     *  }
     * ```
     */
    @Serializable
    public data class Pong(override val s: Int) : Signal<String>() {
        override val d: String? get() = null
        override val sn: Int get() = -1
    }
    //endregion


    //region 信令[5] - RECONNECT
    /**
     * 信令5 - [RECONNECT](https://developer.kaiheila.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[5]%20RECONNECT)
     *
     * 方向： server->client
     *
     * 说明： 服务端通知客户端, 代表该连接已失效, 请重新连接。客户端收到后应该主动断开当前连接。
     *
     * 注意： 客户端收到该信令代表因为某些原因导致当前连接已失效, 需要进行以下操作以避免消息丢失.
     *
     * 1. 重新获取 gateway;
     * 2. 清空本地的 sn 计数;
     * 3. 清空本地消息队列.
     *
     * 状态码	描述
     * - 40106	resume 失败, 缺少参数
     * - 40107	当前 session 已过期 (resume 失败, PING的sn无效)
     * - 40108	无效的 sn , 或 sn 已经不存在 (resume 失败, PING的 sn 无效)
     *
     * 示例：
     *  ```json
     *  {
     *      "s": 5
     *      "d": {
     *          "code": 41008,
     *          "err": "Missing params"
     *      }
     *  }
     *
     *  ```
     *
     */
    @Serializable
    public data class Reconnect(override val s: Int, override val d: ReconnectPack) : Signal<ReconnectPack>() {
        override val sn: Int get() = -1
    }


    @Serializable
    public data class ReconnectPack(val code: Int, val err: String? = null)

    /**
     * 响应值
     */
    public enum class ReconnectCode(val code: Int, val err: String) {
        RESUME_FAIL_MISS_PARAM(40106, "resume 失败, 缺少参数"),
        SESSION_EXPIRED(40107, "当前 session 已过期 (resume 失败, PING的sn无效)"),
        SN_INVALID_OR_NON_EXISTENT(40108, "无效的sn , 或 sn 已经不存在 (resume 失败, PING的 sn 无效)"),
        /** 其他未知 */
        UNKNOWN(-99999, "未知错误")
        ;

        companion object {
            @JvmStatic
            fun byCode(code: Int): ReconnectCode {
                return when(code) {
                    RESUME_FAIL_MISS_PARAM.code -> RESUME_FAIL_MISS_PARAM
                    SESSION_EXPIRED.code -> SESSION_EXPIRED
                    SN_INVALID_OR_NON_EXISTENT.code -> SN_INVALID_OR_NON_EXISTENT
                    else -> UNKNOWN
                }
            }
        }
    }

    //endregion


    //region 信令[6] - RESUME ACK
    /**
     *
     * 信令6 - [RESUME ACK](https://developer.kaiheila.cn/doc/websocket#%E4%BF%A1%E4%BB%A4[6]%20RESUME%20ACK)
     * 方向： server->client
     * 说明： 服务端通知客户端 resume 动作成功，中间所有离线消息已经全部发送成功
     * 示例：
     * ```json
     * {
     *     "s": 6
     *     "d": {
     *         "session_id": "xxxx-xxxxxx-xxx-xxx"
     *     }
     * }
     * ```
     *
     */
    @Serializable
    public data class ResumeAck(override val s: Int, override val d: ResumeAckPack) : Signal<ResumeAckPack>() {
        override val sn: Int get() = -1
    }

    /**
     * [ResumeAck.d] 的数据体.
     */
    @Serializable
    public data class ResumeAckPack(@SerialName("session_id") val sessionId: String)


    //endregion


}


/**
 * 开黑啦信令异常。
 */
public open class KaiheilaSignalException : KaiheilaRuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace)
}


/**
 * 开黑啦 [信令5 - Reconnect][Signal.Reconnect] 异常。
 *
 * @see Signal.Reconnect
 * @see reconnectException
 */
public open class KaiheilaSignalReconnectException : KaiheilaSignalException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace)

    companion object {
        @JvmStatic
        fun reconnectException(resp: Signal.ReconnectPack): KaiheilaSignalReconnectException {
            val code = resp.code
            val err = resp.err ?: Signal.ReconnectCode.byCode(code).err
            return KaiheilaSignalReconnectException("code: $code, err: $err")
        }
    }
}
