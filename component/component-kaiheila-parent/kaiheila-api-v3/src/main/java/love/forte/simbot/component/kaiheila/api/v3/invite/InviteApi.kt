package love.forte.simbot.component.kaiheila.api.v3.invite

import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.EmptyResp

// 邀请相关的API



/**
 * [邀请相关接口](https://developer.kaiheila.cn/doc/http/invite) 请求实例接口。
 *
 *
 */
public interface InviteApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>
public interface PostInviteApiReq<RESP : ApiData.Resp<*>> : InviteApiReq<RESP>, ApiData.Req.Post<RESP>
public interface GetInviteApiReq<RESP : ApiData.Resp<*>> : InviteApiReq<RESP>, ApiData.Req.Get<RESP>

public interface EmptyRespInviteApiReq : InviteApiReq<EmptyResp>, ApiData.Req.Empty
public interface EmptyRespPostInviteApiReq : EmptyRespInviteApiReq, PostInviteApiReq<EmptyResp>
public interface EmptyRespGetInviteApiReq : EmptyRespInviteApiReq, GetInviteApiReq<EmptyResp>


/**
 * [邀请相关接口](https://developer.kaiheila.cn/doc/http/invite) 响应实例接口。
 */
public interface InviteApiRespData : ApiData.Resp.Data
