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

package love.forte.simbot.component.kaiheila.api.v3.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.BaseApiDataKey
import love.forte.simbot.component.kaiheila.api.BaseApiDataReq


/**
 * [删除消息的某个回应](https://developer.kaiheila.cn/doc/http/message#%E5%88%A0%E9%99%A4%E6%B6%88%E6%81%AF%E7%9A%84%E6%9F%90%E4%B8%AA%E5%9B%9E%E5%BA%94)
 *
 */
public class MessageDeleteReactionReq(
    private val msgId: String,
    private val emoji: String,
    private val userId: String,
) : EmptyRespPostMessageApiReq, BaseApiDataReq.Empty(Key) {
    companion object Key : BaseApiDataKey("message", "delete-reaction")

    override fun createBody(): Any = Body(msgId, emoji, userId)

    @Serializable
    private data class Body(
        @SerialName("msg_id") val msgId: String,
        val emoji: String,
        @SerialName("user_id") val userId: String,
    )
}
