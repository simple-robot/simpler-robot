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
@file:JvmName("ListenerManagers")
package love.forte.simbot.listener

import love.forte.simbot.api.message.events.MsgGet


/**
 * 消息处理器。
 */
interface MsgGetProcessor {
    /**
     * 接收到消息监听并进行处理。
     * Java中，更建议使用 [onMsgIfExist]，kt中，更建议使用 `MsgGetProcessor.onMsg<T> { ... }`。
     */
    fun onMsg(msgGet: MsgGet): ListenResult<*>

    /**
     * 判断是否存在某个类型的监听函数。
     */
    fun <T : MsgGet> contains(type: Class<out T>): Boolean

    /**
     * 如果存在则触发，否则得到null。
     * @param type [MsgGet]类型
     * @param msgGet 需要触发的实例。
     */
    // @JvmDefault
    fun <T : MsgGet> onMsgIfExist(type: Class<out T>, msgGet: MsgGet): ListenResult<*>? {
        return if (contains(type)) {
            onMsg(msgGet)
        } else null
    }

    /**
     * 如果存在则触发，否则得到null。
     * @param type [MsgGet]类型。
     * @param msgGetBlock MsgGet实例获取函数。
     *
     */
    // @JvmDefault
    fun <T : MsgGet> onMsgIfExist(type: Class<out T>, msgGetBlock: () -> MsgGet): ListenResult<*>? {
        return if (contains(type)) {
            onMsg(msgGetBlock())
        } else null
    }



}


/**
 * 检测，如果存在则触发监听流程，否则得到null。
 */
public inline fun <reified T : MsgGet> MsgGetProcessor.onMsg(block: () -> T?): ListenResult<*>? {
    return if (contains(T::class.java)) {
        block()?.let { onMsg(it) }
    } else null
}


/**
 * 检测，如果存在则触发监听流程，否则得到null。
 */
public inline fun <T : MsgGet> MsgGetProcessor.onMsg(type: Class<out T>, block: () -> T?): ListenResult<*>? {
    return if (contains(type)) {
        block()?.let { onMsg(it) }
    } else null
}


/**
 * 监听函数管理器。
 */
public interface ListenerManager : MsgGetProcessor, ListenerRegistrar {

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


