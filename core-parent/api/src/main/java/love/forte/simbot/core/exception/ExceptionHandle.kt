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

package love.forte.simbot.core.exception

import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.intercept.BaseContext
import love.forte.simbot.core.listener.ListenerContext
import love.forte.simbot.core.listener.ListenerFunction


/**
 *
 * 异常处理器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
fun interface ExceptionHandle<E : Throwable> {

    /**
     * 异常处理函数。对异常进行处理并得到一个监听函数的返回值。
     */
    fun doHandle(context: ExceptionHandleContext<E>): Any?

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