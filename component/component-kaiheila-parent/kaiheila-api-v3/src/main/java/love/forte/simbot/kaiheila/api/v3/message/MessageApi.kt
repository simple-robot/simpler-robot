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

import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.ApiData
import love.forte.simbot.kaiheila.api.EmptyResp


/**
 * [频道消息相关接口](https://developer.kaiheila.cn/doc/http/message) 请求实例接口。
 *
 *
 */
public interface MessageApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>
public interface PostMessageApiReq<RESP : ApiData.Resp<*>> : MessageApiReq<RESP>, ApiData.Req.Post<RESP>
public interface GetMessageApiReq<RESP : ApiData.Resp<*>> : MessageApiReq<RESP>, ApiData.Req.Get<RESP>

public interface EmptyRespMessageApiReq : MessageApiReq<EmptyResp>, ApiData.Req.Empty
public interface EmptyRespPostMessageApiReq : EmptyRespMessageApiReq, PostMessageApiReq<EmptyResp>
public interface EmptyRespGetMessageApiReq : EmptyRespMessageApiReq, GetMessageApiReq<EmptyResp>


/**
 * [频道消息相关接口](https://developer.kaiheila.cn/doc/http/message) 响应实例接口。
 */
public abstract class MessageApiRespData : love.forte.simbot.kaiheila.api.v3.BaseV3RespData()


/**
 * 消息体内容。
 *
 *
 */
@Serializable
public class Message(
    val id: String,
    val type: Int,
    val author: Map<String, String>,
) : MessageApiRespData()





