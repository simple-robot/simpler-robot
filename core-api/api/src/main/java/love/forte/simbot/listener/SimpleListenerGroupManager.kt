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
 * [ListenerGroup] 的简易实现。
 */
@OptIn(SimbotExperimentalApi::class)
internal data class SimpleListenerGroup(
    override val name: String,
    private val listenerSet: MutableSet<ListenerFunction> = mutableSetOf(),
) : ListenerGroup {

    @Synchronized
    internal fun add(listenerFunction: ListenerFunction): Boolean = listenerSet.add(listenerFunction)

    override val listeners: List<ListenerFunction> get() = listenerSet.toList()
}


/**
 * [ListenerGroupManager] 的简易实现。 其中， [assignGroup] 所得到的所有列表均 **不可修改**。
 */
@OptIn(SimbotExperimentalApi::class)
public class SimpleListenerGroupManager : ListenerGroupManager {

    private val groups: MutableMap<String, ListenerGroup> = ConcurrentHashMap<String, ListenerGroup>()

    override fun assignGroup(listenerFunction: ListenerFunction, vararg groups: String): List<ListenerGroup> {
        val list = groups.asSequence().distinct().map { group ->
            val listenerGroup = this.groups.computeIfAbsent(group, ::SimpleListenerGroup)
            listenerGroup as SimpleListenerGroup
            listenerGroup.add(listenerFunction)
            listenerGroup
        }.toList()

        return Collections.unmodifiableList(list)
    }

    override fun getGroup(groupName: String): ListenerGroup? = groups[groupName]
}

