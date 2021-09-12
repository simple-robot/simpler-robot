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

package love.forte.simbot.core.listener

import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.SimbotInternalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.filter.ListenerFilter
import love.forte.simbot.listener.*
import java.lang.reflect.Type


public fun interface AnnotationGetter {
    fun getAnnotation(type: Class<out Annotation>): Annotation?
}


/**
 *
 * 基于函数的 [ListenerFunction] 实现，提供一个原始的执行函数 [func] 来实现一个监听函数流程。
 *
 * [FunctionListenerFunction] 趋近于较为原始的监听函数实现，
 *
 * TODO 提供更加便捷的构建器。
 *
 *
 * @author ForteScarlet
 */
@OptIn(SimbotInternalApi::class, SimbotExperimentalApi::class)
public class FunctionListenerFunction
@SimbotExperimentalApi constructor(
    override val id: String,
    override val name: String,
    override val spare: Boolean,
    override val priority: Int,
    override val async: Boolean,
    /** 所属载体。 */
    override val type: Type,
    override val listenTypes: Set<Class<out MsgGet>>,
    groupNames: List<String> = emptyList(),
    /**
     * 当 [groupNames] 不为空时，[groupManager] 不可为 null.
     */
    groupManager: ListenerGroupManager? = null,
    /**
     * 注解获取器。
     * */
    private val annotationGetter: (type: Class<out Annotation>) -> Annotation?,
    private val func: suspend (data: ListenerFunctionInvokeData) -> ListenResult<*>,
    override val filter: ListenerFilter?,
) : ListenerFunction {


    override val groups: List<ListenerGroup> = if (groupNames.isEmpty()) emptyList() else {
        val manager = checkNotNull(groupManager){ "Group manager cannot be null when groups name was not empty." }
        manager.assignGroup(this, *groupNames.toTypedArray())
    }



    @Suppress("UNCHECKED_CAST")
    override fun <A : Annotation> getAnnotation(type: Class<out A>): A? = annotationGetter(type) as? A

    @Volatile
    private var realInvoker: suspend (data: ListenerFunctionInvokeData) -> ListenResult<*> = func


    override suspend fun invoke(data: ListenerFunctionInvokeData): ListenResult<*> {
        return realInvoker.invoke(data)
    }

    @OptIn(SimbotExperimentalApi::class)
    override val switch: ListenerFunction.Switch = Switch()


    @OptIn(SimbotExperimentalApi::class)
    private inner class Switch : ListenerFunction.Switch {
        override fun enable() {
            realInvoker = func
        }

        override fun disable() {
            realInvoker = ListenerFunction.Switch.DISABLE_FUNCTION_INVOKER
        }

        override val isEnable: Boolean
            get() = realInvoker === func
    }

}
