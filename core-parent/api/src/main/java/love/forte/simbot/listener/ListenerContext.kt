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
import love.forte.simbot.mark.Since


/**
 *
 * 监听函数上下文。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@Since("2.1.0", desc = ["上下文整体结构重构"])
@Suppress("MemberVisibilityCanBePrivate")
public interface ListenerContext : ContextMap {


    /**
     * 从 `global` 中获取信息。
     */
    fun global(key: String): Any? {
        TODO()
        // return contextMap.global[key]
    }

    /**
     * 从 `instant` 中获取信息。
     */
    fun instant(key: String): Any? {
        TODO()
        // return contextMap.instant[key]
    }

    /**
     * 向 `global` 中设置信息。
     */
    fun global(key: String, value: Any): Any? {
        TODO()
        // return contextMap.global.put(key, value)
    }

    /**
     * 向 `instant` 中设置信息。
     */
    fun instant(key: String, value: Any): Any? {
        TODO()
        // return contextMap.instant.put(key, value)
    }




    /**
     * api中提供了定义的作用域。
     */
    public enum class Scope(val key: String) {
        /** 瞬时的，即每次监听事件触发 */
        EVENT_INSTANT("instant"),

        /** 全局的 */
        GLOBAL("global")
        ;
    }

}


public inline fun findScope(block: () -> String): ListenerContext.Scope? {
    return ListenerContext.Scope.values().find { it.key == block() }
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



