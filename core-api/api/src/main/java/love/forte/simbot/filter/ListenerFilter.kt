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

package love.forte.simbot.filter

import love.forte.simbot.annotation.Filter
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.listener.ListenerContext
import love.forte.simbot.listener.ListenerFunction


/**
 * [ListenerFilter] 中可提供的参数。
 */
public class FilterData @OptIn(SimbotExperimentalApi::class) constructor(
    val msgGet: MsgGet,
    val atDetection: AtDetection,
    val listenerContext: ListenerContext,
    val listenerFunction: ListenerFunction
)





/**
 * 监听过滤器。
 *
 * 过滤器属于 [监听函数][ListenerFunction] 的一种属性，
 * 因此何时何地进行过滤匹配 应当由 [监听函数][ListenerFunction] 进行实现。
 *
 *
 * since 2.2.0: 基于函数构建一个 [ListenerFilter] 可以参考：
 * - Kotlin: [listenerFilter { data -> ... }][listenerFilter]、[buildListenerFilter { ... }][buildListenerFilter]
 * - Java: [ListenerFilterBuilder]、[ListenerFilterUtil.listenerFilter(data -> ...)][listenerFilter]
 *
 */
public interface ListenerFilter : (FilterData) -> Boolean {

    /**
     * 判断某个消息是否能够进行监听。
     */
    fun test(data: FilterData): Boolean

    
    override fun invoke(p1: FilterData): Boolean = test(p1)

    /**
     * 尝试从文本中提取动态过滤参数。
     * 自定义实现下可以直接返回一个 `null`。
     */
    
    fun getFilterValue(name: String, text: String): String? = null

    /**
     * 优先级，默认为最低级。
     */
    
    val priority: Int get() = PriorityConstant.LAST
}


/**
 * 一个 [Filter] 的注解过滤器的处理器，同样也是一个过滤器。此过滤器能够得到其对应的 [Filter] 注解实例。
 */
public interface AnnotatedListenerFilterProcessor : ListenerFilter {

    /**
     * 此过滤器所对应的 [Filter] 注解实例。
     */
    val filter: Filter

    /**
     * 注解初始化函数。
     * 在构建完 [AnnotatedListenerFilterProcessor] 实例后，会执行 [init] 函数提供 [Filter] 进行数据初始化。
     */
    fun init(filter: Filter)

    /**
     * 过滤匹配。
     */
    override fun test(data: FilterData): Boolean
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


/**
 * 过滤器不存在。
 */
public class NoSuchFilterException : NoSuchElementException {
    constructor() : super()
    constructor(s: String?) : super(s)
}
