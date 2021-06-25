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

package love.forte.simbot.component.kaiheila.api.v3.channel

import love.forte.simbot.component.kaiheila.api.ApiData


/**
 * [频道相关接口](https://developer.kaiheila.cn/doc/http/channel) 请求实例接口。
 *
 *
 */
public interface ChannelApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>


/**
 * [频道相关接口](https://developer.kaiheila.cn/doc/http/channel) 响应实例接口。
 */
public interface ChannelApiRespData : ApiData.Resp.Data



// 消息详情
