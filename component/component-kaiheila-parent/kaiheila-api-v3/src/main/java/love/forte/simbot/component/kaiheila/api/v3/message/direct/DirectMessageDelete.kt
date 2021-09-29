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

package love.forte.simbot.component.kaiheila.api.v3.message.direct

import kotlinx.serialization.SerialName
import love.forte.simbot.component.kaiheila.api.BaseApiDataKey
import love.forte.simbot.component.kaiheila.api.BaseApiDataReq


/**
 * [删除私信聊天消息](https://developer.kaiheila.cn/doc/http/direct-message#%E5%88%A0%E9%99%A4%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E6%B6%88%E6%81%AF)
 *
 * @author ForteScarlet
 */
public class DirectMessageDeleteReq(private val msgId: String) : EmptyRespPostDirectDirectMessageApiReq,
    BaseApiDataReq.Empty(Key) {
    companion object Key : BaseApiDataKey("direct-message", "delete")

    protected override fun createBody(): Any = Body(msgId)
    private data class Body(@SerialName("msg_id") val msgId: String)
}