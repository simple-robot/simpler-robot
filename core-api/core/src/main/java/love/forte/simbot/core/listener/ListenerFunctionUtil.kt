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

@file:JvmName("ListenerFunctionUtil")

package love.forte.simbot.core.listener

import love.forte.common.utils.annotation.AnnotationUtil
import love.forte.simbot.annotation.Filters
import love.forte.simbot.annotation.Listens
import love.forte.simbot.annotation.Priority
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.filter.ListenerFilter
import love.forte.simbot.listener.ListenResult
import love.forte.simbot.listener.ListenerFunction
import love.forte.simbot.listener.ListenerFunctionInvokeData
import love.forte.simbot.listener.ListenerGroup
import java.lang.reflect.Method
import java.lang.reflect.Type


/**
 * 将一个 [Method] 转化为 [ListenerFunction].
 * 需要提供 [监听类型][listens] 和(可选的) [过滤参数][filters].
 *
 * 如果参数中不提供 [listens], 那么必须保证此method上标注了 [Listens] 注解或者相关的衍生注解，否则将会抛出异常。
 *
 *
 * @throws IllegalStateException 如果没有提供 [listens], method上也没有提供
 *
 */
@JvmSynthetic
public fun Method.toListenerFunction(listens: Listens? = null, filters: Filters? = null): ListenerFunction {
    val realListens = listens ?: AnnotationUtil.getAnnotation(this, Listens::class.java)
    ?: throw IllegalStateException("Cannot found annotation Listens from method $this")

    val realFilters: Filters? = filters ?: AnnotationUtil.getAnnotation(this, Filters::class.java)
    val priority = AnnotationUtil.getAnnotation(this, Priority::class.java)?.value ?: realListens.priority


    TODO("Not implemented yet.")
}

/**
 *
 *
 * @throws IllegalArgumentException [listenTypes] 数量为0时
 *
 * @param listenTypes 监听事件的类型。不可为空
 */
public fun Method.toListenerFunction(
    listenTypes: Array<Class<out MsgGet>>,
    id: String? = null,
    name: String? = null,

    ): ListenerFunction {

    TODO("Not implemented yet.")

    // return LambdaListenerFunction(
    //
    // )
}


/**
 * @see toListenerFunction
 */
// for Java
public fun methodToListenerFunction(
    method: Method,
    listens: Listens? = null,
    filters: Filters? = null,
): ListenerFunction = method.toListenerFunction(listens, filters)


public class LambdaListenerFunction @OptIn(SimbotExperimentalApi::class) constructor(
    id: String,
    name: String,
    spare: Boolean,
    priority: Int,
    /** 所属载体。 */
    type: Type,
    listenTypes: Set<Class<out MsgGet>>,
    groups: List<ListenerGroup>,
    /** 注解获取器。需要注意，得到的返回值的 [Annotation] 类型需要与提供的 [type] 注解类型一致。 */
    annotationGetter: (type: Class<out Annotation>) -> Annotation?,
    invoker: ListenerFunctionInvoker,
    filter: ListenerFilter?,
) : ListenerFunction by @OptIn(SimbotExperimentalApi::class) FunctionListenerFunction(
    id, name, spare, priority, type, listenTypes, groups, annotationGetter,
    invoker.asSuspendFunction(),
    filter
)

/**
 * 服务于 [LambdaListenerFunction] 的函数接口。
 */
public fun interface ListenerFunctionInvoker {
    operator fun invoke(data: ListenerFunctionInvokeData): ListenResult<*>
}


private fun ListenerFunctionInvoker.asSuspendFunction(): (suspend (ListenerFunctionInvokeData) -> ListenResult<*>) =
    { data -> invoke(data) }



