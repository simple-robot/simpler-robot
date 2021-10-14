/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     DirectMessageApi.kt
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

package love.forte.simbot.kaiheila.api.v3.message.direct

import love.forte.simbot.kaiheila.api.ApiData
import love.forte.simbot.kaiheila.api.EmptyResp


/**
 * [用户私聊消息相关接口](https://developer.kaiheila.cn/doc/http/direct-message) 请求实例接口。
 *
 *
 */
public interface DirectMessageApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>
public interface PostDirectMessageApiReq<RESP : ApiData.Resp<*>> : DirectMessageApiReq<RESP>, ApiData.Req.Post<RESP>
public interface GetDirectMessageApiReq<RESP : ApiData.Resp<*>> : DirectMessageApiReq<RESP>, ApiData.Req.Get<RESP>

public interface EmptyRespDirectMessageApiReq : DirectMessageApiReq<EmptyResp>, ApiData.Req.Empty
public interface EmptyRespPostDirectDirectMessageApiReq : EmptyRespDirectMessageApiReq, PostDirectMessageApiReq<EmptyResp>
public interface EmptyRespGetDirectDirectMessageApiReq : EmptyRespDirectMessageApiReq, GetDirectMessageApiReq<EmptyResp>
