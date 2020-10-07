/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerManagerImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.filter.AtDetectionFactory
import love.forte.simbot.core.filter.FilterManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 储存监听函数的List。使用有序队列。
 */
private fun managerList(): MutableCollection<ListenerFunction> = PriorityQueue(ListenerFunctionComparable)


/**
 * [ListenerManager] 实现。
 */
public class ListenerManagerImpl(
    private val atDetectionFactory: AtDetectionFactory
) : ListenerManager, ListenerRegistrar {

    /**
     * 监听函数集合，通过对应的监听类型进行分类。
     */
    private val listenerFunctionMap: MutableMap<Class<out MsgGet>, MutableCollection<ListenerFunction>> =
        ConcurrentHashMap()

    /**
     * 目前所有监听中的类型。
     */
    private val allTypes: MutableSet<Class<out MsgGet>> get() = listenerFunctionMap.keys

    /**
     * 注册一个 [监听函数][ListenerFunction]。
     */
    override fun register(listenerFunction: ListenerFunction) {
        // 获取其监听类型，并作为key存入map
        val listenType = listenerFunction.listenType

        // merge.
        listenerFunctionMap.merge(listenType, managerList()) { oldValue, value ->
            oldValue.apply { addAll(value) }
        }

    }

    /**
     * 接收到消息监听并进行处理。
     */
    override fun onMsg(msgGet: MsgGet): ListenResult<*> {
        TODO("Not yet implemented")
    }

    /**
     * 根据监听类型获取所有对应的监听函数。
     */
    override fun <T : MsgGet> getListenerFunctions(type: Class<out T>?): List<ListenerFunction> {
        TODO("Not yet implemented")
    }
}
