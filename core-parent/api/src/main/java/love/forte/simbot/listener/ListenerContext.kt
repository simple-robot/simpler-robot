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

package love.forte.simbot.listener

import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.intercept.Context


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

    /**
     * 从 `global` 中获取信息。
     */
    @JvmDefault
    fun global(key: String): Any? {
        return contextMap.global[key]
    }

    /**
     * 从 `instant` 中获取信息。
     */
    @JvmDefault
    fun instant(key: String): Any? {
        return contextMap.instant[key]
    }

    /**
     * 向 `global` 中设置信息。
     */
    @JvmDefault
    fun global(key: String, value: Any): Any? {
        return contextMap.global.put(key, value)
    }

    /**
     * 向 `instant` 中设置信息。
     */
    @JvmDefault
    fun instant(key: String, value: Any): Any? {
        return contextMap.instant.put(key, value)
    }

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



