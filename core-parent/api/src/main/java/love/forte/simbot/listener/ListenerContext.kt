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

import love.forte.simbot.Context
import love.forte.simbot.api.message.events.MsgGet


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
    override val mainValue: ContextMap
        get() = contextMap

    /**
     * 从 `global` 中获取信息。
     */
    fun global(key: String): Any? {
        return contextMap.global[key]
    }

    /**
     * 从 `instant` 中获取信息。
     */
    fun instant(key: String): Any? {
        return contextMap.instant[key]
    }

    /**
     * 向 `global` 中设置信息。
     */
    fun global(key: String, value: Any): Any? {
        return contextMap.global.put(key, value)
    }

    /**
     * 向 `instant` 中设置信息。
     */
    fun instant(key: String, value: Any): Any? {
        return contextMap.instant.put(key, value)
    }

}


/**
 * 监听函数上下文构建工厂。
 */
public interface ListenerContextFactory {
    /**
     * 通过 [当前监听事件实例][msgGet] 和 [上下文映射表][contextMap] 构建一个本次监听所需的上下文实例。
     */
    fun getListenerContext(
        msgGet: MsgGet,
        contextMap: ContextMap,
    ): ListenerContext
}



