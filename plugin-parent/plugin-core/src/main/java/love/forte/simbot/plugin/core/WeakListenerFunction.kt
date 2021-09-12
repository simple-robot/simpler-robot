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

package love.forte.simbot.plugin.core

import love.forte.simbot.WeakRef
import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.filter.ListenerFilter
import love.forte.simbot.getValue
import love.forte.simbot.listener.ListenResult
import love.forte.simbot.listener.ListenerFunction
import love.forte.simbot.listener.ListenerFunctionInvokeData
import love.forte.simbot.listener.ListenerGroup
import java.lang.reflect.Type


/**
 *
 * @author ForteScarlet
 */
public class WeakListenerFunction(function: ListenerFunction): ListenerFunction {
    private val delegate by WeakRef(function)


    override val id: String = function.id
    override val name: String = function.name
    override val spare: Boolean = function.spare
    override val priority: Int = function.priority
    override val async: Boolean = function.async

    override fun <A : Annotation> getAnnotation(type: Class<out A>): A? = delegate?.getAnnotation(type)

    override val listenTypes: Set<Class<out MsgGet>> get() = delegate?.listenTypes ?: emptySet()
    override val filter: ListenerFilter? get() = delegate?.filter
    override val type: Type
        get() = delegate?.type ?: throw IllegalStateException("Function $id has been released.")

    @SimbotExperimentalApi
    override val groups: List<ListenerGroup>
        get() = delegate?.groups ?: emptyList()

    override suspend fun invoke(data: ListenerFunctionInvokeData): ListenResult<*> = delegate?.invoke(data) ?: ListenResult

    @SimbotExperimentalApi
    override val switch: ListenerFunction.Switch
        get() = delegate?.switch ?: ListenerFunction.Switch
}