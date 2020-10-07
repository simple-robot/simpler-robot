/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerContext.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.intercept.Context
import java.util.concurrent.ConcurrentHashMap




/**
 *
 * 监听函数上下文。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public interface ListenerContext : Context<ContextMap> {
    /**
     * context map.
     */
    val contextMap: ContextMap

    /**
     * 主体为一个 [ContextMap], 以提供监听函数间的信息传递。
     */
    @JvmDefault
    override val mainValue: ContextMap
        get() = contextMap
}


/**
 * 监听函数上下文构建工厂。
 */
public interface ListenerContextFactory {
    fun getListenerContext(
        msgGet: MsgGet,
        contextMap: ContextMap,
    ): ListenerContext
}



