/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerFilter.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.filter

import love.forte.simbot.core.annotation.Filter
import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.listener.ListenerContext



/**
 * [ListenerFilter] 中可提供的参数。
 */
public data class FilterData(
    val filter: Filter,
    val msgGet: MsgGet,
    val atDetection: AtDetection,
    val listenerContext: ListenerContext
)


/**
 * 监听过滤器。
 */
public interface ListenerFilter {

    /**
     * 判断某个消息是否能够进行监听。
     */
    fun test(data: FilterData): Boolean
}