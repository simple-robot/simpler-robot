/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ExceptionHandle.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.exception

import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.intercept.BaseContext
import love.forte.simbot.listener.ListenResult
import love.forte.simbot.listener.ListenerContext
import love.forte.simbot.listener.ListenerFunction


/**
 *
 * 异常处理器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
fun interface ExceptionHandle<E : Throwable> {

    /**
     * 异常处理函数。对异常进行处理并得到一个监听函数的返回值。
     *
     * @return 监听执行相应。
     */
    fun doHandle(context: ExceptionHandleContext<E>): ListenResult<*>

}


/**
 * 异常异常处理器的context。
 *
 * @property mainValue 捕获到的异常。
 * @property msgGet 监听函数监听到的消息实例。
 * @property listenerFunction 被捕获到异常的监听函数。
 * @property listenerContext
 */
@Suppress("MemberVisibilityCanBePrivate")
public class ExceptionHandleContext<E : Throwable>(
    mainValue: E,
    val msgGet: MsgGet,
    val listenerFunction: ListenerFunction,
    val listenerContext: ListenerContext
) : BaseContext<E>(mainValue) {
    val exception: Throwable get() = mainValue

    override fun toString(): String = "ExceptionHandleContext<$mainValue>(msg=$msgGet, func=$listenerFunction)"
}