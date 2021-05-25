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

package love.forte.simbot.component.kaiheila.api

import love.forte.simbot.component.kaiheila.api.ApiData.Req
import love.forte.simbot.component.kaiheila.api.ApiData.Resp
import kotlin.reflect.KClass


/**
 * 此接口定义一个与Api相关的数据类。
 *
 * 定义两个类型一个来回，分别为 [Request][Req] 和 [Response][Resp].
 *
 * @author ForteScarlet
 */
public interface ApiData {

    /**
     * [Request][Req]. 请求相关的数据类。
     * 一次请求，都会有一个对应的 [响应][RESP].
     */
    public interface Req<RESP : Resp> : ApiData {
        /** 获取响应的数据类型。 */
        val respType: KClass<out RESP>

        /**
         * 此请求对应的api路由路径。
         * 例如：`/guild/list`
         * */
        val route: String

        /**
         * 此次请求所发送的数据。为null则代表没有参数。
         */
        val parameters: Any?
    }

    /**
     * [Response][Resp]. 请求响应相关的数据类。
     */
    public interface Resp : ApiData

}