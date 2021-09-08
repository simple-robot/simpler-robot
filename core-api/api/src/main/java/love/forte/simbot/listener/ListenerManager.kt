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
@file:JvmName("ListenerManagers")
package love.forte.simbot.listener

import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.events.MsgGet


/**
 * 消息处理器。
 *
 */
public interface MsgGetProcessor {

    /**
     * 接收到消息监听并进行处理。
     *
     * 实现中，其应当内置一个事件处理调度器。
     *
     * Java中，更建议使用 [onMsgIfExist]。
     * kt中，更建议使用 `MsgGetProcessor.onMsg<T> { ... }`。
     */
    suspend fun onMsg(msgGet: MsgGet)


    /**
     * 判断是否存在某个类型的监听函数。
     */
    fun <T : MsgGet> contains(type: Class<out T>): Boolean


    /**
     * 如果存在则触发，否则得到null。
     * @param type [MsgGet]类型
     * @param msgGet 需要触发的实例。
     */
    suspend fun <T : MsgGet> onMsgIfExist(type: Class<out T>, msgGet: MsgGet) {
        if (contains(type)) {
            onMsg(msgGet)
        }
    }

    /**
     * 如果存在则触发，否则得到null。
     * @param type [MsgGet]类型。
     * @param msgGetBlock MsgGet实例获取函数。
     *
     */
    suspend fun <T : MsgGet> onMsgIfExist(type: Class<out T>, msgGetBlock: () -> MsgGet) {
        if (contains(type)) {
            onMsg(msgGetBlock())
        }
    }
}


/**
 * 检测，如果存在则触发监听流程，否则得到null。
 */
public suspend inline fun <reified T : MsgGet> MsgGetProcessor.onMsg(block: () -> T?) {
    if (contains(T::class.java)) {
        block()?.let { onMsg(it) }
    }
}


/**
 * 检测，如果存在则触发监听流程，否则得到null。
 */
public suspend inline fun <T : MsgGet> MsgGetProcessor.onMsg(type: Class<out T>, block: () -> T?) {
    if (contains(type)) {
        block()?.let { onMsg(it) }
    }
}


/**
 * 监听函数管理器。
 */
public interface ListenerManager : MsgGetProcessor, ListenerRegistrar {

    /**
     * 根据监听类型获取所有对应的监听函数。
     */
    fun <T : MsgGet> getListenerFunctions(type: Class<out T>? = null) : Collection<ListenerFunction>

    /**
     * 根据ID获取监听函数。
     */
    fun getListenerFunctionById(id: String) : ListenerFunction?

    /**
     * 根据ID清除掉一个监听函数。
     */
    fun removeListenerById(id: String): ListenerFunction?

    /**
     * 移除监听函数。
     */
    fun removeListener(listenerFunction: ListenerFunction): ListenerFunction?

    /**
     * 根据组别清除掉相关的监听函数.
     * 如果
     * @return 清理掉的数量。
     */
    fun removeListenerByGroup(group: String): Int

    /**
     * 根据组别清除掉相关的监听函数.
     * @return 清理掉的数量。
     */
    @OptIn(SimbotExperimentalApi::class)
    fun removeListenerByGroup(group: ListenerGroup): Int
}


/**
 * 监听函数注册器。
 */
interface ListenerRegistrar {
    /**
     * 注册一个 [监听函数][ListenerFunction]。
     */
    fun register(vararg listenerFunctions: ListenerFunction)
}


