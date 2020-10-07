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

import love.forte.common.collections.concurrentSortedQueueOf
import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.filter.AtDetectionFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * 储存监听函数的List。使用有序队列。
 */
private fun managerList(): MutableCollection<ListenerFunction> = PriorityQueue(ListenerFunctionComparable)

/**
 * 监听函数自排序队列。
 */
private fun listenerFunctionQueue(vararg func: ListenerFunction): Queue<ListenerFunction> = concurrentSortedQueueOf(ListenerFunctionComparable , *func)


/**
 * [ListenerManager] 实现。
 */
public class ListenerManagerImpl(
    private val atDetectionFactory: AtDetectionFactory
) : ListenerManager, ListenerRegistrar {

    /**
     * 监听函数集合，通过对应的监听类型进行分类。
     *
     * 此为主集合，所有的监听函数均存在于此。
     */
    private val mainListenerFunctionMap: MutableMap<Class<out MsgGet>, Queue<ListenerFunction>> =
        ConcurrentHashMap()

    /**
     * 监听函数缓冲区，对后续出现的消息类型进行记录并缓存。
     * 当 [register] 了新的监听函数后对应相关类型将会被清理。
     */
    private val cacheListenerFunctionMap: MutableMap<Class<out MsgGet>, Queue<ListenerFunction>> =
        ConcurrentHashMap()


    /**
     * 目前所有监听中的类型。
     */
    private val allListenTypes: MutableSet<Class<out MsgGet>> get() = mainListenerFunctionMap.keys

    /**
     * 注册一个 [监听函数][ListenerFunction]。
     */
    override fun register(listenerFunction: ListenerFunction) {
        // 获取其监听类型，并作为key存入map
        val listenType = listenerFunction.listenType

        // merge into map.
        mainListenerFunctionMap.merge(listenType, listenerFunctionQueue(listenerFunction)) { oldValue, value ->
            oldValue.apply { addAll(value) }
        }

        // clear cache map.
        // 寻找并更新缓存监听
        cacheListenerFunctionMap.forEach {
            if(listenType.isAssignableFrom(it.key)) {
                it.value.add(listenerFunction)
            }
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
