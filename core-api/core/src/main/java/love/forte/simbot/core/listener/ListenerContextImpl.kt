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

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.dispatcher.ContinuousSessionDispatcherFactory
import love.forte.simbot.listener.*


/**
 * [ListenerContext] 数据类实现。
 */
public data class ListenerContextImpl(
    private val eventInstantContext: ScopeContext,
    private val globalContext: ScopeContext,
    private val continuousSessionScopeContext: ContinuousSessionScopeContext,
) : ListenerContext {

    override fun getContext(scope: ListenerContext.Scope): ScopeContext {
        return when (scope) {
            ListenerContext.Scope.EVENT_INSTANT -> eventInstantContext
            ListenerContext.Scope.GLOBAL -> globalContext
            ListenerContext.Scope.CONTINUOUS_SESSION -> continuousSessionScopeContext
        }
    }
}


/**
 * [ListenerContextFactory] 实现。
 * 单例。
 */
public class CoreListenerContextFactory(
    continuousSessionDispatcherFactory: ContinuousSessionDispatcherFactory,
    defaultTimeout: Long,
) :
    ListenerContextFactory {

    /** 每次获取得到一个新的 [MapScopeContext] 实例。 */
    private val eventInstantContext: ScopeContext get() = MapScopeContext(ListenerContext.Scope.EVENT_INSTANT)

    /** 全局初始化的上下文 */
    private val globalContext: ScopeContext = MapScopeContext(ListenerContext.Scope.GLOBAL)

    /**
     * 持续会话作用域，用于支持消息的持续会话。
     *
     */
    private val continuousSessionScopeContext: ContinuousSessionScopeContext = ContinuousSessionScopeContext(
        CoroutineScope(continuousSessionDispatcherFactory.dispatcher + CoroutineName("CoreListenerContextFactory")),
        defaultTimeout
    )


    override fun getListenerContext(msgGet: MsgGet): ListenerContext {
        return ListenerContextImpl(eventInstantContext, globalContext, continuousSessionScopeContext)
    }

    override fun getScopeContext(msgGet: MsgGet, scope: ListenerContext.Scope): ScopeContext {
        return when (scope) {
            ListenerContext.Scope.EVENT_INSTANT -> eventInstantContext
            ListenerContext.Scope.GLOBAL -> globalContext
            ListenerContext.Scope.CONTINUOUS_SESSION -> continuousSessionScopeContext
        }
    }
}

