package love.forte.simbot.component.kaiheila.api.v3.intimacy

import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.EmptyResp
import love.forte.simbot.component.kaiheila.api.v3.BaseV3RespData

// intimacy
// 亲密度相关接口 https://developer.kaiheila.cn/doc/http/intimacy




/**
 * [亲密度相关接口](https://developer.kaiheila.cn/doc/http/intimacy) 请求实例接口。
 *
 *
 */
public interface IntimacyApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>
public interface PostIntimacyApiReq<RESP : ApiData.Resp<*>> : IntimacyApiReq<RESP>, ApiData.Req.Post<RESP>
public interface GetIntimacyApiReq<RESP : ApiData.Resp<*>> : IntimacyApiReq<RESP>, ApiData.Req.Get<RESP>

public interface EmptyRespIntimacyApiReq : IntimacyApiReq<EmptyResp>, ApiData.Req.Empty
public interface EmptyRespPostIntimacyApiReq : EmptyRespIntimacyApiReq, PostIntimacyApiReq<EmptyResp>
public interface EmptyRespGetIntimacyApiReq : EmptyRespIntimacyApiReq, GetIntimacyApiReq<EmptyResp>


/**
 * [亲密度相关接口](https://developer.kaiheila.cn/doc/http/intimacy) 响应实例接口。
 */
public abstract class IntimacyApiRespData : BaseV3RespData()


