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

package love.forte.simbot.kaiheila.api.v3.user

import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.EmptyResp


/**
 * [用户相关接口](https://developer.kaiheila.cn/doc/http/user) 请求实例接口。
 *
 */
public interface UserApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>
public interface PostUserApiReq<RESP : ApiData.Resp<*>> : UserApiReq<RESP>, ApiData.Req.Post<RESP>
public interface GetUserApiReq<RESP : ApiData.Resp<*>> : UserApiReq<RESP>, ApiData.Req.Get<RESP>

public interface EmptyRespUserApiReq : UserApiReq<EmptyResp>, ApiData.Req.Empty
public interface EmptyRespPostUserApiReq : EmptyRespUserApiReq, PostUserApiReq<EmptyResp>
public interface EmptyRespGetUserApiReq : EmptyRespUserApiReq, GetUserApiReq<EmptyResp>


/**
 * [私信聊天会话相关接口](https://developer.kaiheila.cn/doc/http/user-chat) 响应实例接口。
 */
public abstract class UserApiRespData : love.forte.simbot.kaiheila.api.v3.BaseV3RespData()
