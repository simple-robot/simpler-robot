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
 * 监听函数分组的管理器。 一切 [ListenerGroup] 都应该由此类进行管理。
 *
 * @author ForteScarlet
 */
@SimbotExperimentalApi
public interface ListenerGroupManager {

    /**
     * 为一个监听函数分配分组。
     */
    fun assignGroup(listenerFunction: ListenerFunction, vararg groups: String): List<ListenerGroup>

    /**
     * 根据名称尝试得到一个分组。
     */
    fun getGroup(groupName: String): ListenerGroup?
}

