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

package love.forte.simbot.kaiheila.api.v3.asset

import love.forte.simbot.kaiheila.api.ApiData
import love.forte.simbot.kaiheila.api.EmptyResp


/**
 * [媒体模块相关接口](https://developer.kaiheila.cn/doc/http/asset) 请求实例接口。
 *
 *
 */
public interface AssetApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>
public interface PostAssetApiReq<RESP : ApiData.Resp<*>> : AssetApiReq<RESP>, ApiData.Req.Post<RESP>
public interface GetAssetApiReq<RESP : ApiData.Resp<*>> : AssetApiReq<RESP>, ApiData.Req.Get<RESP>

public interface EmptyRespAssetApiReq : AssetApiReq<EmptyResp>, ApiData.Req.Empty
public interface EmptyRespPostAssetApiReq : EmptyRespAssetApiReq, PostAssetApiReq<EmptyResp>
public interface EmptyRespGetAssetApiReq : EmptyRespAssetApiReq, GetAssetApiReq<EmptyResp>


/**
 * [媒体模块相关接口](https://developer.kaiheila.cn/doc/http/asset) 响应实例接口。
 */
public abstract class AssetApiRespData : love.forte.simbot.kaiheila.api.v3.BaseV3RespData()