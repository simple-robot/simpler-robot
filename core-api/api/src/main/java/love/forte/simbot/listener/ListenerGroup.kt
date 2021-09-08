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

package love.forte.simbot.listener

import love.forte.simbot.api.SimbotExperimentalApi


/**
 *
 * 监听函数的组。
 *
 * 一个组管理多个监听函数, 并且有一个(在监听函数管理器中)唯一的名称。
 */
@SimbotExperimentalApi
public interface ListenerGroup {

    /**
     * 此组的分组名称。
     */
    val name: String

    /**
     * 得到此组别下的所有监听函数列表。
     */
    val listeners: List<ListenerFunction>
}


/**
 *
 * 可变的监听函数列表。
 *
 * 内部维护一个可变列表与一个视图列表，当可变列表发生变化的时候，重新生成视图列表。
 *
 * 为了保证线程安全，可变列表的变动是同步的。
 *
 *
 * 其内部隔离出了视图列表，因此保证添加的时候不会影响到视图获取，但是会造成一定的更新滞后问题。
 *
 *
 * [hashCode] 与 [equals] 基于 [name] 进行判断。
 *
 *
 * 这或许看上去类似于Copy On Write.
 *
 *
 */
@SimbotExperimentalApi
public class MutableListenerGroup(
    override val name: String,
    private val mutableListeners: MutableList<ListenerFunction> = mutableListOf(),
) : ListenerGroup {

    override fun hashCode(): Int = name.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is ListenerGroup) return false
        return name == other.name
    }

    @Synchronized
    fun add(listenerFunction: ListenerFunction) {
        mutableListeners.add(listenerFunction)
        resetView()
    }

    @Synchronized
    fun add(index: Int, listenerFunction: ListenerFunction) {
        mutableListeners.add(index, listenerFunction)
        resetView()
    }

    @Synchronized
    fun addAll(elements: Collection<ListenerFunction>) {
        if (elements.isEmpty()) return
        mutableListeners.addAll(elements)
        resetView()
    }

    @Synchronized
    fun addAll(index: Int, elements: Collection<ListenerFunction>) {
        if (elements.isEmpty()) return
        mutableListeners.addAll(index, elements)
        resetView()
    }

    @Synchronized
    fun remove(index: Int) {
        mutableListeners.removeAt(index)
        resetView()
    }

    @Synchronized
    fun removeById(id: String) {
        mutableListeners.removeIf { it.id == id }
        resetView()
    }

    @Synchronized
    fun clean() {
        mutableListeners.clear()
        cleanView()
    }

    private fun resetView() {
        _listenersView = null
    }

    private fun cleanView() {
        _listenersView = emptyList()
    }


    @Volatile
    private var _listenersView: List<ListenerFunction>? = null


    override val listeners: List<ListenerFunction>
        get() {
            val nowView = _listenersView
            return nowView ?: synchronized(this) {
                val view0 = _listenersView
                view0 ?: mutableListeners.toList().apply {
                    _listenersView = this
                }
            }
        }
}



