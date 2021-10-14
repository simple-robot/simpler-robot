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

package love.forte.simbot.kaiheila.api.v3.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.BaseApiDataKey
import love.forte.simbot.kaiheila.api.BaseApiDataReq


/**
 * [删除频道聊天消息](https://developer.kaiheila.cn/doc/http/message#%E5%88%A0%E9%99%A4%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * *无返回参数*
 *
 * @param msgId    消息 id
 */
public class MessageDeleteReq(private val msgId: String) : EmptyRespPostMessageApiReq, BaseApiDataReq.Empty(Key) {

    companion object Key : BaseApiDataKey("message", "delete")

    protected override fun createBody(): Any = Body(msgId)

    @Serializable
    private data class Body(@SerialName("msg_id") val msgId: String)
}