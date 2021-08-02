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

package love.forte.simbot.component.kaiheila.api.v3.guild

import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.EmptyResp


/**
 * [服务器相关接口](https://developer.kaiheila.cn/doc/http/guild) 请求实例接口。
 *
 *
 */
public interface GuildApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>
public interface GetGuildApiReq<RESP : ApiData.Resp<*>> : GuildApiReq<RESP>, ApiData.Req.Get<RESP>
public interface PostGuildApiReq<RESP : ApiData.Resp<*>> : GuildApiReq<RESP>, ApiData.Req.Post<RESP>

public interface EmptyRespGuildApiReq : GuildApiReq<EmptyResp>, ApiData.Req.Empty
public interface EmptyRespGetGuildApiReq : EmptyRespGuildApiReq, GetGuildApiReq<EmptyResp>
public interface EmptyRespPostGuildApiReq : EmptyRespGuildApiReq, PostGuildApiReq<EmptyResp>


/**
 * [服务器相关接口](https://developer.kaiheila.cn/doc/http/guild) 响应实例接口。
 */
public interface GuildApiRespData : ApiData.Resp.Data



