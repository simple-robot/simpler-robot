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

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.*


/**
 * [获取私信聊天会话详情](https://developer.kaiheila.cn/doc/http/user-chat#%E8%8E%B7%E5%8F%96%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E4%BC%9A%E8%AF%9D%E8%AF%A6%E6%83%85)
 */
public class UserChatViewReq(private val chatCode: String) : GetUserChatApiReq<ObjectResp<UserChatView>>, BaseApiDataReq<ObjectResp<UserChatView>>(Key) {
    companion object Key : BaseApiDataKey("user-chat", "view")

    override val dataSerializer: DeserializationStrategy<ObjectResp<UserChatView>>
        get() = UserChatView.objectSerializer

    protected override fun createBody(): Any? = null

    override fun RouteInfoBuilder.doRoute() {
        parameters {
            append("chat_code", chatCode)
        }
    }

}

/**
 * 私信聊天会话详情信息。
 *
 */
@Serializable
public class UserChatView(
    /**
     * 私信会话 Code
     */
    val code: String,

    /**
     * 上次阅读消息的时间
     */
    @SerialName("last_read_time")
    val lastReadTime: Int,
    /**
     * 最新消息时间
     */
    @SerialName("latest_msg_time")
    val latestMsgTime: Int,
    /**
     * 未读消息数
     */
    @SerialName("unread_count")
    val unreadCount: Int,

    /**
     * 目标用户信息
     */
    @SerialName("target_info")
    val targetInfo: UserChatTargetInfo,
) : UserChatApiRespData() {
    companion object {
        val objectSerializer = objectResp<UserChatView>()
        val emptySortListSerializer = emptySortListResp<UserChatView>()
    }
}