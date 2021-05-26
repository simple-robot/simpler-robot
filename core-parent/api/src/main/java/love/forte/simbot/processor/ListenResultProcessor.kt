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

@file:JvmName("ListenResultProcessors")

package love.forte.simbot.processor

import love.forte.simbot.Context
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.listener.ListenResult
import love.forte.simbot.listener.ListenerFunctionInvokeData


/**
 * 监听响应处理器。
 *
 * 此处理器接收一个 [监听函数处理器上下文][ListenResultProcessorContext] 实例
 *
 * @since 2.0.0
 *
 * @author ForteScarlet
 */
public interface ListenResultProcessor : Processor<ListenResult<*>, ListenResultProcessorContext, Boolean> {

    /**
     * 接收 [ListenResultProcessorContext] 进行处理（例如解析并进行自动回复等）。
     *
     * @return 是否处理成功。
     */
    override fun processor(processContext: ListenResultProcessorContext): Boolean


    /**
     * 优先级。默认最低。
     */
    // @JvmDefault
    val priority: Int get() = PriorityConstant.LAST

}


/**
 * 监听函数处理器上下文实例。
 *
 * @see context
 *
 * @since 2.0.0
 */
public interface ListenResultProcessorContext : Context<ListenResult<*>> {

    /**
     * 监听函数执行所需动态参数。
     */
    val listenerFunctionInvokeData: ListenerFunctionInvokeData

    /**
     * 监听函数响应值实例。
     */
    val listenResult: ListenResult<*>


    /**
     * Same as [ListenResult].
     */
    // @JvmDefault
    override val mainValue: ListenResult<*>
        get() = listenResult
}


/**
 * 获取一个 [ListenResultProcessorContext] 的实例。
 */
public fun context(
    listenResult: ListenResult<*>,
    listenerFunctionInvokeData: ListenerFunctionInvokeData,
): ListenResultProcessorContext =
    ListenResultProcessorContextImpl(listenResult, listenerFunctionInvokeData)


/**
 * Impl for [ListenResultProcessorContext].
 */
private data class ListenResultProcessorContextImpl(
    override val listenResult: ListenResult<*>,
    override val listenerFunctionInvokeData: ListenerFunctionInvokeData,
) : ListenResultProcessorContext
