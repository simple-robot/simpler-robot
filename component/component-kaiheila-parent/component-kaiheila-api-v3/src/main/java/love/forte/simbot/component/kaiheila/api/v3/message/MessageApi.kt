/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     MessageApi.kt
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

package love.forte.simbot.component.kaiheila.api.v3.message

import love.forte.simbot.component.kaiheila.api.ApiData


/**
 * [频道消息相关接口](https://developer.kaiheila.cn/doc/http/message) 请求实例接口。
 *
 *
 */
public interface MessageApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>


/**
 * [频道消息相关接口](https://developer.kaiheila.cn/doc/http/message) 响应实例接口。
 */
public interface MessageApiRespData : ApiData.Resp.Data





