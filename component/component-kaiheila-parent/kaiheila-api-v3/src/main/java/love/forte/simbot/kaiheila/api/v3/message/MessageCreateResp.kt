/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     MessageCreateRespData.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.kaiheila.api.v3.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.objectResp


/**
 * [发送频道消息][MessageCreateReq] 和 [发送私聊消息][love.forte.simbot.component.kaiheila.api.v3.message.direct.DirectMessageCreateReq]
 * 的响应值。
 * @author ForteScarlet
 */
@Serializable
public data class MessageCreateResp(
    /**
     * 服务端生成的消息 id
     */
    @SerialName("msg_id")
    val msgId: String,

    /**
     * 消息发送时间(服务器时间戳)
     */
    @SerialName("msg_timestamp")
    val msgTimestamp: Long,

    /**
     * 随机字符串，见参数列表
     */
    val nonce: String? = null,
) : MessageApiRespData() {
    companion object {
        val objectResp = objectResp<MessageCreateResp>()
    }


}