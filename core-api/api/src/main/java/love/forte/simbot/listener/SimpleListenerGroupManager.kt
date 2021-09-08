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
import java.util.*
import java.util.concurrent.ConcurrentHashMap


/**
 * [ListenerGroup] 的简易实现, 其中仅记录 [id][ListenerFunction.id], 通过 [listenerManager] 获取实际值，
 * 而不记录具体的监听函数实例, 每次在获取 [listeners] 的时候会移除 [listenerSet] 中已经不存在的内容。
 */
@OptIn(SimbotExperimentalApi::class)
internal data class SimpleListenerGroup(
    private val listenerManager: ListenerManager,
    override val name: String,
    private val listenerSet: MutableSet<String> = mutableSetOf(),
) : ListenerGroup {

    @Synchronized
    internal fun add(listenerFunction: ListenerFunction): Boolean = listenerSet.add(listenerFunction.id)
    @Synchronized
    internal fun remove(listenerFunction: ListenerFunction): Boolean = listenerSet.remove(listenerFunction.id)
    @Synchronized
    internal fun removeById(id: String): Boolean = listenerSet.remove(id)
    @get:Synchronized
    override val listeners: List<ListenerFunction> get() = listenerSet.asSequence().mapNotNull { id ->
        val function = listenerManager.getListenerFunctionById(id)
        if (function == null) {
            listenerSet.remove(id)
        }
        function
    }.toList()
}


/**
 * [ListenerGroupManager] 的简易实现。 其中， [assignGroup] 所得到的所有列表均 **不可修改**。
 * 内部通过 [SimpleListenerGroup] 作为 [ListenerGroup] 实现。
 *
 */
@OptIn(SimbotExperimentalApi::class)
public class SimpleListenerGroupManager(
    private val listenerManager: ListenerManager
) : ListenerGroupManager {
    private val groups: MutableMap<String, SimpleListenerGroup> = ConcurrentHashMap<String, SimpleListenerGroup>()

    override fun assignGroup(listenerFunction: ListenerFunction, vararg groups: String): List<ListenerGroup> {
        val list = groups.asSequence().distinct().map { group ->
            val listenerGroup = this.groups.computeIfAbsent(group) { groupName ->
                SimpleListenerGroup(listenerManager, groupName)
            }
            listenerGroup.add(listenerFunction)
            listenerGroup
        }.toList()

        return Collections.unmodifiableList(list)
    }

    override fun getGroup(groupName: String): ListenerGroup? = groups[groupName]
}

