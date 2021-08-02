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

import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.EmptyResp


/**
 * [私信聊天会话相关接口](https://developer.kaiheila.cn/doc/http/user-chat) 请求实例接口。
 *
 *
 */
public interface UserChatApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>
public interface PostUserChatApiReq<RESP : ApiData.Resp<*>> : UserChatApiReq<RESP>, ApiData.Req.Post<RESP>
public interface GetUserChatApiReq<RESP : ApiData.Resp<*>> : UserChatApiReq<RESP>, ApiData.Req.Get<RESP>

public interface EmptyRespUserChatApiReq : ApiData.Req.Empty
public interface EmptyRespPostUserChatApiReq : EmptyRespUserChatApiReq, PostUserChatApiReq<EmptyResp>
public interface EmptyRespGetUserChatApiReq : EmptyRespUserChatApiReq, GetUserChatApiReq<EmptyResp>


/**
 * [私信聊天会话相关接口](https://developer.kaiheila.cn/doc/http/user-chat) 响应实例接口。
 */
public interface UserChatApiRespData : ApiData.Resp.Data



/**
 *
 * [UserChatView] 中的 `targetInfo` 属性。
 *
 */
@Serializable
public data class UserChatTargetInfo(
    val id: String,
    val username: String,
    val avatar: String,
    val online: Boolean = false,
)


