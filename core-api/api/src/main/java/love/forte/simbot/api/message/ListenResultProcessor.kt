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

package love.forte.simbot.api.message

import love.forte.simbot.listener.ListenResult
import love.forte.simbot.listener.ListenerFunctionInvokeData


/**
 * 监听响应处理器。提供监听函数的[动态参数][ListenerFunctionInvokeData] 以及最终的 [监听响应][ListenResult]
 *
 * @author ForteScarlet
 */
public interface ListenResultProcessor {

    /**
     * 进行监听响应处理。
     */
    fun process(listenerFunctionInvokeData: ListenerFunctionInvokeData, listenResult: ListenResult<*>): Boolean

}