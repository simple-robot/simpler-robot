/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.listener.ListenerContext
import love.forte.simbot.listener.ListenerContextFactory
import love.forte.simbot.listener.MapScopeContext
import love.forte.simbot.listener.ScopeContext


/**
 * [ListenerContext] 数据类实现。
 */
@SimbotExperimentalApi
public data class ListenerContextImpl(
    private val eventInstantContext: ScopeContext,
    private val globalContext: ScopeContext,
) : ListenerContext {

    override fun getContext(scope: ListenerContext.Scope): ScopeContext {
        return when(scope) {
            ListenerContext.Scope.EVENT_INSTANT -> eventInstantContext
            ListenerContext.Scope.GLOBAL -> globalContext
        }
    }
}


/**
 * [ListenerContextFactory] 实现。
 * 单例。
 */
@SimbotExperimentalApi
public object ListenerContextFactoryImpl : ListenerContextFactory {

    /** 每次获取得到一个新的 [MapScopeContext] 实例。 */
    private val eventInstantContext: ScopeContext get() = MapScopeContext(ListenerContext.Scope.EVENT_INSTANT)

    /** 全局初始化的上下文 */
    private val globalContext: ScopeContext = MapScopeContext(ListenerContext.Scope.GLOBAL)


    override fun getListenerContext(msgGet: MsgGet): ListenerContext {
         return ListenerContextImpl(eventInstantContext, globalContext)
    }
}

