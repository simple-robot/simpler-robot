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

package love.forte.simbot.component.kaiheila.api.v3.server

import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.BaseReq
import kotlin.reflect.KClass


/**
 * [服务器相关接口](https://developer.kaiheila.cn/doc/http/guild) 请求实例接口。
 *
 *
 */
public interface GuildApiReq<RESP : GuildApiResp> : ApiData.Req<RESP>


/**
 * [GuildApiReq] 基础抽象类。
 *
 */
public abstract class BaseGuildApiReq<RESP : GuildApiResp>(
    respType: KClass<out RESP>,
) : BaseReq<RESP>(respType), GuildApiReq<RESP>


/**
 * [服务器相关接口](https://developer.kaiheila.cn/doc/http/guild) 响应实例接口。
 *
 *
 */
public interface GuildApiResp : ApiData.Resp


