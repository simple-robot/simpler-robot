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

package love.forte.simbot.kaiheila.api.v3.userchat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.BaseApiDataKey
import love.forte.simbot.kaiheila.api.BaseApiDataReq


/**
 * [删除私信聊天会话](https://developer.kaiheila.cn/doc/http/user-chat#%E5%88%A0%E9%99%A4%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E4%BC%9A%E8%AF%9D)
 *
 */
public class UserChatDeleteReq(private val chatCode: String) : EmptyRespPostUserChatApiReq, BaseApiDataReq.Empty(Key) {
    companion object Key : BaseApiDataKey("user-chat", "delete")

    protected override fun createBody(): Any = Body(chatCode)

    @Serializable
    private data class Body(@SerialName("chat_code") val chatCode: String)
}
