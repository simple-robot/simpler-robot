/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerManager.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.api.message.events.MsgGet


/**
 * 消息处理器。
 */
interface MsgGetProcessor {
    /**
     * 接收到消息监听并进行处理。
     */
    fun onMsg(msgGet: MsgGet): ListenResult<*>
}


/**
 * 监听函数管理器。
 */
interface ListenerManager : MsgGetProcessor, ListenerRegistrar {

    /**
     * 根据监听类型获取所有对应的监听函数。
     */
    fun <T : MsgGet> getListenerFunctions(type: Class<out T>? = null) : Collection<ListenerFunction>


}


/**
 * 监听函数注册器。
 */
interface ListenerRegistrar {
    /**
     * 注册一个 [监听函数][ListenerFunction]。
     */
    fun register(listenerFunction: ListenerFunction)
}


/**
 * 监听函数构建器。
 */
public interface ListenerManagerBuilder {

    /**
     * 得到一个 [ListenerManager] 实例。
     */
    fun build(): ListenerManager
}

