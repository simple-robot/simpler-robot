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

import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.listener.ContextMap
import love.forte.simbot.listener.ListenerContext
import love.forte.simbot.listener.ListenerContextFactory


/**
 * [ListenerContext] 数据类实现。
 */
public data class ListenerContextImpl(override val contextMap: ContextMap) : ListenerContext


/**
 * [ListenerContextFactory] 实现。
 * 单例。
 */
public object ListenerContextFactoryImpl : ListenerContextFactory {
    override fun getListenerContext(msgGet: MsgGet, contextMap: ContextMap): ListenerContext {
        return ListenerContextImpl(contextMap)
    }
}

