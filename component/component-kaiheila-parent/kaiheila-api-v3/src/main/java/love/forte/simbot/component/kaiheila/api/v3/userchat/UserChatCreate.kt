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

package love.forte.simbot.component.kaiheila.api.v3.userchat

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.BaseApiDataKey
import love.forte.simbot.component.kaiheila.api.BaseApiDataReq
import love.forte.simbot.component.kaiheila.api.ObjectResp


/**
 * [创建私信聊天会话](https://developer.kaiheila.cn/doc/http/user-chat#%E5%88%9B%E5%BB%BA%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E4%BC%9A%E8%AF%9D)
 *
 *
 */
public class UserChatCreateReq(private val targetId: String) : PostUserChatApiReq<ObjectResp<UserChatView>>,
    BaseApiDataReq<ObjectResp<UserChatView>>(Key) {
    companion object Key : BaseApiDataKey("user-chat", "create")

    override val dataSerializer: DeserializationStrategy<ObjectResp<UserChatView>>
        get() = UserChatView.objectSerializer

    override fun createBody(): Any = Body(targetId)

    @Serializable
    private data class Body(@SerialName("target_id") val targetId: String)
}
