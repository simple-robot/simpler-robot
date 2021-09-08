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

import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.BaseApiDataKey
import love.forte.simbot.component.kaiheila.api.ListResp
import love.forte.simbot.component.kaiheila.api.RouteInfoBuilder


/**
 * [获取私信聊天会话列表](https://developer.kaiheila.cn/doc/http/user-chat#%E8%8E%B7%E5%8F%96%E7%A7%81%E4%BF%A1%E8%81%8A%E5%A4%A9%E4%BC%9A%E8%AF%9D%E5%88%97%E8%A1%A8)
 *
 *
 */
public object UserChatListReq :
    GetUserChatApiReq<ListResp<UserChatView, ApiData.Resp.EmptySort>>,
    BaseApiDataKey("user-chat", "list") {
    override val key: ApiData.Req.Key get() = this
    override val dataSerializer get() = UserChatView.emptySortListSerializer

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = route
    }

    override val body: Any? get() = null
}

//
// @Serializable
// public class UserChatListResp(
//
//     /**
//      * 私信会话 Code
//      */
//     val code: String,
//
//     /**
//      * 上次阅读消息的时间
//      */
//     @SerialName("last_read_time")
//     val lastReadTime: Int,
//     /**
//      * 最新消息时间
//      */
//     @SerialName("latest_msg_time")
//     val latestMsgTime: Int,
//     /**
//      * 未读消息数
//      */
//     @SerialName("unread_count")
//     val unreadCount: Int,
//
//     /**
//      * 目标用户信息
//      */
//     @SerialName("target_info")
//     val targetInfo: TargetInfo,
//
//     ) : UserChatApiRespData {
//
//
//     /**
//      * 目标用户信息
//      */
//     @Serializable
//     public data class TargetInfo(
//         val id: String,
//         val username: String,
//         val online: Boolean,
//         val avatar: String,
//     )
// }
//

