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

import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.constant.PriorityConstant
import love.forte.simbot.core.listener.ListenerContext
import love.forte.simbot.core.listener.ListenerFunction


/**
 * [ListenerFilter] 中可提供的参数。
 */
public class FilterData(
    val msgGet: MsgGet,
    val atDetection: AtDetection,
    val listenerContext: ListenerContext,
    val listenerFunction: ListenerFunction
)



/**
 * 监听过滤器。
 */
public interface ListenerFilter {

    /**
     * 判断某个消息是否能够进行监听。
     */
    fun test(data: FilterData): Boolean

    /**
     * 优先级，默认为最低级。
     */
    @JvmDefault
    val priority: Int get() = PriorityConstant.LAST
}


/**
 * [ListenerFilter] 注册器。
 */
public interface ListenerFilterRegistrar {
    /**
     * 注册一个 [过滤器][ListenerFilter] 实例。
     * @throws FilterAlreadyExistsException 如果filter已经存在则可能抛出此异常。
     */
    fun registerFilter(name: String, filter: ListenerFilter)
}




/**
 * 过滤器已经存在。
 */
public class FilterAlreadyExistsException : IllegalStateException {
    constructor() : super()
    constructor(s: String?) : super(s)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}
