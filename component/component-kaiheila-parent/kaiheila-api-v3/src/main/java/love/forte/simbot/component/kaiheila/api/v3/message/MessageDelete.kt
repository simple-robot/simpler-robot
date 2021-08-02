/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     MessageDelete.kt
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

package love.forte.simbot.component.kaiheila.api.v3.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.RouteInfoBuilder
import love.forte.simbot.component.kaiheila.api.key


/**
 * [删除频道聊天消息](https://developer.kaiheila.cn/doc/http/message#%E5%88%A0%E9%99%A4%E9%A2%91%E9%81%93%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * *无返回参数*
 *
 * @param msgId    消息 id
 */
public class MessageDeleteReq(msgId: String) : EmptyRespPostMessageApiReq {

    companion object Key : ApiData.Req.Key by key("/message/delete") {
        private val ROUTE = listOf("message", "delete")
    }

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
    }

    override val body: Any = Body(msgId)

    override val key: ApiData.Req.Key
        get() = Key


    @Serializable
    private data class Body(@SerialName("msg_id") val msgId: String)
}