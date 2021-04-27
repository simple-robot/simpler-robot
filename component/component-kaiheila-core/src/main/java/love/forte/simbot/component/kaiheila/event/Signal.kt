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

package love.forte.simbot.component.kaiheila.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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
    abstract val d: T
    abstract val sn: Int?



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
    public data class HelloPack(override val s: Int, override val d: HelloPackResp) : Signal<HelloPackResp>() {
        override val sn: Int? get() = null
    }

    @Serializable
    public data class HelloPackResp(val code: Int, @SerialName("session_id") val sessionId: String?)
    //endregion












}




