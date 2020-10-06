/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerFunction.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.filter.ListenerFilter
import java.lang.reflect.Type


/**
 * 监听函数。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
interface ListenerFunction {

    /**
     * 监听函数的名称。应当是唯一的。
     */
    val name: String


    /**
     * 获取此监听函数上可以得到的注解。
     */
    fun <A : Annotation> getAnnotation(type: Class<out A>) : A?


    /**
     * 监听的类型。
     */
    val listenType: Class<out MsgGet>


    /**
     * 此监听函数对应的监听器列表。
     */
    val filters: List<ListenerFilter>


    /**
     * 判断当前监听函数是否可以触发当前类型的监听.
     */
    fun <T: MsgGet> canListen(onType: Class<T>): Boolean


    /**
     * 当前监听函数的载体。例如一个 Class。
     * 一般如果是个method，那么此即为[Class]。
     */
    val type: ListenerFunctionCarrierType


    /**
     * 执行监听函数并返回一个执行后的响应结果。
     */
    fun invoke(): ListenResult<*>
}



/**
 * 监听函数载体类型
 */
public interface ListenerFunctionCarrierType : Type

